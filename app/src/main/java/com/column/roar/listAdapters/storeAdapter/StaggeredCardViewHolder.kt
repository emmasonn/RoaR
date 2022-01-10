package com.column.roar.listAdapters.storeAdapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd

class StaggeredCardViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView)  {
    private val productName = itemView.findViewById<TextView>(R.id.product_title)
    private val productPrice = itemView.findViewById<TextView>(R.id.product_price)
    private val productImage = itemView.findViewById<ImageView>(R.id.product_image)
    private val firstAdView = itemView.findViewById<TemplateView>(R.id.smallSecondAdView)
    private val campus = itemView.findViewById<TextView>(R.id.campus)

    private val resource = itemView.resources
    fun bind(data: FirebaseProperty,lifeCycle: LifecycleOwner?,
             propertyListener: PropertyClickListener,
             mutableAdView: MutableLiveData<NativeAd>) {

        Glide.with(productImage.context)
            .load(data.cover).apply(
                RequestOptions().placeholder(R.drawable.loading_background)
                    .error(R.drawable.loading_background)
            ).into(productImage)

        productName.text = data.product
        productPrice.text = resource.getString(R.string.format_price,data.price)
        campus.text = data.campus

        lifeCycle?.let {
            mutableAdView.observe(it, Observer { adData ->
                firstAdView?.setNativeAd(adData)
            })
        }

        productImage.setOnClickListener {
            propertyListener.onTab(data)
        }
    }
}