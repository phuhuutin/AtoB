package com.example.atob

import android.app.Application
import com.example.atob.data.AppContainer
import com.example.atob.data.DefaultAppContainer

class AtoBApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}