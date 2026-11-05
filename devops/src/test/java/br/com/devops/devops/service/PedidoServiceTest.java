package br.com.devops.devops.service;

import br.com.devops.devops.entity.Aluno;
import br.com.devops.devops.entity.Pedido;
import br.com.devops.devops.repository.AlunoRepository;
import br.com.devops.devops.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PedidoServiceTest {

    private PedidoRepository pedidoRepository;
    private AlunoRepository alunoRepository;
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        pedidoRepository = mock(PedidoRepository.class);
        alunoRepository = mock(AlunoRepository.class);
        pedidoService = new PedidoService(pedidoRepository, alunoRepository);
    }

    @Test
    void deveVincularAlunoAoSalvarPedido() {
        Aluno aluno = new Aluno();
        aluno.setIdAluno(10);
        aluno.setNomeAluno("Maria");
        Pedido pedido = new Pedido();

        when(alunoRepository.findById(10)).thenReturn(Optional.of(aluno));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido salvo = pedidoService.salvar(pedido, 10);

        assertSame(aluno, salvo.getAluno());
        assertEquals("Maria", salvo.getNomeCliente());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void naoDeveSalvarPedidoComAlunoInexistente() {
        Pedido pedido = new Pedido();
        when(alunoRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.salvar(pedido, 99));

        assertEquals("Aluno não encontrado", erro.getMessage());
        verify(pedidoRepository, never()).save(pedido);
    }
}
