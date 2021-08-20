package com.beaconinc.roarhousing.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize data class FirebaseLodge(
    var accountType: String? = null,
    var lodgeId: String? = null,
    var agentId: String? = null,
    var agentName: String? = null,
    var agentUrl: String? = null,
    var lodgeName: String? = null,
    var coverImage: String? = null,
    var location: String? = null,
    var campus: String? = null,
    var type: String? = null,
    var size: String? = null,
    var availableRoom: Long? = null,
    var initialPayment: String? = null,
    var subPayment: String? = null,
    var description: String? = null,
    var water: String? = null,
    var light: String? = null,
    var network: String? = null,
    var surrounding: String? = null,
    var distance: String? = null,
    @ServerTimestamp var date: Date? = null
): Parcelable