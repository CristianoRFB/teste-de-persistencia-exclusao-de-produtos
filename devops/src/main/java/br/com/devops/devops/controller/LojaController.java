package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.service.CheckoutService;
import br.com.devops.devops.service.PedidoService;
import br.com.devops.devops.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/loja")
public class LojaController {
    private static final String CARRINHO = "carrinhoProdutos";
    private static final String CHECKOUT_TOKEN = "checkoutToken";
    private final ProdutoService produtos;
    private final CheckoutService checkout;
    private final PedidoService pedidos;

    public LojaController(ProdutoService produtos, CheckoutService checkout, PedidoService pedidos) {
        this.produtos = produtos;
        this.checkout = checkout;
        this.pedidos = pedidos;
    }

    @GetMapping
    public String loja(@RequestParam(defaultValue = "") String busca, Model model) {
        String termo = busca.strip().toLowerCase(Locale.ROOT);
        var resultados = produtos.listarTodos().stream().filter(p -> termo.isBlank()
                || p.getNomeProduto().toLowerCase(Locale.ROOT).contains(termo)
                || p.getDescricaoProduto().toLowerCase(Locale.ROOT).contains(termo)).toList();
        model.addAttribute("produtos", resultados);
        model.addAttribute("busca", busca);
        return "loja/index";
    }

    @GetMapping("/produto/{id}")
    public String detalhe(@PathVariable Integer id, Model model) {
        return produtos.buscarPorId(id)
                .map(p -> { model.addAttribute("produto", p); return "loja/detalhe"; })
                .orElse("redirect:/loja");
    }

    @PostMapping("/carrinho/adicionar/{id}")
    public String adicionar(@PathVariable Integer id, @RequestParam(defaultValue = "1") Integer quantidade,
                            HttpSession session, RedirectAttributes flash) {
        if (quantidade == null || quantidade < 1 || quantidade > 1000) {
            flash.addFlashAttribute("erro", "Informe uma quantidade entre 1 e 1000.");
            return "redirect:/loja";
        }
        var encontrado = produtos.buscarPorId(id);
        if (encontrado.isEmpty() || encontrado.get().getQuantidadeEstoque() == null) {
            flash.addFlashAttribute("erro", "Produto indisponível.");
            return "redirect:/loja";
        }
        int atual = carrinho(session).getOrDefault(id, 0);
        if ((long) atual + quantidade > encontrado.get().getQuantidadeEstoque()) {
            flash.addFlashAttribute("erro", "Estoque insuficiente.");
            return "redirect:/loja/carrinho";
        }
        carrinho(session).put(id, atual + quantidade);
        return "redirect:/loja/carrinho";
    }

    @GetMapping("/carrinho")
    public String carrinho(Model model, HttpSession session) {
        var itens = new LinkedHashMap<Produto, Integer>();
        var subtotais = new LinkedHashMap<Integer, BigDecimal>();
        BigDecimal total = BigDecimal.ZERO;
        for (var entrada : carrinho(session).entrySet()) {
            var encontrado = produtos.buscarPorId(entrada.getKey());
            if (encontrado.isEmpty()) continue;
            Produto p = encontrado.get();
            itens.put(p, entrada.getValue());
            BigDecimal subtotal = p.getPrecoProduto().multiply(BigDecimal.valueOf(entrada.getValue()));
            subtotais.put(p.getIdProduto(), subtotal);
            total = total.add(subtotal);
        }
        model.addAttribute("itens", itens);
        model.addAttribute("subtotais", subtotais);
        model.addAttribute("total", total);
        model.addAttribute("checkoutToken", token(session));
        return "loja/carrinho";
    }

    @PostMapping("/carrinho/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @RequestParam Integer quantidade,
                            HttpSession session, RedirectAttributes flash) {
        if (quantidade == null || quantidade < 0 || quantidade > 1000) {
            flash.addFlashAttribute("erro", "Quantidade inválida.");
        } else if (quantidade == 0) {
            carrinho(session).remove(id);
        } else {
            var encontrado = produtos.buscarPorId(id);
            if (encontrado.isEmpty() || encontrado.get().getQuantidadeEstoque() == null
                    || encontrado.get().getQuantidadeEstoque() < quantidade) {
                flash.addFlashAttribute("erro", "Quantidade maior que o estoque disponível.");
            } else {
                carrinho(session).put(id, quantidade);
            }
        }
        return "redirect:/loja/carrinho";
    }

    @PostMapping("/carrinho/remover/{id}")
    public String remover(@PathVariable Integer id, HttpSession session) {
        carrinho(session).remove(id);
        return "redirect:/loja/carrinho";
    }

    @PostMapping("/finalizar")
    public String finalizar(@RequestParam String token, Authentication authentication,
                            HttpSession session, RedirectAttributes flash) {
        if (!token.equals(session.getAttribute(CHECKOUT_TOKEN))) {
            flash.addFlashAttribute("erro", "Compra expirada ou já enviada. Revise seu carrinho.");
            return "redirect:/loja/carrinho";
        }
        try {
            var pedido = checkout.finalizar(authentication.getName(), Map.copyOf(carrinho(session)), token);
            session.removeAttribute(CARRINHO);
            session.removeAttribute(CHECKOUT_TOKEN);
            return "redirect:/loja/pedidos/" + pedido.getIdPedido();
        } catch (IllegalArgumentException ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/loja/carrinho";
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            flash.addFlashAttribute("erro", "Compra não confirmada. Consulte seus pedidos.");
            return "redirect:/loja/pedidos";
        }
    }

    @GetMapping("/pedidos")
    public String meusPedidos(Authentication authentication, Model model) {
        model.addAttribute("pedidos", pedidos.listarDoCliente(authentication.getName()));
        return "loja/pedidos";
    }

    @GetMapping("/pedidos/{id}")
    public String detalhePedido(@PathVariable Integer id, Authentication authentication, Model model) {
        var pedido = pedidos.buscarDoCliente(id, authentication.getName());
        if (pedido.isEmpty()) return "redirect:/loja/pedidos";
        model.addAttribute("pedido", pedido.get());
        return "loja/confirmacao";
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> carrinho(HttpSession session) {
        var cart = (Map<Integer, Integer>) session.getAttribute(CARRINHO);
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute(CARRINHO, cart);
        }
        return cart;
    }

    private String token(HttpSession session) {
        String valor = (String) session.getAttribute(CHECKOUT_TOKEN);
        if (valor == null) {
            valor = UUID.randomUUID().toString();
            session.setAttribute(CHECKOUT_TOKEN, valor);
        }
        return valor;
    }
}
