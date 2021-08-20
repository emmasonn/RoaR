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
import com.beaconinc.roarhousing.listAdapters.LodgesAdapter.*
import com.google.android.material.button.MaterialButton


class LodgesAdapter (private val clickListener
 :LodgeClickListener) : ListAdapter<FirebaseLodge, LodgesViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LodgesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LodgesViewHolder(inflater.inflate(R.layout.item_lodges_layout,parent,false))
    }

    override fun onBindViewHolder(holder: LodgesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item,clickListener)
    }

    class LodgesViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

           private val lodgeImage = itemView.findViewById<ImageView>(R.id.viewFlipper)
           private val initialPrice = itemView.findViewById<TextView>(R.id.lodgePrice)
           private val lodgeName = itemView.findViewById<TextView>(R.id.lodgeTitle)
           private val exploreBtn =  itemView.findViewById<MaterialButton>(R.id.exploreBtn)
           private val available = itemView.findViewById<TextView>(R.id.availableRoom)

          private val resource = itemView.resources

            fun bind(data: FirebaseLodge,listener: LodgeClickListener) {
                initialPrice.text = resource.getString(R.string.format_price, data.subPayment)
                lodgeName.text = data.lodgeName
                available.text = data.availableRoom.toString()
                lodgeImage.load(data.coverImage)
                exploreBtn.setOnClickListener {
                    listener.clickAction(data)
                }
            }
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<FirebaseLodge>(){
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

class LodgeClickListener(
    val listener: (lodge: FirebaseLodge) -> Unit
) {
    fun clickAction(lodge: FirebaseLodge) = listener(lodge)
}