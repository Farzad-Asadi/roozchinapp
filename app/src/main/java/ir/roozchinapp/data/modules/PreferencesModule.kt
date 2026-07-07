package ir.roozchinapp.data.modules


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.roozchinapp.data.dataStore.AppPreferences
import ir.roozchinapp.data.dataStore.AppPreferencesImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    @Singleton
    abstract fun bindAppPreferences(
        impl: AppPreferencesImpl
    ): AppPreferences
}
