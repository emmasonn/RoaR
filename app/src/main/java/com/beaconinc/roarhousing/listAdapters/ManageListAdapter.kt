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
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.google.android.material.button.MaterialButton

class ManageListAdapter (private val lodgeClickListener:
                         LodgeClickListener): ListAdapter<FirebaseLodge, ManageListAdapter.ManageViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ManageViewHolder(inflater.inflate(R.layout.item_manage_list,parent,false))
    }

    override fun onBindViewHolder(holder: ManageViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data, lodgeClickListener)
    }

    class ManageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.lodgeCover)
        private val lodgeTitle = itemView.findViewById<TextView>(R.id.lodgeTitle)
        private val viewBtn = itemView.findViewById<MaterialButton>(R.id.viewBtn)
        private val location = itemView.findViewById<TextView>(R.id.lodgeLocation)
        private val campus = itemView.findViewById<TextView>(R.id.campus)
        private val availableRoom = itemView.findViewById<TextView>(R.id.availNumber)

        fun bind(data: FirebaseLodge, listener: LodgeClickListener) {
            lodgeTitle.text = data.lodgeName
            location.text = data.location
            campus.text = data.campus
            availableRoom.text = data.availableRoom.toString()
            //expire.text = data.date.toString()

            viewBtn.setOnClickListener {
                listener.clickAction(data)
            }
            imageView.load(data.coverImage)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FirebaseLodge>() {
            override fun areItemsTheSame(oldItem: FirebaseLodge, newItem: FirebaseLodge): Boolean {
                return oldItem.lodgeId == newItem.lodgeId
            }

            override fun areContentsTheSame(
                oldItem: FirebaseLodge,
                newItem: FirebaseLodge
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}