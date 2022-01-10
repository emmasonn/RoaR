package com.column.roar.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseUser
import com.column.roar.databinding.FragmentVerifyPasswordBinding
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

        Glide.with(binding.clientImage.context)
            .load(url)
            .apply(
                RequestOptions().placeholder(R.drawable.ic_person)
            ).into(binding.clientImage)

        binding.forgotPass.setOnClickListener {
            sendEmailMessage()
        }

        binding.continueBtn.setOnClickListener {
            val password = binding.passwordField.text.toString()
            if(passCode!=null && passCode == password) {
                 storeCurrentId(clientId!!, accountType!!)
                 navigate()
            }else {
                binding.passwordInput.error = "Incorrect password"
            }
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.notMeBtn.setOnClickListener {
            findNavController().popBackStack()
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
            userUrl = user.clientImage
            userName = user.clientName
            accountType = user.account
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
        val navOption: NavOptions = NavOptions.Builder().setPopUpTo(R.id.homeFragment,false).build()

        when(accountType) {
            "Business" -> {
                findNavController().navigate(R.id.marketProfile,null, navOption)
            }
            "Realtor" -> {
                findNavController().navigate(R.id.profileFragment,null, navOption)
            }
            "Admin" -> {
                findNavController().navigate(R.id.adminFragment,null, navOption)
            }
        }
    }

    private fun sendEmailMessage() {
        val intent = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("mailto:${getString(R.string.emailUs)}")).apply {
            putExtra(Intent.EXTRA_EMAIL,"Password Reset")
        }
        startActivity(Intent.createChooser(intent,"RoaR Support Services"))
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