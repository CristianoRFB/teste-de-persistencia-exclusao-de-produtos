package br.com.devops.devops.repository;

import br.com.devops.devops.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    @Override
    @EntityGraph(attributePaths = {"aluno", "cliente", "itens", "itens.produto"})
    List<Pedido> findAll();

    @Override
    @EntityGraph(attributePaths = {"aluno", "cliente", "itens", "itens.produto"})
    Optional<Pedido> findById(Integer id);
    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto"})
    List<Pedido> findByClienteLoginUsuarioOrderByIdPedidoDesc(String loginUsuario);

    Optional<Pedido> findByCheckoutToken(String checkoutToken);
}
