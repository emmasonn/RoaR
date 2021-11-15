package com.column.roar.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.column.roar.MainActivity
import com.column.roar.R
import com.google.android.material.button.MaterialButton
import timber.log.Timber

class BecomeAgent : Fragment() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         sharedPref = (activity as MainActivity).sharedPref

        val status = sharedPref.getBoolean("logged", false)
        val accountType = sharedPref.getString("accountType", "")

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
            val action = R.id.action_becomeAgent_to_verifyNumber
            findNavController().navigate(action)
        }

        val partnerPhone = sharedPref.getString("partner_phone","")
        joinBtn.setOnClickListener {
          chatWhatsApp(partnerPhone)
        }

        backBtn.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        return view
    }

    private fun chatWhatsApp(pNumber: String?) {
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