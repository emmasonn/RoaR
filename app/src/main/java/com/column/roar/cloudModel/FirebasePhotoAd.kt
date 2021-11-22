package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize data class FirebasePhotoAd (
    @PropertyName("id") val id: String? = null,
    @PropertyName("image") val image: String? = null,
    @PropertyName("video") val video: String? = null
): Parcelable