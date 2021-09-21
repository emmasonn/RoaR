package com.beaconinc.roarhousing.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.databinding.FragmentAccommodationBinding
import com.google.android.material.button.MaterialButton


class AccommodationFragment : Fragment() {

    private lateinit var dialog: AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentAccommodationBinding.inflate(inflater, container, false)
        showMessageDialog()
        binding.rulesBtn.setOnClickListener {
            showMessageDialog()
        }

        binding.whatsAppCustomer.setOnClickListener {
            chatWhatsAppCustomer("+23407060461403")
        }

        binding.telegramCustomer.setOnClickListener {
            chatTelegramCustomer()
        }

        binding.whatsAppStore.setOnClickListener {
            productWhatsAppCustomer("+23407060461403")
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



    @SuppressLint("InflateParams")
    private fun showMessageDialog() {
        dialog = requireActivity().let {
                AlertDialog.Builder(it).apply {
                    setCancelable(false)
                    val inflater = LayoutInflater.from(requireContext())
                    val view = inflater.inflate(R.layout.rules_dialog_layout,null)
                    val submit = view.findViewById<MaterialButton>(R.id.confirmBtn)

                    submit.setOnClickListener {
                        dialog.dismiss()
                    }
                    setView(view)
                }.create()
            }
        dialog.show()
    }

    private fun joinWhatAppGroup() {
        val groupIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            val uri = Uri.parse("https://chat.whatsapp.com/KUws1EjUdlmG9j6JhYrepH")
            data = uri
        }
        startActivity(groupIntent)
    }

    private fun chatWhatsAppCustomer(number: String) { //chat accommodation customer care
        val uri = "https://api.whatsapp.com/send?phone="+number
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        startActivity(intent)
    }

    private fun chatTelegramCustomer() { //chat telegram accommodation customer service
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://t.me/StrongCode") //or http://telegram.me/userId
        }
        startActivity(intent)
    }


    private fun joinTelegramGroup() {  //join accommodation telegram group
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("http://t.me/roarAccommodation")
        }
        startActivity(intent)
    }

    private fun productWhatsAppCustomer(number: String) { //chat with product customer care
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?phone"+number)
        }
        startActivity(intent)
    }

}