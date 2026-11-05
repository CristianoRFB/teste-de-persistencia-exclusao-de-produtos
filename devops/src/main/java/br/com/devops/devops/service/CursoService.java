package br.com.devops.devops.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.devops.devops.repository.CursoRepository;
import br.com.devops.devops.entity.Curso;
import java.util.List;
import java.util.Optional;

@Service
public class CursoService {

    @Autowired 
    private CursoRepository cursoRepository;

    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    public Optional<Curso> buscarPorId(Integer id) {
        return cursoRepository.findById(id);
    }

    public Curso salvar(Curso curso) {
        return cursoRepository.save(curso);
    }

    public void deletar(Integer id) {
        cursoRepository.deleteById(id);
    }

    public Curso atualizar(Integer id, Curso cursoAtualizado) {
        return cursoRepository.findById(id)
                .map(curso -> {
                    curso.setNomeCurso(cursoAtualizado.getNomeCurso());
                    curso.setPeriodoCurso(cursoAtualizado.getPeriodoCurso());
                    curso.setCargahorariaCurso(cursoAtualizado.getCargahorariaCurso());
                    return cursoRepository.save(curso);
                })
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
    }
}
