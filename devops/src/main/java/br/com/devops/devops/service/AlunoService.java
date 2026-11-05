package br.com.devops.devops.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.devops.devops.entity.Aluno;
import br.com.devops.devops.repository.AlunoRepository;

@Service
public class AlunoService {

    // Injeção de dependência do repositório de alunos
    @Autowired
    private AlunoRepository alunoRepository;

    //Metodos para salvar aluno
    public Aluno saveAluno(Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    //Metodo para buscar aluno por id
    public Aluno getAlunoById(Integer id) {
        return alunoRepository.findById(id).orElse(null);
    }

    //Metodo para buscar todos os alunos
    public java.util.List<Aluno> getAllAlunos() {
        return alunoRepository.findAll();
    }
    //Metodo para atualizar aluno
    public Aluno updateAluno(Aluno aluno) {
        return alunoRepository.save(aluno);
    }
    //Metodo para deletar aluno
    public void deleteAluno(Integer id) {
        alunoRepository.deleteById(id);
    }
    
}
