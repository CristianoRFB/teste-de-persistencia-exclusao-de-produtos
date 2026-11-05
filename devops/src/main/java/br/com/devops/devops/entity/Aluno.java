package br.com.devops.devops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "aluno")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aluno")
    private Integer idAluno;

    @Column(name = "nome_aluno", nullable = false, length = 40)
    private String nomeAluno;
    
    @Column(name = "email_aluno", length = 100)
    private String emailAluno;
    
    @Column(name = "telefone_aluno", nullable = false, length = 15)
    private String telefoneAluno;

    @Column(name = "endereco_aluno", nullable = false, length = 50)
    private String enderecoAluno;

    @Column(name = "cpf_aluno", nullable = false, length = 14)
    private String cpfAluno;

    @Column(name = "ra_aluno", nullable = false, length = 20)
    private String raAluno;

    @ManyToOne
    @JoinColumn(name = "id_curso_fk")
    private Curso curso;
}