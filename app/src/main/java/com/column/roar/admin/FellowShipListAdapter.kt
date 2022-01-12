package com.column.roar.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.column.roar.R
import com.column.roar.admin.FellowShipListAdapter.*
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter

class FellowShipListAdapter(private val propertyListener: PropertyListAdapter.PropertyClickListener) :
    ListAdapter<FirebaseProperty, FellowShipListViewHolder>(
        diffUtil
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FellowShipListViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.fellowship_list_items, parent, false)
        return FellowShipListViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: FellowShipListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, propertyListener)
    }

    class FellowShipListViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firstImage = itemView.findViewById<ImageView>(R.id.fellowImage)

        fun bind(
            data: FirebaseProperty,
            propertyListener: PropertyListAdapter.PropertyClickListener
        ) {

            Glide.with(firstImage.context)
                .load(data.cover).apply(
                    RequestOptions().placeholder(R.drawable.loading_background)
                ).into(firstImage)

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