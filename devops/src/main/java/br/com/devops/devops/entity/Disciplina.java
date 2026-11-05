package br.com.devops.devops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "disciplina")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disciplina")
    private Integer idDisciplina;

    @Column(name = "nome_disciplina", nullable = false, length = 100)
    private String nomeDisciplina;

    @Column(name = "sigla_disciplina", nullable = false, length = 10)
    private String siglaDisciplina;

    @Column(name = "carga_horaria_disciplina", nullable = false)
    private Integer cargaHorariaDisciplina;

    @ManyToOne
    @JoinColumn(name = "id_professor", nullable = false, foreignKey = @ForeignKey(name = "fk_disciplina_professor"))
    private Professor professor;

    @Column(name = "id_curso", nullable = false)
    private Integer idCurso;
}
