package com.column.roar.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.column.roar.R
import com.google.android.material.appbar.MaterialToolbar

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val backBtn = view.findViewById<ImageView>(R.id.pagerBack)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolBar)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.suggestBtn -> {
                    sendEmailMessage()
                    true
                }
                else -> {
                    true
                }
            }
        }
        return view
    }

    private fun sendEmailMessage() {
        val intent = Intent(Intent.ACTION_SENDTO,
        Uri.parse("mailto:${getString(R.string.emailUs)}")).apply {
            putExtra(Intent.EXTRA_EMAIL,"")
        }
        startActivity(Intent.createChooser(intent,"RoaR Support Services"))
    }

}