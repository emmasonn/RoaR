package com.column.roar.listAdapters.businessAds


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.listAdapters.businessAds.AutoScrollListAdapter.*
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class AutoScrollListAdapter(private val propertyListener: PropertyListAdapter.PropertyClickListener) :
    ListAdapter<FirebaseProperty, AutoScrollListViewHolder>(
        diffUtil
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoScrollListViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.auto_scroll_items, parent, false)
        return AutoScrollListViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: AutoScrollListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, propertyListener)
    }

    class AutoScrollListViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val brandName = itemView.findViewById<TextView>(R.id.brandName)
        private val specials = itemView.findViewById<TextView>(R.id.specials)
        private val firstImage = itemView.findViewById<ImageView>(R.id.propertyImage)
        private val dotImage = itemView.findViewById<ImageView>(R.id.dotIcon)
        private val resource = itemView.resources

        fun bind(
            data: FirebaseProperty,
            propertyListener: PropertyListAdapter.PropertyClickListener
        ) {

            Glide.with(firstImage.context)
                .load(data.cover).apply(
                    RequestOptions().placeholder(R.drawable.loading_background)
                ).into(firstImage)

            brandName.text = resource.getString(R.string.format_brandName, data.brand)
            specials.text = data.promo

            data.promo?.let {
                if (it.isNotBlank())
                    dotImage.alpha = 1F
            }

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