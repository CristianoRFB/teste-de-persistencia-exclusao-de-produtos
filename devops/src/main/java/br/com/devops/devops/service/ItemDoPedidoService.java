package br.com.devops.devops.service;

import br.com.devops.devops.entity.*;
import br.com.devops.devops.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemDoPedidoService {
    private final ItemDoPedidoRepository itemRepository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public ItemDoPedidoService(ItemDoPedidoRepository itemRepository, PedidoRepository pedidoRepository,
                               ProdutoRepository produtoRepository) {
        this.itemRepository = itemRepository;
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<ItemDoPedido> listarPorPedido(Integer pedidoId) {
        return itemRepository.findByPedidoIdPedidoOrderByIdItemDoPedido(pedidoId);
    }

    public ItemDoPedido buscarPorId(Integer id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));
    }

    @Transactional
    public ItemDoPedido salvar(Integer pedidoId, Integer produtoId, Integer quantidade) {
        ItemDoPedido item = new ItemDoPedido();
        preparar(item, pedidoId, produtoId, quantidade);
        ItemDoPedido salvo = itemRepository.save(item);
        recalcularTotal(item.getPedido());
        return salvo;
    }

    @Transactional
    public ItemDoPedido atualizar(Integer id, Integer produtoId, Integer quantidade) {
        ItemDoPedido item = buscarPorId(id);
        Integer pedidoId = item.getPedido().getIdPedido();
        preparar(item, pedidoId, produtoId, quantidade);
        ItemDoPedido salvo = itemRepository.save(item);
        recalcularTotal(item.getPedido());
        return salvo;
    }

    @Transactional
    public void deletar(Integer id) {
        ItemDoPedido item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));
        Pedido pedido = item.getPedido();
        itemRepository.delete(item);
        itemRepository.flush();
        recalcularTotal(pedido);
    }

    private void preparar(ItemDoPedido item, Integer pedidoId, Integer produtoId, Integer quantidade) {
        if (quantidade == null || quantidade < 1)
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPrecoProduto());
        item.setSubtotal(produto.getPrecoProduto().multiply(BigDecimal.valueOf(quantidade)));
    }

    private void recalcularTotal(Pedido pedido) {
        BigDecimal total = itemRepository.findByPedidoIdPedidoOrderByIdItemDoPedido(pedido.getIdPedido()).stream()
                .map(ItemDoPedido::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setValorTotal(total);
        pedidoRepository.save(pedido);
    }
}
