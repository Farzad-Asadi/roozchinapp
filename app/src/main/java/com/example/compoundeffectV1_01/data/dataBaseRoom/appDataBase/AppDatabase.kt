package com.example.compoundeffectV1_01.data.dataBaseRoom.appDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.appSystemInfo.AppSystemInfo
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.appSystemInfo.SystemDao
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.category.CategoryEntity
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.category.CategoryDao
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.event.Event
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.event.EventDao
import com.example.compoundeffectV1_01.data.dataBaseRoom.typeConvertor.TypeConverter


@Database(
    entities =
    [CategoryEntity::class,
        Event::class,
        AppSystemInfo::class],

    version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {


    abstract fun categoryDao(): CategoryDao
    abstract fun eventDao(): EventDao
    abstract fun systemDao(): SystemDao




    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }

            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            val builder = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "compound_effect.db"
            )

            return builder.build()
        }

    }


}