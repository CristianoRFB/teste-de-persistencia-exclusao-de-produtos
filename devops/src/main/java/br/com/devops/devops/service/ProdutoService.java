package br.com.devops.devops.service;

import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findByAtivoTrueOrderByIdProdutoAsc();
    }

    public List<Produto> listarArquivados() {
        return produtoRepository.findByAtivoFalseOrderByIdProdutoDesc();
    }

    public Optional<Produto> buscarPorId(Integer id) {
        return produtoRepository.findByIdProdutoAndAtivoTrue(id);
    }

    public Produto salvar(Produto produto) {
        produto.setAtivo(Boolean.TRUE);
        return produtoRepository.save(produto);
    }

    /**
     * Exclusão lógica (Tema 11): a linha permanece no banco para preservar
     * a integridade dos itens de pedidos já finalizados.
     */
    @Transactional
    public void deletar(Integer id) {
        Produto produto = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado ou já arquivado."));
        produto.setAtivo(Boolean.FALSE);
        produtoRepository.save(produto);
    }

    @Transactional
    public void reativar(Integer id) {
        Produto produto = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        if (Boolean.TRUE.equals(produto.getAtivo()))
            throw new IllegalArgumentException("Este produto já está ativo.");
        produto.setAtivo(Boolean.TRUE);
        produtoRepository.save(produto);
    }

    public Produto atualizar(Integer id, Produto dados) {
        return produtoRepository.save(atualizarDados(id, dados));
    }

    public Produto atualizar(Integer id, Produto dados, byte[] imagem, String imagemTipo) {
        Produto produto = atualizarDados(id, dados);
        if (imagem != null && imagem.length > 0) {
            produto.setImagem(imagem);
            produto.setImagemTipo(imagemTipo);
        }
        return produtoRepository.save(produto);
    }

    private Produto atualizarDados(Integer id, Produto dados) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        produto.setNomeProduto(dados.getNomeProduto());
        produto.setDescricaoProduto(dados.getDescricaoProduto());
        produto.setPrecoProduto(dados.getPrecoProduto());
        produto.setQuantidadeEstoque(dados.getQuantidadeEstoque());
        return produto;
    }
}
