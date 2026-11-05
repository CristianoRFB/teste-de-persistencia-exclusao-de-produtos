package br.com.devops.devops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "produto")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProduto;

    @NotBlank(message = "Informe o nome do produto")
    @Column(nullable = false, length = 100)
    private String nomeProduto;

    @NotBlank(message = "Informe a descrição do produto")
    @Column(nullable = false, length = 255)
    private String descricaoProduto;

    @NotNull(message = "Informe o preço do produto")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precoProduto;

    @NotNull(message = "Informe a quantidade em estoque")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "imagem", columnDefinition = "bytea")
    private byte[] imagem;

    @Column(name = "imagem_tipo", length = 100)
    private String imagemTipo;
}
