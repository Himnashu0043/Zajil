package com.example.zajil.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zajil.BaseAdapter
import com.example.zajil.databinding.ItemRideStatusBinding
import com.example.zajil.fragments.ShipmentDetailFragment
import com.example.zajil.util.Commons.toDateFormat
import com.example.zajil.util.Commons.toDateFormatStatus

class ShipmentActionAdapter : BaseAdapter<ShipmentDetailFragment.StatusAction>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemRideStatusBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        get(position)?.let {
            (holder as ViewHolder).apply {
                binding.tvStatus.text = it.text
                if (it.time.isNotEmpty())
                    binding.tvDate.text = it.time.toDateFormatStatus()
            }
        }
    }

    inner class ViewHolder(val binding: ItemRideStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

        }
    }

}
