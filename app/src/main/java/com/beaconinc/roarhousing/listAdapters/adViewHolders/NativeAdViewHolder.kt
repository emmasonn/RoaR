package com.beaconinc.roarhousing.listAdapters.adViewHolders

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class NativeAdViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val smallAdView = itemView.findViewById<TemplateView>(R.id.smallAdView)
    private val loadingImage = itemView.findViewById<ImageView>(R.id.loadingImageView)

    fun bind(data: MutableLiveData<NativeAd>, lifecycleOwner: LifecycleOwner) {

        Glide.with(loadingImage.context)
            .load(R.drawable.loading_animation_medium)
            .into(loadingImage)

        data.observe(lifecycleOwner, {
            CoroutineScope(Dispatchers.Main).launch {
                smallAdView.setNativeAd(it)
                loadingImage.visibility = View.GONE

            }
        })
    }
}