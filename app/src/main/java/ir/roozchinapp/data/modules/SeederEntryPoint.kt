package ir.roozchinapp.data.modules


import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.roozchinapp.data.seed.DatabaseSeeder

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SeederEntryPoint {
    fun seeder(): DatabaseSeeder
}
