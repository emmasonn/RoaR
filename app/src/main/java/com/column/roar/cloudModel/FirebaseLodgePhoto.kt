package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class FirebaseLodgePhoto(
    var id: String? = null,
    var image: String? = null,
    var title: String? = null,
    var certified: Boolean? = null,
    var video: String? = null
): Parcelable
