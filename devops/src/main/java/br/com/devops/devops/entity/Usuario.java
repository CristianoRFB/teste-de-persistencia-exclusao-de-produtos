package br.com.devops.devops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres.")
    @Column(name = "nome_usuario", nullable = false, length = 80)
    private String nomeUsuario;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Informe um email válido.")
    @Size(max = 100, message = "O email deve ter no máximo 100 caracteres.")
    @Column(name = "email_usuario", nullable = false, length = 100)
    private String emailUsuario;

    @NotBlank(message = "O login é obrigatório.")
    @Size(max = 30, message = "O login deve ter no máximo 30 caracteres.")
    @Column(name = "login_usuario", nullable = false, unique = true, length = 30)
    private String loginUsuario;

    @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
    @Column(name = "senha_usuario", nullable = false, length = 100)
    private String senhaUsuario;

    @NotBlank(message = "A role é obrigatória.")
    @Size(max = 20, message = "A role deve ter no máximo 20 caracteres.")
    @Column(name = "role_usuario", nullable = false, length = 20)
    private String roleUsuario;

    @Column(name = "token_reset_senha", length = 255)
    private String tokenResetSenha;

    @Column(name = "data_expiracao_token")
    private LocalDateTime dataExpiracaoToken;
}
