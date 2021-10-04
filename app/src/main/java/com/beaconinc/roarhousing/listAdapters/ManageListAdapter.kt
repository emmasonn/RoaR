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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ManageListAdapter (private val manageAdapterListener: ManageAdapterListener): ListAdapter<FirebaseLodge, ManageListAdapter.ManageViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ManageViewHolder(inflater.inflate(R.layout.item_manage_list,parent,false))
    }

    override fun onBindViewHolder(holder: ManageViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data, manageAdapterListener)
    }

    class ManageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.lodgeCover)
        private val lodgeTitle = itemView.findViewById<TextView>(R.id.lodgeTitle)
        private val viewBtn = itemView.findViewById<MaterialButton>(R.id.viewBtn)
        private val location = itemView.findViewById<TextView>(R.id.lodgeLocation)
        private val campus = itemView.findViewById<TextView>(R.id.campus)
        private val availableRoom = itemView.findViewById<TextView>(R.id.availNumber)
        private val ownerName = itemView.findViewById<TextView>(R.id.ownerName)
        private val ownerPhone = itemView.findViewById<TextView>(R.id.ownerPhone)
        private val updatePhone = itemView.findViewById<MaterialButton>(R.id.updateRoomBtn)
        private val notActive = itemView.findViewById<MaterialCardView>(R.id.notActive)
        private val onLine = itemView.findViewById<MaterialButton>(R.id.online)

        fun bind(data: FirebaseLodge, listener: ManageAdapterListener) {

            Glide.with(imageView.context)
                .load(data.coverImage).apply(
                    RequestOptions().placeholder(R.drawable.loading_animation)
                        .error(R.drawable.loading_animation)
                ).into(imageView)

            lodgeTitle.text = data.lodgeName
            location.text = data.location
            campus.text = data.campus
            ownerName.text = data.ownerName
            ownerPhone.text = data.ownerPhone
            availableRoom.text = data.availableRoom.toString()

            if(data.certified!=null && data.certified == true) {
                onLine.visibility = View.VISIBLE
                notActive.visibility = View.GONE
            }else {
                onLine.visibility = View.GONE
                notActive.visibility = View.VISIBLE
            }

            itemView.setOnLongClickListener {
                listener.otherAction(data)
                true
            }

            viewBtn.setOnClickListener {
                listener.updateDetail(data)
            }

            updatePhone.setOnClickListener {
                listener.updateRoom(data)
            }
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

class ManageAdapterListener (
    val listener: (lodge: FirebaseLodge) -> Unit,
    val roomListener: (lodge: FirebaseLodge) -> Unit,
    val otherListener:(lodge: FirebaseLodge) -> Unit
    ) {
    fun updateDetail(lodge: FirebaseLodge) = listener(lodge)
    fun updateRoom(lodge: FirebaseLodge) = roomListener(lodge)
    fun otherAction(lodge:FirebaseLodge) = otherListener(lodge)
}