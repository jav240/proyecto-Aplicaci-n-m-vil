package com.example.myappnavigation.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {

    @Test fun nombre_vacio_corto_o_sin_apellido_es_invalido() {
        assertNotNull(Validaciones.nombre(""))
        assertNotNull(Validaciones.nombre("Ab"))
        assertEquals("Ingresa nombre y apellido.", Validaciones.nombre("Ana"))
        assertNull(Validaciones.nombre("Ana Pérez"))
    }

    @Test fun email_valida_formato() {
        assertEquals("Ingresa tu correo.", Validaciones.email(" "))
        assertEquals("El correo no es válido.", Validaciones.email("ana@"))
        assertNull(Validaciones.email("ana@correo.cl"))
        assertNull(Validaciones.email("  Ana@Correo.cl  "))
    }

    @Test fun password_exige_largo_y_numero() {
        assertEquals("Ingresa una contraseña.", Validaciones.password(""))
        assertEquals("Debe tener al menos 6 caracteres.", Validaciones.password("ab1"))
        assertEquals("Debe incluir al menos un número.", Validaciones.password("abcdefg"))
        assertNull(Validaciones.password("clave123"))
    }

    @Test fun confirmar_compara_contrasenas() {
        assertEquals("Repite la contraseña.", Validaciones.confirmar("clave123", ""))
        assertEquals("Las contraseñas no coinciden.", Validaciones.confirmar("clave123", "otra123"))
        assertNull(Validaciones.confirmar("clave123", "clave123"))
    }
}
