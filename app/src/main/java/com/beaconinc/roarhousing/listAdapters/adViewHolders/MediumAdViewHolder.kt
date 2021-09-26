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
import kotlinx.coroutines.launch

class MediumAdViewHolder (val itemView: View): RecyclerView.ViewHolder(itemView) {
    private val mediumAdView = itemView.findViewById<TemplateView>(R.id.mediumAdView)
    fun bind(data: MutableLiveData<NativeAd>, lifecycleOwner: LifecycleOwner, ){
        data.observe(lifecycleOwner, Observer {
            CoroutineScope(Dispatchers.Main).launch {
                mediumAdView.setNativeAd(it)
                mediumAdView.visibility = View.VISIBLE
            }
        })
    }
}