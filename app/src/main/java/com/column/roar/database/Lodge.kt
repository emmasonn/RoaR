package com.column.roar.database

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Keep
@Entity(tableName = "lodge_table")
class Lodge(
    @PrimaryKey val id: String,
    val seen: Boolean?,
    val url: String?
)

data class LodgeAndPhotos(
    @Embedded val lodge: Lodge,
    @Relation(parentColumn = "id", entityColumn = "uid")
    val photos: List<Photo>
)