package com.example.zajil.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.zajil.activities.HomeActivity
import com.example.zajil.databinding.FragmentSelectLanguageBinding
import com.example.zajil.util.App
import com.example.zajil.util.LocaleHelper.setLocale

class SelectLanguageFragment : BaseFragment<FragmentSelectLanguageBinding>(), View.OnClickListener {

    override fun getLayout(): FragmentSelectLanguageBinding {
        return FragmentSelectLanguageBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rbEnglish.setOnClickListener(this)
        binding.clEnglish.setOnClickListener(this)
        binding.rbArabic.setOnClickListener(this)
        binding.clArabic.setOnClickListener(this)

        when (App.preferenceManager.SELECTED_LANGUAGE) {
            "en" -> {
                binding.rbEnglish.isChecked = true
                binding.rbArabic.isChecked = false
            }

            else -> {
                binding.rbEnglish.isChecked = false
                binding.rbArabic.isChecked = true
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.clArabic,
            binding.rbArabic -> {
                if (App.preferenceManager.SELECTED_LANGUAGE == "en") {
                    binding.rbEnglish.isChecked = false
                    binding.rbArabic.isChecked = true
                    requireContext().setLocale("ur")
                    requireActivity().window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                    requireActivity().finishAffinity()
                    startActivity(Intent(requireActivity(), HomeActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }
            }

            binding.clEnglish,
            binding.rbEnglish -> {
                if (App.preferenceManager.SELECTED_LANGUAGE == "ur") {
                    binding.rbEnglish.isChecked = true
                    binding.rbArabic.isChecked = false
                    requireContext().setLocale("en")
                    requireActivity().window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
                    requireActivity().finishAffinity()
                    startActivity(Intent(requireActivity(), HomeActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }
            }
        }
    }
}
