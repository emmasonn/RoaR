package com.column.roar.listAdapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseUser
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton

class ClientListAdapter(private val clickListener: UserClickListener): ListAdapter<FirebaseUser, ClientListAdapter.ClientListViewHolder>(diffUtil) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ClientListViewHolder(inflater.inflate(R.layout.item_realtors_layout,parent,false))
    }

    override fun onBindViewHolder(holder: ClientListViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data, clickListener)
    }

    class ClientListViewHolder(private val itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val fullName = itemView.findViewById<TextView>(R.id.fullName)
        private val coverImage = itemView.findViewById<ImageView>(R.id.agentImageCover)
        private val phoneNumber = itemView.findViewById<TextView>(R.id.phoneNumber)
        private val viewAccount = itemView.findViewById<MaterialButton>(R.id.viewAccount)
        private val brandName = itemView.findViewById<TextView>(R.id.brandTitle)
        private val subscription = itemView.findViewById<TextView>(R.id.subscription)
        private val isCertified = itemView.findViewById<TextView>(R.id.certifiedState)
        private val resource = itemView.resources
        fun bind(data: FirebaseUser, clickListener: UserClickListener) {

            Glide.with(coverImage.context)
                .load(data.clientImage).apply(
                    RequestOptions().placeholder(R.drawable.ic_person)
                ).into(coverImage)

            fullName.text = data.clientName
            phoneNumber.text = data.clientPhone
            brandName.text = data.brand
            val condition = data.certified ?: false

            if (condition) {
               isCertified.apply { text = "Ceritfied" }
               }else {
                   isCertified.apply { text = "Uncertified"
                       setTextColor(Color.RED)
                   }
                }

            when(data.slots) {
                "10" -> {
                    subscription.text = resource.getString(R.string.business_personal)
                }
                 "20" -> {
                     subscription.text = resource.getString(R.string.business_pro)
                 }
                else -> {
                    subscription.text = resource.getString(R.string.business_default)
                }
            }


            viewAccount.setOnClickListener {
                clickListener.listener(data)
            }
        }
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FirebaseUser>() {
            override fun areItemsTheSame(oldItem: FirebaseUser, newItem: FirebaseUser): Boolean {
                return oldItem.clientId == newItem.clientId
            }

            override fun areContentsTheSame(oldItem: FirebaseUser,
                                            newItem: FirebaseUser): Boolean {
                return oldItem == newItem
            }
        }
    }

    class UserClickListener(val listener: (user: FirebaseUser) -> Unit)

}