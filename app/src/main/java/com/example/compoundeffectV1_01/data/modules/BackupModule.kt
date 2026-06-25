package com.example.compoundeffectV1_01.data.modules

import com.example.compoundeffectV1_01.data.backup.compound.CompoundBackupProvider
import com.example.compoundeffectV1_01.data.backup.core.BackupModuleProvider
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackupProvideModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BackupBindModule {

    @Binds
    @IntoSet
    abstract fun bindCompoundBackupProvider(
        impl: CompoundBackupProvider
    ): BackupModuleProvider
}