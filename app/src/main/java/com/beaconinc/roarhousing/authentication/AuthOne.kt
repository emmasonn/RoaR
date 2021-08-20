package com.beaconinc.roarhousing.authentication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthOne : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var sharedPref: SharedPreferences
    lateinit var phoneAuthentication: PhoneAuthentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPref = (activity as MainActivity).sharedPref

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_auth_one, container, false)
        val textField = view.findViewById<TextInputEditText>(R.id.phoneNumber)
        val nextBtn = view.findViewById<MaterialButton>(R.id.nextBtn)

        nextBtn.setOnClickListener {
            hideKeyBoard(requireView())
            val phoneText = textField?.text.toString()
            storePhone("+234${phoneText}")
            phoneAuthentication.movePagerForward()
            phoneAuthentication.submitPhoneNumber("+234${phoneText}")
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        val verificationId = sharedPref.getString("VERIFY_ID", null)
        verificationId?.let {
            phoneAuthentication.movePagerForward()
        }
    }

    private fun storePhone(phone: String) {
        with(sharedPref.edit()) {
            putString("VERIFY_PHONE", phone)
            apply()
        }
    }

    private fun hideKeyBoard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

//    fun showKeyBoard(view: View) {
//        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(view, 0)
//    }

    companion object {
        @JvmStatic
        fun newInstance(phoneAuth: PhoneAuthentication): AuthOne {
            return AuthOne().apply {
                phoneAuthentication = phoneAuth
            }
        }
    }
}