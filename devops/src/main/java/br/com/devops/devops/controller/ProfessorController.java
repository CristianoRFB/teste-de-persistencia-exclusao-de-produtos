package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Professor;
import br.com.devops.devops.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("professores", professorService.listarTodos());
        return "professor/listarProfessores";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("professor", new Professor());
        return "professor/formularioProfessor";
    }

    @PostMapping("/salvar")
    public String salvar(Professor professor, RedirectAttributes redirectAttributes) {
        professorService.salvar(professor);
        redirectAttributes.addFlashAttribute("mensagem", "Professor salvo com sucesso!");
        return "redirect:/professor/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Optional<Professor> professor = professorService.buscarPorId(id);
        if (professor.isPresent()) {
            model.addAttribute("professor", professor.get());
            return "professor/formularioProfessor";
        }
        return "redirect:/professor/listar";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, Professor professor, RedirectAttributes redirectAttributes) {
        professorService.atualizar(id, professor);
        redirectAttributes.addFlashAttribute("mensagem", "Professor atualizado com sucesso!");
        return "redirect:/professor/listar";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        professorService.deletar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Professor deletado com sucesso!");
        return "redirect:/professor/listar";
    }
}
