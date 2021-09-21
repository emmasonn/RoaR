package com.beaconinc.roarhousing.authentication

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.beaconinc.roarhousing.databinding.FragmentVerifyPasswordBinding
import com.google.android.gms.common.internal.AccountType
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


class VerifyPassword : Fragment() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var agentId: String
    private lateinit var clientRef:DocumentReference
    private lateinit var fireStore: FirebaseFirestore
    private var passCode: String? = null
    private var clientId: String? = null
    private var accountType: String? = null
    private var userUrl: String? = null
    private var userName: String? = null
    private lateinit var binding: FragmentVerifyPasswordBinding
    private var agentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        sharedPref = (activity as MainActivity).sharedPref
        agentId = sharedPref.getString("user_id","") as String
        clientRef = fireStore.collection("clients").document(agentId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerifyPasswordBinding.inflate(inflater, container, false)
        getUser()
        val url = sharedPref.getString("user_url", null)
        binding.clientImage.load(url)
        binding.continueBtn.setOnClickListener {
            val password = binding.passwordField.text.toString()
            if(passCode!=null && passCode == password) {
                 storeCurrentId(clientId!!, accountType!!)
                 navigate()
            }else {
                binding.passwordInput.error = "Incorrect password"
            }
        }
        binding.notMeBtn.setOnClickListener {
            val action = R.id.action_verifyPassword_to_verifyNumber
            findNavController().navigate(action)
        }
        return binding.root
    }

    private fun getUser() {
        showProgress()
        clientRef.get().addOnSuccessListener {
           val user = it.toObject(FirebaseUser::class.java) as FirebaseUser
            passCode = user.password
            agentUser = user
            clientId = user.clientId
            userUrl = user.clientUrl
            userName = user.clientName
            accountType = user.accountType
            hideProgress()
        }
    }

    private fun storeCurrentId(id: String, accountType: String) {
        with(sharedPref.edit()){
            putString("user_id",id)
            putString("user_url", userUrl)
            putString("user_name", userName)
            putString("accountType", accountType)
            apply()
        }
    }

    fun navigate() {
        //val bundle = bundleOf("Client" to agentUser)
        when(accountType) {
            "Business" -> {
                findNavController().navigate(R.id.marketProfile)
            }
            "Realtor" -> {
                val action = R.id.action_verifyPassword_to_profileFragment
                findNavController().navigate(action)
            }
            "Admin" -> {
                findNavController().navigate(R.id.adminFragment)
            }
        }
    }

    private fun showProgress() {
        binding.continueBtn.alpha = 0.1f
        binding.bigProgressBar.visibility = View.VISIBLE
    }

   private fun hideProgress() {
        lifecycleScope.launch {
            binding.continueBtn.alpha = 1f
            binding.bigProgressBar.visibility = View.GONE
        }
    }


}