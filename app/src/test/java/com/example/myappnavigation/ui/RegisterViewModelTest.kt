package com.example.myappnavigation.ui

import com.example.myappnavigation.Datos
import com.example.myappnavigation.MainDispatcherRule
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.data.repository.VuelosRepository
import com.example.myappnavigation.model.RegisterModel
import com.example.myappnavigation.ui.viewmodel.RegisterViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    private val repo = mockk<VuelosRepository>()
    private val vm = RegisterViewModel(repo)

    private fun llenarFormularioValido() {
        vm.onNameChanged("Ana Pérez")
        vm.onEmailChanged("ana@correo.cl")
        vm.onPasswordChanged("clave123")
        vm.onConfirmPasswordChanged("clave123")
    }

    @Test fun formulario_valido_solo_con_todos_los_campos_correctos() {
        assertFalse(vm.formularioValido)
        llenarFormularioValido()
        assertTrue(vm.formularioValido)
    }

    @Test fun validaciones_en_vivo() {
        vm.onNameChanged("Ana")
        assertNotNull(vm.uiState.nameError)
        vm.onEmailChanged("x")
        assertNotNull(vm.uiState.emailError)
        vm.onPasswordChanged("abc")
        assertNotNull(vm.uiState.passwordError)
        vm.onConfirmPasswordChanged("otra")
        assertNotNull(vm.uiState.confirmError)

        vm.onNameChanged("")
        vm.onEmailChanged("")
        vm.onConfirmPasswordChanged("")
        assertNull(vm.uiState.nameError)
        assertNull(vm.uiState.emailError)
        assertNull(vm.uiState.confirmError)
    }

    @Test fun cambiar_la_contrasena_revalida_la_confirmacion() {
        vm.onPasswordChanged("clave123")
        vm.onConfirmPasswordChanged("clave123")
        assertNull(vm.uiState.confirmError)

        vm.onPasswordChanged("clave1234")

        assertEquals("Las contraseñas no coinciden.", vm.uiState.confirmError)
    }

    @Test fun registro_con_errores_no_llama_al_servidor() {
        var llamado = false

        vm.onRegisterClick { llamado = true }

        assertFalse(llamado)
        assertEquals("Revisa los campos marcados.", vm.uiState.errorMessage)
        coVerify(exactly = 0) { repo.register(any(), any(), any()) }
    }

    @Test fun registro_exitoso_limpia_el_formulario_y_avisa() {
        coEvery { repo.register("Ana Pérez", "ana@correo.cl", "clave123") } returns Resultado.Exito(Datos.socio)
        llenarFormularioValido()
        var exito = false

        vm.onRegisterClick { exito = true }

        assertTrue(exito)
        assertEquals(RegisterModel(), vm.uiState)
        assertFalse(vm.cargando)
    }

    @Test fun correo_duplicado_marca_el_campo_email() {
        coEvery { repo.register(any(), any(), any()) } returns Resultado.Error(
            "El correo ya está registrado.",
            mapOf("email" to "Este correo ya está registrado."),
            409
        )
        llenarFormularioValido()
        var exito = false

        vm.onRegisterClick { exito = true }

        assertFalse(exito)
        assertEquals("Este correo ya está registrado.", vm.uiState.emailError)
        assertEquals("El correo ya está registrado.", vm.uiState.errorMessage)
        assertEquals("Ana Pérez", vm.uiState.name) // el formulario conserva lo escrito
    }
}
