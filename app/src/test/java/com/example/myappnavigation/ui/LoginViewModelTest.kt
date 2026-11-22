package com.example.myappnavigation.ui

import com.example.myappnavigation.Datos
import com.example.myappnavigation.MainDispatcherRule
import com.example.myappnavigation.data.remote.UsuarioApi
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.data.repository.VuelosRepository
import com.example.myappnavigation.ui.viewmodel.LoginViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    private val repo = mockk<VuelosRepository>()
    private val vm = LoginViewModel(repo)

    @Test fun validacion_en_vivo_de_los_campos() {
        vm.onEmailChanged("ana@")
        assertNotNull(vm.uiState.emailError)
        vm.onEmailChanged("ana@correo.cl")
        assertNull(vm.uiState.emailError)
        vm.onEmailChanged("")
        assertNull(vm.uiState.emailError)

        vm.onPasswordChanged("abc")
        assertNotNull(vm.uiState.passwordError)
        vm.onPasswordChanged("clave123")
        assertNull(vm.uiState.passwordError)
    }

    @Test fun con_campos_invalidos_no_llama_al_servidor() {
        vm.onEmailChanged("malo")
        vm.onPasswordChanged("1")
        var llamado = false

        vm.onLoginClick { llamado = true }

        assertFalse(llamado)
        assertEquals("Revisa los campos marcados.", vm.errorMessage)
        coVerify(exactly = 0) { repo.login(any(), any()) }
    }

    @Test fun login_exitoso_entrega_el_usuario() {
        coEvery { repo.login("ana@correo.cl", "clave123") } returns Resultado.Exito(Datos.socio)
        vm.onEmailChanged(" Ana@Correo.cl ")
        vm.onPasswordChanged("clave123")
        var recibido: UsuarioApi? = null

        vm.onLoginClick { recibido = it }

        assertEquals(Datos.socio, recibido)
        assertNull(vm.errorMessage)
        assertFalse(vm.cargando)
    }

    @Test fun login_fallido_muestra_el_mensaje_del_servidor() {
        coEvery { repo.login(any(), any()) } returns Resultado.Error("Correo o contraseña incorrectos.", codigo = 401)
        vm.onEmailChanged("ana@correo.cl")
        vm.onPasswordChanged("clave123")
        var llamado = false

        vm.onLoginClick { llamado = true }

        assertFalse(llamado)
        assertEquals("Correo o contraseña incorrectos.", vm.errorMessage)
        assertFalse(vm.cargando)
    }

    @Test fun escribir_limpia_el_mensaje_de_error() {
        coEvery { repo.login(any(), any()) } returns Resultado.Error("mal")
        vm.onEmailChanged("ana@correo.cl")
        vm.onPasswordChanged("clave123")
        vm.onLoginClick { }
        assertNotNull(vm.errorMessage)

        vm.onPasswordChanged("clave1234")

        assertNull(vm.errorMessage)
    }
}
