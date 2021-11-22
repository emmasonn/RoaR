package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize data class FirebaseLodge(
    @PropertyName("account") var account: String? = null, //login account
    @PropertyName("agentId") var agentId: String? = null, //agentId
    @PropertyName("agentName") var agentName: String? = null, //agentName
    @PropertyName("agentPhone") var agentPhone: String? = null,
    @PropertyName("agentImage") var agentImage: String? = null,
    @PropertyName("rooms") var rooms: Long? = null,
    @PropertyName("brand") var brand: String? = null,
    @PropertyName("campus") var campus: String? = null,
    @PropertyName("certified") var certified: Boolean? = null,
    @PropertyName("coverImage") var coverImage: String? = null,
    @PropertyName("description") var description: String? = null,
    @PropertyName("distance") var distance: String? = null,
    @PropertyName("rent") var rent: String? = null,
    @PropertyName("light") var light: String? = null,
    @PropertyName("location") var location: String? = null,
    @PropertyName("lodgeId") var lodgeId: String? = null,
    @PropertyName("lodgeName") var lodgeName: String? = null,
    @PropertyName("network") var network: String? = null,
    @PropertyName("owner") var owner: String? = null,
    @PropertyName("number") var number: String? = null,
    @PropertyName("hiddenName") var hiddenName: String? = null, //the hidden Name
    @PropertyName("size") var size: String? = null,
    @PropertyName("slots") var slots: Int? = null,
    @PropertyName("payment") var payment: Int? = null, //the payment name
    @PropertyName("surrounding") var surrounding: String? = null,
    @PropertyName("seen") var seen: Boolean? = null,
    @PropertyName("timeStamp") @ServerTimestamp var timeStamp: Date? = null,
    @PropertyName("tour") var tour: String? = null, //tour Name
    @PropertyName("type") var type: String? = null, //house
    @PropertyName("water") var water: String? = null
): Parcelable