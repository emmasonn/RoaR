package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize
data class FirebaseUser (
    @PropertyName("account") var account: String? = null,
    @PropertyName("adminId")var adminId: String? = null,//store id of admin that manages the account
    @PropertyName("campus")var campus: String? = null,
    @PropertyName("clientId") var clientId: String? = null,
    @PropertyName("clientImage") var clientImage: String? = null,
    @PropertyName("clientName") var clientName: String? = null,
    @PropertyName("clientPhone") var clientPhone: String? = null,
    @PropertyName("counter") var counter: Long? = null,
    @PropertyName("certified") var certified: Boolean? = null,
    @PropertyName("businessPhone") var businessPhone: String? = null,
    @PropertyName("realtorPhone") var realtorPhone: String? = null,
    @PropertyName("businessComplaint") var businessComplaint: String? = null,
    @PropertyName("realtorComplaint") var realtorComplaint: String? = null,
    @PropertyName("errand") var errand: String? = null,
    @PropertyName("marquee") var marquee: String? = null,
    @PropertyName("partner") var partner: String? = null,
    @PropertyName("slots") var slots: String? = null,
    @PropertyName("brand") var brand: String? = null,
    @PropertyName("free") var free: String? = null,
    @PropertyName("password") var password: String? = null,
    @PropertyName("enuguImg") var enuguImg: String? = null,
    @PropertyName("nsukkaImg") var nsukkaImg: String? = null,
    @PropertyName("enuguPhone") var  enuguPhone: String? = null,
    @PropertyName("nsukkaPhone") var nsukkaPhone: String? = null,
    @PropertyName("expired") @ServerTimestamp var expired: Date? = null,
    @PropertyName("date") @ServerTimestamp var date: Date? = null
    ):Parcelable