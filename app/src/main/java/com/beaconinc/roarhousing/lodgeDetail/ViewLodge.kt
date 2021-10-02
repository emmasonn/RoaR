package com.beaconinc.roarhousing.lodgeDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import coil.load
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto
import com.bumptech.glide.Glide


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
        val pagerBack = view.findViewById<ImageView>(R.id.pagerBack)

        pagerBack.setOnClickListener {
            findNavController().popBackStack()
        }

        Glide.with(imageView.context)
            .load(lodgePicture.photoUrl)
            .into(imageView)

        title.text = lodgePicture.photoTitle
        return view
    }

}