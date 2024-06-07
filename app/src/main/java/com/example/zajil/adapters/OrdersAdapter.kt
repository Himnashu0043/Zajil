package com.example.zajil.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zajil.BaseAdapter
import com.example.zajil.R
import com.example.zajil.databinding.ItemOrderStatusBinding
import com.example.zajil.util.App
import com.example.zajil.util.Commons
import com.example.zajil.util.Commons.getAddress
import com.example.zajil.util.Commons.prettyCount
import com.example.zajil.util.RideRequests
import com.example.zajil.util.RideStatus
import com.google.android.gms.maps.model.LatLng

class OrdersAdapter(val isActive: Boolean, val handle: (Int) -> Unit) :
    BaseAdapter<RideRequests>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrderViewHolder(
            ItemOrderStatusBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        get(position)?.let {
            (holder as OrderViewHolder).apply {


                if (it.Status == RideStatus.ACCEPT.name || it.Status == RideStatus.WAY_TO_PICKUP.name) {
                    binding.btnNavigateToBranch.text =
                        binding.btnNavigateToBranch.context.getString(
                            R.string.navigate_to_branch
                        )
                } else {
                    binding.btnNavigateToBranch.text =
                        binding.btnNavigateToBranch.context.getString(
                            R.string.navigate_to_receiver
                        )
                }

                if(it.Status == RideStatus.DELIVERED.name){
                    binding.tvStatus.isSelected = true
                    binding.tvStatus.text = binding.tvStatus.context.getString(R.string.delivered)
                }else{
                    binding.tvStatus.isSelected = false
                    binding.tvStatus.text = binding.tvStatus.context.getString(R.string.rejected)
                }


                binding.tvRideId.text = "#${it.trackNumber}"
                binding.tvRideFromAddress.context.getAddress(
                    it.location.coordinates[1],
                    it.location.coordinates[0]
                ) {
                    binding.tvRideFromAddress.text = it
                }
                binding.tvRideFromDistance.text =
                    Commons.getDistance(
                        LatLng(App.LAT, App.LONG),
                        LatLng(it.location.coordinates[1], it.location.coordinates[0])
                    ).prettyCount() + " away"
                binding.tvRideFrom.text = it.branch
                binding.tvRideTo.text = it.quickServiceAddress

                binding.tvRideToAddress.context.getAddress(
                    it.droplocation.coordinates[1],
                    it.droplocation.coordinates[0]
                ) {
                    binding.tvRideToAddress.text = it
                }

                binding.tvRideToDistance.text =
                    Commons.getDistance(
                        LatLng(it.location.coordinates[1], it.location.coordinates[0]),
                        LatLng(it.droplocation.coordinates[1], it.droplocation.coordinates[0])

                    ).prettyCount() + " away"
            }
        }
    }

    inner class OrderViewHolder(val binding: ItemOrderStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

            binding.root.setOnClickListener {
                if (isActive)
                    handle(adapterPosition)
            }

            binding.btnNavigateToBranch.setOnClickListener {
                get(adapterPosition)?.let { order ->
                    if (order.Status == RideStatus.ACCEPT.name || order.Status == RideStatus.WAY_TO_PICKUP.name) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=${order.location.coordinates[1]},${order.location.coordinates[0]}")
                        )
                        binding.root.context.startActivity(intent)
                    } else {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=${order.location.coordinates[1]},${order.location.coordinates[0]}&daddr=${order.droplocation.coordinates[1]},${order.droplocation.coordinates[0]}")
                        )
                        binding.root.context.startActivity(intent)
                    }
                }
//                handle.invoke(adapterPosition)
            }
            binding.llNavigateToBranch.visibility = if (isActive) View.VISIBLE else View.GONE
            binding.tvStatus.visibility = if (!isActive) View.VISIBLE else View.GONE
        }
    }
}