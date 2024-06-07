package com.example.zajil.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zajil.BaseAdapter
import com.example.zajil.R
import com.example.zajil.databinding.ItemTransactionsBinding
import com.example.zajil.util.Commons.toDateFormat
import com.example.zajil.util.Transactions

class TransactionAdapter : BaseAdapter<Transactions>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemTransactionsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        get(position)?.let {
            (holder as ViewHolder).apply {

                if (it.type == "Paid") {
                    binding.tvTransactionDetail.text = "Credited"
                    binding.ivAction.setImageResource(R.drawable.credit)
                    binding.tvWithdrawStatus.visibility = View.GONE
                } else {

                    binding.tvTransactionDetail.text = "Withdraw"
                    binding.ivAction.setImageResource(R.drawable.withdraw)
                    binding.tvWithdrawStatus.visibility = View.VISIBLE

                    if (it.checkoutStatus.equals("PENDING", true)) {
                        binding.tvWithdrawStatus.text = "Pending"
                        binding.tvWithdrawStatus.isSelected = false
                    } else {
                        binding.tvWithdrawStatus.isSelected = true
                        binding.tvWithdrawStatus.text = "Success"
                    }
                }

                binding.tvTransactionId.text = "#${it._id}"
                binding.tvTime.text = it.createdAt.toDateFormat()
                binding.tvAmount.text = "SAR ${it.amount ?: 0}"
                if (position == itemCount - 1)
                    binding.viewBottom.visibility = View.GONE
                else binding.viewBottom.visibility = View.VISIBLE
            }
        }
    }

    inner class ViewHolder(val binding: ItemTransactionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

        }
    }
}