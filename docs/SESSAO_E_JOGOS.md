# Meu TEA — autenticação persistente e jogos interativos

## Entrar uma vez e continuar usando

A navegação inicial (`sessionGate`) observa `FirebaseAuth.currentUser` por um `AuthStateListener`.

- **Sessão válida:** abre diretamente a tela inicial da conta, sem redigitar e-mail/senha.
- **Sem sessão:** abre o login.
- **Sair:** invoca `FirebaseAuth.signOut()` e volta ao login.
- **Sem conexão:** uma sessão já armazenada localmente pode continuar autenticada; as operações que dependem de Firestore ainda precisam de rede ou cache.

**Não armazenar a senha** em SharedPreferences nem em banco de dados customizado. Não habilitar login automático para conta inexistente ou após logout.

## Jogos disponíveis

Os nove modos são implementados localmente em Kotlin/Jetpack Compose. Não são iframes nem demonstrações.

| Jogo | Como funciona |
| --- | --- |
| Quebra-cabeça | Troca peças vizinhas ao espaço vazio de um quadro 3×3; embaralhamento por movimentos legais |
| Caça-palavras | Seleção contínua horizontal/vertical de CASA, LUA, SOL, PAZ; marca progresso |
| Cobrinha | Grade 11×11, setas, coletar fruta, pontuação, pausa, reinício e velocidade reduzida |
| Procure o objeto | Encontre um objeto específico entre várias figuras, sem contagem de tempo |
| Matemática | Questões objetivas em três dificuldades, com explicações |
| Português | Questões de leitura e língua portuguesa em três dificuldades, com explicações |
| Lógica | Sequências, padrões, inferências e raciocínio em três dificuldades |
| Emoções | Identificar possibilidades de expressão em imagens, sem afirmar diagnosticar emoções |
| Pausa tranquila | Etapas voluntárias de descanso, sem obrigação nem temporizador |

### Princípios de UX

- Nenhum jogo tem anúncios, monetização ou rastreamento adicional.
- As opções de dificuldade não se baseiam no diagnóstico nem determinam faixa etária.
- É possível interromper a atividade e voltar ao menu.
- Os jogos são apoio lúdico, **não tratamento, diagnóstico ou avaliação clínica**.

## Direitos sobre jogos

Os jogos foram implementados com código próprio e conteúdo local. Não se importou código, assets, marcas ou personagens comerciais de terceiros. Copiar jogos completos ou artes de lojas exige análise da licença dos respectivos titulares, mesmo quando o aplicativo é gratuito.

## Testes indispensáveis no dispositivo

1. Abrir o Meu TEA já autenticado, encerrar o app e abrir novamente: deve ir à home.
2. Tocar **Sair**, encerrar e abrir novamente: deve exibir login.
3. Testar login em rede intermitente: autenticação bem-sucedida não deve depender da leitura do perfil Firestore.
4. Abrir cada um dos nove jogos, interagir, sair e voltar.
5. Verificar peças móveis do quebra-cabeça, seleção correta de letras e controles sem inversão da cobrinha.
6. Testar atividades em telas pequenas e com fontes ampliadas e leitor de tela.
7. Testar no Android 13 e no Android 17; verificar sem internet.

## Evoluções posteriores

Incluir escolha entre temas com dificuldade adaptável, temas de conteúdo personalizados, figuras vetoriais ampliadas, controles alternativos para limitações motoras, sons opcionais por atividade e salvar progresso apenas com consentimento explícito. Fazer testes participativos com diferentes usuários e profissionais antes de promover a produção.
