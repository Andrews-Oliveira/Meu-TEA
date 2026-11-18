package com.example.meutea.menu.careiradigital

import com.example.meutea.models.Usuario
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CarteirinhaPremiumTest {
    @Test
    fun resumoNaoIncluiDocumentosOuDadosClinicos() {
        val pessoa = Usuario(
            nome = "Pessoa de Teste",
            cpf = "99988877766",
            rg = "11223344",
            cid = "CODIGO_TESTE",
            endereco = "Rua Exemplo 123",
            telefone = "11112222333",
            tipoSanguineo = "O+",
            preferenciaComunicacao = "Prefiro frases curtas",
            contatoEmergencia = "Contato autorizado 55555"
        )
        val resumo = resumoSeguroCarteira(pessoa)
        assertTrue(resumo.contains("Pessoa de Teste"))
        assertTrue(resumo.contains("Prefiro frases curtas"))
        assertTrue(resumo.contains("Contato autorizado 55555"))
        assertTrue(resumo.contains("não oficial"))
        listOf("99988877766", "11223344", "CODIGO_TESTE",
            "Rua Exemplo 123", "11112222333", "O+"
        ).forEach { assertFalse("O resumo vazou um dado privado", resumo.contains(it)) }
    }

    @Test
    fun resumoFuncionaSemDadosDeApoio() {
        val resumo = resumoSeguroCarteira(Usuario(nome = "Visitante"))
        assertTrue(resumo.contains("Visitante"))
        assertFalse(resumo.contains("Contato de emergência:"))
        assertFalse(resumo.contains("Comunicação preferida:"))
    }

    @Test
    fun compartilhamentoNaoAlegaValidacaoOficial() {
        val resumo = resumoSeguroCarteira(Usuario(nome = "Pessoa"))
        assertTrue(resumo.contains("não substitui a CIPTEA"))
        assertFalse(resumo.contains("Documento válido"))
    }
}
