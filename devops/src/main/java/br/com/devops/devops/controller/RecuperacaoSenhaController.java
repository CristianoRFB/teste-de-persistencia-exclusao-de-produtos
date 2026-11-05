package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Usuario;
import br.com.devops.devops.service.RecuperacaoSenhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class RecuperacaoSenhaController {

    @Autowired
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/recuperar-senha")
    public String paginaRecuperacao() {
        return "recuperar-senha";
    }

    @PostMapping("/recuperar-senha")
    public String solicitarRecuperacao(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            recuperacaoSenhaService.solicitarRecuperacaoSenha(email);
            redirectAttributes.addFlashAttribute(
                    "mensagemSucesso",
                    "Se o email existir em nosso sistema, voce recebera um link para redefinir sua senha.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    "Erro ao processar solicitacao. Tente novamente.");
        }
        return "redirect:/recuperar-senha";
    }

    @GetMapping("/redefinir-senha")
    public String paginaRedefinir(@RequestParam String token, Model model) {
        Optional<Usuario> usuarioOpt = recuperacaoSenhaService.validarToken(token);

        if (usuarioOpt.isPresent()) {
            model.addAttribute("token", token);
            return "redefinir-senha";
        }

        model.addAttribute("mensagemErro", "Link invalido ou expirado.");
        return "recuperar-senha";
    }

    @PostMapping("/redefinir-senha")
    public String redefinirSenha(
            @RequestParam String token,
            @RequestParam String novaSenha,
            @RequestParam String confirmaSenha,
            RedirectAttributes redirectAttributes) {
        try {
            if (!novaSenha.equals(confirmaSenha)) {
                redirectAttributes.addFlashAttribute("mensagemErro", "As senhas nao correspondem.");
                return "redirect:/redefinir-senha?token=" + token;
            }

            if (novaSenha.length() < 8) {
                redirectAttributes.addFlashAttribute("mensagemErro", "A senha deve ter pelo menos 8 caracteres.");
                return "redirect:/redefinir-senha?token=" + token;
            }

            String senhaEncriptada = passwordEncoder.encode(novaSenha);
            recuperacaoSenhaService.redefinirSenha(token, senhaEncriptada);
            redirectAttributes.addFlashAttribute(
                    "mensagemSucesso",
                    "Senha redefinida com sucesso! Faca login com sua nova senha.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao redefinir senha. Link pode estar expirado.");
            return "redirect:/recuperar-senha";
        }
    }
}
