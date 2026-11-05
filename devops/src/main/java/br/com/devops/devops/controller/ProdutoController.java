package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produto")
public class ProdutoController {

    private static final long TAMANHO_MAXIMO_IMAGEM = 5 * 1024 * 1024;
    private static final Set<String> TIPOS_DE_IMAGEM = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        return "produto/listarProdutos";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto/formularioProduto";
    }

    //Salva o cadastro do produto
    @PostMapping("/salvar")
    public String salvar(@Valid Produto produto, BindingResult bindingResult,
                         @RequestParam(name = "arquivoImagem", required = false) MultipartFile arquivoImagem,
                         Model model, RedirectAttributes redirectAttributes) throws IOException {
        String erroImagem = validarImagem(arquivoImagem);
        if (bindingResult.hasErrors() || erroImagem != null) {
            model.addAttribute("erroImagem", erroImagem);
            return "produto/formularioProduto";
        }
        aplicarImagem(produto, arquivoImagem);
        produtoService.salvar(produto);
        redirectAttributes.addFlashAttribute("mensagem", "Produto salvo com sucesso!");
        return "redirect:/produto/listar";
    }
    
    //Busca um produto para ser editado por ID
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return produtoService.buscarPorId(id).map(produto -> {
            model.addAttribute("produto", produto);
            return "produto/formularioProduto";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
            return "redirect:/produto/listar";
        });
    }
    //Atualiza um produto por ID
    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @Valid Produto produto, BindingResult bindingResult,
                            @RequestParam(name = "arquivoImagem", required = false) MultipartFile arquivoImagem,
                            Model model, RedirectAttributes redirectAttributes) throws IOException {
        produto.setIdProduto(id);
        String erroImagem = validarImagem(arquivoImagem);
        if (bindingResult.hasErrors() || erroImagem != null) {
            model.addAttribute("erroImagem", erroImagem);
            return "produto/formularioProduto";
        }
        produtoService.atualizar(id, produto,
                arquivoImagem == null || arquivoImagem.isEmpty() ? null : arquivoImagem.getBytes(),
                arquivoImagem == null ? null : arquivoImagem.getContentType());
        redirectAttributes.addFlashAttribute("mensagem", "Produto atualizado com sucesso!");
        return "redirect:/produto/listar";
    }

    //Metodo para excluir um produto por ID
    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        produtoService.deletar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Produto deletado com sucesso!");
        return "redirect:/produto/listar";
    }

    @GetMapping("/{id}/imagem")
    public ResponseEntity<byte[]> imagem(@PathVariable Integer id) {
        return produtoService.buscarPorId(id)
                .filter(produto -> produto.getImagem() != null && produto.getImagem().length > 0)
                .map(produto -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(produto.getImagemTipo()))
                        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePrivate())
                        .body(produto.getImagem()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private String validarImagem(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) return null;
        if (arquivo.getSize() > TAMANHO_MAXIMO_IMAGEM) return "A imagem deve ter no máximo 5 MB.";
        if (!TIPOS_DE_IMAGEM.contains(arquivo.getContentType())) return "Use uma imagem JPG, PNG, WebP ou GIF.";
        return null;
    }

    private void aplicarImagem(Produto produto, MultipartFile arquivo) throws IOException {
        if (arquivo != null && !arquivo.isEmpty()) {
            produto.setImagem(arquivo.getBytes());
            produto.setImagemTipo(arquivo.getContentType());
        }
    }
}
