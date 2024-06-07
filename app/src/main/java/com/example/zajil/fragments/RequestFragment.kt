package com.example.zajil.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.zajil.BaseAdapter
import com.example.zajil.activities.CommonActivity
import com.example.zajil.activities.HomeActivity
import com.example.zajil.adapters.RequestAdapter
import com.example.zajil.databinding.FragmentRequestBinding
import com.example.zajil.databinding.ItemRequestBinding
import com.example.zajil.util.App
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.showProgress
import com.example.zajil.util.Commons.showReasonDialog
import com.example.zajil.util.Commons.showToast
import com.example.zajil.util.Constants
import com.example.zajil.util.RideRequests
import com.example.zajil.util.RideStatus
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success
import com.google.gson.Gson
import kotlinx.coroutines.handleCoroutineException

class RequestFragment : BaseFragment<FragmentRequestBinding>() {

    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }
    private lateinit var adapter: RequestAdapter
    override fun getLayout(): FragmentRequestBinding {
        return FragmentRequestBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RequestAdapter { key, position, rejectReason ->
            when (key) {
                "Detail" -> {
                    startActivity(Intent(requireActivity(), CommonActivity::class.java).apply {
                        putExtra(
                            Constants.FRAGMENT_NAME,
                            ShipmentDetailFragment::class.java.simpleName
                        )
                        putExtra(Constants.DATA, Gson().toJson(adapter.get(position)))
                    })
                }

                "Accept" -> {
                    showProgress(requireContext())
                    viewModel.acceptRejectRequest(
                        adapter.get(position)?._id ?: "",
                        true
                    ) {
                        dismissProgress()
                        adapter.removeAt(position)
                        try {
                            (requireActivity() as HomeActivity).apply {
                                binding.tabLayoutHome.selectTab(
                                    binding.tabLayoutHome.getTabAt(2),
                                    true
                                )
                            }
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    }
                }

                "Reject" -> {
                    showProgress(requireContext())
                    viewModel.acceptRejectRequest(
                        adapter.get(position)?._id ?: "",
                        false, rejectReason
                    ) {
                        dismissProgress()
                        adapter.removeAt(position)
                    }
                }
            }
        }

        binding.rvRequests.adapter = adapter

        viewModel.requestListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    dismissProgress()
                    binding.swipeToRefresh.isRefreshing = false
                    if (it.response?.code?.isSuccessful() == true) {
                        /*val list = mutableListOf<RideRequests>()
                        it.response.result1.forEach { ride ->
                            if (ride.Status == RideStatus.PENDING.name)
                                list.add(ride)
                        }*/
                        adapter.set(it.response.result1)
                    }
                }

                is Failure -> {
                    binding.swipeToRefresh.isRefreshing = false
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                    dismissProgress()
                }
            }
        }

        binding.swipeToRefresh.setOnRefreshListener {
            getData()
        }

    }

    private fun getData() {
        if (App.preferenceManager.user?.is_online == true) {
            binding.swipeToRefresh.isRefreshing = true
            viewModel.getRequestList()
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }


}