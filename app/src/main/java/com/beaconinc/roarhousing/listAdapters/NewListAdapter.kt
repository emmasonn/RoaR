package com.beaconinc.roarhousing.listAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.listAdapters.adViewHolders.MediumAdViewHolder
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter.*
import com.beaconinc.roarhousing.listAdapters.adViewHolders.NativeAdViewHolder
import com.beaconinc.roarhousing.listAdapters.businessAds.BusinessAdsViewHolder
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException
import kotlin.random.Random

const val ITEM_VIEW_PROPERTY = 0
const val ITEM_VIEW_LODGE = 1
const val ITEM_VIEW_HEADER = 2
const val ITEM_AD_VIEW = 3
const val ITEM_MEDIUM_AD_VIEW = 4
const val ITEM_EMPTY_LODGE = 5
const val ITEM_AUTO_SCROLL_HEADER = 6
const val ITEM_AUTO_SCROLL_ITEMS = 7

//Adapter for main homeScreen for display both the lodges and product with headers
class NewListAdapter(
    private val lodgeListener: LodgeClickListener,
    private val propertyListener: PropertyClickListener,
    private val lifeCycle: LifecycleOwner
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(diffUtil) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val mutableNativeAd1 = MutableLiveData<NativeAd>()
    private val mutableNativeAd2 = MutableLiveData<NativeAd>()
    var showEmpty: Boolean = false

    fun postAd1(_nativeAd: NativeAd) {
        mutableNativeAd1.postValue(_nativeAd)
    }

    fun postAd2(_nativeAd: NativeAd) {
        mutableNativeAd2.postValue(_nativeAd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val lodgesView = layoutInflater.inflate(R.layout.item_lodges_layout, parent, false)
        val propertyView = layoutInflater.inflate(R.layout.item_advert_property, parent, false)
        val itemHeader = layoutInflater.inflate(R.layout.item_advert_header, parent, false)
        val businessAdView = layoutInflater.inflate(R.layout.business_ads_layout, parent, false)
        val businessAdHeader = layoutInflater.inflate(R.layout.business_item_header, parent, false) as ConstraintLayout

        val adView = layoutInflater.inflate(
            R.layout.native_small_advert_layout,
            parent,
            false
        )

        val mediumAdView = layoutInflater.inflate(
            R.layout.native_medium_advert_layout,
            parent,
            false
        )
        val emptyListLayout = layoutInflater.inflate(R.layout.item_empty_list_card, parent, false)

        return when (viewType) {
            ITEM_VIEW_LODGE -> LodgeViewHolder(lodgesView)
            ITEM_VIEW_PROPERTY -> PropertyViewHolder(propertyView)
            ITEM_VIEW_HEADER -> HeaderViewHolder(itemHeader)
            ITEM_AD_VIEW -> NativeAdViewHolder(adView)
            ITEM_MEDIUM_AD_VIEW -> MediumAdViewHolder(mediumAdView)
            ITEM_EMPTY_LODGE -> EmptyViewHolder(emptyListLayout)
            ITEM_AUTO_SCROLL_HEADER -> BusinessHeaderViewHolder(businessAdHeader)
            ITEM_AUTO_SCROLL_ITEMS -> BusinessAdsViewHolder(businessAdView)
            else -> throw ClassCastException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LodgeViewHolder -> {
                val lodgeItem = getItem(position) as DataItem.LodgeItem
                holder.bind(lodgeItem.lodge, lodgeListener)
            }
            is PropertyViewHolder -> {
                val propertyItem = getItem(position) as DataItem.PropertyItem
                holder.bind(propertyItem.property, propertyListener)
            }
            is BusinessAdsViewHolder -> {
                val businessItems = getItem(position) as DataItem.BusinessAdsItem
                holder.bind(businessItems.property,propertyListener)
            }
            is HeaderViewHolder -> {
                val item = getItem(position) as DataItem.Header
                holder.bind(item.title, propertyListener)
            }
            is NativeAdViewHolder -> {
                holder.bind(mutableNativeAd1, lifeCycle)
            }
            is MediumAdViewHolder -> {
                holder.bind(mutableNativeAd2, lifeCycle)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun addLodgeAndProperty(
        lodges: List<FirebaseLodge>,
        properties: List<FirebaseProperty>
    ): NewListAdapter {

      adapterScope.launch {

//            val item = lodges.take(1).map { DataItem.LodgeItem(it) } +
//                    listOf(DataItem.Header("Items")) +
//                    properties.run { DataItem.PropertyItem(this) } +
//                    lodges.drop(1).map { DataItem.LodgeItem(it) } +
//                    listOf(DataItem.AdHeader)

            val result: List<DataItem> = when (lodges.size) {

                0 -> {
                    if(showEmpty) {
                       listOf(DataItem.AdHeader) +
                                listOf(DataItem.EmptyCard) +
                                listOf(DataItem.Header("Properties")) +
                                properties.run { DataItem.PropertyItem(this) } +
                                listOf(DataItem.MediumAd)
                    } else {
                        return@launch
                    }
                }
                else -> {
                    lodges.take(1).map { DataItem.LodgeItem(it) } +
                            listOf(DataItem.CampusBusinessHeader) +
                            properties.run { this.filter{ it.propertyType == "Ads" }.let{
                                DataItem.BusinessAdsItem(it)
                            }} +
                            listOf(DataItem.AdHeader) +
                            listOf(DataItem.Header("Properties")) +
                            properties.run {
                                this.filter { it.propertyType == "House-Property" }.let {
                                    DataItem.PropertyItem(it)
                                }
                            } + //first items for sale
                            lodges.drop(1).take(4).map { DataItem.LodgeItem(it) } +
                    listOf(DataItem.Header("Accessory")) +
                            properties.run {
                                this.filter { it.propertyType == "Accessory" }.let {
                                    DataItem.PropertyItem(it)
                                }
                            } +    //second property
                            lodges.drop(5).take(3).map { DataItem.LodgeItem(it) } +
                            listOf(DataItem.Header("Jewellery")) +
                            properties.run {
                                this.filter { it.propertyType == "Jewellery" }.let {
                                    DataItem.PropertyItem(it)
                                }
                            } +    //third property
                            lodges.drop(8).take(2).map { DataItem.LodgeItem(it) } +
                            listOf(DataItem.Header("Stationary")) +
                            properties.run {
                                this.filter { it.propertyType == "Stationary" }.let {
                                    DataItem.PropertyItem(it)
                                }
                            } +   //forth property
                            lodges.drop(10).take(4).map { DataItem.LodgeItem(it) } +
                            lodges.drop(14).map { DataItem.LodgeItem(it) }
                }
            }
            withContext(Dispatchers.Main) {
                val mutableResult = result.toMutableList()
                submitList(mutableResult)
            }
        }
        return this
    }

    fun clear() {
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.LodgeItem -> ITEM_VIEW_LODGE
            is DataItem.PropertyItem -> ITEM_VIEW_PROPERTY
            is DataItem.Header -> ITEM_VIEW_HEADER
            is DataItem.AdHeader -> ITEM_AD_VIEW
            is DataItem.MediumAd -> ITEM_MEDIUM_AD_VIEW
            is DataItem.EmptyCard -> ITEM_EMPTY_LODGE
            is DataItem.CampusBusinessHeader -> ITEM_AUTO_SCROLL_HEADER
            is DataItem.BusinessAdsItem -> ITEM_AUTO_SCROLL_ITEMS
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return newItem == oldItem
            }
        }
    }
}

sealed class DataItem {

    data class LodgeItem(val lodge: FirebaseLodge) : DataItem() {
        override val id: String? = lodge.lodgeId
    }

    data class PropertyItem(val property: List<FirebaseProperty>) : DataItem() {
        override val id: String = Random.nextDouble().toString()
    }

    data class BusinessAdsItem(val property: List<FirebaseProperty>) : DataItem() {
        override val id: String = Random.nextDouble().toString()
    }

    data class Header(val title: String) : DataItem() {
        override val id: String
            get() = "xkdskrkewjrk"
    }

    object AdHeader : DataItem() {
        override val id: String
            get() = "yreiewuxixixu"
    }

    object CampusBusinessHeader : DataItem() {
        override val id: String
            get() = "yrwxiescfdedd"
    }

    object MediumAd : DataItem() {
        override val id: String
            get() = "xresdfsesrerw"

    }

    object EmptyCard : DataItem() {
        override val id: String
            get() = "woersdfwer3idiskd"

    }
    abstract val id: String?
}

//ViewHolder for the Header view items
class HeaderViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val moreIcon = itemView.findViewById<TextView>(R.id.viewAll)
    private val titleView = itemView.findViewById<TextView>(R.id.productType)
    private val resource = itemView.resources

    fun bind(title: String, propertyListener: PropertyClickListener) {
        titleView.text = resource.getString(R.string.format_property_header, title)
        moreIcon.setOnClickListener {
            propertyListener.onJustClick(null)
        }
    }
}

class BusinessHeaderViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView)

class EmptyViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView)