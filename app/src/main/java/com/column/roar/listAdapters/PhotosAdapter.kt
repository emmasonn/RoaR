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
import com.column.roar.cloudModel.FirebaseLodgePhoto
import com.column.roar.listAdapters.PhotosAdapter.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

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

            Glide.with(photoView.context)
                .load(data.image).apply(
                    RequestOptions().placeholder(R.drawable.animated_gradient)
                        .error(R.drawable.animated_gradient)
                ).into(photoView)

            photoTitle.text = data.title

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
               return oldItem.id == newItem.id
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