package com.beaconinc.roarhousing.dashBoard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import coil.load
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class ProfileFragment : Fragment() {

    private lateinit var clientId: String
    private lateinit var userDocument: DocumentReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var profileImage: ImageView
    private lateinit var fullName: TextView
    private lateinit var notifyCounter: TextView
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         firebaseAuth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        sharedPref = (activity as MainActivity).sharedPref

        val currentUserId = firebaseAuth.currentUser?.uid
        val verificationId = sharedPref.getString("VERIFY_ID", null)

        verificationId.let { state ->
            if(state == "set_up"){
                findNavController().navigate(R.id.setUpFragment)
            }else if(currentUserId != null) {
                userDocument = fireStore.collection("clients").document(currentUserId)
            }else {
                val action = R.id.action_profileFragment_to_phoneAuthentication
                findNavController().navigate(action)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)
        val backBtn = view.findViewById<ImageView>(R.id.pagerBack)
        val manageBtn = view.findViewById<RelativeLayout>(R.id.sellBtn)
        val accountBtn = view.findViewById<RelativeLayout>(R.id.accountBtn)
        val signOut = view.findViewById<RelativeLayout>(R.id.signOutBtn)
        fullName = view.findViewById<TextView>(R.id.fullName)
        profileImage = view.findViewById<ImageView>(R.id.profilePics)
        notifyCounter = view.findViewById<TextView>(R.id.notifyCount)
        val notifyBtn = view.findViewById<ImageView>(R.id.notifyBtn)
        val agentBtn = view.findViewById<RelativeLayout>(R.id.agentBtn)

        backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        manageBtn.setOnClickListener {
            findNavController().navigate(R.id.manageLodge)
        }

        accountBtn.setOnClickListener {
            val action = R.id.action_profileFragment_to_manageAccount
            findNavController().navigate(action)
        }

        agentBtn.setOnClickListener {
            val action = R.id.action_profileFragment_to_careerFragment
            //if the clicker is not an agent show them dialog
            findNavController().navigate(action)
        }

        notifyBtn.setOnClickListener {
            val action = R.id.action_profileFragment_to_notifyFragment
            findNavController().navigate(action)
        }

        signOut.setOnClickListener {
            showSignOutDialog()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
         if(::userDocument.isInitialized) {
            userDocument.get().addOnSuccessListener {
                it.toObject(FirebaseUser::class.java).also { client ->
                    fullName.text = client?.clientName
                    profileImage.load(client?.clientUrl)
                    if(client?.visitCounter != 0L) {
                        notifyCounter.text = client?.visitCounter.toString()
                    }
                }
            }
         }
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("You're About to Sign out")
            setPositiveButton("Okay") { dialog , _ ->
                dialog.dismiss()
                firebaseAuth.signOut()
                findNavController().navigateUp()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

}