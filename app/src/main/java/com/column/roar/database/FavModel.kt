package com.column.roar.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "fav_table")
data class FavModel ( @PrimaryKey val id: String)