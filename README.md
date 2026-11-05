# Teste de persistência — Exclusão de produtos

**Cristiano Ronaldo Ferreira Bueno · Tema 11 · Qualidade e Testes de Software**

[![Java CI](https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/workflows/java-ci.yml/badge.svg)](https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/workflows/java-ci.yml)

## Sobre esta atividade acadêmica

Este repositório documenta uma **atividade prática do curso superior de Tecnologia em Análise e Desenvolvimento de Sistemas (ADS)**, realizada na **Faculdade de Tecnologia de Jales (Fatec Jales)**, instituição do Centro Paula Souza, no ano de **2026**.

| Informação | Descrição |
| --- | --- |
| **Instituição** | Fatec Jales — Faculdade de Tecnologia de Jales |
| **Curso** | Tecnologia em Análise e Desenvolvimento de Sistemas (ADS) |
| **Disciplina** | Integração e Entrega Contínua (DevOps) |
| **Atividade** | Qualidade e Testes de Software |
| **Tema atribuído** | Tema 11 — Teste de persistência: exclusão de produtos |
| **Estudante** | Cristiano Ronaldo Ferreira Bueno |
| **Ano** | 2026 |

### Proposta da atividade

A proposta acadêmica consiste em **pesquisar, implementar, executar e demonstrar técnicas de teste e qualidade de software** aplicadas a um projeto de e-commerce já existente em **Java com Spring Boot**. Além da pesquisa teórica, a atividade exige implementação funcional, evidências de execução, documentação e apresentação prática para a turma.

Para o **Tema 11**, o objetivo é verificar se a exclusão de produtos funciona corretamente no banco de dados, respeitando o modelo de exclusão física ou lógica adotado e preservando a integridade das informações relacionadas.

Este trabalho utiliza como ponto de partida o projeto [DevOpsCR](https://github.com/CristianoRFB/devopscr), adaptado especificamente para essa avaliação. O foco é demonstrar o ciclo de qualidade **falha identificada → teste automatizado → correção → validação dos resultados**.

> **Finalidade educacional:** projeto desenvolvido como exercício de graduação. Não se trata de uma aplicação comercial pronta para produção.

Atividade prática desenvolvida sobre o projeto original **[DevOpsCR](https://github.com/CristianoRFB/devopscr)** (Java 17, Spring Boot 4, Thymeleaf, JPA, PostgreSQL e H2 para testes). O repositório de origem foi mantido intacto. Esta cópia contém a implementação específica do Tema 11, melhorias visuais e testes automatizados.

## Problema reproduzido

O método original `ProdutoService.deletar(id)` executava `produtoRepository.deleteById(id)` (exclusão física). Se um produto já estivesse associado a `ItemDoPedido`, o banco recusava a operação devido à restrição de chave estrangeira `item_do_pedido.id_produto -> produto.id_produto`.

**Evidência RED — execução real de 05/11/2026:** [GitHub Actions #42099790268](https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/runs/42099790268), branch `feat/evidencia-erro-exclusao`. 10 testes executados, 2 falhas, incluindo `ConstraintViolationException` ao executar `delete from produto where id_produto=?`.

## Correção aplicada

Agora `ProdutoService.deletar` marca `produto.ativo=false` dentro de uma transação. O produto arquivado:
- deixa de aparecer no catálogo e na consulta pública/administrativa de produtos ativos;
- não pode ser comprado ou vinculado a um novo item de pedido;
- permanece na tabela `produto` para preservar as referências de pedidos antigos;
- pode ser consultado em `/produto/arquivados` e reativado por um administrador.

A entidade introduz a coluna `ativo` com padrão `true`. Em uma base PostgreSQL existente, antes de ativar a nova versão em ambiente persistente, recomenda-se validar a migração de esquema e, se necessário, executar:

```sql
ALTER TABLE produto ADD COLUMN IF NOT EXISTS ativo BOOLEAN NOT NULL DEFAULT TRUE;
```

Para uso acadêmico local, Hibernate está configurado com `ddl-auto=update`. Em produção, use migração versionada.

## Resultado GREEN

**Evidência GREEN — execução real de 05/11/2026:** [GitHub Actions #42099999143](https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/runs/42099999143). **11 testes, 0 falhas e 0 erros**, com build Maven concluído com `BUILD SUCCESS`. A versão com melhorias de interface também foi aprovada pelo pipeline: [GitHub Actions #42100436510](https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos/actions/runs/42100436510).

Testes novos em [ProdutoExclusaoPersistenciaTest.java](devops/src/test/java/br/com/devops/devops/service/ProdutoExclusaoPersistenciaTest.java): exclusão de produto com pedido, exclusão sem pedido e tentativa com identificador inexistente. Testes usam **Spring Boot Test + JUnit 5 + H2 + EntityManager** e validam persistência efetiva após `flush()` e `clear()`.

## Interface redesenhada (tema vermelho)

- `/produto/listar`: tabela responsiva, busca rápida, etiquetas de estoque, explicação da exclusão lógica, ação **Arquivar** com confirmação explícita.
- `/produto/arquivados`: histórico com estado de cada item e botão **Reativar produto**.
- Navegação administrativa e loja preservadas; formulários protegidos por CSRF, foco visível e mensagens de resultado.

## Como executar

Pré-requisitos: Java 17, acesso à internet para dependências Maven e PostgreSQL, ou H2 para demonstração.

```bash
git clone https://github.com/CristianoRFB/teste-de-persistencia-exclusao-de-produtos.git
cd teste-de-persistencia-exclusao-de-produtos/devops
./mvnw -B clean verify
```

No Windows, use `mvnw.cmd -B clean verify`.

Para a aplicação no banco PostgreSQL configurado, defina `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` no ambiente e execute `./mvnw spring-boot:run`.

Para uma demonstração local temporária usando H2 e as contas geradas pelo perfil `dev`, configure `SPRING_PROFILES_ACTIVE=dev`, `DB_URL=jdbc:h2:mem:demo;MODE=PostgreSQL;DB_CLOSE_DELAY=-1`, `DB_USERNAME=sa`, `DB_PASSWORD=`, `DEV_ADMIN_PASSWORD` e `DEV_USER_PASSWORD` com senhas exclusivas de sua máquina. Não versionar senhas. Abra http://localhost:8080.

## Como demonstrar em sala

1. Cadastre um produto e finalize um pedido que contenha esse produto.
2. Como ADMIN, abra **Produtos ativos**, clique **Arquivar** e confirme.
3. Confira o alerta de sucesso, a ausência do produto na loja e a presença em **Produtos arquivados**.
4. Mostre que o histórico e os itens do pedido permanecem íntegros.
5. Mostre o GitHub Actions **RED** (erro de integridade no método antigo) e **GREEN** (11 testes aprovados).

Veja o [relatório detalhado](docs/TEMA11_RELATORIO.md), com a metodologia, os resultados e os links para as evidências.

---

**Cristiano Ronaldo Ferreira Bueno** · ADS — Fatec Jales · Integração e Entrega Contínua (DevOps) · 2026. Não há cobrança real, gateway de pagamento ou prontidão de produção.
