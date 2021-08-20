package com.beaconinc.roarhousing.lodgeDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import coil.load
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto

private const val PHOTO = "param"


class PagerPhotoSlide : Fragment() {
    private var lodgePhoto: FirebaseLodgePhoto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lodgePhoto = it.get(PHOTO) as FirebaseLodgePhoto
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pager_photo_slide, container, false)
        val lodgeImageView = view.findViewById<ImageView>(R.id.photoItem)
        val viewType = view.findViewById<TextView>(R.id.viewType)
        viewType.text = lodgePhoto?.photoTitle

        Toast.makeText(requireContext(),
            "imgUrl: ${lodgePhoto?.photoUrl}",Toast.LENGTH_SHORT).show()

        lodgeImageView.load(lodgePhoto?.photoUrl)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param: FirebaseLodgePhoto) =
            PagerPhotoSlide().apply {
                arguments = Bundle().apply {
                    putParcelable(PHOTO, param)
                }
            }
    }
}