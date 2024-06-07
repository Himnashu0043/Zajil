package com.example.zajil.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.zajil.R
import com.example.zajil.databinding.FragmentOrdersBinding
import com.google.android.material.tabs.TabLayoutMediator

class OrdersFragment : BaseFragment<FragmentOrdersBinding>() {
    override fun getLayout(): FragmentOrdersBinding {
        return FragmentOrdersBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vpOrders.adapter = OrderFragmentAdapter()
        TabLayoutMediator(binding.tabOrders, binding.vpOrders) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.active)
                else -> getString(R.string.completed)
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        binding.tabOrders.selectTab(binding.tabOrders.getTabAt(0),true)
    }

    inner class OrderFragmentAdapter : FragmentStateAdapter(this) {
        override fun getItemCount() = 2
        override fun createFragment(position: Int): Fragment {
            return SubOrderFragment.get(position == 0)
        }
    }

}