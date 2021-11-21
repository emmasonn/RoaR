package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize data class FirebaseProperty (
    var id: String? = null,
    var product: String? = null,
    var price: String? = null,
    var cover: String? = null,
    var description : String? = null,
    var type: String? = null,
    var owner: String? = null, //owner implies the contact of the owner
    var number: String? = null, //number implies the contact of the agent
    var ownerId: String? = null, //implies agent id
    var image: String? = null,
    var promo: String? = null,
    var brand: String? = null, //this implies the name of the agent
    var certified: Boolean? = null,
    var video: String? = null,
    var campus: String? = null,
    @ServerTimestamp var postDate: Date? = null
    ): Parcelable
