package com.example.zajil.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.zajil.activities.CommonActivity
import com.example.zajil.adapters.OrdersAdapter
import com.example.zajil.databinding.FragmentSubOrdersBinding
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.showToast
import com.example.zajil.util.Constants
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success
import com.google.gson.Gson


class SubOrderFragment private constructor() : BaseFragment<FragmentSubOrdersBinding>() {

    private var isActive: Boolean = false
    private lateinit var adapter: OrdersAdapter

    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isActive = it.getBoolean("isActive", false)
        }
    }

    override fun getLayout(): FragmentSubOrdersBinding {
        return FragmentSubOrdersBinding.inflate(layoutInflater)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OrdersAdapter(isActive) {
            startActivity(Intent(requireActivity(), CommonActivity::class.java).apply {
                putExtra(Constants.FRAGMENT_NAME, ShipmentDetailFragment::class.java.simpleName)
                putExtra(Constants.DATA,Gson().toJson(adapter.get(it)))
            })
        }
        binding.rvRecycler.adapter = adapter

        if (isActive) {
            viewModel.getActiveOrderResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is Success -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        dismissProgress()
                        if (it.response?.code?.isSuccessful() == true)
                            adapter.set(it.response.result1)
                        else it.response?.message?.showToast()
                    }

                    is Failure -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        it.error?.getError(requireContext())?.message?.showToast(binding)
                        dismissProgress()
                    }
                }
            }
            viewModel.getActiveOrders()
        } else {
            viewModel.getCompletedOrderResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is Success -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        dismissProgress()
                        if (it.response?.code?.isSuccessful() == true)
                            adapter.set(it.response.result1)
                        else it.response?.message?.showToast()
                    }

                    is Failure -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        it.error?.getError(requireContext())?.message?.showToast(binding)
                        dismissProgress()
                    }

                }
            }
            viewModel.getCompletedOrders()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (isActive) {
                viewModel.getActiveOrders()
            } else {
                viewModel.getCompletedOrders()
            }
        }

        binding.swipeRefreshLayout.isRefreshing = true
    }

    override fun onResume() {
        super.onResume()
        if (isActive) {
            viewModel.getActiveOrders()
        } else {
            viewModel.getCompletedOrders()
        }
        binding.swipeRefreshLayout.isRefreshing = true
    }

    companion object {
        fun get(isActive: Boolean): SubOrderFragment {
            return SubOrderFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isActive", isActive)
                }
            }
        }
    }

}