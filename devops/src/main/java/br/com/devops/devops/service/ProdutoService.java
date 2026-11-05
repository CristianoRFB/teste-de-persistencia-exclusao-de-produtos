package br.com.devops.devops.service;

import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() { return produtoRepository.findAll(); }
    public Optional<Produto> buscarPorId(Integer id) { return produtoRepository.findById(id); }
    public Produto salvar(Produto produto) { return produtoRepository.save(produto); }
    public void deletar(Integer id) { produtoRepository.deleteById(id); }

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
