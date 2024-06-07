package com.example.zajil.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.zajil.databinding.FragmentWithdrawMoneyBinding
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.showProgress
import com.example.zajil.util.Commons.showToast
import com.example.zajil.util.Constants
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success

class WithdrawMoneyFragment : BaseFragment<FragmentWithdrawMoneyBinding>() {

    private var walletMoney: Number = 0
    private val inputMoney get() = binding.etEnterAmount.text.trim().toString()
    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            walletMoney = it.get(Constants.WALLET_MONEY) as Number
        }
    }

    override fun getLayout(): FragmentWithdrawMoneyBinding {
        return FragmentWithdrawMoneyBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.withdrawMoneyResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    dismissProgress()
                    if (it.response?.code?.isSuccessful() == true) {
                        requireActivity().finish()
                    } else it.response?.message?.showToast(binding)
                }

                is Failure -> {
                    dismissProgress()
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                }
            }
        }

        binding.btnWithdraw.setOnClickListener {
            when {
                inputMoney.isEmpty() -> "Please enter amount.".showToast(binding)
                inputMoney.toInt() > walletMoney.toInt() -> "Don't have enough money to withdraw, Please try again.".showToast(
                    binding
                )

                else -> {
                    showProgress(requireContext())
                    viewModel.withdrawWallet(inputMoney.toInt())
                }
            }
        }
    }


}