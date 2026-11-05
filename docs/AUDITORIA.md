# Registro de auditoria — DevOpsCR

**Responsável editorial:** Cristiano Ronaldo Ferreira Bueno

## Domínios preservados
A plataforma tem dois grupos funcionais: acadêmico (alunos, cursos, professores e disciplinas) e comercial (produtos, usuários, itens, pedidos e loja). O painel administrativo separa os dois conjuntos; a loja tem rotas de catálogo, carrinho e histórico.

## Descobertas e remediações
| Prioridade | Problema | Alteração |
|---|---|---|
| P0 | CSRF desligado globalmente | Configuração Spring Security reativada |
| P0 | GETs excluíam registros | Rotas de exclusão convertidas em POST e formulários com confirmação |
| P0 | /devops acessível publicamente | Acesso limitado a ADMIN |
| P0 | Contas iniciais com senhas fixas | Initializer limitado ao perfil dev e credenciais via ambiente |
| P0 | Links de redefinição apareciam no stdout | Removidos os logs de tokens |
| P1 | Carrinho sem checkout | CheckoutService transacional, com preços de banco, locks e estoque |
| P1 | Pedido com dois mapeamentos de itens | Consolidada a coleção de itens |
| P1 | Sem consulta de pedido pelo cliente | Rotas com validação da conta logada |
| P2 | Visual vermelho não sistematizado | Tokens CSS, componentes de loja e painel administrativo agrupado |
| P2 | Sem CI | Workflow Maven com JDK 17 |
| P2 | Documentação curta | README com arquitetura, execução e limitações |

## Observações e limites

- Nunca declarar integração de pagamentos: não existe gateway; o pedido é somente acadêmico.
- Pedido do cliente tem token único de idempotência e valida estoque sob trava pessimista.
- A edição manual dos itens pelo administrador não realiza uma operação de checkout: revisitar as regras de estoque para a operação administrativa é melhoria futura.
- Exclusões devem ser avaliadas quanto a referências já persistidas: FKs e histórico podem impedir apagar registros relacionados.
- A ligação de novas linhas/tabelas ao PostgreSQL usa `ddl-auto=update`, que é aceitável para estudos, mas não substitui migrações de produção.
- As contas presentes em bancos existentes não são alteradas; rotacione senhas antigas manualmente.
- O resgate de senha depende de SMTP; nunca registrar token sensível no console.

## Screenshots pendentes

Não se pode chamar imagens geradas de prints do sistema. Capturar por navegador real com aplicação iniciada e banco de dados fictício, nomear `docs/screenshots/login.png`, `painel-admin.png`, `loja.png`, `carrinho.png`, `pedido.png`. Incorporar no README apenas depois que os arquivos PNG realmente existirem.

## Verificação

- `cd devops && ./mvnw -B clean verify` para compilar e executar testes.
- GitHub Actions `java-ci.yml` foi incluído para executar a mesma verificação.
- Testar manualmente login, USER/ADMIN, CSRF, estoque insuficiente, atualização e remoção, checkout, acesso de outro usuário e CRUD administrativo.
- Nunca afirmar que todas as funcionalidades passaram sem visualizar os resultados.
