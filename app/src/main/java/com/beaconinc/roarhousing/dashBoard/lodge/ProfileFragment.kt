package com.beaconinc.roarhousing.dashBoard.lodge

import android.content.SharedPreferences
import android.os.Bundle
import android.util.TimeUtils
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
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class ProfileFragment : Fragment() {

    //private lateinit var clientId: String
    private lateinit var userDocument: DocumentReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var profileImage: ImageView
    private lateinit var fullName: TextView
    //private lateinit var notifyCounter: TextView
    private lateinit var sharedPref: SharedPreferences
    //private lateinit var registration: ListenerRegistration
    private lateinit var progressBar: ConstraintLayout
    private lateinit var client: FirebaseUser
    private lateinit var phoneNumber: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         fireStore = FirebaseFirestore.getInstance()
         sharedPref = (activity as MainActivity).sharedPref
         userLoggedState(true) //identifies that user has logged in
         val currentUserId = sharedPref.getString("user_id","")
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
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)
        val backBtn = view.findViewById<ImageView>(R.id.pagerBack)
        val careerBtn = view.findViewById<ConstraintLayout>(R.id.careerBtn)
        val accountBtn = view.findViewById<ConstraintLayout>(R.id.accountBtn)
        val signOut = view.findViewById<ConstraintLayout>(R.id.signOutBtn)
        val subscribeBtn = view.findViewById<ConstraintLayout>(R.id.subscriptionBtn)
        fullName = view.findViewById<TextView>(R.id.fullName)
        profileImage = view.findViewById<ImageView>(R.id.profilePics)
        val termBtn = view.findViewById<ConstraintLayout>(R.id.terms)
        progressBar = view.findViewById(R.id.progressBackground)
        phoneNumber = view.findViewById(R.id.phoneNumber)

        fullName.text = sharedPref.getString("user_name", "")
        val imageUrl = sharedPref.getString("user_url", "")
        profileImage.load(imageUrl)

        backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        termBtn.setOnClickListener {
          findNavController().navigate(R.id.termsFragment)
        }

        subscribeBtn.setOnClickListener {
            if(::client.isInitialized) {
                val bundle = bundleOf("Client" to client)
                findNavController().navigate(R.id.subscriptionFragment, bundle)
            }
        }

        accountBtn.setOnClickListener {
            if(::client.isInitialized) {
                val bundle = bundleOf("Client" to client )
                val action = R.id.action_profileFragment_to_manageAccount
                findNavController().navigate(action, bundle)
            }
        }

        careerBtn.setOnClickListener {
            val action = R.id.action_profileFragment_to_careerFragment
            findNavController().navigate(action)
        }

        signOut.setOnClickListener {
            showSignOutDialog()
        }
        return view
    }

    override fun onStart() {
        super.onStart()
         listenerForChange()
    }

//    override fun onStop() {
//        super.onStop()
//        registration.remove()
//    }

    private fun listenerForChange() {
        showProgress()
        if(::userDocument.isInitialized) {
            userDocument.get().addOnSuccessListener {
                it.toObject(FirebaseUser::class.java).also { user ->
                    client = user!!
                    phoneNumber.text = user.clientPhone
                    computeRemainingDay(client.expired!!)
                    hideProgress()
                }
            }

//            registration = userDocument.addSnapshotListener { value, _ ->
//                value?.toObject(FirebaseUser::class.java).also { client ->
//                    if(client?.visitCounter != 0L) {
//                        notifyCounter.text = client?.visitCounter.toString()
//                        notifyCounter.visibility = View.VISIBLE
//                    }else {
//                        notifyCounter.visibility = View.GONE
//                    }
//                }
//            }
        }
    }

    private fun userLoggedState(state: Boolean) {
        with(sharedPref.edit()){
            putBoolean("logged", state)
            apply()
        }
    }

    private fun computeRemainingDay(expiredDate: Date) {
        val currentDate = System.currentTimeMillis()
        val diff = currentDate - expiredDate.time
        val result  = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS)
        val remainingDay = 30 - result

        if(remainingDay <= 0L) {
             userDocument.update("slots","1")
             cacheRemainingDay(0L)
        }else {
            cacheRemainingDay(remainingDay)
        }
    }

    private fun cacheRemainingDay(day: Long) {
        with(sharedPref.edit()) {
            putLong("remaining",day)
            commit()
        }
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("You're About to Sign out")
            setPositiveButton("Okay") { dialog , _ ->
                dialog.dismiss()
                userLoggedState(false)
                findNavController().popBackStack(R.id.becomeAgent,false)
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
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