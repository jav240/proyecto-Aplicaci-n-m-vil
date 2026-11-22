package com.example.myappnavigation.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ClimaApi {

    @GET("v1/forecast")
    suspend fun actual(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") variables: String
    ): Response<ClimaRespuesta>
}
