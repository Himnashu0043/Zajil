package com.example.zajil.fragments

import com.example.zajil.databinding.FragmentAboutUsBinding

class AboutUsFragment : BaseFragment<FragmentAboutUsBinding>() {

    override fun getLayout(): FragmentAboutUsBinding {
       return FragmentAboutUsBinding.inflate(layoutInflater)
    }

}