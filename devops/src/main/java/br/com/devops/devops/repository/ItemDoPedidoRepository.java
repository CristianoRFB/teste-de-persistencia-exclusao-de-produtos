package br.com.devops.devops.repository;

import br.com.devops.devops.entity.ItemDoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemDoPedidoRepository extends JpaRepository<ItemDoPedido, Integer> {
    List<ItemDoPedido> findByPedidoIdPedidoOrderByIdItemDoPedido(Integer pedidoId);
}
