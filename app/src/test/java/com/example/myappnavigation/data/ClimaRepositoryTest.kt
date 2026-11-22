package com.example.myappnavigation.data

import com.example.myappnavigation.data.remote.ClimaActual
import com.example.myappnavigation.data.remote.ClimaApi
import com.example.myappnavigation.data.remote.ClimaRespuesta
import com.example.myappnavigation.data.repository.ClimaRepository
import com.example.myappnavigation.data.repository.Resultado
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class ClimaRepositoryTest {

    private val api = mockk<ClimaApi>()
    private val repo = ClimaRepository(api)
    private val clima = ClimaActual(18.5, 12.0, 0, "2026-11-07T10:00")

    @Test fun clima_de_base_conocida() = runTest {
        coEvery { api.actual(any(), any(), any()) } returns Response.success(ClimaRespuesta(clima))

        assertEquals(Resultado.Exito(clima), repo.climaDe("Tobalaba"))
    }

    @Test fun base_desconocida_no_llama_a_la_api() = runTest {
        val r = repo.climaDe("Marte") as Resultado.Error

        assertTrue(r.mensaje.contains("Marte"))
        coVerify(exactly = 0) { api.actual(any(), any(), any()) }
    }

    @Test fun respuesta_sin_datos_actuales_es_error() = runTest {
        coEvery { api.actual(any(), any(), any()) } returns Response.success(ClimaRespuesta(null))

        assertTrue(repo.climaDe("Talcahuano") is Resultado.Error)
    }

    @Test fun error_http_de_la_api_externa() = runTest {
        coEvery { api.actual(any(), any(), any()) } returns
            Response.error(500, "x".toResponseBody("text/plain".toMediaType()))

        val r = repo.climaDe("Tobalaba") as Resultado.Error

        assertEquals(500, r.codigo)
    }

    @Test fun falla_de_red() = runTest {
        coEvery { api.actual(any(), any(), any()) } throws IOException("sin red")

        assertTrue(repo.climaDe("Tobalaba") is Resultado.Error)
    }
}
