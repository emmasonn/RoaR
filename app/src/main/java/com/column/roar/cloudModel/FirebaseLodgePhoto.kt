package com.column.roar.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class FirebaseLodgePhoto(
    var photoId: String? = null,
    var photoUrl: String? = null,
    var photoTitle: String? = null
): Parcelable

