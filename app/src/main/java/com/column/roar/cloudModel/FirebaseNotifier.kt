package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*


@IgnoreExtraProperties
@Parcelize
data class FirebaseNotifier(
    var notifierId: String? = null,
    var lodgeCover: String? = null,
    var lodgeName: String? = null,
    var lodgeLocation: String? = null,
    var campus: String? = null,
    var clientName: String? = null,
    var clientPhone: String? = null,
    var visitDate: String? = null,
    @ServerTimestamp var postDate: Date? = null,
) : Parcelable