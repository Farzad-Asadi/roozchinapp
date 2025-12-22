package com.example.compoundeffectV1_01.data.room.category


class OfflineCategoryRepository(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override suspend fun insertCategory(vararg category: Category) =
        categoryDao.insertCategory(*category)

    override suspend fun deleteCategory(category: Category) =
        categoryDao.deleteCategory(category)


    override suspend fun updateCategory(category: Category) =
        categoryDao.updateCategory(category)

    override suspend fun getAllCategory(): List<Category> =
        categoryDao.getAllCategory()

    override suspend fun getCategoryById(categoryId: Int): Category? =
        categoryDao.getCategoryById(categoryId)

    override suspend fun getCategoryWithchildren(): List<CategoryWithChildren> =
        categoryDao.getCategoryWithchildren()

    override suspend fun getCategoryWithChildrenById(categoryId: Int): CategoryWithChildren? =
            categoryDao.getCategoryWithChildrenById(categoryId)


}