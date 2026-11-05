package br.com.devops.devops.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.devops.devops.entity.Usuario;
import br.com.devops.devops.service.UsuarioService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private static final String[] ROLES = { "ADMIN", "USER" };

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuario/listarUsuarios";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", ROLES);
        return "usuario/formularioUsuario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Usuario usuario, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        if (usuario.getIdUsuario() == null
                && (usuario.getSenhaUsuario() == null || usuario.getSenhaUsuario().isBlank())) {
            bindingResult.rejectValue("senhaUsuario", "senhaUsuario.required", "A senha é obrigatória.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", ROLES);
            return "usuario/formularioUsuario";
        }

        usuarioService.salvar(usuario);
        redirectAttributes.addFlashAttribute("mensagem", "Usuário salvo com sucesso!");
        return "redirect:/usuario/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        if (usuario.isPresent()) {
            Usuario usuarioEditado = usuario.get();
            usuarioEditado.setSenhaUsuario(null);
            model.addAttribute("usuario", usuarioEditado);
            model.addAttribute("roles", ROLES);
            return "usuario/formularioUsuario";
        }
        return "redirect:/usuario/listar";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        usuarioService.deletar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Usuário deletado com sucesso!");
        return "redirect:/usuario/listar";
    }
}
