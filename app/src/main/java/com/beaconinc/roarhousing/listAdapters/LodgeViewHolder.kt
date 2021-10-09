package com.beaconinc.roarhousing.listAdapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton

class LodgeViewHolder (val itemView: View):
    RecyclerView.ViewHolder(itemView) {

    private val lodgeImage = itemView.findViewById<ImageView>(R.id.viewFlipper)
    private val initialPrice = itemView.findViewById<TextView>(R.id.lodgePrice)
    private val lodgeName = itemView.findViewById<TextView>(R.id.lodgeTitle)
    private val location = itemView.findViewById<TextView>(R.id.location)
    private val exploreBtn =  itemView.findViewById<MaterialButton>(R.id.exploreBtn)
    private val available = itemView.findViewById<TextView>(R.id.availableRoom)
    private val campus = itemView.findViewById<TextView>(R.id.campus)

    private val resource = itemView.resources

    fun bind(data: FirebaseLodge, listener: LodgeClickListener) {
        initialPrice.text = resource.getString(R.string.format_price, data.subPayment)
        lodgeName.text = data.randomId
        location.text = data.location
        campus.text = data.campus
        if(data.availableRoom == null) {
            available.text = "0"
        }else {
            available.text = data.availableRoom.toString()
        }

        Glide.with(lodgeImage.context)
            .load(data.coverImage).apply(
                RequestOptions().placeholder(R.drawable.animated_gradient)
                    .error(R.drawable.animated_gradient)).into(lodgeImage)

        exploreBtn.setOnClickListener {
            listener.clickAction(data)
        }
    }
}