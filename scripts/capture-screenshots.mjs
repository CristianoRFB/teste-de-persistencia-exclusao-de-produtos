import { chromium } from 'playwright';
import { mkdir, readFile, writeFile } from 'node:fs/promises';

const base = 'http://localhost:8080';
const folder = 'docs/screenshots';
const adminPassword = process.env.DEV_ADMIN_PASSWORD;
const userPassword = process.env.DEV_USER_PASSWORD;
if (!adminPassword || !userPassword) throw new Error('Missing isolated CI demo credentials');
await mkdir(folder, { recursive: true });

const browser = await chromium.launch({ headless: true });
const page = await browser.newPage({ viewport: { width: 1440, height: 900 }, deviceScaleFactor: 1 });
page.setDefaultTimeout(15000);

async function snap(filename) {
  await page.screenshot({ path: folder + '/' + filename, fullPage: true, animations: 'disabled' });
}
async function login(name, password) {
  await page.goto(base + '/login');
  await page.locator('#username').fill(name);
  await page.locator('#password').fill(password);
  await Promise.all([page.waitForURL(url => !url.pathname.endsWith('/login')), page.locator('form.auth-form button[type="submit"]').click()]);
}
try {
  await page.goto(base + '/login');
  await snap('login.png');
  await login('admin', adminPassword);
  await page.goto(base + '/home');
  await page.locator('h1').filter({ hasText: 'Painel de gerenciamento' }).waitFor();
  await snap('painel-admin.png');
  await page.goto(base + '/aluno/listar');
  await page.locator('h1').waitFor();
  await snap('gestao-academica.png');

  await page.goto(base + '/produto/formulario');
  await page.locator('#nomeProduto').fill('Camiseta DevOpsCR');
  await page.locator('#descricaoProduto').fill('Produto fictício utilizado exclusivamente nos testes de interface.');
  await page.locator('#precoProduto').fill('59.90');
  await page.locator('#quantidadeEstoque').fill('8');
  await Promise.all([page.waitForURL('**/produto/listar'), page.locator('button[type="submit"]').filter({ hasText: 'Salvar' }).click()]);
  await page.goto(base + '/produto/listar');
  await snap('produtos-admin.png');

  await page.goto(base + '/loja');
  await page.locator('form[action="/logout"] button').click();
  await login('cliente', userPassword);
  await page.goto(base + '/loja');
  await page.locator('text=Camiseta DevOpsCR').first().waitFor();
  await snap('loja.png');
  await page.locator('a.product-card-link').first().click();
  await page.locator('h1').filter({ hasText: 'Camiseta DevOpsCR' }).waitFor();
  await snap('detalhe-produto.png');
  await page.locator('form.purchase-form button').click();
  await page.waitForURL('**/loja/carrinho');
  await page.locator('.cart-row').first().waitFor();
  await snap('carrinho.png');
  await page.locator('.cart-checkout form button').click();
  await page.waitForURL(/\/loja\/pedidos\/\d+$/);
  await page.locator('h1').filter({ hasText: 'Pedido #' }).waitFor();
  await snap('checkout-confirmacao.png');
  await page.goto(base + '/loja/pedidos');
  await snap('meus-pedidos.png');

  const files = ['login.png','painel-admin.png','gestao-academica.png','produtos-admin.png','loja.png','detalhe-produto.png','carrinho.png','checkout-confirmacao.png','meus-pedidos.png'];
  const captions = ['Login e autenticação','Painel administrativo','Gestão acadêmica','Cadastro de produtos','Catálogo da loja','Detalhes de produto','Carrinho de compras','Confirmação de pedido','Histórico de pedidos'];
  const gallery = '## Capturas do sistema\n\nCapturas reais produzidas automaticamente com Spring Boot e H2 em uma execução de teste, utilizando contas e produtos fictícios.\n\n' +
    files.map((name,i) => '### ' + captions[i] + '\n\n![Screenshot real: ' + captions[i] + '](docs/screenshots/' + name + ')').join('\n\n') + '\n\n';
  const current = await readFile('README.md','utf8');
  const updated = current.replace(/## Capturas do sistema[\s\S]*?(?=## Como executar no computador)/, gallery);
  if (current === updated) throw new Error('README gallery section missing');
  await writeFile('README.md',updated,'utf8');
  console.log('Nine verified browser screenshots captured with fictional test data.');
} finally {
  await browser.close();
}
