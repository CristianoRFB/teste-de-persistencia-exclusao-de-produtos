package br.com.devops.devops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "professor")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_professor")
    private Integer idProfessor;

    @Column(name = "nome_professor", nullable = false, length = 100)
    private String nomeProfessor;

    @Column(name = "telefone_professor", nullable = false, length = 15)
    private String telefoneProfessor;

    @Column(name = "graduacao_professor", nullable = false, length = 100)
    private String graduacaoProfessor;

    @Column(name = "rm_professor", nullable = false, length = 20)
    private String rmProfessor;
}
