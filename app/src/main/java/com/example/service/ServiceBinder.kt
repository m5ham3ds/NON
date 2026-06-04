package com.example.service

import android.os.Binder

class ServiceBinder(private val service: TestService) : Binder() {
    fun getService(): TestService = service
}
