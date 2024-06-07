package com.example.zajil.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.zajil.BaseAdapter
import com.example.zajil.databinding.FragmentNotificationsBinding
import com.example.zajil.databinding.ItemNotificationBinding
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.getTimeAgo
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.showToast
import com.example.zajil.util.Commons.span
import com.example.zajil.util.Commons.toDateFormat
import com.example.zajil.util.Commons.toNotificationDate
import com.example.zajil.util.Notification
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success

class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {


    private lateinit var notificationAdapter: NotificationAdapter

    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }


    override fun getLayout(): FragmentNotificationsBinding {
        return FragmentNotificationsBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationAdapter = NotificationAdapter()
        binding.rvNotification.adapter = notificationAdapter
        viewModel.notificationListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    if (it.response?.code?.isSuccessful() == true) {
                        notificationAdapter.set(it.response.result1)
                    } else it.response?.message?.showToast(binding)
                    binding.swipeToRefresh.isRefreshing = false
                }

                is Failure -> {
                    binding.swipeToRefresh.isRefreshing = false
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                }
            }
        }

        binding.swipeToRefresh.setOnRefreshListener {
            getNotificationList()
        }

        getNotificationList()
    }

    private fun getNotificationList() {
        binding.swipeToRefresh.isRefreshing = true
        viewModel.getNotification()
    }


    class NotificationAdapter : BaseAdapter<Notification>() {

        class NotificationViewHolder(val binding: ItemNotificationBinding) :
            RecyclerView.ViewHolder(binding.root) {
            init {

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return NotificationViewHolder(
                ItemNotificationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            get(position)?.let {
                (holder as NotificationViewHolder).apply {
                    binding.tvTitle.text = it.content.span(it.trackNumber){}
                    binding.tvDate.text = it.createdAt.toNotificationDate()
                    binding.tvTime.text = it.createdAt.getTimeAgo()
                }
            }
        }

    }

}