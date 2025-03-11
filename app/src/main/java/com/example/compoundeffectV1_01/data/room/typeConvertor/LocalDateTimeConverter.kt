package com.example.compoundeffectV1_01.data.room.typeConvertor

import androidx.room.TypeConverter
import java.time.LocalDateTime

//@ProvidedTypeConverter
class LocalDateTimeConverter {

    @TypeConverter
    fun localDateTimeToString(localDateTime: LocalDateTime?): String? = localDateTime?.toString()

    @TypeConverter
    fun stringToTimeToString(localDateTime: String?): LocalDateTime? = LocalDateTime.parse(localDateTime)
}