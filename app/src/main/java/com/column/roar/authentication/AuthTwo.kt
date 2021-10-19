package com.column.roar.authentication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.column.roar.MainActivity
import com.column.roar.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import timber.log.Timber


class AuthTwo : Fragment() {

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var phoneNumber: String? = null
    private lateinit var sharedPref: SharedPreferences
    lateinit var phoneAuthentication: PhoneAuthentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        sharedPref = (activity as MainActivity).sharedPref
        phoneNumber = sharedPref.getString("VERIFY_PHONE", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_auth_two, container, false)
        val verifyTextField = view.findViewById<TextInputEditText>(R.id.verifyTextView)
        val resendBtn = view.findViewById<ConstraintLayout>(R.id.resentBtn)
        val phoneTitle = view.findViewById<TextView>(R.id.phoneTitle)

        phoneTitle.text = getString(R.string.verify_number, phoneNumber)
        verifyTextField.doOnTextChanged { text, _, _, _ ->

            val verifiedCode = text.toString()
            if (verifiedCode.length == 6) {
                hideKeyBoard(requireView())
                submitVerificationCode(verifiedCode)
            }
        }

        Timber.i("Phone Code:$verificationId")
        Timber.i("Phone Code:$phoneNumber")

        resendBtn.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Request clicked", Toast.LENGTH_SHORT
            ).show()
        }
        return view
    }

    private fun submitVerificationCode(code: String) {
        verificationId = sharedPref.getString("VERIFY_ID", null)
        Timber.i("$verificationId")

        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user

                    lifecycleScope.launch {
                        verifyStateToNull()
                        val action = R.id.setUpFragment
                        val bundle = bundleOf("phone" to phoneNumber)
                        findNavController().navigate(action, bundle)
                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        //verification entered is invalid
                        Toast.makeText(
                            requireContext(),
                            "Invalid ${(task.exception as FirebaseAuthInvalidCredentialsException).message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun verifyStateToNull() {
        with(sharedPref.edit()) {
            putString("VERIFY_ID", "set_up")
            putString("VERIFY_PHONE",null)
            apply()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(phoneAuth: PhoneAuthentication) = AuthTwo().apply {
                phoneAuthentication = phoneAuth
            }
        }

    private fun hideKeyBoard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}