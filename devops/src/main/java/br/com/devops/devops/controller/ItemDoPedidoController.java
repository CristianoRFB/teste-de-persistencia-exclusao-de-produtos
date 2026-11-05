package br.com.devops.devops.controller;

import br.com.devops.devops.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/item-pedido")
public class ItemDoPedidoController {
    private final ItemDoPedidoService itemService;
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;

    public ItemDoPedidoController(ItemDoPedidoService itemService, PedidoService pedidoService,
                                  ProdutoService produtoService) {
        this.itemService = itemService;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
    }

    @GetMapping("/listar/{pedidoId}")
    public String listar(@PathVariable Integer pedidoId, Model model) {
        model.addAttribute("pedido", pedidoService.buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado")));
        model.addAttribute("itens", itemService.listarPorPedido(pedidoId));
        model.addAttribute("produtos", produtoService.listarTodos());
        return "itemPedido/listarItens";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam Integer pedidoId, @RequestParam Integer produtoId,
                         @RequestParam Integer quantidade, RedirectAttributes redirectAttributes) {
        itemService.salvar(pedidoId, produtoId, quantidade);
        redirectAttributes.addFlashAttribute("mensagem", "Item adicionado com sucesso!");
        return "redirect:/item-pedido/listar/" + pedidoId;
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        var item = itemService.buscarPorId(id);
        model.addAttribute("item", item);
        model.addAttribute("produtos", produtoService.listarTodos());
        return "itemPedido/formularioItem";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @RequestParam Integer produtoId,
                            @RequestParam Integer quantidade, RedirectAttributes redirectAttributes) {
        var item = itemService.atualizar(id, produtoId, quantidade);
        redirectAttributes.addFlashAttribute("mensagem", "Item atualizado com sucesso!");
        return "redirect:/item-pedido/listar/" + item.getPedido().getIdPedido();
    }

    @PostMapping("/deletar/{id}/{pedidoId}")
    public String deletar(@PathVariable Integer id, @PathVariable Integer pedidoId,
                          RedirectAttributes redirectAttributes) {
        itemService.deletar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Item removido com sucesso!");
        return "redirect:/item-pedido/listar/" + pedidoId;
    }
}
