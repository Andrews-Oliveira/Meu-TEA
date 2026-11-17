# Meu TEA — migração Android 2026

## Stack do projeto

- Android Gradle Plugin 9.5.1 com Kotlin integrado (sem `org.jetbrains.kotlin.android`).
- Gradle Wrapper 9.9.1.
- Kotlin Compose Compiler 2.4.21.
- Android 17 / SDK 37 (alvo e compilação), suporte mínimo: Android 7.0 / API 24.
- AndroidX / Compose BoM 2026.09.00.
- Firebase BoM 35.2.0, sem dependências Firebase `-ktx` obsoletas.
- Linguagem e bytecode Java 17; isso é escolha de compatibilidade do **aplicativo Android**, não impede usar JDK 25 como JVM do Gradle.

## Atualizar a cópia que já foi clonada

No PowerShell:

```powershell
cd "$env:USERPROFILE\Documents\Meu-TEA-atualizacao"
git status
git switch fix/fluxos-essenciais-meu-tea-20261117
git pull --ff-only origin fix/fluxos-essenciais-meu-tea-20261117
.\gradlew.bat --version
```

Se `git status` mostrar alterações locais, não descarte nada sem revisar; faça commit ou stash antes de atualizar.

## Android Studio e SDK

1. Instalar versão **estável** do Android Studio compatível com AGP 9.5 (Rabbit 2 ou superior).
2. Em **Settings > Build, Execution, Deployment > Build Tools > Gradle**, escolher uma instalação de JDK reconhecida pelo Gradle 9.9.1. Java 25 é suportado pelo próprio Gradle 9.9.1; Java 17 é o baseline usado no CI para maximizar compatibilidade.
3. Em **Tools > SDK Manager > SDK Platforms**, instalar Android 17 / API 37 e ferramentas de build recomendadas.
4. Sincronizar o Gradle; depois executar `app` em emulador.
5. Rodar `./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` (Windows: `.\gradlew.bat`).

## Validações antes de produção

- A versão maior do Android aumenta os requisitos de UX/segurança; testar edge-to-edge, teclado, permissões, notificações e navegação.
- Verificar fluxos de login/cadastro/logout, cartão interno e áudio em dispositivo.
- Verificar regras reais do Cloud Firestore antes de usar dados pessoais.
- Este projeto **não emite** a CIPTEA oficial; sua carteirinha é registro pessoal.
- CI e Dependabot podem sinalizar regressões; nunca aplicar versões novas sem testes.

## Manutenção contínua

O `dependabot.yml` propõe semanalmente PRs para Gradle e GitHub Actions na branch padrão. Revisar testes, changelogs, compatibilidade do AGP com Gradle e alterações de target SDK antes de cada merge.
