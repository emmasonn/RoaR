package com.beaconinc.roarhousing.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.beaconinc.roarhousing.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ViaAccount : Fragment() {

    lateinit var paymentPager: PaymentPager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_via_account, container, false)
        val phoneNumber = view.findViewById<TextInputLayout>(R.id.phoneNumber)
        val accountNumber = view.findViewById<TextInputLayout>(R.id.accountNumber)
        val bankName = view.findViewById<TextInputLayout>(R.id.bankSpinner)
        val amount = view.findViewById<TextView>(R.id.amount)
        val payBtn = view.findViewById<MaterialButton>(R.id.payBtn)

        payBtn.setOnClickListener {
            paymentPager.moveForward(2)
        }

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(_paymentPager: PaymentPager) =
            ViaAccount().apply {
                   paymentPager = _paymentPager
                }
            }
}