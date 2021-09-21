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
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter.*
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
        private val propertyPrice = itemView.findViewById<TextView>(R.id.propertyPrice)
        private val firstImage = itemView.findViewById<ImageView>(R.id.firstImage)
        private val campus = itemView.findViewById<TextView>(R.id.campus)
        private val propertyDesc = itemView.findViewById<TextView>(R.id.propertyDesc)
        private val editBtn = itemView.findViewById<ImageView>(R.id.editBtn)
        private val online = itemView.findViewById<MaterialButton>(R.id.online)
        private val notActive = itemView.findViewById<MaterialCardView>(R.id.notActive)

        private val resource = itemView.resources
        fun bind(data: FirebaseProperty, clickListener: PropertyClickListener) {
         firstImage.load(data.firstImage)
            propertyName.text = data.propertyTitle
            propertyPrice.text = resource.getString(R.string.format_price,data.propertyPrice)
            propertyDesc.text = data.propertyDesc
            campus.text = data.campus

            if (data.certified!=null && data.certified == true){
                online.visibility = View.VISIBLE
                editBtn.visibility = View.GONE
                notActive.visibility = View.GONE
            }else {
                online.visibility = View.GONE
                notActive.visibility = View.VISIBLE
                editBtn.visibility = View.VISIBLE
            }

            editBtn.setOnClickListener {
                clickListener.onAction(data)
            }

            itemView.setOnLongClickListener {
                clickListener.onLongClick(data)
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