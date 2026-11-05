package br.com.devops.devops.service;

import br.com.devops.devops.entity.Usuario;
import br.com.devops.devops.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecuperacaoSenhaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    public void solicitarRecuperacaoSenha(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailUsuario(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Gerar token único
            String token = UUID.randomUUID().toString();
            
            // Definir expiração em 24 horas
            LocalDateTime dataExpiracao = LocalDateTime.now().plusHours(24);
            
            // Atualizar usuário com token e data de expiração
            usuario.setTokenResetSenha(token);
            usuario.setDataExpiracaoToken(dataExpiracao);
            usuarioRepository.save(usuario);
            
            // Enviar email com link de recuperação
            emailService.enviarEmailRecuperacaoSenha(email, token, usuario.getNomeUsuario());
        }
        // Não informar se email existe ou não (segurança)
    }

    public Optional<Usuario> validarToken(String token) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByTokenResetSenha(token);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar se token não expirou
            if (usuario.getDataExpiracaoToken() != null && 
                usuario.getDataExpiracaoToken().isAfter(LocalDateTime.now())) {
                return usuarioOpt;
            } else {
                // Token expirado
                usuario.setTokenResetSenha(null);
                usuario.setDataExpiracaoToken(null);
                usuarioRepository.save(usuario);
            }
        }
        return Optional.empty();
    }

    public void redefinirSenha(String token, String novaSenha) {
        Optional<Usuario> usuarioOpt = validarToken(token);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setSenhaUsuario(novaSenha);
            usuario.setTokenResetSenha(null);
            usuario.setDataExpiracaoToken(null);
            usuarioRepository.save(usuario);
        } else {
            throw new RuntimeException("Token inválido ou expirado");
        }
    }
}
