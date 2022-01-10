package com.column.roar.dashBoard.lodge

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseUser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit


class ProfileFragment : Fragment() {

    private lateinit var userDocument: DocumentReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var profileImage: ImageView
    private lateinit var fullName: TextView
    private lateinit var sharedPref: SharedPreferences
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
        fullName = view.findViewById<TextView>(R.id.fullName)
        profileImage = view.findViewById<ImageView>(R.id.profilePics)
        progressBar = view.findViewById(R.id.progressBackground)
        phoneNumber = view.findViewById(R.id.phoneNumber)
        val navView = view.findViewById<NavigationView>(R.id.navView)
        val drawer = view.findViewById<DrawerLayout>(R.id.drawerNav)
        val menu = view.findViewById<ImageView>(R.id.adminMenu)

        menu.setOnClickListener {
            drawer.open()
        }

        fullName.text = sharedPref.getString("user_name", "")
        val imageUrl = sharedPref.getString("user_url", "")
        profileImage.load(imageUrl)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.exitProfile -> {
                    findNavController().popBackStack(R.id.homeFragment,false)
                    true
                }

                R.id.career -> {
                    val action = R.id.action_profileFragment_to_careerFragment
                    findNavController().navigate(action)
                    true
                }

                R.id.callAdmin -> {
                    val phone = sharedPref.getString("partner_phone",null)
                    if(phone == null) {
                       Toast.makeText(requireContext(),"Not available at the moment",
                           Toast.LENGTH_SHORT).show()
                    }else {
                        callDialog(phone)
                    }
                    true
                }

                R.id.termsAndCondition -> {
                    findNavController().navigate(R.id.termsFragment)
                    true
                }

                R.id.manageAccount -> {
                    if(::client.isInitialized) {
                        val bundle = bundleOf("Client" to client)
                        findNavController().navigate(R.id.manageAccount,bundle)
                    }
                    true
                }

                R.id.signOut -> {
                    drawer.close()
                    showSignOutDialog()
                    true
                }

                else -> {
                    true
                }
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
         listenerForChange()
    }

    private fun listenerForChange() {
        showProgress()
        if(::userDocument.isInitialized) {
            userDocument.get().addOnSuccessListener {
                it.toObject(FirebaseUser::class.java).also { user ->
                    client = user!!
                    phoneNumber.text = user.clientPhone
//                    computeRemainingDay(client.expired!!)
                    hideProgress()
                }
            }
        }
    }

    private fun userLoggedState(state: Boolean) {
        with(sharedPref.edit()){
            putBoolean("logged", state)
            apply()
        }
    }

    //show dialog for calling realtor
    private fun callDialog(number: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("You are about call help desk")
            setPositiveButton("Okay") { dialog, _ ->
                dialog.dismiss()
                dialPhoneNumber(number)
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun dialPhoneNumber(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber") //or use Uri.fromParts()
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
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
                findNavController().popBackStack(R.id.homeFragment,false)
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