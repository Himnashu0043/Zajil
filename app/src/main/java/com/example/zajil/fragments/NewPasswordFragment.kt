package com.example.zajil.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.zajil.R
import com.example.zajil.databinding.FragmentNewPasswordBinding
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.showDialog
import com.example.zajil.util.Commons.showProgress
import com.example.zajil.util.Commons.showToast
import com.example.zajil.util.Constants
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success

class NewPasswordFragment : BaseFragment<FragmentNewPasswordBinding>() {

    private val inputNewPassword get() = binding.etNewPassword.text.trim().toString()
    private val inputConfirmPassword get() = binding.etConfirmPassword.text.trim().toString()

    private var phoneNumber: String = ""
    private var countryCode: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            phoneNumber = it.getString(Constants.PHONE_NUMBER, "") ?: ""
            countryCode = it.getString(Constants.COUNTRY_CODE, "") ?: ""
        }
    }

    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    override fun getLayout(): FragmentNewPasswordBinding {
        return FragmentNewPasswordBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setPasswordResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    dismissProgress()
                    if (it.response?.code?.isSuccessful() == true) {
                        requireContext().showDialog(
                            R.drawable.ic_tick,
                            "Congratulation",
                            "Your password has been updated successfully"
                        ) {
                            requireActivity().supportFragmentManager.apply {
                                popBackStack()
                            }
                            /*requireActivity().startActivity(
                                        Intent(
                                            requireActivity(),
                                            HomeActivity::class.java
                                        )
                                    )
                                    requireActivity().finish()*/
                        }
                    } else it.response?.message?.showToast(binding)
                }

                is Failure -> {
                    dismissProgress()
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                }
            }
        }
        binding.btnChange.setOnClickListener {
            when {
                inputNewPassword.isEmpty() -> "Please enter new password".showToast(binding)
                inputConfirmPassword.isEmpty() -> "Please enter confirm password".showToast(binding)
                inputNewPassword != inputConfirmPassword -> "New password and confirm password doesn't match".showToast(
                    binding
                )

                else -> {
                    showProgress(requireContext())
                    viewModel.setPassword("$countryCode$phoneNumber", inputNewPassword)
                }
            }
        }
    }

}