package com.beaconinc.roarhousing.dashBoard.property

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit


class MarketProfile : Fragment() {

    private lateinit var userDocument: DocumentReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var profileImage: ImageView
    private lateinit var fullName: TextView
    private lateinit var sharedPref: SharedPreferences
    private lateinit var client: FirebaseUser
    private lateinit var progressBar: ConstraintLayout
    private lateinit var phoneNumber: TextView


    //format of string am using shared pref is string_other string
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        sharedPref = (activity as MainActivity).sharedPref
        userLoggedState(true) //identifies that user has logged in
        val currentUserId = sharedPref.getString("user_id", "")

        if (currentUserId.isNullOrBlank()) {
            findNavController().navigateUp()
        }
        userDocument = fireStore.collection("clients").document(currentUserId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_market_profile, container, false)
        val backBtn = view.findViewById<ImageView>(R.id.pagerBack)
        val sellBtn = view.findViewById<ConstraintLayout>(R.id.sellBtn)
        val accountBtn = view.findViewById<ConstraintLayout>(R.id.accountBtn)
        val signOut = view.findViewById<ConstraintLayout>(R.id.signOutBtn)
        fullName = view.findViewById<TextView>(R.id.fullName)
        profileImage = view.findViewById<ImageView>(R.id.profilePics)
        val termAndCondition = view.findViewById<ConstraintLayout>(R.id.termsAnd)
        val subscriptionBtn = view.findViewById<ConstraintLayout>(R.id.subscriptionBtn)
        progressBar = view.findViewById(R.id.progressBackground)
        phoneNumber = view.findViewById(R.id.phoneNumber)

        fullName.text = sharedPref.getString("user_name", "")
        val imageUrl = sharedPref.getString("user_url", "")
        profileImage.load(imageUrl)

        backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        sellBtn.setOnClickListener {
            if(::client.isInitialized) {
                val bundle = bundleOf("Client" to client)
                findNavController().navigate(R.id.manageProperty, bundle)
            }
        }

        accountBtn.setOnClickListener {
            if (::client.isInitialized) {
                val bundle = bundleOf("Client" to client)
                findNavController().navigate(R.id.manageAccount, bundle)
            }
        }

        termAndCondition.setOnClickListener {
            findNavController().navigate(R.id.termsFragment)
        }

        subscriptionBtn.setOnClickListener {
            if (::client.isInitialized) {
                val bundle = bundleOf("Client" to client)
                findNavController().navigate(R.id.subscriptionFragment, bundle)
            }
        }

        signOut.setOnClickListener {
            showSignOutDialog()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenerForChange()
    }

    private fun listenerForChange() {
        showProgress()
        if (::userDocument.isInitialized) {
            userDocument.get().addOnSuccessListener {
                it.toObject(FirebaseUser::class.java).also { user ->
                    client = user!!
                    phoneNumber.text = user.clientPhone
                    profileImage.load(client.clientUrl)
                    computeRemainingDay(client.expired!!)
                    hideProgress()
                }
            }
        }
    }

    private fun userLoggedState(state: Boolean) {
        with(sharedPref.edit()) {
            putBoolean("logged", state)
            apply()
        }
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("You're About to Sign out")
            setPositiveButton("Okay") { dialog, _ ->
                dialog.dismiss()
                userLoggedState(false)
                findNavController().popBackStack(R.id.homeFragment,false)
            }

            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun computeRemainingDay(expiredDate: Date) {
        val currentDate = System.currentTimeMillis()
        val diff = currentDate - expiredDate.time
        val result = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        val remainingDay = 30 - result
        cacheRemainingDay(remainingDay)

        if (remainingDay <= 0L) {
            userDocument.update("slots", "1")
        }
    }

    private fun cacheRemainingDay(day: Long) {
        with(sharedPref.edit()) {
            putLong("remaining", day)
            commit()
        }
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        lifecycleScope.launch {
            progressBar.visibility = View.GONE
        }
    }
}