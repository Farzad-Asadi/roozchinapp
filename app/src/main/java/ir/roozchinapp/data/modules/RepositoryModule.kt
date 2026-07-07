package ir.roozchinapp.data.modules


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.roozchinapp.data.dataBaseRoom.tables.appSystemInfo.AppSystemInfoRepository
import ir.roozchinapp.data.dataBaseRoom.tables.appSystemInfo.AppSystemInfoRepositoryImpl
import ir.roozchinapp.data.dataBaseRoom.tables.category.CategoryRepository
import ir.roozchinapp.data.dataBaseRoom.tables.category.CategoryRepositoryImpl
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.TaskReminderRepository
import ir.roozchinapp.data.dataBaseRoom.tables.reminder.TaskReminderRepositoryImpl
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildRepository
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskChildRepositoryImpl
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskRepository
import ir.roozchinapp.data.dataBaseRoom.tables.task.TaskRepositoryImpl
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskScheduleRepository
import ir.roozchinapp.data.dataBaseRoom.tables.taskSchedule.TaskScheduleRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds @Singleton
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Binds @Singleton
    abstract fun bindTaskScheduleRepository(
        impl: TaskScheduleRepositoryImpl
    ): TaskScheduleRepository

    @Binds @Singleton
    abstract fun bindAppSystemInfoRepository(
        impl: AppSystemInfoRepositoryImpl
    ): AppSystemInfoRepository

    @Binds @Singleton
    abstract fun bindTaskReminderRepository (
        impl: TaskReminderRepositoryImpl
    ): TaskReminderRepository

    @Binds
    abstract fun bindTaskChildRepository(
        impl: TaskChildRepositoryImpl
    ): TaskChildRepository





}
