package com.example.compoundeffectV1_01.data.room.typeConvertor

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.TypeConverter
import com.example.compoundeffectV1_01.utils.topic_iconMap
import java.time.LocalDateTime

//@ProvidedTypeConverter
class TypeConverter {

    @TypeConverter
    fun localDateTimeToString(localDateTime: LocalDateTime?): String? = localDateTime?.toString()

    @TypeConverter
    fun stringToTimeToString(localDateTime: String?): LocalDateTime? = LocalDateTime.parse(localDateTime)


    @TypeConverter
    fun fromIconToString(imageVector: ImageVector): String {
        val iconMap  = topic_iconMap.values.flatMap { it.entries }.associate { it.toPair() }
        return  iconMap.filterValues { it == imageVector }.keys.firstOrNull()   ?: throw IllegalArgumentException("Unknown icon type")
    }

    @TypeConverter
    fun toIcon(iconName: String): ImageVector {
        val iconMap  = topic_iconMap.values.flatMap { it.entries }.associate { it.toPair() }
        return iconMap[iconName] ?: throw IllegalArgumentException("Unknown icon name: $iconName")
    }

}

