package com.example.meutea.models

data class Usuario(
    val nome: String = "",
    val cpf: String? = null,
    val rg: String? = null,
    val nascimento: String? = null,
    val tipoSanguineo: String = "",
    val endereco: String = "",
    val telefone: String? = null,
    val estadoCivil: String = "",
    val email: String = "",
    val naturalidade: String = "",
    val cid: String = "",
    val genero: String = "",
    val estado: String = "",
    // Informações opcionais de apoio para identificação pessoal — não oficial.
    val responsavelLegal: String = "",
    val contatoEmergencia: String = "",
    val preferenciaComunicacao: String = "",
    val observacoesApoio: String = ""
)
