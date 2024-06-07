package com.example.zajil.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.lifecycle.ViewModelProvider
import com.example.zajil.R
import com.example.zajil.activities.CommonActivity
import com.example.zajil.activities.SplashActivity
import com.example.zajil.databinding.DialogLogoutBinding
import com.example.zajil.databinding.FragmentSettingsBinding
import com.example.zajil.util.App
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.showLogoutDialog
import com.example.zajil.util.Commons.showProgress
import com.example.zajil.util.Commons.showToast
import com.example.zajil.util.Constants
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(), View.OnClickListener {

    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    override fun getLayout(): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clLanguage.setOnClickListener(this)
        binding.clChangePass.setOnClickListener(this)
        binding.clLogout.setOnClickListener(this)


        viewModel.pushNotificationStatusResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    dismissProgress()
                    if (it.response?.code?.isSuccessful() == true) {

                    } else it.response?.message?.showToast(binding)
                }

                is Failure -> {
                    dismissProgress()
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                }
            }
        }

        binding.switchPushEnable.setOnCheckedChangeListener { _, isChecked ->
            viewModel.pushNotificationStatus(isChecked)
        }
        binding.switchPushEnable.isChecked = App.preferenceManager.user?.is_notification ?: false

    }

    private fun showLogoutDialog() {
        Dialog(requireContext(), R.style.dialog_style).apply {
            DialogLogoutBinding.inflate(layoutInflater).let {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(false)
                setCancelable(false)
                setContentView(it.root)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                it.tvYes.setOnClickListener {
                    showProgress(requireContext())
                    viewModel.logout {
                        dismissProgress()
                        dismiss()
                        App.preferenceManager.logout()
                        requireActivity().finishAffinity()
                        startActivity(Intent(requireActivity(), SplashActivity::class.java).apply {
                            putExtra("fromLogout", true)
                        })
                    }
                }
                it.tvNo.setOnClickListener {
                    dismiss()
                }
                show()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.clLogout -> showLogoutDialog()

            binding.clChangePass -> {
                startActivity(Intent(requireActivity(), CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, ChangePasswordFragment::class.java.simpleName)
                })
            }

            binding.clLanguage -> {
                startActivity(Intent(requireActivity(), CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, SelectLanguageFragment::class.java.simpleName)
                })
            }
        }
    }
}