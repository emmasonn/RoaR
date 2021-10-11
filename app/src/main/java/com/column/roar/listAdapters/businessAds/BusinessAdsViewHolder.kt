package com.column.roar.listAdapters.businessAds

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.*
import kotlinx.coroutines.*

class BusinessAdsViewHolder (val itemView: View): RecyclerView.ViewHolder(itemView) {
    private val recycleView = itemView.findViewById<RecyclerView>(R.id.businessAdRecycler)

    fun bind(data: List<FirebaseProperty>,
             propertyListener: PropertyClickListener
    ) {
        val adapter = AutoScrollListAdapter(
            PropertyClickListener({
                propertyListener.onAction(it)
            },{})
        )
        recycleView.adapter = adapter
        adapter.submitList(data)

//        CoroutineScope(Dispatchers.Main).launch {
//            autoScrollFeature(recycleView,adapter)
//        }

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