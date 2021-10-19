package com.column.roar.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.column.roar.R
import com.google.android.material.button.MaterialButton


class AccountVerification : Fragment() {
    lateinit var paymentPager: PaymentPager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account_verification, container, false)
        val submitBtn = view.findViewById<MaterialButton>(R.id.submitOTP)

//        submitBtn.setOnClickListener {
//            paymentPager.moveForward(4)
//        }
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(_paymentPager: PaymentPager) =
            AccountVerification().apply {
                  paymentPager = _paymentPager
                }
            }
}