package br.com.devops.devops.service;

import br.com.devops.devops.entity.*;
import br.com.devops.devops.repository.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutServiceTest {
    private final PedidoRepository pedidos = mock(PedidoRepository.class);
    private final ProdutoRepository produtos = mock(ProdutoRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final CheckoutService checkout = new CheckoutService(pedidos, produtos, usuarios);

    private void usuario() {
        Usuario u = new Usuario();
        u.setLoginUsuario("cliente");
        u.setNomeUsuario("Cliente de Teste");
        when(usuarios.findByLoginUsuario("cliente")).thenReturn(Optional.of(u));
    }

    private Produto produto(int estoque) {
        Produto p = new Produto();
        p.setIdProduto(9);
        p.setNomeProduto("Camiseta");
        p.setPrecoProduto(new BigDecimal("19.99"));
        p.setQuantidadeEstoque(estoque);
        when(produtos.buscarParaCompra(9)).thenReturn(Optional.of(p));
        return p;
    }

    @Test
    void totalCalculadoNoServidorEEstoqueDescontado() {
        usuario();
        Produto p = produto(5);
        when(pedidos.saveAndFlush(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));
        Pedido pedido = checkout.finalizar("cliente", Map.of(9, 2), UUID.randomUUID().toString());
        assertEquals(new BigDecimal("39.98"), pedido.getValorTotal());
        assertEquals(3, p.getQuantidadeEstoque());
        assertEquals(1, pedido.getItens().size());
        assertEquals("PENDENTE", pedido.getStatus());
        verify(pedidos).saveAndFlush(pedido);
    }

    @Test
    void impedeEstoqueInsuficienteAntesDeSalvar() {
        usuario();
        Produto p = produto(1);
        assertThrows(IllegalArgumentException.class, () ->
                checkout.finalizar("cliente", Map.of(9, 2), UUID.randomUUID().toString()));
        assertEquals(1, p.getQuantidadeEstoque());
        verify(pedidos, never()).saveAndFlush(any());
    }

    @Test
    void impedeQuantidadeInvalida() {
        usuario();
        assertThrows(IllegalArgumentException.class, () ->
                checkout.finalizar("cliente", Map.of(9, -1), UUID.randomUUID().toString()));
        verify(produtos, never()).buscarParaCompra(anyInt());
        verify(pedidos, never()).saveAndFlush(any());
    }

    @Test
    void reenvioDoMesmoTokenNaoCriaPedidoDuplicado() {
        String idempotencia = UUID.randomUUID().toString();
        Usuario u = new Usuario();
        u.setLoginUsuario("cliente");
        Pedido anterior = new Pedido();
        anterior.setIdPedido(42);
        anterior.setCliente(u);
        when(pedidos.findByCheckoutToken(idempotencia)).thenReturn(Optional.of(anterior));
        Pedido resultado = checkout.finalizar("cliente", Map.of(9, 2), idempotencia);
        assertSame(anterior, resultado);
        verify(pedidos, never()).saveAndFlush(any());
    }

    @Test
    void impedeConsultarTokenDeOutroUsuario() {
        String idempotencia = UUID.randomUUID().toString();
        Usuario outro = new Usuario();
        outro.setLoginUsuario("outra-conta");
        Pedido anterior = new Pedido();
        anterior.setCliente(outro);
        when(pedidos.findByCheckoutToken(idempotencia)).thenReturn(Optional.of(anterior));
        assertThrows(IllegalArgumentException.class, () ->
                checkout.finalizar("cliente", Map.of(9, 1), idempotencia));
    }
}
