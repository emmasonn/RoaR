package com.beaconinc.roarhousing.authentication

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PhoneAuthentication : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var sharedPref: SharedPreferences
    private lateinit var authViewPager: ViewPager2
    private lateinit var progressBar: ProgressBar

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
        val view = inflater.inflate(R.layout.fragment_phone_authentication, container, false)
        authViewPager = view.findViewById(R.id.authViewPager)
        progressBar = view.findViewById(R.id.progressBar)
        authViewPager.isUserInputEnabled = false

        setUpVerifyCallback()
        val adapter = AuthPagerAdapter(this, this)
        authViewPager.adapter = adapter
        return view
    }

    fun submitPhoneNumber(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this.requireActivity())
            .setCallbacks(callback)
            .build()

        lifecycleScope.launch {
            storePhone(phoneNumber)
            showProgress()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun setUpVerifyCallback() {
        callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                hideProgress()
                signInWithPhoneNumber(credential)
            }

            override fun onVerificationFailed(credential: FirebaseException) {
                hideProgress()
                Toast.makeText(
                    requireContext(),
                    "Verification Failed: ${credential.message}", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                Timber.i("Verification Code:$verificationId")
                lifecycleScope.launch {
                    hideProgress()
                    verificationSent(verificationId)
                }
            }
        }
    }

    fun signInWithPhoneNumber(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    //move to set up profile screen
                    val user = task.result?.user
                    Toast.makeText(
                        requireContext(),
                        "User is Verified: ${user!!.uid}", Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.setUpFragment)
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        //the verification Code entered is invalid
                    }
                    //update UI
                }
            }
    }

    fun verificationSent(verId: String) {
        with(sharedPref.edit()) {
            putString("VERIFY_ID", verId)
            apply()
        }
    }

   private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgress() {
        progressBar.visibility = View.GONE
    }

   fun movePagerForward() {
        authViewPager.currentItem = authViewPager.currentItem + 1
    }

    private fun storePhone(phone: String) {
        with(sharedPref.edit()) {
            putString("VERIFY_PHONE", phone)
            apply()
        }
    }

    class AuthPagerAdapter(
        val fragment: Fragment,
        private val phoneAuth: PhoneAuthentication,
    ) : FragmentStateAdapter(fragment) {

        private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
            0 to { AuthOne.newInstance(phoneAuth) },
            1 to { AuthTwo.newInstance(phoneAuth) }
        )

        override fun getItemCount() = tabFragmentsCreators.size

        override fun createFragment(position: Int): Fragment {
            return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
        }
    }
}

