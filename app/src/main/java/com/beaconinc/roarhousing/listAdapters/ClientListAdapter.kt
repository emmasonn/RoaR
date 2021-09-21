package com.beaconinc.roarhousing.listAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
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

        fun bind(data: FirebaseUser, clickListener: UserClickListener) {
            coverImage.load(data.clientUrl)
            fullName.text = data.clientName
            phoneNumber.text = data.clientPhone
            brandName.text = data.brandName

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