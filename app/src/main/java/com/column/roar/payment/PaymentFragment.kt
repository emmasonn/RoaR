package com.column.roar.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.column.roar.R
import com.google.android.material.card.MaterialCardView

class PaymentFragment : Fragment() {

    lateinit var paymentPager: PaymentPager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_payment, container, false)
        val payViaAccount = view.findViewById<MaterialCardView>(R.id.payViaAccount)

        payViaAccount.setOnClickListener {
            paymentPager.moveForward(1)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(_paymentPager: PaymentPager) =
            PaymentFragment().apply {
                paymentPager = _paymentPager
            }
    }
}