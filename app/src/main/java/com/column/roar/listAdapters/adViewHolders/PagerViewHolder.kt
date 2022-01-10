package com.column.roar.listAdapters.adViewHolders

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.column.roar.cloudModel.FirebasePhotoAd
import com.column.roar.lodgeDetail.PagerPhotoSlide
import com.google.android.gms.ads.nativead.NativeAd

class PagerViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
  fun bind(photoAds:List<FirebasePhotoAd>,
           ad: NativeAd,
           fragment: Fragment
  ) {
     val pagerAdapter = PhotosPager(photoAds,fragment,ad)
  }
}

class PhotosPager(
    private val photos: List<FirebasePhotoAd>,
    fragment: Fragment,
    private val adData: NativeAd
):
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = photos.size

    override fun createFragment(position: Int): Fragment =
        PagerPhotoSlide.newInstance(photos[position], adData)
}
