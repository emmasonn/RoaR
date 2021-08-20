package com.beaconinc.roarhousing.dashBoard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.google.android.material.button.MaterialButton


class ManageLodgeDetail : Fragment() {

    val lodge: FirebaseLodge by lazy {
        arguments?.get("Lodge") as FirebaseLodge
    }


    //Invalidate cache to make data binding available
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.item_lodge_detail, container, false)
        val editBtn = view.findViewById<MaterialButton>(R.id.materialButton)

        editBtn.setOnClickListener {
            val bundle = bundleOf("lodgeId" to lodge, "editing" to true )
            findNavController().navigate(R.id.lodgeDetailUpload, bundle)
        }

        return view
    }
}