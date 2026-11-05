package br.com.devops.devops.service;

import br.com.devops.devops.entity.Aluno;
import br.com.devops.devops.entity.Pedido;
import br.com.devops.devops.repository.AlunoRepository;
import br.com.devops.devops.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final AlunoRepository alunoRepository;

    public PedidoService(PedidoRepository pedidoRepository, AlunoRepository alunoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.alunoRepository = alunoRepository;
    }

    public List<Pedido> listarTodos() { return pedidoRepository.findAll(); }
    public List<Pedido> listarDoCliente(String login) {
        return pedidoRepository.findByClienteLoginUsuarioOrderByIdPedidoDesc(login);
    }
    public Optional<Pedido> buscarDoCliente(Integer id, String login) {
        return pedidoRepository.findById(id).filter(p -> p.getCliente() != null
                && login.equals(p.getCliente().getLoginUsuario()));
    }
    public Optional<Pedido> buscarPorId(Integer id) { return pedidoRepository.findById(id); }
    public void deletar(Integer id) { pedidoRepository.deleteById(id); }

    public Pedido salvar(Pedido pedido, Integer alunoId) {
        vincularAluno(pedido, alunoId);
        if (pedido.getDataPedido() == null) pedido.setDataPedido(LocalDate.now());
        if (pedido.getStatus() == null || pedido.getStatus().isBlank()) pedido.setStatus("PENDENTE");
        pedido.setValorTotal(BigDecimal.ZERO);
        return pedidoRepository.save(pedido);
    }

    public Pedido atualizar(Integer id, Pedido dados, Integer alunoId) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        vincularAluno(pedido, alunoId);
        pedido.setDataPedido(dados.getDataPedido());
        pedido.setStatus(dados.getStatus());
        return pedidoRepository.save(pedido);
    }

    private void vincularAluno(Pedido pedido, Integer alunoId) {
        if (alunoId == null) {
            throw new IllegalArgumentException("Selecione um aluno para o pedido");
        }

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        pedido.setAluno(aluno);
        pedido.setNomeCliente(aluno.getNomeAluno());
    }
}
