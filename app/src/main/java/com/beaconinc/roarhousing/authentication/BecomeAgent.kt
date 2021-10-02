package com.beaconinc.roarhousing.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.google.android.gms.common.internal.AccountType
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import timber.log.Timber

class BecomeAgent : Fragment() {


    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         sharedPref = (activity as MainActivity).sharedPref

        val status = sharedPref.getBoolean("logged", false)
        val accountType = sharedPref.getString("accountType", "")

        Timber.i("status: $status, accountType: $accountType")
        if (status) {
            navigateToScreen(accountType)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_become_agent, container, false)
        val signInBtn = view.findViewById<MaterialButton>(R.id.signInBtn)
        val joinBtn = view.findViewById<MaterialButton>(R.id.joinBtn)
        val backBtn = view.findViewById<ImageView>(R.id.becomeBackBtn)

        signInBtn.setOnClickListener {
            findNavController().navigate(R.id.verifyNumber)
        }

        joinBtn.setOnClickListener {
          chatWhatsApp("+23407060461403")
        }

        backBtn.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        return view
    }

    private fun chatWhatsApp(pNumber:String) {
        val uri =
            "https://api.whatsapp.com/send?phone=+234$pNumber"
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(uri)
        }
        startActivity(intent)
    }

    private fun navigateToScreen(accountType: String?) {

        when (accountType) {
            "Realtor" -> {
                val action = R.id.action_becomeAgent_to_profileFragment
                findNavController().navigate(action)
            }
            "Admin" -> {
                val action = R.id.action_becomeAgent_to_adminFragment
                findNavController().navigate(action)
            }

            "Business" -> {
                val action = R.id.action_becomeAgent_to_marketProfile
                findNavController().navigate(action)
            }
        }
    }
}