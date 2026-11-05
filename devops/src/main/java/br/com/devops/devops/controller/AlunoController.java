package br.com.devops.devops.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.devops.devops.entity.Aluno;
import br.com.devops.devops.service.AlunoService;
import br.com.devops.devops.service.CursoService;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;
    
    @Autowired
    private CursoService cursoService;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("alunos", alunoService.getAllAlunos());
        return "aluno/listarAlunos";
    }

    @GetMapping("/formulario")
    public String novoAluno(Model model) {
        model.addAttribute("aluno", new Aluno());
        model.addAttribute("cursos", cursoService.listarTodos());
        return "aluno/formularioAluno";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Aluno aluno) {
        alunoService.saveAluno(aluno);
        return "redirect:/aluno/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Aluno aluno = alunoService.getAlunoById(id);

        model.addAttribute("aluno", aluno);
        model.addAttribute("cursos", cursoService.listarTodos());

        return "aluno/formularioAluno";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id) {
        alunoService.deleteAluno(id);
        return "redirect:/aluno/listar";
    }
}