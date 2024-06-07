package com.example.zajil.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.zajil.R
import com.example.zajil.databinding.FragmentChangePasswordBinding
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.showDialog
import com.example.zajil.util.Commons.showProgress
import com.example.zajil.util.Commons.showToast
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success

class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {

    private val inputOldPassword get() = binding.etOldPassword.text.trim().toString()
    private val inputNewPassword get() = binding.etNewPassword.text.trim().toString()
    private val inputConfirmPassword get() = binding.etConfirmPassword.text.trim().toString()

    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    override fun getLayout(): FragmentChangePasswordBinding {
        return FragmentChangePasswordBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.changePasswordResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    dismissProgress()
                    if (it.response?.code?.isSuccessful() == true) {
                        requireContext().showDialog(
                            R.drawable.ic_tick,
                            getString(R.string.congratulation),
                            getString(R.string.your_password_has_been_updated_successfully)
                        ) {
                            requireActivity().finish()
                        }
                    } else it.response?.message?.showToast(binding)
                }

                is Failure -> {
                    dismissProgress()
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                }
            }
        }

        binding.btnChangePassword.setOnClickListener {
            when {
                inputOldPassword.isEmpty() -> getString(R.string.please_enter_old_password).showToast(
                    binding
                )

                inputNewPassword.isEmpty() -> getString(R.string.please_enter_new_password).showToast(
                    binding
                )

                inputConfirmPassword.isEmpty() -> getString(R.string.please_enter_confirm_password).showToast(
                    binding
                )

                inputNewPassword != inputConfirmPassword -> getString(R.string.password_doesnt_match).showToast(
                    binding
                )

                else -> {
                    showProgress(requireContext())
                    viewModel.changePassword(inputOldPassword, inputNewPassword)
                }
            }
        }

    }
}