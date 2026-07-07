package ir.roozchinapp.data.dataBaseRoom.typeConvertor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.ui.graphics.vector.ImageVector

fun iconNameToImageVector(name: String): ImageVector {
    return when (name) {
        "AccountTree" -> Icons.Filled.AccountTree
        "Apps" -> Icons.Filled.Apps
        "QuestionMark" -> Icons.Filled.QuestionMark
        else -> Icons.Filled.Apps // پیشفرض
    }
}
