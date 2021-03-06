package com.column.roar.lodgeDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import coil.load
import com.column.roar.R
import com.column.roar.cloudModel.FirebasePhotoAd
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd

private const val PHOTO = "param"


class PagerPhotoSlide : Fragment() {

    private lateinit var adPhoto: FirebasePhotoAd
    private lateinit var adImageView: ImageView
    private lateinit var mediumNativeAdView: TemplateView
    private lateinit var nativeAd: NativeAd

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pager_photo_slide, container, false)
        adImageView = view.findViewById<ImageView>(R.id.photoItem)
        mediumNativeAdView = view.findViewById(R.id.pagerMediumAd)

        Toast.makeText(requireContext(),
            "imgUrl: ${adPhoto.image}",Toast.LENGTH_SHORT).show()
        adImageView.load(adPhoto.image)

        mediumNativeAdView.setNativeAd(nativeAd)
        if(isDetached) {
            nativeAd.destroy()
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(photoAd: FirebasePhotoAd, _nativeAd: NativeAd) =
            PagerPhotoSlide().apply {
                adPhoto = photoAd
                nativeAd = _nativeAd
            }
    }
}