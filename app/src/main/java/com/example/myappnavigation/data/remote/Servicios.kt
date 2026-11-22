package com.example.myappnavigation.data.remote

import android.content.Context
import com.example.myappnavigation.data.database.AppDatabase
import com.example.myappnavigation.data.repository.ClimaRepository
import com.example.myappnavigation.data.repository.VuelosRepository
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Servicios {
    private lateinit var contexto: Context


    fun inicializar(context: Context) {
        contexto = context.applicationContext
    }

    private val cliente: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private fun retrofit(urlBase: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlBase)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val vuelosRepositorio: VuelosRepository by lazy {
        val base = AppDatabase.getDatabase(contexto)
        VuelosRepository(
            retrofit(ApiConfig.BACKEND_URL).create(VuelosApi::class.java),
            base.sesionDao(),
            base.vueloDao()
        )
    }

    val climaRepositorio: ClimaRepository by lazy {
        ClimaRepository(retrofit(ApiConfig.CLIMA_URL).create(ClimaApi::class.java))
    }
}
