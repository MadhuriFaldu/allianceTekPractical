package com.example.alliancetekpractical

import android.app.Application
import com.google.firebase.FirebaseApp

lateinit var instanceApp: MyApplication

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instanceApp = this
        FirebaseApp.initializeApp(this)
    }
}