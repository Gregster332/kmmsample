package com.example.mykmmtest.android.screens

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mykmmtest.android.MyApplicationTheme
import com.example.mykmmtest.stories.debugMenu.DebugMenuComponent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DialogContent(debugMenu: DebugMenuComponent) {
    ModalBottomSheetLayout(sheetContent = {}) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.8f)
        ) {
            Text("dsdsdsds")

            Button(onClick = { debugMenu.close() }) {
                Text("Close")
            }
        }
    }
}