package com.example.zajil.fragments

import android.os.Bundle
import android.view.View
import com.example.zajil.R
import com.example.zajil.databinding.FragmentSignInIntroBinding
import com.example.zajil.util.Commons

class SignInIntroFragment : BaseFragment<FragmentSignInIntroBinding>(), View.OnClickListener {

    override fun getLayout(): FragmentSignInIntroBinding {
        return FragmentSignInIntroBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignIn.setOnClickListener(this)
        Commons.setLightStatusBar(requireActivity())
    }

    override fun onClick(p0: View?) {

        when (p0) {
            binding.btnSignIn -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.franeLayout, SignInFragment()).addToBackStack("").commit()
            }
        }

    }

}