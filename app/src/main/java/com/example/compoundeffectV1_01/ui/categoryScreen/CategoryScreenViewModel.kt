package com.example.compoundeffectV1_01.ui.categoryScreen

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compoundeffectV1_01.data.room.appSystemInfo.AppSystemInfoRepository
import com.example.compoundeffectV1_01.data.room.category.Category
import com.example.compoundeffectV1_01.data.room.category.CategoryRepository
import com.example.compoundeffectV1_01.data.room.event.EventRepository
import com.example.compoundeffectV1_01.utils.colorToString
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryScreenViewModel(
    private val categoryRepository: CategoryRepository,
    private val eventRepository: EventRepository,
    private val appSystemInfoRepository: AppSystemInfoRepository
) : ViewModel() {


    // region init

    private val _categoryUiState = MutableStateFlow(CategoryUiState())
    val categoryUiState = _categoryUiState.asStateFlow()

    init {
        initializeCategoryScreenViewModel()
    }

    private fun initializeCategoryScreenViewModel() {

        viewModelScope.launch {
            val job1 = async {
                fetchAndUpdateCategory()
            }
            job1.await()

            val job2 = async {
                createNewCategory()
            }
            job2.await()

            val job3 = async {
                _categoryUiState.update { categoryUiState ->
                    categoryUiState.copy(
                        isDataLoaded = true
                    )
                }
            }
            job3.await()
        }
    }

    // endregion


    // region main fun
    fun onClickConfirmBackInNewCategory(
        name: String = "",
        parentId: Int? = null,
        icon: ImageVector? = null,
        color: Color? = null,
        back: Boolean = false,
        confirm: Boolean = false
    ) {

        if (_categoryUiState.value.newCategory != null) {
            val updateNewCategory = _categoryUiState.value.newCategory!!.copy(
                name = if (name != "") name else _categoryUiState.value.newCategory!!.name,
                parentCategoryId = parentId
                    ?: 1,
                icon = icon ?: _categoryUiState.value.newCategory!!.icon,
                color = color?.colorToString() ?: _categoryUiState.value.newCategory!!.color,
            )

            _categoryUiState.update { categoryUiState ->
                categoryUiState.copy(
                    newCategory = updateNewCategory
                )
            }

            if (confirm) {
                viewModelScope.launch {

                    val job1 = async {
                        categoryRepository.insertCategory(updateNewCategory)
                    }
                    job1.await()

                    val job2 = async {
                        fetchAndUpdateCategory()
                    }
                    job2.await()
                }
                createNewCategory()
            }

            if (back) {
                createNewCategory()
            }

        }

    }

    private fun createNewCategory() {
        val newCategory = Category(
            name = "",
            parentCategoryId = 1,
            icon = Icons.Filled.QuestionMark,
            color = Color(0xFF000000).colorToString(),
            description = ""
        )
        _categoryUiState.update { categoryUiState ->
            categoryUiState.copy(
                newCategory = newCategory
            )
        }
    }

    fun drugCategoryInCategoryContent(
        category: Category,
        offsetX: Float,
        offsetY: Float,
        endDrag: Boolean = false
    ) {
        Log.i("TEST", "offsetY=$offsetY")
        val categoryOffset: Map<Int?, Pair<Float, Float>> =
            mutableStateMapOf(category.categoryId to Pair(offsetX, offsetY))
        _categoryUiState.update { categoryUiState ->
            categoryUiState.copy(
                categoryOffset = categoryOffset
            )
        }


        val categoryList = _categoryUiState.value.categoryList
        var sortedCategoryWhitGeneration = _categoryUiState.value.sortedCategoryWhitGeneration

        var changedCategory = categoryList.first { it.categoryId == category.categoryId }

        val parentCategory =
            // ریشه اصلی       for firstBranch
            categoryList.first { it.categoryId == changedCategory.parentCategoryId }

        val siblingCategoryList =
            sortedCategoryWhitGeneration.keys.filter { it.parentCategoryId == changedCategory.parentCategoryId }

        val indexOfCategoryInSiblingCategory: Int = siblingCategoryList.indexOf(changedCategory)

        val topCategoryOfSelectedCategory: Category =
            when {
                indexOfCategoryInSiblingCategory == 0 -> parentCategory
                indexOfCategoryInSiblingCategory > 0 -> siblingCategoryList[indexOfCategoryInSiblingCategory - 1]
                else -> parentCategory
            }


        if (offsetX - _categoryUiState.value.lastParentChangeOffsetX >= 50) {
            if ((sortedCategoryWhitGeneration[changedCategory] ?: 5) <= 5) {
                topCategoryOfSelectedCategory.let {
                    changedCategory = category.copy(parentCategoryId = it.categoryId)
                    _categoryUiState.value.lastParentChangeOffsetX = offsetX // مقدار جدید ذخیره شود
                }
            }
        }
        if (offsetX - _categoryUiState.value.lastParentChangeOffsetX <= -50) {
            if ((sortedCategoryWhitGeneration[changedCategory] ?: 2) >= 3) {
                changedCategory = category.copy(parentCategoryId = parentCategory.parentCategoryId)
                _categoryUiState.value.lastParentChangeOffsetX = offsetX // مقدار جدید ذخیره شود
            }
        }









        if (offsetY - _categoryUiState.value.lastParentChangeOffsetY >= 50) {

            if (siblingCategoryList.lastIndex >= indexOfCategoryInSiblingCategory + 1) {
                val parentIdOfBelowSibling =
                    siblingCategoryList[indexOfCategoryInSiblingCategory + 1].categoryId

                if (category.expandable) {

                    changedCategory =
                        category.copy(isExtended = false, parentCategoryId = parentIdOfBelowSibling)
                    val allChildren = getAllChildren(categoryList.toSet(), category)

                    sortedCategoryWhitGeneration = sortedCategoryWhitGeneration.mapKeys {
                        if (it.key in allChildren) {
                            it.key.copy(visible = false)
                        } else {
                            it.key

                        }
                    }
//                updatedSortedCategoryWhitGeneration.keys.forEach {
//                    Log.i("TEST", "${it.name} =${it.visible}")
//                }


                    _categoryUiState.value.lastParentChangeOffsetY = offsetY // مقدار جدید ذخیره شود
                }
            }

        }


        val updatedList = categoryList.map { categoryInMap ->
            if (categoryInMap.categoryId == category.categoryId) {
                changedCategory
            } else {
                categoryInMap
            }
        }
        val expandableCheckedCategoryList = expandableCheckedCategory(updatedList)

        sortedCategoryWhitGeneration = sortedCategoryWhitGeneration.mapKeys { entry ->
            expandableCheckedCategoryList.find { it == entry.key } ?: entry.key
        }
        _categoryUiState.update { categoryUiState ->
            categoryUiState.copy(
                sortedCategoryWhitGeneration = sortedCategoryWhitGeneration,
            )
        }

        if (endDrag) {


            _categoryUiState.value.lastParentChangeOffsetX = 0f // ریست مقدار ذخیره‌شده
            _categoryUiState.value.lastParentChangeOffsetY = 0f // ریست مقدار ذخیره‌شده
            val newCategoryOffset: Map<Int?, Pair<Float, Float>> =
                mutableStateMapOf(category.categoryId to Pair(0f, 0f))

            _categoryUiState.update { categoryUiState ->
                categoryUiState.copy(
                    categoryOffset = newCategoryOffset,
                    resetKey = !_categoryUiState.value.resetKey
                )
            }
            viewModelScope.launch {
                val job0 = async {
                    categoryRepository.updateCategory(changedCategory)
                }
                job0.await()

                val job1 = async {

                    fetchAndUpdateCategory()

                }
                job1.await()
            }
        }


        Log.i("TEST", "---------------")

    }

    fun onClickExpandInCategoryListInCategoryScreen(categoryId: Int, isExtended: Boolean) {
        val category =
            _categoryUiState.value.sortedCategoryWhitGeneration.keys.firstOrNull { it.categoryId == categoryId }
                ?: return


        if (isExtended) {
            val updatedCategoryList = _categoryUiState.value.categoryList.map { categoryInMap ->
                if (categoryInMap.categoryId == categoryId) {
                    categoryInMap.copy(isExtended = false)
                } else {
                    categoryInMap
                }
            }
            val allChildrenOfUpdatedCategoryList =
                getAllChildren(updatedCategoryList.toSet(), category)
            val updateCategoryList = updatedCategoryList.map { categoryInMap ->
                if (categoryInMap in allChildrenOfUpdatedCategoryList) {
                    categoryInMap.copy(visible = false)
                } else {
                    categoryInMap
                }
            }
            _categoryUiState.update { categoryUiState ->
                categoryUiState.copy(
                    categoryList = updateCategoryList,
                )
            }
            saveCategoryToDatabase(updateCategoryList)


            val updatedSortedCategoryWhitGeneration =
                _categoryUiState.value.sortedCategoryWhitGeneration.mapKeys {
                    if (it.key.categoryId == categoryId) {
                        it.key.copy(isExtended = false)
                    } else {
                        it.key
                    }
                }
            val allChildrenOfSortedCategoryWhitGeneration =
                getAllChildren(updatedSortedCategoryWhitGeneration.keys.toSet(), category)

            val updateSortedCategoryWhitGeneration = updatedSortedCategoryWhitGeneration.mapKeys {
                if (it.key in allChildrenOfSortedCategoryWhitGeneration) {
                    it.key.copy(visible = false)
                } else {
                    it.key
                }
            }
            _categoryUiState.update { categoryUiState ->
                categoryUiState.copy(
                    sortedCategoryWhitGeneration = updateSortedCategoryWhitGeneration,
                )
            }


        } else {


            val updatedCategoryList = _categoryUiState.value.categoryList.map { categoryInMap ->
                if (categoryInMap.categoryId == categoryId) {
                    categoryInMap.copy(isExtended = true)
                } else {
                    categoryInMap
                }
            }
            val allChildrenOfUpdatedCategoryList =
                getAllChildren(updatedCategoryList.toSet(), category)
            val updateCategoryList = updatedCategoryList.map { categoryInMap ->
                if (categoryInMap in allChildrenOfUpdatedCategoryList) {
                    categoryInMap.copy(visible = true)
                } else {
                    categoryInMap
                }
            }
            _categoryUiState.update { categoryUiState ->
                categoryUiState.copy(
                    categoryList = updateCategoryList,
                )
            }
            saveCategoryToDatabase(updateCategoryList)


            val updatedSortedCategoryWhitGeneration =
                _categoryUiState.value.sortedCategoryWhitGeneration.mapKeys {
                    if (it.key.categoryId == categoryId) {
                        it.key.copy(isExtended = true)
                    } else {
                        it.key
                    }
                }
            val allChildrenOfSortedCategoryWhitGeneration =
                getAllChildren(updatedSortedCategoryWhitGeneration.keys.toSet(), category)
            val updateSortedCategoryWhitGeneration = updatedSortedCategoryWhitGeneration.mapKeys {
                if (it.key in allChildrenOfSortedCategoryWhitGeneration) {
                    it.key.copy(visible = true)
                } else {
                    it.key
                }
            }
            _categoryUiState.update { categoryUiState ->
                categoryUiState.copy(
                    sortedCategoryWhitGeneration = updateSortedCategoryWhitGeneration,
                )
            }
        }


    }

    // endregion


    // region side fun


    private fun fetchAndUpdateCategory() {
        viewModelScope.launch {
            val categoryList = categoryRepository.getAllCategory()
            val sortedCategoryWhitGeneration =
                sortCategoriesWithGenerations(expandableCheckedCategory(categoryList))
            _categoryUiState.update { categoryUiState ->
                categoryUiState.copy(
                    sortedCategoryWhitGeneration = sortedCategoryWhitGeneration
                )
            }
        }
    }

    private fun expandableCheckedCategory(categoryList: List<Category>): List<Category> {
        val categoryMap = categoryList.groupBy { it.parentCategoryId }
        val expandableCheckedCategoryList = categoryList.map { category ->
            if (categoryMap.keys.contains(category.categoryId)) {
                category.copy(expandable = true)
            } else {
                category.copy(expandable = false)
            }
        }
        _categoryUiState.update { categoryUiState ->
            categoryUiState.copy(
                categoryList = expandableCheckedCategoryList,
            )
        }
        return expandableCheckedCategoryList
    }

    private fun saveCategoryToDatabase(categoryList: List<Category>) {
        viewModelScope.launch {
            categoryList.forEach { category ->
                categoryRepository.updateCategory(category)
            }
        }
    }

    private fun updateSortedCategoryWhitGeneration(sortedCategoryWhitGeneration: Map<Category, Int>) {

        _categoryUiState.update { categoryUiState ->
            categoryUiState.copy(
                sortedCategoryWhitGeneration = sortedCategoryWhitGeneration,
            )
        }
    }

    private fun sortCategoriesWithGenerations(categoryList: List<Category>): Map<Category, Int> {

        val categoryMap = categoryList.groupBy { it.parentCategoryId }
        val rootCategories = categoryList.filter { it.parentCategoryId == -1 }
        val generationMap = mutableMapOf<Category, Int>()

        fun traverseTree(category: Category, generation: Int): List<Category> {
            val sortedList = mutableListOf<Category>()

            generationMap[category] = generation
            sortedList.add(category)
            val children = categoryMap[category.categoryId] ?: emptyList()
            for (child in children) {
                sortedList.addAll(traverseTree(child, generation + 1)) // پیمایش فرزند با نسل +1
            }
            return sortedList
        }

        val sortedCategories = rootCategories.flatMap { traverseTree(it, 1) }
        val sortedCategoriesMinesRootCategories =
            sortedCategories.filterNot { it.parentCategoryId == -1 }
        val newSortCategoriesWithGeneration =
            sortedCategoriesMinesRootCategories.associateWith { generationMap[it]!! }
        _categoryUiState.update { categoryUiState ->
            categoryUiState.copy(
                sortedCategoryWhitGeneration = newSortCategoriesWithGeneration,
            )
        }
        return newSortCategoriesWithGeneration
    }

    private fun getAllChildren(allCategories: Set<Category>, parent: Category): List<Category> {
        val directChildren = allCategories.filter { it.parentCategoryId == parent.categoryId }

        // پیدا کردن فرزندهای این فرزندان
        return directChildren + directChildren.flatMap { getAllChildren(allCategories.toSet(), it) }
    }
    // endregion

}

// Data class for UI

data class CategoryUiState(
    val newCategory: Category? = null,
    val categoryList: List<Category> = listOf(),
    val sortedCategoryWhitGeneration: Map<Category, Int> = mutableStateMapOf(),
    val categoryOffset: Map<Int?, Pair<Float, Float>> = mutableStateMapOf(),
    var lastParentChangeOffsetX: Float = 0f,
    var lastParentChangeOffsetY: Float = 0f,
    val resetKey: Boolean = false,
    val isDataLoaded: Boolean = false

)