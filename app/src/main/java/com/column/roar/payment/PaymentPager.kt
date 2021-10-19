package com.column.roar.payment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.column.roar.R
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RaveUiManager
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class PaymentPager : Fragment() {

    private lateinit var ravePayment: RaveUiManager
    private lateinit var firstNameView: TextInputEditText
    private lateinit var lastNameView: TextInputEditText
    private lateinit var emailAddressView: TextInputEditText
    private lateinit var amountView: TextInputEditText
    private lateinit var paymentView: TextInputLayout
    private lateinit var phoneNumberView: TextInputEditText
    private lateinit var alertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_payment_pager, container, false)
        val processBtn = view.findViewById<MaterialButton>(R.id.processBtn)
        val backBtn = view.findViewById<ImageView>(R.id.paymentBack)
        firstNameView = view.findViewById(R.id.firstName)
        lastNameView = view.findViewById(R.id.lastName)
        emailAddressView = view.findViewById(R.id.emailAddress)
        amountView = view.findViewById(R.id.amount)
        paymentView = view.findViewById(R.id.descriptionSpinner)
        phoneNumberView = view.findViewById(R.id.phoneNumber)

        backBtn.setOnClickListener {
           findNavController().popBackStack()
        }

        processBtn.setOnClickListener {
            processPayment()
        }

        val descriptionAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.payment_description,
            android.R.layout.simple_spinner_dropdown_item
        )
        (paymentView.editText as AutoCompleteTextView).setAdapter(descriptionAdapter)

        return view
    }

    private fun processPayment() {
        val firstName = firstNameView.text.toString()
        val lastName = lastNameView.text.toString()
        val emailAddress = emailAddressView.text.toString()
        val amount = amountView.text.toString()
        val phoneNumber = phoneNumberView.text.toString()
        val description = paymentView.editText?.text.toString()

        when {
            firstName.isBlank() -> {
                firstNameView.error = "Cannot be blank"
            }
            lastName.isBlank() -> {
                lastNameView.error = "Cannot be blank"
            }
            emailAddress.isBlank() -> {
                emailAddressView.error = "Cannot be blank"
            }
            amount.isBlank() -> {
                amountView.error = "Cannot be blank"
            }
            phoneNumber.isBlank() -> {
                phoneNumberView.error = "Cannot be blank"
            }
            else -> {
                makePayment(firstName, lastName, emailAddress, amount
                    ,phoneNumber, description)
            }
        }
    }

    private fun makePayment(
        firstName: String,
        lastName: String,
        emailAddress: String,
        amount: String,
        phoneNumber: String,
        description: String,
    ) {
        ravePayment = RaveUiManager(requireActivity())
            .setCurrency("NGN")
            .setAmount(amount.toDouble())
            .setfName(firstName).setlName(lastName)
            .setEmail(emailAddress)
            .setNarration(description)
            .setPublicKey("FLWPUBK_TEST-e7d117437412009acd3d91c40e5705fb-X")
            .setEncryptionKey("FLWSECK_TEST3e51f598263e")
            .setTxRef(System.currentTimeMillis().toString())
            .setPhoneNumber(phoneNumber)
            .acceptAccountPayments(true)
            .acceptCardPayments(true)
            .onStagingEnv(true)
            .withTheme(R.style.MyCustomTheme)
            .initialize()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            val message = data.getStringExtra("response")
            when (resultCode) {
                RavePayActivity.RESULT_SUCCESS -> {
                        successDialog()
                }

                RavePayActivity.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), "ERROR $message", Toast.LENGTH_SHORT).show();
                }

                RavePayActivity.RESULT_CANCELLED -> {
                    Toast.makeText(requireContext(), "CANCELLED $message", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

//    class PaymentPagerAdapter(fragment: Fragment, private val paymentPager: PaymentPager) :
//        FragmentStateAdapter(fragment) {
//
//        override fun getItemCount(): Int = 4
//
//        override fun createFragment(position: Int): Fragment =
//            when (position) {
//                0 -> PaymentFragment.newInstance(paymentPager)
//                1 -> ViaAccount.newInstance(paymentPager)
//                2 -> AccountVerification.newInstance(paymentPager)
//                else -> PaySuccessful()
//            }
//    }

    @SuppressLint("InflateParams")
    private fun successDialog() {
      alertDialog = requireActivity().let {
            AlertDialog.Builder(it).apply {
                setCancelable(false)
                val inflater = LayoutInflater.from(requireContext())
                val view = inflater.inflate(R.layout.successful_dialog,null)
                val submit = view.findViewById<MaterialButton>(R.id.continueBtn)

                submit.setOnClickListener {
                    alertDialog.dismiss()
                    findNavController().popBackStack()
                }
                setView(view)
            }.create()
        }
        alertDialog.show()
    }
}