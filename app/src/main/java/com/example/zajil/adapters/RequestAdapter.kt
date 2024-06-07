package com.example.zajil.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zajil.BaseAdapter
import com.example.zajil.databinding.ItemRequestBinding
import com.example.zajil.util.App
import com.example.zajil.util.Commons
import com.example.zajil.util.Commons.getAddress
import com.example.zajil.util.Commons.getAddressLine
import com.example.zajil.util.Commons.prettyCount
import com.example.zajil.util.Commons.showReasonDialog
import com.example.zajil.util.RideRequests
import com.example.zajil.util.RideStatus
import com.google.android.gms.maps.model.LatLng

class RequestAdapter(val handle: (String, Int, String) -> Unit) : BaseAdapter<RideRequests>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).apply {
            get(position)?.let {


                binding.tvRideId.text = "#${it.trackNumber}"

                binding.tvRideToAddress.context.getAddress(
                    it.droplocation.coordinates[1],
                    it.droplocation.coordinates[0]
                ) {
                    Log.d("AddressDataAdapter", "rideToAddress:1 $it")
                    binding.tvRideToAddress.text = it
                }

                binding.tvRideFromAddress.context.getAddress(
                    it.location.coordinates[1],
                    it.location.coordinates[0]
                ) {
                    Log.d("AddressDataAdapter", "rideFromAddress:1 $it")
                    binding.tvRideFromAddress.text = it
                }
                binding.tvRideFromDistance.text =
                    Commons.getDistance(
                        LatLng(App.LAT, App.LONG),
                        LatLng(it.location.coordinates[1], it.location.coordinates[0])
                    ).prettyCount() + " away"
                binding.tvRideFrom.text = it.branch
                binding.tvRideTo.text = it.quickServiceAddress



                binding.tvRideToDistance.text =
                    Commons.getDistance(
                        LatLng(it.location.coordinates[1], it.location.coordinates[0]),
                        LatLng(it.droplocation.coordinates[1], it.droplocation.coordinates[0])
                    ).prettyCount() + " away"

            }
        }
    }

    inner class MyViewHolder(val binding: ItemRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                handle("Detail", adapterPosition, "")
            }
            binding.btnAccept.setOnClickListener {
                handle("Accept", adapterPosition, "")
            }
            binding.btnReject.setOnClickListener {
                it.context.showReasonDialog {
                    handle("Reject", adapterPosition, it)
                }
            }
        }
    }

}
