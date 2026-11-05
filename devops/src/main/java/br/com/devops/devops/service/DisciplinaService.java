package br.com.devops.devops.service;

import br.com.devops.devops.entity.Disciplina;
import br.com.devops.devops.repository.DisciplinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public List<Disciplina> listarTodas() {
        return disciplinaRepository.findAll();
    }

    public Optional<Disciplina> buscarPorId(Integer id) {
        return disciplinaRepository.findById(id);
    }

    public Disciplina salvar(Disciplina disciplina) {
        return disciplinaRepository.save(disciplina);
    }

    public void deletar(Integer id) {
        disciplinaRepository.deleteById(id);
    }

    public Disciplina atualizar(Integer id, Disciplina disciplinaAtualizada) {
        return disciplinaRepository.findById(id)
                .map(disciplina -> {
                    disciplina.setNomeDisciplina(disciplinaAtualizada.getNomeDisciplina());
                    disciplina.setSiglaDisciplina(disciplinaAtualizada.getSiglaDisciplina());
                    disciplina.setCargaHorariaDisciplina(disciplinaAtualizada.getCargaHorariaDisciplina());
                    disciplina.setProfessor(disciplinaAtualizada.getProfessor());
                    disciplina.setIdCurso(disciplinaAtualizada.getIdCurso());
                    return disciplinaRepository.save(disciplina);
                })
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));
    }
}
