package com.column.roar.listAdapters

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
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ManagePropertyListAdapter(private val clickListener: PropertyClickListener ):
    ListAdapter<FirebaseProperty, ManagePropertyListAdapter.ManagePropertyVieHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagePropertyVieHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ManagePropertyVieHolder(inflater.inflate(R.layout.item_property_edit,parent,false))
    }

    override fun onBindViewHolder(holder: ManagePropertyVieHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data, clickListener)
    }

    class ManagePropertyVieHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val propertyName = itemView.findViewById<TextView>(R.id.propertyTitle)
        private val propertyPrice = itemView.findViewById<TextView>(R.id.productPrice)
        private val firstImage = itemView.findViewById<ImageView>(R.id.firstImage)
        private val campus = itemView.findViewById<TextView>(R.id.campus)
        private val propertyDesc = itemView.findViewById<TextView>(R.id.propertyDesc)
        private val editBtn = itemView.findViewById<ImageView>(R.id.editBtn)
        private val online = itemView.findViewById<MaterialButton>(R.id.online)
        private val notActive = itemView.findViewById<MaterialCardView>(R.id.notActive)
        private val ownerPhone = itemView.findViewById<TextView>(R.id.ownerPhone)
        private val agentName = itemView.findViewById<TextView>(R.id.agentName)
        private val postDate = itemView.findViewById<TextView>(R.id.postDate)

        private val resource = itemView.resources
        fun bind(data: FirebaseProperty, clickListener: PropertyClickListener) {

            Glide.with(firstImage.context)
                .load(data.cover).apply(
                    RequestOptions().placeholder(R.drawable.animated_gradient)
                        .error(R.drawable.animated_gradient)
                ).into(firstImage)
            propertyName.text = data.product
            propertyPrice.text = resource.getString(R.string.format_price,data.price)
            propertyDesc.text = data.description
            campus.text = data.campus
            ownerPhone.text = data.owner
            agentName.text = data.brand

            if (data.certified!=null && data.certified == true){
                online.visibility = View.VISIBLE
                editBtn.visibility = View.GONE
                notActive.visibility = View.GONE
            } else {
                online.visibility = View.GONE
                notActive.visibility = View.VISIBLE
                editBtn.visibility = View.VISIBLE
            }

            editBtn.setOnClickListener {
                clickListener.onAction(data)
            }

            itemView.setOnClickListener {
                clickListener.onJustClick(data,"business")
            }

            itemView.setOnLongClickListener {
                clickListener.onTab(data)
                true
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