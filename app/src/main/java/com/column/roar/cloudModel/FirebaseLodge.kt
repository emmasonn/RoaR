package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize data class FirebaseLodge(
    var account: String? = null, //login account
    var agentId: String? = null, //agentId
    var agentName: String? = null, //agentName
    var agentPhone: String? = null,
    var agentImage: String? = null,
    var rooms: Long? = null,
    var brand: String? = null,
    var campus: String? = null,
    var certified: Boolean? = null,
    var coverImage: String? = null,
    var description: String? = null,
    var distance: String? = null,
    var rent: String? = null,
    var light: String? = null,
    var location: String? = null,
    var lodgeId: String? = null,
    var lodgeName: String? = null,
    var network: String? = null,
    var landLord: String? = null,
    var ownerPhone: String? = null,
    var hiddenName: String? = null, //the hidden Name
    var size: String? = null,
    var slots: Int? = null,
    var payment: Int? = null, //the payment name
    var surrounding: String? = null,
    @ServerTimestamp var timeStamp: Date? = null,
    var tour: String? = null, //tour Name
    var type: String? = null, //house
    var water: String? = null
): Parcelable