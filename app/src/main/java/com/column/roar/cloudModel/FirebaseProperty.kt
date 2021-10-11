package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize data class FirebaseProperty (
    var id: String? = null,
    var propertyTitle: String? = null,
    var propertyPrice: String? = null,
    var firstImage: String? = null,
    var propertyDesc : String? = null,
    var propertyType: String? = null,
    var sellerName: String? = null,
    var sellerNumber: String? = null,
    var sellerId: String? = null,
    var sellerUrl: String? = null,
    var specials: String? = null,
    var brandName: String? = null,
    var certified: Boolean? = null,
    var campus: String? = null,
    @ServerTimestamp var postDate: Date? = null
    ): Parcelable