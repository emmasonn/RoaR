package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class FirebaseLodgePhoto(
    @PropertyName("id") var id: String? = null,
    @PropertyName("image") var image: String? = null,
    @PropertyName("title") var title: String? = null,
    @PropertyName("certified") var certified: Boolean? = null,
    @PropertyName("video") var video: String? = null
): Parcelable
