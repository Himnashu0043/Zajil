package com.example.zajil.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.zajil.activities.CommonActivity
import com.example.zajil.adapters.TransactionAdapter
import com.example.zajil.databinding.FragmentWalletBinding
import com.example.zajil.util.App
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.showToast
import com.example.zajil.util.Constants
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success

class WalletFragment : BaseFragment<FragmentWalletBinding>() {
    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    private lateinit var adapter: TransactionAdapter
    private var walletMoney: Number = 0
    override fun getLayout(): FragmentWalletBinding {
        return FragmentWalletBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.walletTransactionResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    dismissProgress()
                    if (it.response?.code?.isSuccessful() == true) {
                        adapter.set(it.response.result1)
                        walletMoney = it.response.walletMoney
                        binding.tvTotalEarning.text = "SAR ${it.response.walletMoney}"
                        binding.tvTotalDeliveries.text = "SAR ${it.response.totalRide}"
                        binding.tvAvailableBalance.text = "SAR ${it.response.remainingBallance}"
                    } else it.response?.message?.showToast(binding)
                    binding.swipeRefreshLayout.isRefreshing = false
                }

                is Failure -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    dismissProgress()
                }
            }
        }

        adapter = TransactionAdapter()
        binding.rvTransaction.adapter = adapter

        binding.swipeRefreshLayout.isRefreshing = true


        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getWalletTransaction()
        }

        binding.btnWithdraw.setOnClickListener {
            startActivity(Intent(requireContext(), CommonActivity::class.java).apply {
                putExtra(Constants.FRAGMENT_NAME, WithdrawMoneyFragment::class.java.simpleName)
                putExtra(Constants.WALLET_MONEY, walletMoney)
            })
        }

        if (App.preferenceManager.user?.riderType?.equals("Freelancer", true) == true) {
            binding.btnWithdraw.visibility = View.VISIBLE
        } else {
            binding.btnWithdraw.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefreshLayout.isRefreshing = true
        viewModel.getWalletTransaction()
    }

}