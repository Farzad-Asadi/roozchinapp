package ir.roozchinapp.data.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.roozchinapp.data.dataBaseRoom.appDataBase.AppDatabase
import ir.roozchinapp.data.dataBaseRoom.tables.appSystemInfo.SystemDao
import ir.roozchinapp.data.dataBaseRoom.tables.category.CategoryDao
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.TaskReminderDao
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.TaskReminderRepository
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildDao
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskDao
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskScheduleDao
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskScheduleRepository
import ir.roozchinapp.data.workManager.ReminderScheduler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getDatabase(context)






    @Provides fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideEventDao(db: AppDatabase): TaskDao = db.taskDao()
    @Provides fun provideTaskScheduleDao(db: AppDatabase): TaskScheduleDao = db.taskScheduleDao()
    @Provides fun provideTaskReminderDao(db: AppDatabase): TaskReminderDao = db.taskReminderDao()
    @Provides fun provideSystemDao(db: AppDatabase): SystemDao = db.systemDao()
    @Provides fun provideTaskChildDao(db: AppDatabase): TaskChildDao = db.taskChildDao()




    //Others

    @Provides
    @Singleton
    fun provideReminderScheduler(
        @ApplicationContext context: Context,
        reminderRepo: TaskReminderRepository,
        scheduleRepo: TaskScheduleRepository,
    ): ReminderScheduler = ReminderScheduler(context, reminderRepo, scheduleRepo)


}
