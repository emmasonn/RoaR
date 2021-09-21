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
 :LodgeClickListener, private val fav: Boolean? = null) : ListAdapter<FirebaseLodge, LodgesViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LodgesViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return if(fav!=null && fav) {
            LodgesViewHolder(inflater.inflate(R.layout.item_favorite_layout,parent,false))
        }else {
            LodgesViewHolder(inflater.inflate(R.layout.item_lodges_layout,parent,false))
        }
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
           private val location = itemView.findViewById<TextView>(R.id.location)
           private val exploreBtn =  itemView.findViewById<MaterialButton>(R.id.exploreBtn)
           private val available = itemView.findViewById<TextView>(R.id.availableRoom)
           private val campus = itemView.findViewById<TextView>(R.id.campus)
           private val favBtn = itemView.findViewById<ImageView>(R.id.favBtn)

          private val resource = itemView.resources

            fun bind(data: FirebaseLodge,listener: LodgeClickListener) {
                lodgeImage.load(data.coverImage)

                initialPrice.text = resource.getString(R.string.format_price, data.subPayment)
                lodgeName.text = data.lodgeName
                location.text = data.location
                campus.text = data.campus
                if(data.availableRoom == null) {
                    available.text = "0"
                }else {
                    available.text = data.availableRoom.toString()
                }

                favBtn?.setOnClickListener {
                    listener.favClick(data.lodgeId!!)
                }

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
    val listener: (lodge: FirebaseLodge) -> Unit,
    val favClick: (id: String) -> Unit
) {
    fun clickAction(lodge: FirebaseLodge) = listener(lodge)
}