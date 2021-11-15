package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class FirebaseUser (
    var account: String? = null,
    var adminId: String? = null,//store id of admin that manages the account
    var campus: String? = null,
    var clientId: String? = null,
    var clientImage: String? = null,
    var clientName: String? = null,
    var clientPhone: String? = null,
    var counter: Long? = null,
    var certified: Boolean? = null,
    var businessPhone: String? = null,
    var realtorPhone: String? = null,
    var businessComplaint: String? = null,
    var realtorComplaint: String? = null,
    var errand: String? = null,
    var marquee: String? = null,
    var partner: String? = null,
    var slots: String? = null,
    var report: String? = null,
    var brand: String? = null,
    var password: String? = null,
    @ServerTimestamp var expired: Date? = null,
    @ServerTimestamp var date: Date? = null
    ):Parcelable