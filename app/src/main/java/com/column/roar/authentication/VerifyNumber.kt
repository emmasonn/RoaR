package com.column.roar.authentication

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseUser
import com.column.roar.databinding.FragmentVerifyNumberBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber


class VerifyNumber : Fragment() {

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var agents: CollectionReference
    private lateinit var binding: FragmentVerifyNumberBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        agents = fireStore.collection("clients")
        sharedPref = (activity as MainActivity).sharedPref
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerifyNumberBinding.inflate(inflater,container,false)

        binding.nextBtn.setOnClickListener {
            verifyNumber()
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

   private fun verifyNumber() {
       showProgress()
       val number = binding.phoneNumber.text.toString()
       Timber.i("+234${number}")
       agents.whereEqualTo("clientPhone",number)
           .get().addOnSuccessListener { value ->
               if(value.documents.isNotEmpty()) {
                   value.documents.first().also {
                    val user =   it.toObject(FirebaseUser::class.java) as FirebaseUser
                       storeUserId(user.clientId!!,user.clientUrl)
                       val bundle = bundleOf("verify" to user)
                       findNavController().navigate(R.id.verifyPassword, bundle)
                   }
               }else {
                   Toast.makeText(requireContext(),"Phone Number does not exist",Toast.LENGTH_SHORT).show()
                   hideProgress()
               }
       }.addOnFailureListener {
           hideProgress()
           Toast.makeText(requireContext(),"Network Error",Toast.LENGTH_SHORT).show()
       }
   }

    private fun storeUserId(id: String, url: String?) {
        with(sharedPref.edit()){
            putString("user_id", id)
            putString("user_url",url)
            apply()
        }
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
        binding.nextBtn.alpha = 0.1f
    }

    private fun hideProgress() {
        lifecycleScope.launchWhenCreated {
            binding.progressBar.visibility = View.GONE
            binding.nextBtn.alpha = 1f
        }
    }
}