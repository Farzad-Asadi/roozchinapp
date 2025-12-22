package com.example.compoundeffectV1_01.data.modules

import com.example.compoundeffectV1_01.data.seed.DatabaseSeeder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SeederEntryPoint {
    fun seeder(): DatabaseSeeder
}
