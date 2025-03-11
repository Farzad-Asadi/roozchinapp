package com.example.compoundeffectV1_01.data.room.forlearning

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(

    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "first_name", defaultValue = "test name")
    val firstName: String?,

    @ColumnInfo(name = "last_name")
    val lastName: String?,

    @Ignore     //ignore this column
    val picture: Bitmap?
)
