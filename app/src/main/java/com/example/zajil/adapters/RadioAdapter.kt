package com.example.zajil.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zajil.BaseAdapter
import com.example.zajil.databinding.ItemRejectedReasonsBinding
import com.example.zajil.util.RadioOption

class RadioAdapter(val handle: (Int) -> Unit) : BaseAdapter<RadioOption>() {

    var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemRejectedReasonsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        get(position)?.let {
            (holder as ViewHolder).apply {
                binding.tvReason.text = it.text
                binding.rbReason.isChecked = it.isSelected
                if (it.isSelected)
                    selectedPosition = position
            }
        }
    }

    inner class ViewHolder(val binding: ItemRejectedReasonsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {

                if (selectedPosition == -1) {
                    get(adapterPosition)?.isSelected = true
                    notifyItemChanged(adapterPosition)
                } else {
                    get(selectedPosition)?.isSelected = false
                    notifyItemChanged(selectedPosition)
                    get(adapterPosition)?.isSelected = true
                    notifyItemChanged(adapterPosition)
                }

                handle.invoke(adapterPosition)

            }
        }
    }

}