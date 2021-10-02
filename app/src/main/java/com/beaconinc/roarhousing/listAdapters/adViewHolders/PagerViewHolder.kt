package com.beaconinc.roarhousing.listAdapters.adViewHolders

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto
import com.beaconinc.roarhousing.cloudModel.FirebasePhotoAd
import com.beaconinc.roarhousing.lodgeDetail.PagerPhotoSlide
import com.google.android.gms.ads.nativead.NativeAd
import okhttp3.internal.addHeaderLenient

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
