package com.example.compoundeffectV1_01.data.room.category


interface CategoryRepository {

    suspend fun insertCategory(vararg category: Category)

    suspend fun deleteCategory(category: Category)

    suspend fun updateCategory(category: Category)


    suspend fun getAllCategory() : List<Category>

    suspend fun getCategoryById(categoryId:Int) : Category?


    suspend fun getCategoryWithchildren(): List<CategoryWithChildren>

    suspend fun getCategoryWithChildrenById(categoryId: Int): CategoryWithChildren?
}