package br.com.devops.devops.service;

import br.com.devops.devops.entity.ItemDoPedido;
import br.com.devops.devops.entity.Pedido;
import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.repository.PedidoRepository;
import br.com.devops.devops.repository.ProdutoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tema 11 — Teste de persistência da exclusão de produtos.
 * A referência de itens de pedidos obriga preservar o produto no banco.
 * O teste reproduz o problema no código original e valida a correção.
 */
@SpringBootTest
@Transactional
class ProdutoExclusaoPersistenciaTest {
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private ProdutoService produtoService;
    @Autowired private EntityManager entityManager;

    private Produto novoProduto(String nome) {
        Produto produto = new Produto();
        produto.setNomeProduto(nome);
        produto.setDescricaoProduto("Produto criado exclusivamente pelo teste de persistência");
        produto.setPrecoProduto(new BigDecimal("39.90"));
        produto.setQuantidadeEstoque(6);
        return produtoRepository.saveAndFlush(produto);
    }

    private Pedido pedidoDe(Produto produto) {
        Pedido pedido = new Pedido();
        pedido.setNomeCliente("Cliente fictício da atividade");
        pedido.setDataPedido(LocalDate.of(2026, 11, 5));
        pedido.setStatus("PENDENTE");
        pedido.setValorTotal(new BigDecimal("39.90"));

        ItemDoPedido item = new ItemDoPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(1);
        item.setPrecoUnitario(new BigDecimal("39.90"));
        item.setSubtotal(new BigDecimal("39.90"));
        pedido.getItens().add(item);
        return pedidoRepository.saveAndFlush(pedido);
    }

    @Test
    void exclusaoDeProdutoComPedidoDevePreservarHistoricoSemErroDeChaveEstrangeira() {
        Produto produto = novoProduto("Camiseta vendida");
        Pedido pedido = pedidoDe(produto);
        Integer idProduto = produto.getIdProduto();
        Integer idPedido = pedido.getIdPedido();
        entityManager.flush();
        entityManager.clear();

        // Antes da correção, deleteById provoca violação de FK ao sincronizar.
        assertDoesNotThrow(() -> {
            produtoService.deletar(idProduto);
            entityManager.flush();
        });
        entityManager.clear();

        assertTrue(produtoRepository.existsById(idProduto), "O histórico da venda precisa do registro original.");
        assertTrue(produtoService.buscarPorId(idProduto).isEmpty(), "Produto excluído não deve aparecer no catálogo.");
        Pedido registrado = pedidoRepository.findById(idPedido).orElseThrow();
        assertEquals(1, registrado.getItens().size());
        assertEquals(idProduto, registrado.getItens().get(0).getProduto().getIdProduto());
    }

    @Test
    void exclusaoInexistenteDeveRetornarErroClaro() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class, () -> produtoService.deletar(Integer.MAX_VALUE));
        assertTrue(erro.getMessage().contains("não encontrado"));
    }

    @Test
    void exclusaoDeProdutoSemPedidoDevePersistirComoInativo() {
        Produto produto = novoProduto("Caneca sem pedidos");
        Integer id = produto.getIdProduto();
        entityManager.flush();
        entityManager.clear();

        produtoService.deletar(id);
        entityManager.flush();
        entityManager.clear();

        assertTrue(produtoRepository.existsById(id), "A exclusão lógica mantém o registro no banco.");
        assertTrue(produtoService.buscarPorId(id).isEmpty(), "Produto excluído não pode ser consultado como ativo.");
        assertTrue(produtoService.listarTodos().stream().noneMatch(p -> p.getIdProduto().equals(id)));
    }
}
