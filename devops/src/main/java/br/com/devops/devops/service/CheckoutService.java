package br.com.devops.devops.service;

import br.com.devops.devops.entity.ItemDoPedido;
import br.com.devops.devops.entity.Pedido;
import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.entity.Usuario;
import br.com.devops.devops.repository.PedidoRepository;
import br.com.devops.devops.repository.ProdutoRepository;
import br.com.devops.devops.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CheckoutService {
    private final PedidoRepository pedidos;
    private final ProdutoRepository produtos;
    private final UsuarioRepository usuarios;

    public CheckoutService(PedidoRepository pedidos, ProdutoRepository produtos, UsuarioRepository usuarios) {
        this.pedidos = pedidos;
        this.produtos = produtos;
        this.usuarios = usuarios;
    }

    @Transactional
    public Pedido finalizar(String login, Map<Integer, Integer> carrinho, String token) {
        if (token == null || token.isBlank()) throw new IllegalArgumentException("Sessão de compra inválida.");
        try { UUID.fromString(token); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("Identificador de compra inválido."); }

        var anterior = pedidos.findByCheckoutToken(token);
        if (anterior.isPresent()) {
            Pedido registrado = anterior.get();
            if (registrado.getCliente() == null || !login.equals(registrado.getCliente().getLoginUsuario()))
                throw new IllegalArgumentException("Compra não autorizada.");
            return registrado;
        }
        if (carrinho == null || carrinho.isEmpty()) throw new IllegalArgumentException("Carrinho vazio.");
        Usuario cliente = usuarios.findByLoginUsuario(login)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        List<Map.Entry<Integer, Integer>> itens = carrinho.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey)).toList();

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setNomeCliente(cliente.getNomeUsuario());
        pedido.setDataPedido(LocalDate.now());
        pedido.setStatus("PENDENTE");
        pedido.setCheckoutToken(token);
        BigDecimal total = BigDecimal.ZERO;

        for (var entrada : itens) {
            Integer quantidade = entrada.getValue();
            if (quantidade == null || quantidade < 1 || quantidade > 1000)
                throw new IllegalArgumentException("Quantidade inválida no carrinho.");
            Produto produto = produtos.buscarParaCompra(entrada.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não disponível."));
            if (produto.getPrecoProduto() == null || produto.getPrecoProduto().signum() <= 0)
                throw new IllegalArgumentException("Produto sem preço válido.");
            if (produto.getQuantidadeEstoque() == null || produto.getQuantidadeEstoque() < quantidade)
                throw new IllegalArgumentException("Estoque insuficiente para " + produto.getNomeProduto() + ".");
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);

            ItemDoPedido item = new ItemDoPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPrecoProduto());
            item.setSubtotal(produto.getPrecoProduto().multiply(BigDecimal.valueOf(quantidade)));
            pedido.getItens().add(item);
            total = total.add(item.getSubtotal());
        }
        pedido.setValorTotal(total);
        return pedidos.saveAndFlush(pedido);
    }
}
