package com.beaconinc.roarhousing

import android.app.Application
import timber.log.Timber

class HousingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}