package com.example.zajil.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.zajil.R
import com.example.zajil.databinding.FragmentPhoneVerificationBinding
import com.example.zajil.util.App
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.showProgress
import com.example.zajil.util.Commons.showToast
import com.example.zajil.util.Commons.span
import com.example.zajil.util.Constants
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneVerificationFragment : BaseFragment<FragmentPhoneVerificationBinding>(),
    View.OnClickListener {

    private var phoneNumber: String = ""
    private var countryCode: String = ""
    private var verificationId: String = ""
    private val completeCode get() = binding.etEnterOtp.text?.trim().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            phoneNumber = it.getString(Constants.PHONE_NUMBER, "") ?: ""
            countryCode = it.getString(Constants.COUNTRY_CODE, "") ?: ""
        }
    }

    override fun getLayout(): FragmentPhoneVerificationBinding {
        return FragmentPhoneVerificationBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.mAuth = FirebaseAuth.getInstance()
        binding.btnSubmit.setOnClickListener(this)
        binding.tvDontAccount.text = "Didn't get a code? Send again".span("Send again") {}
        binding.includedToolbar.llEnd.visibility = View.GONE
        binding.includedToolbar.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        sendVerificationCode(phoneNumber)
    }

    private fun sendVerificationCode(number: String) {
        showProgress(requireContext())
        val options = PhoneAuthOptions.newBuilder(App.mAuth)
            .setPhoneNumber("$countryCode$number")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationId = p0
                dismissProgress()
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                dismissProgress()
                if (code != null) {

                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                dismissProgress()
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }

        }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        App.mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                dismissProgress()
                if (task.isSuccessful) {
                    "Phone number verified".showToast(binding)
                    requireActivity().supportFragmentManager.popBackStack()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.franeLayout, NewPasswordFragment().apply {
                            arguments = Bundle().apply {
                                putString(Constants.PHONE_NUMBER, phoneNumber)
                                putString(Constants.COUNTRY_CODE, countryCode)
                            }
                        }).addToBackStack("")
                        .commit()

                } else {

                }
            }
    }

    override fun onClick(p0: View?) {

        when (p0) {
            binding.btnSubmit -> {
                when {
                    completeCode.isEmpty() -> "Please enter code".showToast(binding)
                    completeCode.length < 6 -> "Please enter complete otp".showToast(binding)
//                    !completeCode.equals("1234") -> "Please enter valid otp".showToast(binding)
                    else -> {
                        showProgress(requireContext())
                        verifyCode(completeCode)
                    }
                }

            }
        }
    }

}
