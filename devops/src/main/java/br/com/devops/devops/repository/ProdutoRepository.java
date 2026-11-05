package br.com.devops.devops.repository;

import br.com.devops.devops.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    java.util.List<Produto> findByAtivoTrueOrderByIdProdutoAsc();
    java.util.List<Produto> findByAtivoFalseOrderByIdProdutoDesc();
    java.util.Optional<Produto> findByIdProdutoAndAtivoTrue(Integer id);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from Produto p where p.idProduto = :id and p.ativo = true")
    java.util.Optional<Produto> buscarParaCompra(@org.springframework.data.repository.query.Param("id") Integer id);
}
