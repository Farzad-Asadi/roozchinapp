package com.example.compoundeffectV1_01.data.room.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(vararg category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)



    @Query("SELECT * FROM category ")
    suspend fun getAllCategory() : List<Category>

    @Query("SELECT * FROM category WHERE categoryId=:categoryId")
    suspend fun getCategoryById(categoryId:Int) : Category?


    @Transaction
    @Query("SELECT * FROM category")
    suspend fun getCategoryWithchildren(): List<CategoryWithChildren>

    @Transaction
    @Query("SELECT * FROM category WHERE categoryId = :categoryId")
    suspend fun getCategoryWithChildrenById(categoryId: Int): CategoryWithChildren?
}