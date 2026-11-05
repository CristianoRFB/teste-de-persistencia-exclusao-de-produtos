package br.com.devops.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String remetente;

    @Value("${app.base-url:http://localhost:${server.port:8080}}")
    private String baseUrl;

    public void enviarEmailRecuperacaoSenha(String email, String token, String nomeUsuario) {
        String linkReset = baseUrl + "/redefinir-senha?token=" + token;

        if (remetente.isBlank()) {
            System.err.println("Email nao configurado. Defina MAIL_USERNAME e MAIL_PASSWORD antes de iniciar a aplicacao.");
            // Recovery tokens must never be logged.
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Recuperacao de Senha - Sistema DevOps");
            if (!remetente.isBlank()) {
                message.setFrom(remetente);
            }

            String corpo = "Ola " + nomeUsuario + ",\n\n"
                    + "Voce solicitou uma recuperacao de senha. Clique no link abaixo para redefinir sua senha:\n\n"
                    + linkReset + "\n\n"
                    + "Este link expira em 24 horas.\n\n"
                    + "Se voce nao solicitou isto, ignore este email.\n\n"
                    + "Atenciosamente,\nSistema DevOps";

            message.setText(corpo);
            mailSender.send(message);
            // Envio concluído.
        } catch (Exception e) {
            System.err.println("Nao foi possivel enviar email de recuperacao.");
            // Recovery tokens must never be logged.
        }
    }
}
