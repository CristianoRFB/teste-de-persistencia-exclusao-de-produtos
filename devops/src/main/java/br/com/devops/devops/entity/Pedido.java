package br.com.devops.devops.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;

    @Column(nullable = false, length = 100)
    private String nomeCliente;

    @Column(nullable = false)
    private LocalDate dataPedido;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

@ManyToOne
    @JoinColumn(name = "idALuno")
    private Aluno aluno;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDoPedido> itens = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_cliente")
    private Usuario cliente;

    @Column(name = "checkout_token", unique = true, length = 36)
    private String checkoutToken;
}
