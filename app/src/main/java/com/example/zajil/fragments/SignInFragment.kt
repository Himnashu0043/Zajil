package com.example.zajil.fragments

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.zajil.R
import com.example.zajil.activities.HomeActivity
import com.example.zajil.databinding.FragmentSignInBinding
import com.example.zajil.util.App
import com.example.zajil.util.Commons
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.showProgress
import com.example.zajil.util.Commons.showToast
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success

class SignInFragment : BaseFragment<FragmentSignInBinding>(), View.OnClickListener {


    private val inputCountryCode get() = binding.tvCountryCode.text.trim().toString()
    private val inputPhone get() = binding.etPhone.text.trim().toString()
    private val inputPassword get() = binding.etPassword.text.trim().toString()

    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    private val countryCodes = arrayOf("+91", "+966")
    override fun getLayout() = FragmentSignInBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Commons.setLightStatusBar(requireActivity())
        binding.tvForgotPassword.setOnClickListener(this)
        binding.btnSignIn.setOnClickListener(this)
        binding.tvCountryCode.setOnClickListener(this)
        binding.tvCountryCode.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.item_drop_down_country,
                countryCodes
            )
        )

        binding.tvCountryCode.setOnItemClickListener { _, _, i, _ ->
            if (i == 0) {
                binding.etPhone.filters = arrayOf(InputFilter.LengthFilter(10))
            } else {
                binding.etPhone.filters = arrayOf(InputFilter.LengthFilter(9))
            }
        }


        viewModel.loginResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    dismissProgress()
                    if (it.response?.code?.isSuccessful() == true) {
                        App.preferenceManager.token = it.response.token
                        App.preferenceManager.user = it.response.activeUser
                        App.token = it.response.token
                        startActivity(Intent(requireActivity(), HomeActivity::class.java).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                        requireActivity().finish()
                    } else
                        it.response?.message?.showToast(binding)
                }

                is Failure -> {
                    dismissProgress()
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                }
            }
        }


        binding.etPhone.doOnTextChanged { text, start, before, count ->
            if (text?.startsWith("0") == true)
                binding.etPhone.setText("")
        }


    }

    override fun onClick(p0: View?) {
        when (p0) {

            binding.tvCountryCode -> binding.tvCountryCode.showDropDown()
            binding.tvForgotPassword -> requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.franeLayout, ForgotPasswordFragment()).addToBackStack("").commit()

            binding.btnSignIn -> {
                when {
                    inputPhone.isEmpty() -> "Please enter phone".showToast(binding)
                    inputPhone.length < 5 -> "Please enter a valid password".showToast(binding)
                    inputPassword.isEmpty() -> "Please enter password".showToast(binding)
                    else -> {
                        showProgress(requireContext())
                        viewModel.login("$inputCountryCode$inputPhone", inputPassword)
                    }
                }

            }
        }

    }

}