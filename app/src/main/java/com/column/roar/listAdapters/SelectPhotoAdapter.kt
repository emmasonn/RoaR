package com.column.roar.listAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodgePhoto

class SelectPhotoAdapter(private val clickListener: ClickListener) :
    ListAdapter<FirebaseLodgePhoto, SelectPhotoAdapter.SelectPhotoViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectPhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SelectPhotoViewHolder(inflater.inflate(R.layout.item_manage_lodge_photo,parent,false))
    }

    override fun onBindViewHolder(holder: SelectPhotoViewHolder, position: Int) {
        val item  = getItem(position)
        holder.bind(item,clickListener)
    }

    class SelectPhotoViewHolder (itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        private val selectBtn = itemView.findViewById<TextView>(R.id.selectImage)
        private val lodgeImage = itemView.findViewById<ImageView>(R.id.firstImage)
        private val imageTitle = itemView.findViewById<TextView>(R.id.imageTitle)

        fun bind(data: FirebaseLodgePhoto, listener: ClickListener) {
            lodgeImage.load(data.image)
            imageTitle.text = data.title
            selectBtn.setOnClickListener {
                listener.clickAction(data)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FirebaseLodgePhoto>() {
            override fun areItemsTheSame(oldItem: FirebaseLodgePhoto,
                                         newItem: FirebaseLodgePhoto): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FirebaseLodgePhoto, newItem: FirebaseLodgePhoto): Boolean {
                return oldItem == newItem
            }
        }
    }

}