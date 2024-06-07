package com.example.zajil.util

import android.app.Application
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth


class App : Application() {


    companion object {
        lateinit var preferenceManager: PreferenceManager
        lateinit var instance: Application
        var token: String = ""
        var LAT: Double = 0.0
        var LONG: Double = 0.0
        lateinit var mAuth: FirebaseAuth
        lateinit var mRes: Resources
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferenceManager = PreferenceManager(this)
        mRes = resources
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        token = preferenceManager.token
        LAT = preferenceManager.LATITUDE
        LONG = preferenceManager.LONGITUDE

    }
}