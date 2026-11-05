package br.com.devops.devops.service;

import br.com.devops.devops.entity.Professor;
import br.com.devops.devops.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    public List<Professor> listarTodos() {
        return professorRepository.findAll();
    }

    public Optional<Professor> buscarPorId(Integer id) {
        return professorRepository.findById(id);
    }

    public Professor salvar(Professor professor) {
        return professorRepository.save(professor);
    }

    public void deletar(Integer id) {
        professorRepository.deleteById(id);
    }

    public Professor atualizar(Integer id, Professor professorAtualizado) {
        return professorRepository.findById(id)
                .map(professor -> {
                    professor.setNomeProfessor(professorAtualizado.getNomeProfessor());
                    professor.setTelefoneProfessor(professorAtualizado.getTelefoneProfessor());
                    professor.setGraduacaoProfessor(professorAtualizado.getGraduacaoProfessor());
                    professor.setRmProfessor(professorAtualizado.getRmProfessor());
                    return professorRepository.save(professor);
                })
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }
}
