package com.nammakelsa

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class NammaKelsaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            Log.d("NammaKelsaApp", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("NammaKelsaApp", "Firebase initialization failed", e)
        }
    }
}
