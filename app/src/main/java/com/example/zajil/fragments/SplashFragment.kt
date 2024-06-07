package com.example.zajil.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.zajil.R
import com.example.zajil.activities.HomeActivity
import com.example.zajil.databinding.FragmentSplashBinding
import com.example.zajil.util.App

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override fun getLayout(): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            if (App.preferenceManager.token.isNotBlank()) {
                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finish()
            } else {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.franeLayout, LanguageFragment()).commit()
            }
        }, 1500) // 2000 is the delayed time in milliseconds.

    }

}