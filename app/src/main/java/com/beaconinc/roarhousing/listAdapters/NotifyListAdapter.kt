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
import com.beaconinc.roarhousing.cloudModel.FirebaseNotifier
import com.google.android.material.card.MaterialCardView

class NotifyListAdapter(private val deleteClickListener: DeleteClickListener) :
    ListAdapter<FirebaseNotifier, NotifyListAdapter.NotifyViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NotifyViewHolder(inflater.inflate(R.layout.item_notify_layout, parent, false))
    }

    override fun onBindViewHolder(holder: NotifyViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data, deleteClickListener)
    }

    class NotifyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val notifierCard = itemView.findViewById<MaterialCardView>(R.id.notifyCard)
        private val clientName = itemView.findViewById<TextView>(R.id.notifyTxt)
        private val notifyChatBtn = itemView.findViewById<MaterialCardView>(R.id.chatBtn)
        private val visitDay = itemView.findViewById<TextView>(R.id.visitDay)
        private val postDay = itemView.findViewById<TextView>(R.id.postDate)
        private val lodgeName = itemView.findViewById<TextView>(R.id.lodgeTitle)
        private val campus  = itemView.findViewById<TextView>(R.id.campus)
        private val location = itemView.findViewById<TextView>(R.id.lodgeLocation)
        private val lodgeCover = itemView.findViewById<ImageView>(R.id.lodgeCover)

        private val resource = itemView.resources

        fun bind(data: FirebaseNotifier, delete: DeleteClickListener) {
            notifierCard.setOnLongClickListener {
                delete.onclick(data)
                true
            }

            notifyChatBtn.setOnClickListener {
                delete.chatClicked(data)
            }

            lodgeCover.load(data.lodgeCover)
            lodgeName.text = data.lodgeName
            campus.text = data.campus
            location.text = data.lodgeLocation
            visitDay.text = data.visitDate
            clientName.text = resource.getString(R.string.notifyText,data.clientName)

        }

    }

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<FirebaseNotifier>() {
            override fun areItemsTheSame(
                oldItem: FirebaseNotifier,
                newItem: FirebaseNotifier
            ): Boolean {
                return oldItem.notifierId == newItem.notifierId
            }

            override fun areContentsTheSame(
                oldItem: FirebaseNotifier,
                newItem: FirebaseNotifier
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class DeleteClickListener(
        val listener: (data: FirebaseNotifier) -> Unit,
        val chatListener: (data: FirebaseNotifier) -> Unit
    ) {
        fun onclick(data: FirebaseNotifier) = listener(data)
        fun chatClicked(data: FirebaseNotifier) = chatListener(data)
    }

}