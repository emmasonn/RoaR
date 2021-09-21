package com.beaconinc.roarhousing.lodgeDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto


class ViewLodge: Fragment() {

    private val lodgePicture: FirebaseLodgePhoto by lazy{
        arguments?.get("picture") as FirebaseLodgePhoto
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_lodge, container, false)
        val imageView = view.findViewById<ImageView>(R.id.fullImage)
        val title = view.findViewById<TextView>(R.id.titleText)
        imageView.load(lodgePicture.photoUrl)
        title.text = lodgePicture.photoTitle
        return view
    }

}