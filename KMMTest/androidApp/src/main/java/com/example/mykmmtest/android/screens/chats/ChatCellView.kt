package com.example.mykmmtest.android.screens.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.corenetwork.model.chats.ChatUnit
import com.example.mykmmtest.MR

@Composable
fun ChatCellView(chatUnit: ChatUnit, click: () -> Unit) {
    Row(
        modifier = Modifier
            .height(60.dp)
            .clickable {
                click()
            }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    colorResource(id = MR.colors.successStateMainColor.resourceId)
                )
        )

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                fontSize = TextUnit(16f, type = TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                text = chatUnit.name
            )
            Text(
                fontSize = TextUnit(14f, type = TextUnitType.Sp),
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
                maxLines = 2,
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
            )
        }
    }
}