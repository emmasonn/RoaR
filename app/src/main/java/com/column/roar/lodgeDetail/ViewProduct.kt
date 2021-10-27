package com.column.roar.lodgeDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.column.roar.R


class ViewProduct : Fragment() {

    private val lodgeImageUrl:  String by lazy{
        arguments?.get("picture") as String
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_product, container, false)
        val fullImage = view.findViewById<ImageView>(R.id.fullImage)

        Glide.with(fullImage.context)
            .load(lodgeImageUrl)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.animated_gradient)
                .error(R.drawable.animated_gradient)).into(fullImage)

        return view
    }

}