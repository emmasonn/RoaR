package com.beaconinc.roarhousing.dashBoard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.beaconinc.roarhousing.R

class TermsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_terms, container, false)
        val backKey = view.findViewById<ImageView>(R.id.termBack)

        backKey.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

}