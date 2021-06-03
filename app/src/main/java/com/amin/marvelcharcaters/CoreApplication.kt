package com.amin.marvelcharcaters

import android.app.Application
import com.amin.marvelcharcaters.utils.TimberFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        TimberFactory.setupOnDebug()
    }
}