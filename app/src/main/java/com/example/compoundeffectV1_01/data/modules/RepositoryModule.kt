package com.example.compoundeffectV1_01.data.modules

import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.category.CategoryRepository
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.category.CategoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository




}
