package ir.roozchinapp.data.dataBaseRoom.appDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ir.roozchinapp.data.dataBaseRoom.tables.appSystemInfo.AppSystemInfo
import ir.roozchinapp.data.dataBaseRoom.tables.appSystemInfo.SystemDao
import ir.roozchinapp.data.dataBaseRoom.tables.category.CategoryDao
import ir.roozchinapp.data.dataBaseRoom.tables.category.CategoryEntity
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.TaskReminderDao
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.TaskReminderEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.PomodoroDailyAdjustmentEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.ReusableListItemEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildDao
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildRequirementEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildRuleEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskDao
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskEntity
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskListSessionEntity
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskSchedule
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskScheduleDao
import ir.roozchinapp.data.dataBaseRoom.typeConvertor.ScheduleConverters
import ir.roozchinapp.data.dataBaseRoom.typeConvertor.TypeConverter


@Database(
    entities =
    [
        CategoryEntity::class,
        TaskEntity::class,
        AppSystemInfo::class,
        TaskSchedule::class,
        TaskReminderEntity::class,
        PomodoroDailyAdjustmentEntity::class,
        TaskChildRuleEntity::class,
        TaskChildRequirementEntity::class,
        TaskListSessionEntity::class,
        ReusableListItemEntity::class,

    ],

    version = 3, exportSchema = false)
@TypeConverters(
    TypeConverter::class,
    ScheduleConverters::class

)
abstract class AppDatabase : RoomDatabase() {


    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao
    abstract fun taskScheduleDao(): TaskScheduleDao
    abstract fun taskReminderDao(): TaskReminderDao
    abstract fun systemDao(): SystemDao
    abstract fun taskChildDao(): TaskChildDao




    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            ALTER TABLE task
            ADD COLUMN entityStatus TEXT NOT NULL DEFAULT 'ACTIVE'
            """.trimIndent()
                )

                db.execSQL(
                    """
            ALTER TABLE task
            ADD COLUMN draftCreatedAtEpochMillis INTEGER
            """.trimIndent()
                )

                db.execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_task_entityStatus
            ON task(entityStatus)
            """.trimIndent()
                )

                db.execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_task_categoryId_entityStatus
            ON task(categoryId, entityStatus)
            """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            ALTER TABLE task
            ADD COLUMN childStructure TEXT NOT NULL DEFAULT 'SUBTASKS'
            """.trimIndent()
                )

                db.execSQL(
                    """
            ALTER TABLE task
            ADD COLUMN showInAnytimePallet INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
                )

                db.execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_task_showInAnytimePallet
            ON task(showInAnytimePallet)
            """.trimIndent()
                )

                db.execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_task_entityStatus_showInAnytimePallet
            ON task(entityStatus, showInAnytimePallet)
            """.trimIndent()
                )
            }
        }


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
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3
                )

            return builder.build()
        }

    }


}