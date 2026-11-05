package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Curso;
import br.com.devops.devops.service.CursoService;
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
@RequestMapping("/curso")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("cursos", cursoService.listarTodos());
        return "curso/listarCursos";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("curso", new Curso());
        return "curso/formularioCurso";
    }

    @PostMapping("/salvar")
    public String salvar(Curso curso, RedirectAttributes redirectAttributes) {
        cursoService.salvar(curso);
        redirectAttributes.addFlashAttribute("mensagem", "Curso salvo com sucesso!");
        return "redirect:/curso/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Optional<Curso> curso = cursoService.buscarPorId(id);
        if (curso.isPresent()) {
            model.addAttribute("curso", curso.get());
            return "curso/formularioCurso";
        }
        return "redirect:/curso/listar";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, Curso curso, RedirectAttributes redirectAttributes) {
        cursoService.atualizar(id, curso);
        redirectAttributes.addFlashAttribute("mensagem", "Curso atualizado com sucesso!");
        return "redirect:/curso/listar";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        cursoService.deletar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Curso deletado com sucesso!");
        return "redirect:/curso/listar";
    }
}
