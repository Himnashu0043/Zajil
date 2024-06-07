package com.example.zajil.activities

import com.example.zajil.databinding.ActivityRegistrationBinding

class RegistrationActivity : BaseActivity<ActivityRegistrationBinding>() {
    override fun getLayout(): ActivityRegistrationBinding {
        return ActivityRegistrationBinding.inflate(layoutInflater)
    }
}