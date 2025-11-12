package com.fredrueda.huecoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HueApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //com.facebook.FacebookSdk.sdkInitialize(this)
        //com.facebook.appevents.AppEventsLogger.activateApp(this)
    }
}
