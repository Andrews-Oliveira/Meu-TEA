# Meu TEA — Cartões de comunicação com foto própria

## Criar um cartão personalizado

Na prancha **Minha comunicação**, toque em **+ Meu cartão**.

1. Escolha **Câmera** para fotografar um cartão físico de emoções/expressões ou **Galeria** para utilizar uma fotografia existente.
2. Confira a pré-visualização. A imagem será ajustada à tela sem recorte automático.
3. Preencha **Mensagem falada** (até 100 caracteres), por exemplo: **"Quero um tempo sozinho para me acalmar."**
4. Escolha a categoria (Emoções, Necessidades, Conforto, Escolhas ou Convivência).
5. Toque em **Salvar cartão**. O texto é salvo no Firestore sob o UID autenticado. A foto é compactada e gravada no armazenamento privado do aparelho.
6. Na prancha, toque no cartão: ele falará a frase com TTS do Android ou, quando existir, a gravação pessoal associada àquele cartão.

## Editar ou excluir

Os cartões personalizados apresentam **Editar cartão**. No editor, é possível substituir a imagem, apagar somente a imagem, alterar a frase ou excluir o cartão inteiro (com confirmação). A exclusão também apaga os arquivos locais de voz e imagem associados, após o Firestore confirmar a exclusão.

## Privacidade

- Fotos ficam em `noBackupFilesDir/fotos_cartoes_v1` com nomes derivados de SHA-256 de `UID:cardId`, não são enviadas ao Firebase.
- Mensagens/categorias sincronizam pelo Firestore, mas **fotos não acompanham outra instalação ou aparelho**.
- Se desinstalar o aplicativo, as fotos locais serão perdidas. O arquivo original na galeria **não é apagado**.
- A câmera é acionada via `ActivityResultContracts.TakePicture` com um `FileProvider` limitado à pasta de cache `tea_camera/`; não há permissão `CAMERA` no app.
- O seletor `GetContent("image/*")` concede acesso somente à imagem escolhida; não pede acesso amplo a fotos.
- Imagens capturadas são limitadas a 30 MB de entrada e compactadas para JPEG de no máximo 1200 px de lado, com correção de orientação EXIF suportada.
- Não há processamento remoto de foto nem detecção automática de emoções. A descrição é definida pela pessoa responsável/usuária.

## Sobre materiais comprados

É possível fotografar cartões físicos adquiridos para uso pessoal. Antes de compartilhar, redistribuir ou publicar um catálogo completo de imagens de terceiros, confira os direitos e a licença das imagens.

## Checklist de validação

- [ ] Criar cartão usando a câmera, escrever frase e salvar.
- [ ] Criar cartão usando a galeria e confirmar a imagem na grade.
- [ ] Tocar na foto e confirmar TTS/gravação reproduzindo a frase personalizada.
- [ ] Reiniciar o aplicativo e confirmar que imagem e texto reaparecem.
- [ ] Editar somente o texto mantendo a foto original.
- [ ] Trocar foto e salvar; cancelar edição sem trocar a imagem original.
- [ ] Remover a foto mantendo texto e categoria.
- [ ] Excluir cartão e confirmar que áudio/foto privados são removidos.
- [ ] Testar usuário A e usuário B no mesmo celular: não devem compartilhar fotos.
- [ ] Confirmar que uma foto tirada em retrato aparece com orientação correta.
- [ ] Recusar o uso da câmera ou cancelar a galeria sem fechar o aplicativo.
- [ ] Testar layout em telas pequenas e com tamanho de fonte ampliado.

A compilação automatizada valida o código; o resultado do uso real da câmera e galeria precisa ser verificado no emulador ou no aparelho.
