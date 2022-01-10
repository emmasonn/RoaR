package com.column.roar.listAdapters

import android.view.View

import androidx.recyclerview.widget.RecyclerView
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.*
import com.google.android.material.card.MaterialCardView
import timber.log.Timber


//view Holder for home product recyclerview...in form of ads
class PropertyViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView) {
     private val recycleView = itemView.findViewById<RecyclerView>(R.id.advertPropertyRecycler)
     private val emptyList = itemView.findViewById<MaterialCardView>(R.id.emptyListView)

    fun bind(data: List<FirebaseProperty>,
             propertyListener: PropertyClickListener) {

        val adapter = PropertyHomeListAdapter(
            PropertyClickListener({
                 propertyListener.onAction(it)
            },{}))
        recycleView.adapter = adapter
        if(data.isNullOrEmpty()) {
            emptyList.visibility = View.VISIBLE
        }else {
            emptyList.visibility = View.GONE
            adapter.submitList(data)
        }
    }
}