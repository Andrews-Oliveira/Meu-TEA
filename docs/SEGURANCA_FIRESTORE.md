# Segurança dos dados no Firebase — ações obrigatórias

O app usa Firebase Authentication e grava informações potencialmente sensíveis (CPF, nascimento, endereço e CID) em `usuarios/{uid}`.

## O que o cliente Android consegue — e o que não consegue

A verificação de UID no aplicativo reduz erros de navegação, **mas não é uma barreira de segurança**. As regras do Cloud Firestore precisam ser aplicadas no Firebase Console ou via Firebase CLI, com testes prévios.

As regras ativas do projeto Firebase **não foram verificadas** nesta auditoria. Portanto, não há garantia atual de isolamento entre contas.

## Regras iniciais de referência (não implantadas)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /usuarios/{userId} {
      allow read, create, update, delete:
        if request.auth != null && request.auth.uid == userId;
    }
    // Nenhuma regra permite acesso à antiga coleção global "Projeto 01.1".
    // Migrar rotinas para um caminho protegido por UID antes de reativá-las.
    match /usuarios/{userId}/rotinas/{rotinaId} {
      allow read, create, update, delete:
        if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

**Atenção:** ao implantar, estas regras negam acesso a quaisquer outras coleções que não tenham regra apropriada. Antes de publicar, inventariar todas as coleções e acessos usados pelo app. Nunca adotar `allow read, write: if true` em produção.

## Checklist antes de publicar

- [ ] Criar ambiente Firebase de homologação separado.
- [ ] Levantar regras atualmente ativas e coleções existentes.
- [ ] Validar usuário A lendo/escrevendo seus dados.
- [ ] Garantir que usuário A não possa ler, editar nem apagar dados de usuário B.
- [ ] Validar logout, criação de conta, carteira e exclusão sem perda do documento de login.
- [ ] Validar operações com conexão ausente.
- [ ] Migrar atividades da coleção global de rotinas para caminho individual.
- [ ] Revisar retenção, exportação e exclusão efetiva de dados pessoais, inclusive dados de saúde.
- [ ] Revisar apresentação da carteirinha como registro informativo, sem alegar emissão oficial.

**Não publicar dados reais em issues, commits, logs ou capturas de tela.**
