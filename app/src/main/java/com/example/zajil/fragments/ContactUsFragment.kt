package com.example.zajil.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zajil.R
import com.example.zajil.databinding.FragmentContactUsBinding
import com.example.zajil.util.Commons.redirectToWhatsapp

class ContactUsFragment : BaseFragment<FragmentContactUsBinding>(), View.OnClickListener {

    override fun getLayout() = FragmentContactUsBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivWhatsapp.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.ivWhatsapp -> requireContext().redirectToWhatsapp()
        }
    }

}