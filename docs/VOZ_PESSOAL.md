# Meu TEA — voz pessoal por cartão (versão experimental)

## O que foi integrado

Na tela **Minha comunicação**, cada cartão mostra **Gravar voz**. A pessoa responsável ou usuária pode:

1. Tocar em **Gravar voz** no cartão desejado.
2. Autorizar o microfone quando solicitado pelo Android.
3. Selecionar **Começar gravação**, falar a frase e tocar **Parar e salvar** (máximo de 15 segundos).
4. Tocar normalmente no cartão. **A gravação própria terá prioridade sobre o TTS do Android**.
5. Ouvir, regravar, importar áudio ou excluir a gravação.

Em **Minha voz**, também há estilos de síntese **Serena, Animada e Grave** (ajustes moderados de ritmo/pitch) e seleção das vozes pt-BR instaladas. Isso não transforma uma voz de sistema em voz neural.

## Natureza do recurso

**Não é clonagem de voz:** cada arquivo gravado ou importado reproduz exatamente a frase daquele cartão. Não gera frases inéditas com o timbre do usuário. O botão **Ouvir tudo** para frases compostas permanece usando TTS do Android.

Para futura clonagem neural (texto livre na própria voz), será necessário contratar/integrar um provedor de síntese com comprovação de consentimento, armazenamento seguro, custos, política para vozes de menores, controles de exclusão e auditoria.

**Vozes de personagens, anime e filmes:** não distribuir no app cópias de trilhas, dublagens ou timbres de atores sem autorização/licença. Estilos originais são permitidos; arquivos importados devem ser próprios ou devidamente autorizados.

## Privacidade, isolamento e limites

- Permission `android.permission.RECORD_AUDIO` é solicitada apenas ao tocar **Gravar voz**, nunca ao iniciar o app.
- Áudios ficam em `Context.noBackupFilesDir/vozes_cartoes_v1/` (pasta privada, fora do backup automático).
- Cada nome de arquivo usa SHA-256 de `UID:cardId`, evitando colisão entre usuários e path traversal.
- Gravação em AAC/MPEG-4 (`.m4a`) e importação de arquivos MIME `audio/*` até 8 MB.
- Áudios **não são enviados ao Firestore**. A exclusão é local e não apaga documentos da conta.
- Remover o app apaga arquivos privados; trocar de celular não transfere as gravações automaticamente.
- A gravação de voz de crianças ou terceiros deve ter consentimento apropriado do responsável e da própria pessoa quando possível.

## Testes de aceite no emulador/dispositivo

- [ ] Ao tocar Gravar voz, microfone pede permissão em tempo de execução; negar não fecha o aplicativo.
- [ ] Gravando e salvando uma frase, tocá-la reproduz a voz real, offline.
- [ ] Voltar à tela e abrir novamente preserva a gravação local.
- [ ] Regravar substitui apenas o áudio do cartão correspondente.
- [ ] Excluir gravação faz o cartão voltar ao TTS.
- [ ] Importar áudio válido com até 8 MB funciona sem subir à nuvem.
- [ ] Importar arquivo acima de 8 MB ou inválido exibe mensagem de erro, mantendo a gravação anterior.
- [ ] Usuário B não recebe gravação de usuário A.
- [ ] Navegar para fora durante gravação encerra/cancela o recorder.
- [ ] Ouvir tudo continua funcional com TTS.
- [ ] Testar pelo menos Android 13, 16 e 17 e conferir microfone real do emulador.

**Nota:** estes testes de interface e microfone exigem execução em aparelho ou emulador; o CI inicial valida compilação, testes existentes e lint, não comprova gravação de áudio real.
