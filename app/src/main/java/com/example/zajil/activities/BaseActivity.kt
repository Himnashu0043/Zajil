package com.example.zajil.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.zajil.util.App
import com.example.zajil.util.LocaleHelper.changeDirection
import com.example.zajil.util.LocaleHelper.setLocale

abstract class BaseActivity<viewBinding : ViewBinding> : AppCompatActivity() {


    private var _binding: viewBinding? = null
    val binding get() = _binding!!

    abstract fun getLayout(): viewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getLayout()
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        App.mRes = setLocale(App.preferenceManager.SELECTED_LANGUAGE).resources
        changeDirection(App.preferenceManager.SELECTED_LANGUAGE)
    }

}