package com.beaconinc.roarhousing.admin

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
import com.beaconinc.roarhousing.databinding.FragmentAdminBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

class AdminFragment : Fragment() {

    private lateinit var binding: FragmentAdminBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var clientRef: DocumentReference
    private lateinit var navView: NavigationView
    private lateinit var client: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = (activity as MainActivity).sharedPref
        fireStore = FirebaseFirestore.getInstance()
        val clientId = sharedPref.getString("user_id","")
        clientRef  = fireStore.collection("clients").document(clientId!!)

        val accountType = sharedPref.getString("accountType","")
        userLogged(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminBinding.inflate(inflater, container, false)
 //        binding.fullName.text = sharedPref.getString("user_name", "")
//        val imageUrl = sharedPref.getString("user_url","")
//        binding.profilePics.load(imageUrl)
        showProgressBar()

        val drawer = binding.drawerNav
        val menu = binding.adminMenu
        navView = binding.navView

        menu.setOnClickListener {
            drawer.open()
        }

        val accountType = sharedPref.getString("accountType","")
        val adminsItem = navView.menu.findItem(R.id.adminsFragment)

        if(accountType != "Super Admin") {
            adminsItem.isVisible = false
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.exitProfile -> {
                    findNavController().popBackStack(R.id.homeFragment,false)
                    true
                }

                R.id.addProduct -> {
                    findNavController().navigate(R.id.uploadProperty)
                    true
                }

                R.id.addLodge -> {
                    findNavController().navigate(R.id.lodgeDetailUpload)
                    true
                }

                R.id.businessFragment -> {
                    drawer.close()
                    findNavController().navigate(R.id.businessFragment)
                    true
                }

                R.id.broadCastLodge -> {
                    drawer.close()
                    findNavController().navigate(R.id.notifyFragment)
                    true
                }

                R.id.broadCastProduct -> {
                    drawer.close()
                    findNavController().navigate(R.id.broadCastProduct)
                    true
                }

                R.id.realtorFragment -> {
                    drawer.close()
                    findNavController().navigate(R.id.realtorFragment)
                    true
                }

                R.id.adminsFragment -> {
                    drawer.close()
                    true
                }

                R.id.setUpFragment -> {
                    drawer.close()
                    findNavController().navigate(R.id.setUpFragment)
                    true
                }

                R.id.manageAccount -> {
                    drawer.close()
                    if(::client.isInitialized) {
                        val bundle = bundleOf("Client" to client)
                        findNavController().navigate(R.id.manageAccount,bundle)
                    }
                    true
                }

                R.id.signOut -> {
                    showSignOutDialog()
                    true
                }

                else -> {
                    true
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            fetchClients()
        }

    private fun fetchClients() {
        clientRef.get().addOnSuccessListener { value ->
            value?.toObject(FirebaseUser::class.java).also { firebaseUser ->
                client = firebaseUser!!
                binding.profilePics.load(firebaseUser.clientUrl)
                binding.fullName.text = firebaseUser.clientName
                binding.phoneNumber.text = firebaseUser.clientPhone
                hideProgressBar()
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun userLogged(state: Boolean) {
        with(sharedPref.edit()) {
            putBoolean("logged",state)
            apply()
        }
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("You're About to Sign out")
            setPositiveButton("Okay") { dialog , _ ->
                dialog.dismiss()
                userLogged(false)
                findNavController().popBackStack(R.id.becomeAgent,false)
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

}