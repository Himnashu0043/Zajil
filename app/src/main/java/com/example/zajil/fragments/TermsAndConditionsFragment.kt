package com.example.zajil.fragments

import com.example.zajil.databinding.FragmentTermsAndConditionsBinding

class TermsAndConditionsFragment : BaseFragment<FragmentTermsAndConditionsBinding>() {

    override fun getLayout(): FragmentTermsAndConditionsBinding {
        return FragmentTermsAndConditionsBinding.inflate(layoutInflater)
    }

}