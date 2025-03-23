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


    private val _categoryUiState = MutableStateFlow(CategoryUiState())
    val categoryUiState = _categoryUiState.asStateFlow()

    init {
        initializeCategoryScreenViewModel()
    }


    private fun initializeCategoryScreenViewModel() {

        viewModelScope.launch {
            val job1 = async {

                val categoryList = categoryRepository.getAllCategory()
                _categoryUiState.update { categorydUiState ->
                    categorydUiState.copy(
                        categoryList = categoryList
                    )
                }

            }
            job1.await()

            val job2 = async {

                createNewCategory()

            }
            job2.await()

            val job3 = async {
                _categoryUiState.update { categorydUiState ->
                    categorydUiState.copy(
                        isDataLoaded = true
                    )
                }
            }
            job3.await()


        }
    }


    fun updateNewCategoryCategoryUiState(
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
                    ?: _categoryUiState.value.newCategory!!.parentCategoryId,
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

                        val categoryList = categoryRepository.getAllCategory()
                        _categoryUiState.update { categoryUiState ->
                            categoryUiState.copy(
                                categoryList = categoryList
                            )
                        }

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
            parentCategoryId = _categoryUiState.value.categoryList.lastOrNull()?.categoryId,
            icon = Icons.Filled.QuestionMark,
            color = Color(0xFF000000).colorToString(),
            description = ""
        )
        _categoryUiState.update { categorydUiState ->
            categorydUiState.copy(
                newCategory = newCategory
            )
        }
    }

    fun drugCategoryInCategoryContent(
        category: Category,
        offsetX: Float,
        offsetY: Float,
        endOfChangBranch:Boolean=false,
        endDrag: Boolean = false
    ) {
        val categoryOffset: Map<Int?, Pair<Float, Float>> =
            mutableStateMapOf(category.categoryId to Pair(offsetX, offsetY))
        _categoryUiState.update { categoryUiState ->
            categoryUiState.copy(
                categoryOffset = categoryOffset
            )
        }





        val categoryList = _categoryUiState.value.categoryList

        var changedCategory =categoryList.first { it.categoryId==category.categoryId }


        val parentCategory =
            // ریشه اصلی       for firstBranch
            categoryList.first { it.categoryId == changedCategory.parentCategoryId }


        val siblingCategoryList =
            categoryList.filter { it.parentCategoryId == changedCategory.parentCategoryId }


        val indexOfCategoryInSiblingCategory: Int = siblingCategoryList.indexOf(changedCategory)


        val topCategoryOfSelectedCategory: Category =
            when {
                indexOfCategoryInSiblingCategory == 0 -> parentCategory
                indexOfCategoryInSiblingCategory >  0 -> siblingCategoryList[indexOfCategoryInSiblingCategory -1]
                else -> parentCategory
            }


        if (offsetX - _categoryUiState.value.lastParentChangeOffsetX >= 50) {
            topCategoryOfSelectedCategory.let {
                changedCategory = category.copy(parentCategoryId = it.categoryId)
                _categoryUiState.value.lastParentChangeOffsetX = offsetX // مقدار جدید ذخیره شود
            }
        }
        if (offsetX - _categoryUiState.value.lastParentChangeOffsetX <= -50) {
                changedCategory = category.copy(parentCategoryId =parentCategory.parentCategoryId)
                _categoryUiState.value.lastParentChangeOffsetX = offsetX // مقدار جدید ذخیره شود
        }


        if (offsetY - _categoryUiState.value.lastParentChangeOffsetY >= 50) {



            changedCategory = category.copy(parentCategoryId =parentCategory.parentCategoryId)






            _categoryUiState.value.lastParentChangeOffsetY= offsetY // مقدار جدید ذخیره شود
        }


        val updatedList = categoryList.map { categoryInMap ->
            if (categoryInMap.categoryId == category.categoryId) {
                changedCategory
            } else {
                categoryInMap
            }
        }

        _categoryUiState.update { categoryUiState ->
            categoryUiState.copy(
                categoryList = updatedList
            )
        }

        if (endDrag) {


            _categoryUiState.value.lastParentChangeOffsetX = 0f // ریست مقدار ذخیره‌شده
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

                    val categoryListFromDatabase = categoryRepository.getAllCategory()
                    _categoryUiState.update { categoryUiState ->
                        categoryUiState.copy(
                            categoryList = categoryListFromDatabase
                        )
                    }

                }
                job1.await()
            }
        }


        Log.i("TEST", "---------------")

    }


}

// Data class for UI

data class CategoryUiState(
    val newCategory: Category? = null,
    val categoryList: List<Category> = listOf(),
    val categoryOffset: Map<Int?, Pair<Float, Float>> = mutableStateMapOf(),
    var lastParentChangeOffsetX: Float = 0f,
    var lastParentChangeOffsetY: Float = 0f,
    val resetKey:Boolean=false,
    val isDataLoaded: Boolean = false

)