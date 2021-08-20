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
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto
import com.beaconinc.roarhousing.listAdapters.PhotosAdapter.*

class PhotosAdapter(private val clickListener: ClickListener) : ListAdapter<FirebaseLodgePhoto, PhotoViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PhotoViewHolder(inflater.inflate(R.layout.item_detail_photos,parent,false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind( data, clickListener)
    }

    class PhotoViewHolder( itemView: View): RecyclerView.ViewHolder(itemView) {

        private val photoView = itemView.findViewById<ImageView>(R.id.itemPhoto)
        private val photoTitle = itemView.findViewById<TextView>(R.id.itemTitle)

        fun bind(data: FirebaseLodgePhoto, listener: ClickListener) {
            photoView.load(data.photoUrl)
            photoTitle.text = data.photoTitle

            itemView.setOnClickListener {
                photoView.setBackgroundColor(R.drawable.blue_card_outline)
                listener.clickAction(data)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FirebaseLodgePhoto>(){
            override fun areItemsTheSame(
                oldItem: FirebaseLodgePhoto,
                newItem: FirebaseLodgePhoto
            ): Boolean {
               return oldItem.photoId == newItem.photoId
            }

            override fun areContentsTheSame(
                oldItem: FirebaseLodgePhoto,
                newItem: FirebaseLodgePhoto
            ): Boolean {
                return  oldItem == newItem
            }
        }
    }

}