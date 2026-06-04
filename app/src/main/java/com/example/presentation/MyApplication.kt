package com.example.presentation

import android.app.Application
import com.example.di.appModule
import com.example.di.viewModelModule
import com.example.util.AppLogger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Timber logging
        Timber.plant(Timber.DebugTree())
        Timber.d("MyApplication onCreate called")

        // App Logger
        AppLogger.init(this)

        // Koin DI Initialize
        startKoin {
            androidContext(this@MyApplication)
            modules(listOf(appModule, viewModelModule))
        }
    }
}
