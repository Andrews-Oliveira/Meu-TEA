package com.example.meutea.menu.rotinas

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RotinasValidacaoTest {
    @Test fun aceitaHorariosValidos() {
        assertTrue(horarioValido("00:00"))
        assertTrue(horarioValido("08:30"))
        assertTrue(horarioValido("23:59"))
    }

    @Test fun rejeitaHorariosInvalidos() {
        assertFalse(horarioValido(""))
        assertFalse(horarioValido("7:00"))
        assertFalse(horarioValido("24:00"))
        assertFalse(horarioValido("12:60"))
        assertFalse(horarioValido("abcde"))
        assertFalse(horarioValido("18:3"))
    }
}
