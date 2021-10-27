package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize data class FirebaseProperty (
    var id: String? = null,
    var productName: String? = null,
    var productPrice: String? = null,
    var coverImage: String? = null,
    var description : String? = null,
    var type: String? = null,
    var sellerName: String? = null,
    var sellerNumber: String? = null,
    var sellerId: String? = null,
    var sellerImage: String? = null,
    var promo: String? = null,
    var brand: String? = null,
    var certified: Boolean? = null,
    var video: String? = null,
    var campus: String? = null,
    @ServerTimestamp var postDate: Date? = null
    ): Parcelable
