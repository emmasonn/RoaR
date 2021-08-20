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
    var accountType: String? = null,
    @ServerTimestamp var date: Date? = null
    ):Parcelable