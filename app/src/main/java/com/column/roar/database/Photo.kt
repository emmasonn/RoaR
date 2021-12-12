package com.column.roar.database

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.column.roar.cloudModel.FirebaseLodgePhoto

@Keep
@Entity(
    tableName = "photo_table", foreignKeys = [ForeignKey(
        entity = Lodge::class,
        parentColumns = ["id"], childColumns = ["uid"]
    )], indices = [Index("uid")]
)
class Photo(
    @PrimaryKey val id: String,
    val uid: String?,
    val url: String?,
    val title: String?
)

fun List<Photo>.toFirebasePhotos(): List<FirebaseLodgePhoto> {
    return this.map {
        FirebaseLodgePhoto(
            id = it.id,
            image = it.url,
            title = it.title
        ) }
}

fun List<FirebaseLodgePhoto>.toPhotos(lodgeId: String?): List<Photo> {
    return this.map {
        Photo(
            id = it.id?: "",
            url = it.image,
            title = it.title,
            uid = lodgeId
        )
    }
}