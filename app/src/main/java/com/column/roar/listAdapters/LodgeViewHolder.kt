package com.column.roar.listAdapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.DecimalFormat

class LodgeViewHolder (val itemView: View):
    RecyclerView.ViewHolder(itemView) {

    private val lodgeImage = itemView.findViewById<ImageView>(R.id.viewFlipper)
    private val initialPrice = itemView.findViewById<TextView>(R.id.lodgePrice)
    private val lodgeTitle = itemView.findViewById<TextView>(R.id.lodgeTitle)
    private val location = itemView.findViewById<TextView>(R.id.location)
    private val exploreBtn =  itemView.findViewById<MaterialButton>(R.id.exploreBtn)
    private val available = itemView.findViewById<TextView>(R.id.availableRoom)
    private val campus = itemView.findViewById<TextView>(R.id.campus)
    private val lock = itemView.findViewById<ImageView>(R.id.lockLodge)
    private val newCard = itemView.findViewById<MaterialCardView>(R.id.newCard)
    private val resource = itemView.resources

    fun bind(data: FirebaseLodge, listener: LodgeClickListener, seenLodges: List<String?>) {
         val formatter = DecimalFormat()

        initialPrice.text = resource.getString(R.string.format_price_string, formatter.format(data.payment))

        lodgeTitle.text = data.hiddenName
        location.text = data.location
        campus.text = data.campus

        if(data.rooms == null) {
            lock.alpha = 1F
        }else {
            if(data.rooms == 0L) {
                lock.alpha = 1F
                 available.alpha = 0F
            }else {
                lock.alpha = 0F
                available.alpha = 1F
                available.text = data.rooms.toString()
            }
        }

        if(seenLodges.contains(data.lodgeId)) {
            newCard.visibility = View.GONE
        }else {
            newCard.visibility = View.VISIBLE
        }

        Glide.with(lodgeImage.context)
            .load(data.coverImage).apply(
                RequestOptions().placeholder(R.drawable.loading_background)
                    .error(R.drawable.loading_background)).into(lodgeImage)

        exploreBtn.setOnClickListener {
            listener.clickAction(data)
        }
    }
}