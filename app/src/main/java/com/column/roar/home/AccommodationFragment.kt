package com.column.roar.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseUser
import com.column.roar.databinding.FragmentAccommodationBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class AccommodationFragment : Fragment() {
    private lateinit var dialog: AlertDialog
//    private var customerAgent: FirebaseUser? = null
//    private lateinit var fireStore: FirebaseFirestore
//    private lateinit var customerDoc: DocumentReference
    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        fireStore = FirebaseFirestore.getInstance()
//        customerDoc = fireStore.collection("clients").document("customer")
        sharedPref = (activity as MainActivity).sharedPref
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentAccommodationBinding.inflate(inflater, container, false)
        showMessageDialog()

        val realtorComplaint = sharedPref.getString("realtor_complaint","")
        val businessComplaint = sharedPref.getString("business_complaint","")


        binding.textBtn.setOnClickListener {
            showMessageDialog()
        }

        binding.whatsAppCustomer.setOnClickListener {
                chatWhatsAppCustomer(realtorComplaint)
        }

        binding.telegramCustomer.setOnClickListener {
            chatTelegramCustomer()
        }

        binding.whatsAppStore.setOnClickListener {
                productWhatsAppCustomer(businessComplaint)
        }

        binding.whatsAppGroup.setOnClickListener {
            joinWhatAppGroup()
        }

        binding.telegramGroup.setOnClickListener {
             joinTelegramGroup()
        }

        binding.accomBack.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

//    private fun fetchCustomer() {
//        val source = Source.DEFAULT
//        customerDoc.get(source).addOnSuccessListener {
//            it.toObject(FirebaseUser::class.java).also { user ->
//                customerAgent = user
//            }
//        }.addOnFailureListener {
//
//        }
//    }

    @SuppressLint("InflateParams")
    private fun showMessageDialog() {
        dialog = requireActivity().let {
                AlertDialog.Builder(it).apply {
                    setCancelable(false)
                    val inflater = LayoutInflater.from(requireContext())
                    val view = inflater.inflate(R.layout.rules_dialog_layout,null)
                    val submit = view.findViewById<MaterialButton>(R.id.confirmBtn)

                    submit.setOnClickListener {
//                        fetchCustomer()
                        dialog.dismiss()
                    }
                    setView(view)
                }.create()
            }
        dialog.show()
    }

    //Group whats app group
    private fun joinWhatAppGroup() {
        val groupIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            val uri = Uri.parse("https://chat.whatsapp.com/KUws1EjUdlmG9j6JhYrepH")
            data = uri
        }
        try {
            startActivity(groupIntent)
        }catch (ex: android.content.ActivityNotFoundException){
            Toast.makeText(requireContext(),"WhatsApp is not Found",
                Toast.LENGTH_SHORT).show()
        }
    }

    //chat customer whats-app group
    private fun chatWhatsAppCustomer(number: String?) { //chat accommodation customer care
        val uri = "https://api.whatsapp.com/send?phone=+234$number"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        try {
            startActivity(intent)
        }catch (ex: android.content.ActivityNotFoundException){
            Toast.makeText(requireContext(),"WhatsApp is not Found",
                Toast.LENGTH_SHORT).show()
        }
    }

    //chat group link
    private fun chatTelegramCustomer() { //chat telegram accommodation customer service
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://t.me/StrongCode") //or http://telegram.me/userId
        }
        try {
            startActivity(intent)
        }catch (ex: android.content.ActivityNotFoundException){
            Toast.makeText(requireContext(),"Telegram is not Found",
                Toast.LENGTH_SHORT).show()
        }
    }

    //Telegram group link
    private fun joinTelegramGroup() {  //join accommodation telegram group
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("http://t.me/roarAccommodation")
        }
        try {
            startActivity(intent)
        }catch (ex: android.content.ActivityNotFoundException){
            Toast.makeText(requireContext(),"Telegram is not Found",
                Toast.LENGTH_SHORT).show()
        }
    }

    //product group link
    private fun productWhatsAppCustomer(number: String?) { //chat with product customer care
        val uri = "https://api.whatsapp.com/send?phone=+234$number"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(uri)
        }
        try {
            startActivity(intent)
        }catch (ex: android.content.ActivityNotFoundException){
            Toast.makeText(requireContext(),"Telegram is not Found",
                Toast.LENGTH_SHORT).show()
        }
    }
}