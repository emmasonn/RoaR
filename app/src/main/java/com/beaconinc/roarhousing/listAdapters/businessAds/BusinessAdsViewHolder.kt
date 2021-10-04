package com.beaconinc.roarhousing.listAdapters.businessAds

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory.DIRECTION_RIGHT
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter
import kotlinx.coroutines.*
import timber.log.Timber

class BusinessAdsViewHolder (val itemView: View): RecyclerView.ViewHolder(itemView) {
    private val recycleView = itemView.findViewById<RecyclerView>(R.id.businessAdRecycler)

    fun bind(data: List<FirebaseProperty>,
             propertyListener: PropertyListAdapter.PropertyClickListener
    ) {
        val adapter = AutoScrollListAdapter(
            PropertyListAdapter.PropertyClickListener({
                Timber.i("Clicked")
                propertyListener.onAction(it)
            },{})
        )
        recycleView.adapter = adapter
        adapter.submitList(data)

//        recycleView.post {
//            recycleView.smoothScrollToPosition(adapter.itemCount)
//        }

    }

    private tailrec suspend fun autoScrollFeature(recyclerView: RecyclerView, adapter: AutoScrollListAdapter) {
        if(recyclerView.canScrollHorizontally(1)){
            recyclerView.smoothScrollBy(5,0)
        }else {
            val firstPosition =
                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if(firstPosition != RecyclerView.NO_POSITION) {
                val currentList = adapter.currentList
                val secondPart = currentList.subList(0, firstPosition)
                val firstPart= currentList.subList(firstPosition,currentList.size)
                adapter.submitList(firstPart + secondPart)
            }
        }

        delay(25L)
        autoScrollFeature(recyclerView,adapter)

    }
}