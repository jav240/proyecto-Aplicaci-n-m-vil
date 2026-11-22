package com.example.myappnavigation.data.remote

import com.example.myappnavigation.model.VueloModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query


interface VuelosApi {

    @POST("auth/register")
    suspend fun register(@Body body: RegistroRequest): Response<SesionApi>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<SesionApi>

    @GET("vuelos")
    suspend fun vuelos(
        @Query("fecha") fecha: String?,
        @Query("disponibles") disponibles: String?
    ): Response<List<VueloModel>>

    @GET("reserva")
    suspend fun reserva(@Header("Authorization") auth: String): Response<ReservaRespuesta>

    @PUT("reserva")
    suspend fun aceptar(
        @Header("Authorization") auth: String,
        @Body body: AceptarRequest
    ): Response<ReservaRespuesta>

    @PUT("reserva/consentimiento")
    suspend fun consentimiento(
        @Header("Authorization") auth: String,
        @Body body: ConsentimientoRequest
    ): Response<ReservaRespuesta>

    @DELETE("reserva")
    suspend fun cancelar(@Header("Authorization") auth: String): Response<Unit>

    @GET("admin/socios")
    suspend fun socios(@Header("Authorization") auth: String): Response<List<SocioApi>>

    @GET("admin/reservas")
    suspend fun reservasAdmin(@Header("Authorization") auth: String): Response<List<ReservaAdminApi>>
}
