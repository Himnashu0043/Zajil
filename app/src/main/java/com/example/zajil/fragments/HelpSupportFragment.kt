package com.example.zajil.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.zajil.R
import com.example.zajil.databinding.FragmentHelpSupportBinding
import com.example.zajil.util.Commons.composeEmailIntent
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.openWhatsApp
import com.example.zajil.util.Commons.showDialog
import com.example.zajil.util.Commons.showProgress
import com.example.zajil.util.Commons.showToast
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success

class HelpSupportFragment : BaseFragment<FragmentHelpSupportBinding>(),View.OnClickListener {

    private val inputSubject
        get() = binding
            .etSubject.text.trim().toString()
    private val inputDescription get() = binding.etDescription.text.trim().toString()
    private val inputOrderId get() = binding.etSelectOrder.text.trim().toString()

    private var contactPhone: String = ""
    private var contactEmail: String = ""


    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    override fun getLayout(): FragmentHelpSupportBinding {
        return FragmentHelpSupportBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderArray = arrayOf(
            "#wqerydbsjsk",
            "#scjkhishwyu2",
            "#12dvnkjdh",
            "#ndiuhaw2iour02",
            "#ndu19039031"
        )

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_down, orderArray)
        binding.etSelectOrder.setAdapter(arrayAdapter)
        binding.etSelectOrder.setOnClickListener(this)
        binding.btnWhatsapp.setOnClickListener(this)
        binding.btnSignIn.setOnClickListener(this)

        viewModel.helpSupportResponse.observe(viewLifecycleOwner) {
            when (it) {

                is Success -> {
                    dismissProgress()
                    if (it.response?.code?.isSuccessful() == true) {
                        contactPhone = it.response.askedUser.phoneNumber
                        contactEmail = it.response.askedUser.email
                    } else it.response?.message?.showToast(binding)
                }

                is Failure -> {
                    dismissProgress()
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                }
            }
        }

        showProgress(requireContext())
        viewModel.getHelpSupportContact()
    }


    private val emailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if(it.resultCode == Activity.RESULT_OK){
            requireContext().showDialog(
                R.drawable.ic_tick,
                "Request sent",
                "Your request has been sent to the admin. Admin will contact you."
            ) {
                requireActivity().onBackPressed()
            }
//            }
        }

    private val whatsAppLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

    override fun onClick(v: View?) {
        when(v){
            binding.etSelectOrder -> binding.etSelectOrder.showDropDown()
            binding.btnWhatsapp ->{
                when {
                    inputSubject.isEmpty() -> "Please enter subject".showToast(binding)
                    inputDescription.isEmpty() -> "Please enter description".showToast(binding)
                    else -> {
                        val message = "$inputSubject\n\n$inputDescription\n$inputOrderId"
                        requireContext().openWhatsApp(contactPhone, message)
                    }
                }
            }
            binding.btnSignIn -> {
                when {
                    inputSubject.isEmpty() -> "Please enter subject".showToast(binding)
                    inputDescription.isEmpty() -> "Please enter description".showToast(binding)
                    else -> {
                        val intent = requireActivity().composeEmailIntent(
                            contactEmail,
                            inputSubject,
                            "$inputDescription\n\nOrderId: $inputOrderId"
                        )
                        emailLauncher.launch(intent)
                    }
                }
            }
        }
    }


}