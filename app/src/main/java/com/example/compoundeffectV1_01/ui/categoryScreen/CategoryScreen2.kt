package com.example.compoundeffectV1_01.ui.categoryScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compoundeffectV1_01.utils.DimmedDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen2(
    onNavigateToSchedule: () -> Unit, // فعلاً استفاده نمی‌کنیم، بعداً به bottom bar وصل می‌کنیم
    viewModel: CategoryViewModel2 = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val draft by viewModel.draft.collectAsState()


    var showPickParent by rememberSaveable { mutableStateOf(false) }
    var showAddCategory by rememberSaveable { mutableStateOf(false) }




    Scaffold(
        topBar = { TopAppBar(title = { Text("CategoryScreen2") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddCategory = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Category")
            }

        },

        // bottomBar فعلاً از Navigation foundation میاد، بعداً یکدستش می‌کنیم
    ) { padding ->

            Column(modifier = Modifier.padding(padding)) {

                LazyColumn {
                    items(
                        state.renderItems,
                        key = { it.category.categoryId ?: it.hashCode() }
                    ) { item ->
                        AnimatedVisibility(
                            visible = item.isVisible,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            CategoryRow2(item, onToggleExpand = viewModel::toggleExpand)
                        }
                    }

                }
            }

    }


    if (showAddCategory) {
        AddCategoryDialog2(
            draft = draft,
            parentName = state.categories.firstOrNull { it.categoryId == draft.parentId }?.name ?: "ریشه اصلی",
            onDismiss = {
                showAddCategory = false
                viewModel.resetDraft()
            },
            onPickParent = { showPickParent = true },
            onNameChange = viewModel::setDraftName,
            onConfirm = {
                val ok = viewModel.createCategoryFromDraft()
                if (ok) showAddCategory = false
            }
        )
    }

    if (showAddCategory && showPickParent) {
        PickParentDialogSmall(
            items = state.renderItems,
            levelById = state.levelById,
            onDismiss = { showPickParent = false },
            onPick = { parentId ->
                val ok = viewModel.trySetDraftParent(parentId)
                if (ok) showPickParent = false
            }
        )
    }

}

@Composable
fun AddCategoryDialog2(
    draft: CategoryDraft2,
    parentName: String,
    onDismiss: () -> Unit,
    onPickParent: () -> Unit,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .fillMaxHeight(0.85f)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
        dimAlpha = 0.6f,
        dismissOnBackdropClick = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Top row (مثل TopBar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) { Text("Back") }
                Text(text = "گروه جدید", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onConfirm) { Text("Confirm") }
            }

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 12.dp))

            OutlinedTextField(
                value = draft.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("نام گروه") },
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPickParent() }
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("والد:", modifier = Modifier.padding(end = 8.dp))
                Text(parentName, style = MaterialTheme.typography.titleMedium)
            }

            HorizontalDivider()

            Text(
                text = "ParentId: ${draft.parentId}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}


@Composable
private fun CategoryRow2(
    item: CategoryRenderItem,
    onToggleExpand: (Int) -> Unit
) {
    val indent = (item.level - 1).coerceAtLeast(0) * 16

    ListItem(
        modifier = Modifier
            .padding(start = indent.dp),
        headlineContent = { Text(item.category.name) },
        supportingContent = { Text("id=${item.category.categoryId}, level=${item.level}") },
        trailingContent = {
            val id = item.category.categoryId
            if (id != null && item.hasChildren) {
                IconButton(onClick = { onToggleExpand(id) }) {
                    Icon(
                        imageVector = if (item.isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = "expand"
                    )
                }
            }
        }
    )
}


@Composable
fun PickParentDialogSmall(
    items: List<CategoryRenderItem>,
    levelById: Map<Int, Int>,
    onDismiss: () -> Unit,
    onPick: (parentId: Int) -> Unit,
) {
    DimmedDialog(
        onDismiss = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.86f)
            .fillMaxHeight(0.65f) // کوچکتر از قبلی
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge),
        dimAlpha = 0.4f, // کمی کمتر چون روی دیالوگ دیگر میاد
        dismissOnBackdropClick = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) { Text("Close") }
                Text("انتخاب والد", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.size(48.dp)) // برای بالانس
            }

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items, key = { it.category.categoryId ?: it.hashCode() }) { item ->
                    val id = item.category.categoryId ?: return@items
                    val level = levelById[id] ?: item.level
                    val enabled = level < 4
                    val indent = ((item.level - 1).coerceAtLeast(0) * 14).dp

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = indent, top = 6.dp, bottom = 6.dp)
                            .then(if (enabled) Modifier.clickable { onPick(id) } else Modifier),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.category.name,
                            modifier = Modifier.weight(1f),
                            color = if (enabled) LocalContentColor.current
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        if (!enabled) {
                            Text(
                                text = "حداکثر عمق",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    HorizontalDivider(thickness = 0.5.dp)
                }
            }
        }
    }
}


