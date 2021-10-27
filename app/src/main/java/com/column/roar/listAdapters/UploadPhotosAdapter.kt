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
import com.column.roar.listAdapters.UploadPhotosAdapter.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class UploadPhotosAdapter(private val clickListener: ClickListener) :
    ListAdapter<FirebaseLodgePhoto, UploadPhotosViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadPhotosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UploadPhotosViewHolder(inflater.inflate(R.layout.layout_lodge_image,parent,false))
    }

    override fun onBindViewHolder(holder: UploadPhotosViewHolder, position: Int) {
        val item  = getItem(position)
        holder.bind(item, clickListener)
    }

    class UploadPhotosViewHolder (itemView: View)
        : RecyclerView.ViewHolder(itemView) {

          private val imageTitle = itemView.findViewById<TextView>(R.id.imageTitle)
          private val imageView = itemView.findViewById<ImageView>(R.id.imageLink)
          private val resource = itemView.resources
            fun bind(data: FirebaseLodgePhoto, listener: ClickListener) {
                imageTitle.text = resource.getString(R.string.format_brandName,data.title)

                data.title?.let {
                    imageTitle.alpha = 1F
                }

                Glide.with(imageView.context)
                    .load(data.image).apply(
                        RequestOptions().placeholder(R.drawable.animated_gradient)
                            .error(R.drawable.animated_gradient)
                    ).into(imageView)

                itemView.setOnLongClickListener {
                    listener.removeClick(data)
                    true
                }

                itemView.setOnClickListener {
                    listener.clickAction(data)
                }
            }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FirebaseLodgePhoto>() {
            override fun areItemsTheSame(oldItem: FirebaseLodgePhoto, newItem: FirebaseLodgePhoto): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FirebaseLodgePhoto, newItem: FirebaseLodgePhoto): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class ClickListener(
    private val removeListener: ((photo: FirebaseLodgePhoto) -> Unit)? = null,
    private val listener: ((photo: FirebaseLodgePhoto) -> Unit)? = null
) {
    fun removeClick(photo: FirebaseLodgePhoto) = removeListener?.let { it(photo) }
    fun clickAction(photo: FirebaseLodgePhoto) = listener?.let { it(photo) }
}