# Auditoria técnica — DevOpsCR (29/10/2026)

Responsável pelo projeto: **Cristiano Ronaldo Ferreira Bueno**.

## Situação desta branch

Implementação parcial e **NÃO APROVADA PARA MERGE**. A `main` foi preservada.

### Alterações propostas e já presentes

- `Pedido`: removeu associação duplicada de itens e criou vínculo opcional com cliente e token de checkout.
- `ProdutoRepository`: consulta com bloqueio pessimista para validar estoque.
- `CheckoutService`: valida produtos, preços e quantidades no servidor e tenta persistir o pedido e os itens em transação.
- `LojaController`: finalização e consultas de pedidos vinculados à conta autenticada.
- `templates/loja`: telas de catálogo, produto, carrinho, histórico e confirmação.
- `.github/workflows/java-ci.yml`: verificação Maven com Java 17.

### Falha reproduzida no CI

GitHub Actions, execução 40950703217: `BUILD FAILURE`. Erros de compilação por falta de dois métodos em `PedidoRepository`:

- `findByClienteLoginUsuarioOrderByIdPedidoDesc(String loginUsuario)`
- `findByCheckoutToken(String checkoutToken)`

**Não afirmar que o checkout funciona.**

### Pendências P0 / P1

- Inserir os dois métodos no `PedidoRepository`.
- Habilitar CSRF e converter exclusões por GET para POST com token.
- Proteger `/devops/**` com perfil ADMIN.
- Remover contas fixas de demonstração e links de recuperação dos logs.
- Validar compilação, persistência, login, sessão, carrinho, checkout e autorização.
- Revisar telas de administração e completar redesign responsivo.
- Atualizar README e adicionar prints REAIS somente após execução.

A publicação de novos arquivos existentes e a abertura do PR foram bloqueadas na integração durante esta sessão. Nenhum screenshot foi fabricado.
