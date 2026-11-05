package br.com.devops.devops.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.devops.devops.entity.Usuario;
import br.com.devops.devops.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Usuario salvar(Usuario usuario) {
        if (usuario.getIdUsuario() == null) {
            usuario.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
            return usuarioRepository.save(usuario);
        }

        return usuarioRepository.findById(usuario.getIdUsuario())
                .map(usuarioExistente -> {
                    usuarioExistente.setNomeUsuario(usuario.getNomeUsuario());
                    usuarioExistente.setEmailUsuario(usuario.getEmailUsuario());
                    usuarioExistente.setLoginUsuario(usuario.getLoginUsuario());
                    usuarioExistente.setRoleUsuario(usuario.getRoleUsuario());

                    if (usuario.getSenhaUsuario() != null && !usuario.getSenhaUsuario().isBlank()) {
                        usuarioExistente.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
                    }

                    return usuarioRepository.save(usuarioExistente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public void deletar(Integer id) {
        usuarioRepository.deleteById(id);
    }
}
