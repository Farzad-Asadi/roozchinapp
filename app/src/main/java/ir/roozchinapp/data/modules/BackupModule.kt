package ir.roozchinapp.data.modules


import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import ir.roozchinapp.data.backup.compound.CompoundBackupProvider
import ir.roozchinapp.data.backup.core.BackupModuleProvider
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