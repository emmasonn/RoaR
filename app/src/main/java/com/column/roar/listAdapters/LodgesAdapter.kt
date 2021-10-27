package com.column.roar.listAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge
import com.column.roar.listAdapters.DataItem.*
import com.column.roar.listAdapters.adViewHolders.MediumAdViewHolder
import com.column.roar.listAdapters.adViewHolders.NativeAdViewHolder
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val LODGE_VIEW = 0
const val SMALL_AD = 1
const val MED_AD = 2
const val EMPTY = 3

class LodgesAdapter(
    private val clickListener: LodgeClickListener,
    private val lifeCycle: LifecycleOwner,
    private val fav: Boolean? = null
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(diffUtil) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val mutableNativeAd1 = MutableLiveData<NativeAd>()
    private val mutableNativeAd2 = MutableLiveData<NativeAd>()

    fun postAd1(_nativeAd: NativeAd) {
        mutableNativeAd1.postValue(_nativeAd)
    }

    fun postAd2(_nativeAd: NativeAd) {
        mutableNativeAd2.postValue(_nativeAd)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val lodgesView = inflater.inflate(R.layout.item_lodges_layout, parent, false)
        val favViewLayout = inflater.inflate(R.layout.item_favorite_layout, parent, false)
        val adView =
            inflater.inflate(R.layout.native_small_advert_layout, parent, false)
        val mediumAdView =
            inflater.inflate(R.layout.native_medium_advert_layout, parent, false)
        val emptyListLayout = inflater.inflate(R.layout.item_empty_list_card, parent, false)

        return if (fav != null && fav == true) {
            when (viewType) {
                LODGE_VIEW -> FavViewHolder(favViewLayout)
                MED_AD -> MediumAdViewHolder(mediumAdView)
                SMALL_AD -> NativeAdViewHolder(adView)
                EMPTY -> EmptyViewHolder(emptyListLayout)
                else -> throw ClassCastException("unknown view type $viewType")
            }
        } else {

            when (viewType) {
                LODGE_VIEW -> LodgeViewHolder(lodgesView)
                SMALL_AD -> NativeAdViewHolder(adView)
                MED_AD -> MediumAdViewHolder(mediumAdView)
                EMPTY -> EmptyViewHolder(emptyListLayout)
                else -> throw ClassCastException("Unknown view type $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LodgeViewHolder -> {
                val lodgeItem = getItem(position) as LodgeItem
                holder.bind(lodgeItem.lodge, clickListener)
            }
            is FavViewHolder -> {
                val lodgeItem = getItem(position) as LodgeItem
                holder.bind(lodgeItem.lodge, clickListener)
            }
            is NativeAdViewHolder -> {
                holder.bind(mutableNativeAd1, lifeCycle)
            }
            is MediumAdViewHolder -> {
                holder.bind(mutableNativeAd2, lifeCycle)
            }
        }
    }

    fun clear() {
        submitList(emptyList())
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        return if (fav != null && fav == true) {
            when (getItem(position)) {
                is LodgeItem -> LODGE_VIEW
                is MediumAd -> MED_AD
                is EmptyCard -> EMPTY
                is AdHeader -> SMALL_AD
                else -> throw ClassCastException("Unknown view type ${getItem(position)}")
            }
        } else {
            when (getItem(position)) {
                is LodgeItem -> LODGE_VIEW
                is AdHeader -> SMALL_AD
                is MediumAd -> MED_AD
                is EmptyCard -> EMPTY
                else -> throw ClassCastException("Unknown view type ${getItem(position)}")
            }
        }
    }

    fun addLodgeAndProperty(
        lodges: List<FirebaseLodge>,
        detail: Boolean
    ) {
        adapterScope.launch {

            val result: List<DataItem> = when (lodges.size) {
                0 -> {
                    if (detail) {
                        listOf(MediumAd)
                    } else {
                        listOf(EmptyCard) + listOf(MediumAd)
                    }
                }
                1 -> {
                    if (detail) {
                        lodges.take(1).map { LodgeItem(it) }
                        listOf(MediumAd)
                    } else {
                        lodges.take(1).map { LodgeItem(it) } +
                                listOf(MediumAd)
                    }
                }
                else -> {
                    if (detail) {
                        lodges.take(1).map { LodgeItem(it) }
                        listOf(MediumAd)
                        lodges.drop(1).map { LodgeItem(it) }
                    } else {
                        lodges.take(1).map { LodgeItem(it) } +
                                listOf(AdHeader) +
                                lodges.drop(1).map { LodgeItem(it) }
                    }

                }
            }

            withContext(Dispatchers.Main) {
                submitList(result)
            }
        }
    }


    class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val lodgeImage = itemView.findViewById<ImageView>(R.id.viewFlipper)
        private val initialPrice = itemView.findViewById<TextView>(R.id.lodgePrice)
        private val lodgeName = itemView.findViewById<TextView>(R.id.lodgeTitle)
        private val location = itemView.findViewById<TextView>(R.id.location)
        private val exploreBtn = itemView.findViewById<MaterialButton>(R.id.exploreBtn)
        private val available = itemView.findViewById<TextView>(R.id.availableRoom)
        private val campus = itemView.findViewById<TextView>(R.id.campus)
        private val favBtn = itemView.findViewById<ImageView>(R.id.favBtn)
        private val lock = itemView.findViewById<ImageView>(R.id.lockLodge)
        private val resource = itemView.resources

        fun bind(data: FirebaseLodge, listener: LodgeClickListener) {

            Glide.with(lodgeImage.context)
                .load(data.coverImage).apply(
                    RequestOptions().placeholder(R.drawable.animated_gradient)).into(lodgeImage)


            initialPrice.text = resource.getString(R.string.format_price_integer, data.payment)
            lodgeName.text = data.hiddenName
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

            favBtn?.setOnClickListener {
                listener.favClick(data.lodgeId!!)
            }

            exploreBtn.setOnClickListener {
                listener.clickAction(data)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem.id == oldItem.id
            }

            override fun areContentsTheSame(
                oldItem: DataItem,
                newItem: DataItem
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