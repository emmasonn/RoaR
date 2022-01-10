package com.column.roar.dashBoard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.column.roar.cloudModel.FirebaseUser
import com.column.roar.databinding.FragmentChangePasswordBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


class ChangePassword: Fragment() {

    private val client: FirebaseUser by lazy {
        arguments?.get("Client") as FirebaseUser
    }

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var clientDocument: DocumentReference
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        clientDocument = fireStore.collection("clients").document(client.clientId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        binding.passwordBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.submitBtn.setOnClickListener {
            updatePassword()
        }
        return binding.root
    }


    private fun updatePassword() {
        showProgressBar()
        val oldPass = binding.oldPassword.text.toString()
        val newPass = binding.newPassword.text.toString()
        if (client.password != null
            && client.password == oldPass
        ) {
            clientDocument.update("password", newPass)
                .addOnSuccessListener {
                hideProgressBar()
                Toast.makeText(requireContext(),
                    "Password has been changed",Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        findNavController().navigateUp()
                    }
            }.addOnFailureListener {
                hideProgressBar()
                    Toast.makeText(requireContext(),
                        "Failed to change password",Toast.LENGTH_SHORT).show()
                }
        }else {
            binding.oldPassword.error = "Old password is wrong"
        }
    }

    private fun showProgressBar() {
        binding.submitBtn.alpha = 0.1f
        binding.passwordProgress.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.submitBtn.alpha = 1f
        binding.passwordProgress.visibility = View.GONE
    }

}