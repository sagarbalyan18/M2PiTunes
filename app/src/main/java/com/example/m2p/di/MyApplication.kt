package com.example.m2p.di

import android.app.Application
import android.os.StrictMode
import com.example.m2p.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog().penaltyDialog().penaltyFlashScreen().build())
        }
    }

}