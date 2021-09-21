package com.beaconinc.roarhousing.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class FirebaseUser (
    var clientId: String? = null,
    var clientUrl: String? = null,
    var clientName: String? = null,
    var visitCounter: Long? = null,
    var clientPhone: String? = null,
    var slots: String? = null,
    @ServerTimestamp var expired: Date? = null,
    var certified: Boolean? = null,
    var identityCard: String? = null,
    var whySuspended: String? = null,
    var accountType: String? = null,
    var brandName: String? = null,
    var password: String? = null,
    var campus: String? = null,
    var realtorCounter: Int? = null,
    var businessCounter: Int? = null,
    var adminId: String? = null,
    @ServerTimestamp var date: Date? = null
    ):Parcelable