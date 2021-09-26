package com.beaconinc.roarhousing.listAdapters.adViewHolders

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class NativeAdViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView) {
   private val smallAdView = itemView.findViewById<TemplateView>(R.id.smallAdView)

    fun bind(data: MutableLiveData<NativeAd>,lifecycleOwner: LifecycleOwner) {
        data.observe(lifecycleOwner, Observer {
            CoroutineScope(Dispatchers.Main).launch {
                smallAdView.setNativeAd(it)
                smallAdView.visibility = View.VISIBLE
            }
        })
    }
}