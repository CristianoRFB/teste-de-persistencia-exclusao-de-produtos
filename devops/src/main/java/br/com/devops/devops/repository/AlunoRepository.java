package br.com.devops.devops.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.devops.devops.entity.Aluno;

public interface AlunoRepository extends JpaRepository <Aluno, Integer> {
     
}
