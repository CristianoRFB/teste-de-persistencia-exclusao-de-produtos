package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Disciplina;
import br.com.devops.devops.service.CursoService;
import br.com.devops.devops.service.DisciplinaService;
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
@RequestMapping("/disciplina")
public class DisciplinaController {

    @Autowired
    private DisciplinaService disciplinaService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private CursoService cursoService;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("disciplinas", disciplinaService.listarTodas());
        return "disciplina/listarDisciplinas";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("disciplina", new Disciplina());
        adicionarOpcoesFormulario(model);
        return "disciplina/formularioDisciplina";
    }

    @PostMapping("/salvar")
    public String salvar(Disciplina disciplina, RedirectAttributes redirectAttributes) {
        disciplinaService.salvar(disciplina);
        redirectAttributes.addFlashAttribute("mensagem", "Disciplina salva com sucesso!");
        return "redirect:/disciplina/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Optional<Disciplina> disciplina = disciplinaService.buscarPorId(id);
        if (disciplina.isPresent()) {
            model.addAttribute("disciplina", disciplina.get());
            adicionarOpcoesFormulario(model);
            return "disciplina/formularioDisciplina";
        }
        return "redirect:/disciplina/listar";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, Disciplina disciplina, RedirectAttributes redirectAttributes) {
        disciplinaService.atualizar(id, disciplina);
        redirectAttributes.addFlashAttribute("mensagem", "Disciplina atualizada com sucesso!");
        return "redirect:/disciplina/listar";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        disciplinaService.deletar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Disciplina deletada com sucesso!");
        return "redirect:/disciplina/listar";
    }

    private void adicionarOpcoesFormulario(Model model) {
        model.addAttribute("professores", professorService.listarTodos());
        model.addAttribute("cursos", cursoService.listarTodos());
    }
}
