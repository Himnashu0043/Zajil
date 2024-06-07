package com.example.zajil.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.zajil.R
import com.example.zajil.databinding.ActivityCommonBinding
import com.example.zajil.fragments.AboutUsFragment
import com.example.zajil.fragments.ChangePasswordFragment
import com.example.zajil.fragments.ContactUsFragment
import com.example.zajil.fragments.HelpSupportFragment
import com.example.zajil.fragments.NotificationsFragment
import com.example.zajil.fragments.PrivacyPolicyFragment
import com.example.zajil.fragments.SelectLanguageFragment
import com.example.zajil.fragments.SettingsFragment
import com.example.zajil.fragments.ShipmentDetailFragment
import com.example.zajil.fragments.TermsAndConditionsFragment
import com.example.zajil.fragments.WebViewFragment
import com.example.zajil.fragments.WithdrawMoneyFragment
import com.example.zajil.util.App
import com.example.zajil.util.Commons
import com.example.zajil.util.Constants
import com.example.zajil.util.LocaleHelper.changeDirection

class CommonActivity : BaseActivity<ActivityCommonBinding>(), View.OnClickListener {

    private var fragment: Fragment? = null
    override fun getLayout(): ActivityCommonBinding {
        return ActivityCommonBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Commons.setTransparentStatusBarOnly(this)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        if (intent.hasExtra(Constants.FRAGMENT_NAME)) {

            when (intent.getStringExtra(Constants.FRAGMENT_NAME)) {

                NotificationsFragment::class.java.simpleName -> {
                    binding.includedToolbar.ivNotification.visibility = View.GONE
                    fragment = NotificationsFragment()
                }

                WithdrawMoneyFragment::class.java.simpleName -> {
                    binding.includedToolbar.ivNotification.visibility = View.GONE
                    binding.includedToolbar.ivLanguage.visibility = View.GONE
                    fragment = WithdrawMoneyFragment()
                }

                SelectLanguageFragment::class.java.simpleName -> {
                    fragment = SelectLanguageFragment()
                }

                ChangePasswordFragment::class.java.simpleName -> {
                    binding.includedToolbar.llEnd.visibility = View.GONE
                    fragment = ChangePasswordFragment()
                }

                SettingsFragment::class.java.simpleName -> {
                    fragment = SettingsFragment()
                }

                AboutUsFragment::class.java.simpleName,
                TermsAndConditionsFragment::class.java.simpleName,
                PrivacyPolicyFragment::class.java.simpleName -> {
                    fragment = WebViewFragment()
                    binding.includedToolbar.root.visibility = View.GONE
                }


                ContactUsFragment::class.java.simpleName -> {
                    fragment = ContactUsFragment()
                }

                HelpSupportFragment::class.java.simpleName -> {
                    fragment = HelpSupportFragment()
                }

                ShipmentDetailFragment::class.java.simpleName -> {
                    fragment = ShipmentDetailFragment()
                }

            }

            fragment?.arguments = intent.extras
        }

        if (fragment != null) {
            supportFragmentManager.beginTransaction().add(R.id.frameCommon, fragment!!).commit()
        }

        binding.includedToolbar.ivBack.setOnClickListener(this)
        binding.includedToolbar.ivNotification.setOnClickListener(this)
        binding.includedToolbar.ivLanguage.setOnClickListener(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("HomeActivity", "onConfigurationChanged: ${newConfig.locales}")
    }


    override fun onResume() {
        super.onResume()
        changeDirection(App.preferenceManager.SELECTED_LANGUAGE)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.includedToolbar.ivBack -> finish()
            binding.includedToolbar.ivNotification -> {
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, NotificationsFragment::class.java.simpleName)
                })
            }

            binding.includedToolbar.ivLanguage -> {
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, SelectLanguageFragment::class.java.simpleName)
                })
            }
        }
    }
}