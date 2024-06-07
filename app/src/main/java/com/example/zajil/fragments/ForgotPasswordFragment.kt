package com.example.zajil.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.zajil.R
import com.example.zajil.databinding.FragmentForgotPasswordBinding
import com.example.zajil.util.Commons
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.showToast
import com.example.zajil.util.Constants
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success


class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>(), View.OnClickListener {


    private val inputCountryCode get() = binding.tvCountryCode.text.trim().toString()
    private val inputPhone get() = binding.etPhone.text.trim().toString()

    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    override fun getLayout(): FragmentForgotPasswordBinding {
        return FragmentForgotPasswordBinding.inflate(layoutInflater)
    }

    private val countryCodes = arrayOf("+91", "+966")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSendOtp.setOnClickListener(this)
        binding.includedToolbar.llEnd.visibility = View.GONE
        binding.includedToolbar.ivBack.setOnClickListener(this)
        binding.tvCountryCode.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.item_drop_down_country,
                countryCodes
            )
        )
        binding.tvCountryCode.setOnClickListener(this)
        viewModel.checkResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    dismissProgress()
                    if (it.response?.code?.isSuccessful() == true) {
                        requireActivity().supportFragmentManager.popBackStack()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.franeLayout, PhoneVerificationFragment().apply {
                                arguments = Bundle().apply {
                                    putString(Constants.PHONE_NUMBER, inputPhone)
                                    putString(Constants.COUNTRY_CODE, inputCountryCode)
                                }
                            })
                            .addToBackStack("")
                            .commit()

                    } else it.response?.message?.showToast(binding)
                }

                is Failure -> {
                    dismissProgress()
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                }
            }
        }
    }

    override fun onClick(p0: View?) {

        when (p0) {

            binding.tvCountryCode -> binding.tvCountryCode.showDropDown()

            binding.btnSendOtp -> {
                when {
                    inputPhone.isEmpty() -> "Please enter phone".showToast(binding)
                    inputPhone.length < 5 -> "Please enter a valid password".showToast(binding)
                    else -> {
                        Commons.showProgress(requireContext())
                        viewModel.checkUser("$inputCountryCode$inputPhone")
                    }
                }
            }

            binding.includedToolbar.ivBack -> requireActivity().onBackPressed()

        }

    }

}