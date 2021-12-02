package com.column.roar.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge

class SimilarLodgeAdapter(
    private val clickListener: LodgeClickListener,
    ): ListAdapter<FirebaseLodge, LodgeViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LodgeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val lodgesView = inflater.inflate(R.layout.item_lodges_layout, parent, false)
        return LodgeViewHolder(lodgesView)
    }

    override fun onBindViewHolder(holder: LodgeViewHolder, position: Int) {
        val lodgeItem = getItem(position) as FirebaseLodge
        holder.bind(lodgeItem, clickListener)
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