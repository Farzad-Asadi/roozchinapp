package com.example.compoundeffectV1_01.data.room.category

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "category")
data class Category(

    @PrimaryKey(autoGenerate = true)
    val categoryId : Int?=null,

    val name: String,
    val parentCategoryId: Int?=null,
    val icon: ImageVector ,
    val color: String ,
    val description: String,
)

data class CategoryWithChildren(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "parentCategoryId"
    )val categoryChildren: List<Category>?
)




