package com.example.zajil.fragments

import android.os.Bundle
import android.view.View
import com.example.zajil.R
import com.example.zajil.databinding.FragmentLanguageBinding

class LanguageFragment : BaseFragment<FragmentLanguageBinding>(), View.OnClickListener {

    override fun getLayout(): FragmentLanguageBinding {
        return FragmentLanguageBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnEnglish.setOnClickListener(this)
        binding.btnUrdu.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {

        when (p0) {
            binding.btnEnglish -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.franeLayout, SignInIntroFragment()).commit()
            }

            binding.btnUrdu -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.franeLayout, SignInIntroFragment()).commit()
            }
        }

    }

}