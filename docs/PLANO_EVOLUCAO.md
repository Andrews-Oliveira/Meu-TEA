# Meu TEA — auditoria inicial e plano de evolução

**Origem auditada:** `Andrews-Oliveira/Meu-TEA` (branch `main`, commit `590654a`, 21/02/2025).
**Stack encontrada:** Android nativo, Kotlin, Jetpack Compose, Firebase Authentication, Cloud Firestore, Text-to-Speech.
**Objetivo:** eliminar fluxos quebrados e dados demonstrativos, evoluindo o aplicativo para uso real e acessível.

## Diagnóstico confirmado no código

| Área | Evidência | Prioridade |
| --- | --- | --- |
| Cadastro | `MainActivity.kt` navegava para a rota literal `menuPrincipalScreen/{usuarioId}` e `cadastar.kt` voltava à tela anterior mesmo após o sucesso | P0 |
| Conversa | Menu navegava para `ConversaScreen`, mas a rota declarada era `conversaScreen` | P0 |
| Conta | Menu fazia consulta `usuarios.limit(1)` desnecessária, sem filtro pelo usuário autenticado | P0 |
| Carteirinha | Salvar com `set(usuario)` substituía todo o documento da conta; excluir apagava o documento inteiro de `usuarios/{uid}` | P0 |
| Carteirinha | Interface apresentava cartão interno como CIPTEA, sem distinguir emissão oficial | P0 |
| Rotinas | Botão da tela principal estava comentado; dados iam para coleção global `Projeto 01.1`, sem isolamento por UID ou recuperação da lista | P1 |
| Comunicação | Frases e figuras são fixas; há síntese de voz, mas não há criação de cartões personalizados persistentes | P1 |
| Acessibilidade | Revisar alvos de toque, contraste, leitura de telas, TTS em ciclo de vida, navegação e testes com usuários | P1 |
| Qualidade | Sem CI no repositório, testes ainda básicos e dependências de fevereiro/2025 | P1 |

## Entrega 1 — correções essenciais

- [x] Corrigir a rota da conversa.
- [x] Corrigir navegação após cadastro e logout.
- [x] Não consultar um usuário aleatório no menu.
- [x] Preservar dados da conta ao criar a carteirinha.
- [x] Excluir apenas campos da carteirinha, não o cadastro do usuário.
- [x] Identificar a carteirinha interna como **não oficial**.
- [x] Adicionar automação de testes, build e lint do Android.
- [ ] Validar compilação e navegação no emulador/dispositivo.
- [ ] Validar/restringir regras do Firestore no projeto Firebase real (não basta criar arquivo no Git).

## Entrega 2 — módulo de rotinas utilizável

- [ ] Migrar atividades para `usuarios/{uid}/rotinas/{rotinaId}`.
- [ ] Implementar listar, adicionar, editar, remover e sincronizar o estado após operações.
- [ ] Validar data, hora e recorrência; não aceitar datas impossíveis.
- [ ] Implementar lembretes e permissões de notificações no Android atual.
- [ ] Habilitar o botão Rotina apenas após testes end-to-end.

**Aceite:** uma atividade criada reaparece ao reabrir o app e nunca é acessível em outra conta.

## Entrega 3 — comunicação alternativa e aumentativa (CAA)

- [ ] Usuário/cuidador consegue criar, editar e excluir cartões com texto, imagem e frase falada.
- [ ] Persistência por perfil e funcionamento offline para cartões frequentes.
- [ ] Reprodução de áudio em português com estados de carregamento e encerramento correto do TTS.
- [ ] Categorias e ordem personalizáveis, sem depender de conteúdo exclusivamente fixo.

**Aceite:** o cartão personalizado continua disponível após fechar e reabrir o aplicativo.

## Entrega 4 — experiência, segurança e release

- [ ] Auditoria de contraste, tipografia, ícones e alvos de toque.
- [ ] Tratamento de estados vazios, erros de rede, loading e retry.
- [ ] Atualização controlada de AGP, Gradle, Kotlin, Compose, AndroidX e Firebase; verificação de regressão.
- [ ] Validação no Android 13 a 16 (ou matriz compatível com os alvos mantidos).
- [ ] Revisão de LGPD, consentimentos, minimização e retenção, sobretudo informações de menores e de saúde.
- [ ] Testes reais com pessoas usuárias e/ou profissionais da área, sem presumir adequação clínica a partir do código.

## Critérios para afirmar que uma funcionalidade está pronta

1. Não utiliza conteúdo de demonstração sem indicação explícita.
2. Possui persistência e isolamento por usuário quando aplicável.
3. Tem estados de erro, vazio, carregamento e recuperação.
4. Passa por testes automatizados e no emulador/dispositivo.
5. Não expõe dados de terceiros, nem se apresenta como documento/serviço oficial sem sê-lo.

> Este plano é evolutivo. Os itens marcados como implementados ainda exigem validação de compilação e execução antes de merge e publicação.
