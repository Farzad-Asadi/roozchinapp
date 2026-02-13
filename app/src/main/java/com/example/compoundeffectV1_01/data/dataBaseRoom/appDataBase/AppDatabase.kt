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
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.TaskDao
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.task.Task
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskSchedule
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.TaskScheduleDao
import com.example.compoundeffectV1_01.data.dataBaseRoom.typeConvertor.ScheduleConverters
import com.example.compoundeffectV1_01.data.dataBaseRoom.typeConvertor.TypeConverter


@Database(
    entities =
    [
        CategoryEntity::class,
        Task::class,
        AppSystemInfo::class,
        TaskSchedule::class,

    ],

    version = 1, exportSchema = false)
@TypeConverters(
    TypeConverter::class,
    ScheduleConverters::class

)
abstract class AppDatabase : RoomDatabase() {


    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao
    abstract fun taskScheduleDao(): TaskScheduleDao
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