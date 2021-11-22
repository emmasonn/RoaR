package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@IgnoreExtraProperties
@Parcelize data class FirebaseProperty (
    @PropertyName("id") var id: String? = null,
    @PropertyName("product") var product: String? = null,
    @PropertyName("price") var price: String? = null,
    @PropertyName("cover") var cover: String? = null,
    @PropertyName("description") var description : String? = null,
    @PropertyName("type") var type: String? = null,
    @PropertyName("owner") var owner: String? = null, //owner implies the contact of the owner
    @PropertyName("number") var number: String? = null, //number implies the contact of the agent
    @PropertyName("ownerId") var ownerId: String? = null, //implies agent id
    @PropertyName("image") var image: String? = null,
    @PropertyName("promo") var promo: String? = null,
    @PropertyName("brand") var brand: String? = null, //this implies the name of the agent
    @PropertyName("certified") var certified: Boolean? = null,
    @PropertyName("video") var video: String? = null,
    @PropertyName("campus") var campus: String? = null,
    @PropertyName("postDate") @ServerTimestamp var postDate: Date? = null
    ): Parcelable
