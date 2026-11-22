package com.example.myappnavigation

import android.app.Application
import com.example.myappnavigation.data.remote.Servicios

class CapebeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Servicios.inicializar(this)
    }
}
