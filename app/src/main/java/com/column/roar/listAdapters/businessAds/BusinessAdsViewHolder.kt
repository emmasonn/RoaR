package com.column.roar.listAdapters.businessAds

import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.*
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.*

class BusinessAdsViewHolder (val itemView: View): RecyclerView.ViewHolder(itemView) {
    private val businessRecycler = itemView.findViewById<RecyclerView>(R.id.businessAdRecycler)
    private val emptyList = itemView.findViewById<MaterialCardView>(R.id.emptyListView)

    fun bind(data: List<FirebaseProperty>,
             propertyListener: PropertyClickListener
    ) {
        val adapter = AutoScrollListAdapter(
            PropertyClickListener({
                propertyListener.onAction(it)
            },{})
        )

        if (data.size <= 3) {
            businessRecycler.layoutManager = LinearLayoutManager(
                itemView.context, LinearLayoutManager.HORIZONTAL, false
            )
        } else {
            businessRecycler.layoutManager = GridLayoutManager(
                itemView.context,
                2, GridLayoutManager.HORIZONTAL, false
            )
        }

        if(data.isEmpty()){
            emptyList.visibility = View.VISIBLE
        }else{
            emptyList.visibility = View.GONE
        }
        businessRecycler.adapter = adapter
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