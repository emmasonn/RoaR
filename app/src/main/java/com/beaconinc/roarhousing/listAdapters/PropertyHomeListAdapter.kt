package com.beaconinc.roarhousing.listAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.listAdapters.PropertyHomeListAdapter.PropertyHomeListViewHolder
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter.*


//this adapter is for the list of items in the ads section of the homeScreen
class PropertyHomeListAdapter(private val propertyListener: PropertyClickListener) :
    ListAdapter<FirebaseProperty, PropertyHomeListViewHolder>(
        diffUtil
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyHomeListViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_sell_property, parent, false)
        return PropertyHomeListViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: PropertyHomeListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, propertyListener)
    }

    class PropertyHomeListViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val propertyTitle = itemView.findViewById<TextView>(R.id.propertyTitle)
        private val propertyPrice = itemView.findViewById<TextView>(R.id.propertyPrice)
        private val firstImage = itemView.findViewById<ImageView>(R.id.propertyImage)
        private val resource = itemView.resources
        fun bind(data: FirebaseProperty, propertyListener: PropertyClickListener) {
            firstImage.load(data.firstImage)
            propertyTitle.text = data.propertyTitle
            propertyPrice.text = resource.getString(R.string.format_price, data.propertyPrice)

            itemView.setOnClickListener {
                propertyListener.onAction(data)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FirebaseProperty>() {
            override fun areItemsTheSame(
                oldItem: FirebaseProperty,
                newItem: FirebaseProperty
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: FirebaseProperty,
                newItem: FirebaseProperty
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}