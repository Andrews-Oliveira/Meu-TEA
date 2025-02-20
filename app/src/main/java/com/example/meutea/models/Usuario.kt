package com.example.meutea.models

data class Usuario(
    val nome: String = "",
    val cpf: String = "",
    val rg: String = "",
    val nascimento: String = "",
    val naturalidade: String = "",
    val uf: String = "",
    val cid: String = "",
    val tipoSanguineo: String = "",
    val endereco: String = "",
    val telefone: String = "",
    val imageUrl: String = "" // URL da imagem (se você armazenar a imagem no Firebase Storage)
)