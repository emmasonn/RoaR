package com.column.roar.listAdapters.adViewHolders

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.column.roar.R
import com.bumptech.glide.Glide
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediumAdViewHolder (val itemView: View): RecyclerView.ViewHolder(itemView) {
    private val mediumAdView = itemView.findViewById<TemplateView>(R.id.mediumAdView)
//            private val loadingImage = itemView.findViewById<ImageView>(R.id.loadingImageView)

        fun bind(data: MutableLiveData<NativeAd>, lifecycleOwner: LifecycleOwner, ){
//            Glide.with(loadingImage.context)
//                .load(R.drawable.loading_animation_medium)
//                .into(loadingImage)
        data.observe(lifecycleOwner, {
            CoroutineScope(Dispatchers.Main).launch {
                mediumAdView.setNativeAd(it)
//                mediumAdView.visibility = View.VISIBLE
//                loadingImage.visibility = View.GONE
            }
        })
    }
}