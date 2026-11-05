package br.com.devops.devops.config;

import br.com.devops.devops.entity.Usuario;
import br.com.devops.devops.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class UsuarioPadraoInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public UsuarioPadraoInitializer(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        criar("DEV_ADMIN_PASSWORD", "admin", "Administrador de Teste", "admin@localhost.invalid", "ADMIN");
        criar("DEV_USER_PASSWORD", "cliente", "Cliente de Teste", "cliente@localhost.invalid", "USER");
    }

    private void criar(String variavel, String login, String nome, String email, String role) {
        String senha = System.getenv(variavel);
        if (senha == null || senha.length() < 8 || usuarios.findByLoginUsuario(login).isPresent()) return;
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(nome);
        usuario.setEmailUsuario(email);
        usuario.setLoginUsuario(login);
        usuario.setRoleUsuario(role);
        usuario.setSenhaUsuario(encoder.encode(senha));
        usuarios.save(usuario);
    }
}
