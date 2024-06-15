package com.clara.timekeeping

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TimeTicketApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}