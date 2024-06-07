package com.example.zajil.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.example.zajil.R
import com.example.zajil.databinding.ActivitySplashBinding
import com.example.zajil.fragments.SignInFragment
import com.example.zajil.fragments.SplashFragment
import com.example.zajil.util.App
import com.example.zajil.util.Commons
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override fun getLayout(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Commons.setTransparentStatusBarOnly(this)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            App.preferenceManager.deviceToken = it
        }

        Log.d("AccessToken", "onCreate: ${App.preferenceManager.token}")
        if (intent.hasExtra("fromLogout")) {
            supportFragmentManager.beginTransaction().add(R.id.franeLayout, SignInFragment())
                .commit()
        } else
            supportFragmentManager.beginTransaction().add(R.id.franeLayout, SplashFragment())
                .commit()
    }

}