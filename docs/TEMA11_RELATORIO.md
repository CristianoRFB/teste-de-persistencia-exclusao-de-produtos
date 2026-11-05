# Relatório de atividade prática — Tema 11

**Autor:** Cristiano Ronaldo Ferreira Bueno  
**Disciplina:** Integração e Entrega Contínua (DevOps) — Qualidade e Testes de Software  
**Tema:** Teste de persistência — exclusão de produtos  
**Sistema utilizado:** DevOpsCR (e-commerce Java Spring Boot)  
**Data da execução:** 05/11/2026

## 1. Conceito e importância

Teste de persistência verifica se uma alteração nos registros de um banco permanece correta após as operações do programa e a sincronização do estado da sessão de persistência. Na exclusão de produtos, também verifica a integridade referencial: pedidos anteriores não devem perder referências aos produtos que foram vendidos.

A exclusão física elimina a linha. A exclusão lógica mantém a linha, mas marca seu estado como inativo, permitindo ocultá-la das operações comuns.

## 2. Ferramentas

- Java 17, Spring Boot 4.0.3, Spring Data JPA/Hibernate.
- JUnit 5 e `@SpringBootTest` para executar o contexto real.
- H2 em memória para banco de testes isolado; PostgreSQL é o banco configurado no uso convencional.
- `EntityManager.flush()` e `clear()` para exigir a gravação e forçar uma leitura nova da base.
- GitHub Actions e Maven para registrar execuções reproduzíveis.

## 3. Cenário RED — problema comprovado

Código original: `ProdutoService.deletar(id) -> produtoRepository.deleteById(id)`.

**Caso:** cadastrar um produto, criar um pedido com `ItemDoPedido` associado a esse produto, tentar excluir o produto.

**Esperado:** produto não disponível para novas compras; histórico do pedido preservado.  
**Obtido:** erro de integridade referencial do banco; o método de exclusão física tentou remover uma linha usada por outro registro.

Log real (GitHub Actions [#42099790268](https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/runs/42099790268)):

```text
Referential integrity constraint violation:
PUBLIC.ITEM_DO_PEDIDO FOREIGN KEY(ID_PRODUTO)
REFERENCES PUBLIC.PRODUTO(ID_PRODUTO)
delete from produto where id_produto=? [23503-240]

[ERROR] Tests run: 10, Failures: 2, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

O segundo teste também comprovou que uma exclusão física retirava permanentemente a linha do produto do banco (`expected: true but was: false`).

## 4. Correção GREEN — exclusão lógica e segurança de compra

A entidade `Produto` recebeu a propriedade booleana `ativo` com valor padrão `true`. O `ProdutoService.deletar`, em transação, procura o produto ativo e grava `ativo=false` em vez de chamar `deleteById`.

```java
@Transactional
public void deletar(Integer id) {
    Produto produto = buscarPorId(id)
        .orElseThrow(() -> new IllegalArgumentException(
            "Produto não encontrado ou já arquivado."));
    produto.setAtivo(Boolean.FALSE);
    produtoRepository.save(produto);
}
```

`ProdutoRepository` passou a filtrar somente produtos ativos em consultas do catálogo e no bloqueio de estoque da compra. `ItemDoPedidoService` também não permite associar novos itens a produtos arquivados. Um novo endpoint permite a reativação por ADMIN.

## 5. Resultados comprovados

| Caso | Antes | Depois |
|---|---|---|
| Produto já aparece em um pedido | Exclusão falha com chave estrangeira | Produto arquivado sem erro, pedido preservado |
| Produto sem pedidos | Linha apagada fisicamente | Linha permanece com `ativo=false` |
| Produto arquivado visível no catálogo | Sem arquivamento | Oculto do catálogo |
| Identificador de produto inexistente | Comportamento dependente da operação JPA | Mensagem de erro clara |
| Compra de produto arquivado | Não havia estado inativo | Consulta de checkout filtra `ativo=true` |

GitHub Actions **RED**, branch com teste antes da correção: [execução #42099790268](https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/runs/42099790268): 10 testes, 2 falhas, BUILD FAILURE.

GitHub Actions **GREEN**, commit de correção: [execução #42099999143](https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/runs/42099999143): 11 testes, 0 falhas, 0 erros, BUILD SUCCESS.

GitHub Actions **GREEN** após melhorias visuais: [execução #42100436510](https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/runs/42100436510): sucesso.

## 6. Procedimento de demonstração

1. Acessar o painel administrativo (perfil ADMIN).
2. Cadastrar um produto com nome, descrição, preço e estoque.
3. Finalizar uma compra de demonstração para gerar `Pedido` e `ItemDoPedido`.
4. Acessar `/produto/listar`, clicar em **Arquivar** e confirmar.
5. Verificar que o produto não aparece na loja nem em produtos ativos.
6. Acessar `/produto/arquivados` e localizar o mesmo produto.
7. Confirmar que os pedidos existentes continuam preservados.
8. Apresentar os logs RED/GREEN e o teste JUnit executado automaticamente.

## 7. Limites e considerações

Os testes automatizados usam H2, não substituem homologação com PostgreSQL; CI validou o build e os testes, mas não constitui prova de uma operação manual por navegador. Um screenshot ilustrativo de interface não deve ser apresentado como captura de um servidor executado. Para produção, revisar a migração da coluna `ativo` usando Flyway/Liquibase, validar autorização e testar com transações reais no PostgreSQL.

## 8. Entregáveis

- Código-fonte: https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos
- Teste JUnit: `devops/src/test/java/br/com/devops/devops/service/ProdutoExclusaoPersistenciaTest.java`
- Execução original com erro: https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/runs/42099790268
- Execução corrigida: https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/runs/42099999143
- Execução com novo visual: https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/runs/42100436510
