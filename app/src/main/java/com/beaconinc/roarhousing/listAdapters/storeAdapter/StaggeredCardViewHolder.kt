package com.beaconinc.roarhousing.listAdapters.storeAdapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd

class StaggeredCardViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView)  {
    private val productName = itemView.findViewById<TextView>(R.id.product_title)
    private val productPrice = itemView.findViewById<TextView>(R.id.product_price)
    private val productImage = itemView.findViewById<ImageView>(R.id.product_image)
    private val adView = itemView.findViewById<TemplateView>(R.id.smallAdView)
    private val firstAdView = itemView.findViewById<TemplateView>(R.id.smallSecondAdView)
    private val campus = itemView.findViewById<TextView>(R.id.campus)

    private val resource = itemView.resources
    fun bind(data: FirebaseProperty,lifeCycle: LifecycleOwner?,
             propertyListener: PropertyClickListener,
             mutableAdView: MutableLiveData<NativeAd>) {

        Glide.with(productImage.context)
            .load(data.firstImage).apply(
                RequestOptions().placeholder(R.drawable.loading_animation)
                    .error(R.drawable.loading_animation)
            ).into(productImage)

        productName.text = data.propertyTitle
        productPrice.text = resource.getString(R.string.format_price,data.propertyPrice)
        campus.text = data.campus

        lifeCycle?.let {
            mutableAdView.observe(it, Observer { adData ->

                if(firstAdView != null) {
                    adView.visibility = View.VISIBLE
                    firstAdView.visibility = View.VISIBLE
                    adView.setNativeAd(adData)
                    firstAdView.setNativeAd(adData)
                }
            })
        }

//        nativeAd.let { adData ->
//            adView.setNativeAd(adData)
//            firstAdView.setNativeAd(adData)
//            adView.visibility = View.VISIBLE
//            adView.visibility = View.VISIBLE
//        }

        productImage.setOnClickListener {
            propertyListener.onLongClick(data)
        }
    }
}