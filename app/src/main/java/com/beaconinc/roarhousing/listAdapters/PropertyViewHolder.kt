package com.beaconinc.roarhousing.listAdapters

import android.view.View
import android.widget.Toast
import androidx.navigation.navDeepLink

import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.home.RoarStore
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter.*
import timber.log.Timber


//view Holder for home product recyclerview
class PropertyViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView) {
     private val recycleView = itemView.findViewById<RecyclerView>(R.id.advertPropertyRecycler)
    fun bind(data: List<FirebaseProperty>,
             propertyListener: PropertyClickListener) {

        val adapter = PropertyHomeListAdapter(
            PropertyClickListener({
                 Timber.i("Clicked")
                 propertyListener.onAction(it)
            },{}))

        recycleView.adapter = adapter
        adapter.submitList(data)

    }
}