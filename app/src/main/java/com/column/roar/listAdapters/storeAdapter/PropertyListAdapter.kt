package com.column.roar.listAdapters.storeAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseProperty
import com.google.android.gms.ads.nativead.NativeAd

/*Adapter for displaying list of item in the store Screen*/
class PropertyListAdapter (
    private val propertyListener: PropertyClickListener,
    private val lifecycleOwner: LifecycleOwner,
) :ListAdapter<FirebaseProperty, StaggeredCardViewHolder>(diffUtil) {

    private val mutableNativeAd = MutableLiveData<NativeAd>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaggeredCardViewHolder {
        var layoutId = R.layout.product_card_one
        if(viewType == 1) {
            layoutId = R.layout.product_card_two
        } else if (viewType == 2) {
            layoutId = R.layout.product_card_three
        }
        val layoutInflater = LayoutInflater.from(parent.context).inflate(layoutId, parent,false)
        return StaggeredCardViewHolder(layoutInflater)
    }

    fun setNativeAd(_nativeAd: NativeAd) {
        mutableNativeAd.postValue(_nativeAd)
    }

    override fun onBindViewHolder(holder: StaggeredCardViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item,lifecycleOwner, propertyListener,
            mutableNativeAd)
    }

    override fun getItemViewType(position: Int): Int {
               return position % 3
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FirebaseProperty>() {
            override fun areItemsTheSame(
                oldItem: FirebaseProperty,
                newItem: FirebaseProperty
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: FirebaseProperty,
                newItem: FirebaseProperty
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class PropertyClickListener(
        private val listener: (data: FirebaseProperty) -> Unit,
        private val tabClick: (data: FirebaseProperty) -> Unit,
        private val justClick: ((data: FirebaseProperty?, argument: String) -> Unit)? = null,
    ) {
        fun onAction(data: FirebaseProperty) = listener(data)
        fun onTab(data: FirebaseProperty) = tabClick(data)
        fun onJustClick(data: FirebaseProperty?,
                        argument: String) = justClick?.invoke(null,argument)
    }
}