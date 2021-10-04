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
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.beaconinc.roarhousing.listAdapters.AdminListAdapter.AdminListViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class AdminListAdapter(private val adminClickListener: AdminClickListener)
    : ListAdapter<FirebaseUser, AdminListViewHolder>(diffUtil) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminListViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return AdminListViewHolder(inflater.inflate(R.layout.item_select_admin, parent,false))
        }

        override fun onBindViewHolder(holder: AdminListViewHolder, position: Int) {
            val item = getItem(position)
            holder.bind(item,adminClickListener)
        }

        class AdminListViewHolder(itemView: View)
            : RecyclerView.ViewHolder(itemView) {

            private val adminImage = itemView.findViewById<ImageView>(R.id.adminImage)
            private val adminFullName = itemView.findViewById<TextView>(R.id.adminFullName)

            fun bind(data: FirebaseUser, listener: AdminClickListener) {

                Glide.with(adminImage.context)
                    .load(data.clientUrl).apply(
                        RequestOptions().placeholder(R.drawable.loading_animation)
                            .error(R.drawable.loading_animation)
                    ).into(adminImage)


                adminFullName.text = data.clientName
                itemView.setOnClickListener {
                    listener.onClick(data)
                }
            }
        }

        companion object{
            val diffUtil = object : DiffUtil.ItemCallback<FirebaseUser>(){
                override fun areItemsTheSame(oldItem: FirebaseUser, newItem: FirebaseUser): Boolean {
                    return oldItem.clientId == newItem.clientId
                }

                override fun areContentsTheSame(
                    oldItem: FirebaseUser,
                    newItem: FirebaseUser
                ): Boolean {
                    return oldItem == newItem
                }

            }
        }

    class AdminClickListener(
        val listener: (admin: FirebaseUser) -> Unit
    ) {
        fun onClick(admin: FirebaseUser) = listener(admin)
    }
}