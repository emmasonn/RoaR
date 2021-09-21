package com.beaconinc.roarhousing.cloudModel

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize data class FirebasePhotoAd (
    val id: String? = null,
    val adUrl: String? = null
): Parcelable