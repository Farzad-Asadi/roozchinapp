package com.example.compoundeffectV1_01.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// دیالوگ زمینه تیره
@Composable
fun DimmedDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dimAlpha: Float = 0.2f,
    usePlatformDefaultWidth: Boolean = false,
    decorFitsSystemWindows: Boolean = false,
    dismissOnBackdropClick: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),   // 👈 کنترل گردی
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = usePlatformDefaultWidth,
            decorFitsSystemWindows = decorFitsSystemWindows
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = dimAlpha))
                .then(
                    if (dismissOnBackdropClick) {
                        Modifier.clickable(
                            onClick = onDismiss,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {

            Surface(
                shape = shape, // 👈 اینجا اعمال می‌شود
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 10.dp,
                modifier = modifier
            ) {
                Box(
                    modifier = Modifier.clickable(
                        onClick = {},
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                ) {
                    content()
                }
            }
        }
    }
}


//یک Confirm Dialog عمومی (برای حذف‌ها)
@Composable
fun ConfirmDimmedDialog(
    visible: Boolean,
    title: String,
    message: @Composable () -> Unit,
    confirmText: String = "بله، حذف شود",
    dismissText: String = "انصراف",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    DimmedDialog(onDismiss = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)

                message()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text(dismissText) }
                    TextButton(onClick = {
                        onConfirm()
                        onDismiss()
                    }) { Text(confirmText) }
                }
            }
        }
    }
}