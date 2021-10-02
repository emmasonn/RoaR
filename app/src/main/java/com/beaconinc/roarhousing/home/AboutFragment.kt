package com.beaconinc.roarhousing.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.beaconinc.roarhousing.R
import com.google.android.material.appbar.MaterialToolbar

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val backBtn = view.findViewById<ImageButton>(R.id.pagerBack)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolBar)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.suggestBtn -> {
                    true
                }
                else -> {
                    true
                }
            }
        }
        return view
    }


}