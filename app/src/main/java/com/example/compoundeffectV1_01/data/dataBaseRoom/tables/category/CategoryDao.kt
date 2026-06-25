package com.example.compoundeffectV1_01.data.dataBaseRoom.tables.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(vararg categoryEntity: CategoryEntity)

    @Delete
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    @Update
    suspend fun updateCategory(categoryEntity: CategoryEntity)



    @Query("SELECT * FROM category ")
    suspend fun getAllCategory() : List<CategoryEntity>

    @Query("SELECT * FROM category WHERE categoryId=:categoryId")
    suspend fun getCategoryById(categoryId:Int) : CategoryEntity?


    @Transaction
    @Query("SELECT * FROM category")
    suspend fun getCategoryWithchildren(): List<CategoryWithChildren>

    @Transaction
    @Query("SELECT * FROM category WHERE categoryId = :categoryId")
    suspend fun getCategoryWithChildrenById(categoryId: Int): CategoryWithChildren?

    @Query("SELECT * FROM category")
    fun observeAll(): Flow<List<CategoryEntity>>



    //for seeder
    @Query("SELECT COUNT(*) FROM category")
    suspend fun count(): Int


    //region Backup / Restore

    @Query("SELECT * FROM category")
    suspend fun getAllCategoriesForBackup(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoriesForRestore(categories: List<CategoryEntity>)

    @Query("DELETE FROM category")
    suspend fun deleteAllCategoriesForRestore()

//endregion


}