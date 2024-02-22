package com.example.mykmmtest.android.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.example.core.Services.SettingsValue
import com.example.mykmmtest.MR
import com.example.mykmmtest.stories.debugMenu.DebugMenu
import com.example.mykmmtest.stories.debugMenu.PreviewDebugMenu
import com.example.mykmmtest.stories.debugMenu.SettingsSections

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DialogContent(debugMenu: DebugMenu) {

    // Resources
    val xmarkImage = painterResource(id = MR.images.xmark.drawableResId)

    // State
    val settings = debugMenu.settings.subscribeAsState()

    Surface {
        ModalBottomSheetLayout(sheetContent = {}) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.8f)
            ) {
                Column {
                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(horizontal = Dp(8f))
                            ,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(
                                id = MR.colors.textFieldBGColor.resourceId
                            ).copy(alpha = 0.6f)
                        ),
                        onClick = { debugMenu.close() }
                    ) {
                        Image(painter = xmarkImage, contentDescription = "")
                    }
                }

                Sections(settings.value) { newValue, old ->
                    debugMenu.updateSettings(old, newValue)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Sections(
    section: SettingsSections,
    didTapOnBoolValue: (Boolean, SettingsValue) -> Unit
) {
    LazyColumn(
        Modifier
            .padding(top = 60.dp)
    ) {
        stickyHeader {
            Text(
                section.boolSection.title,
                modifier = Modifier
                    .padding(8.dp)
                    .offset(10.dp),
                fontSize = TextUnit(16f, TextUnitType.Sp),
                fontWeight = FontWeight.Medium
            )
        }

        items(section.boolSection.settings) { value ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(
                                topEnd = 8.dp,
                                topStart = 8.dp,
                                bottomEnd = 8.dp,
                                bottomStart = 8.dp
                            )
                        )
                        .background(
                            colorResource(
                                id = MR.colors.textFieldBGColor.resourceId
                            ).copy(0.1f)
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        value.key,
                        modifier = Modifier.weight(1f),
                        fontSize = TextUnit(16f, TextUnitType.Sp),
                        fontWeight = FontWeight.Medium
                    )

                    FilledIconToggleButton(
                        checked = value.value,
                        onCheckedChange = {
                            didTapOnBoolValue(it, value)
                        },
                        modifier = Modifier
                            .height(20.dp)
                            .width(20.dp)
                    ) {}
            }
        }

        stickyHeader {
            Text(
                section.stringsSection.title,
                modifier = Modifier
                    .padding(8.dp)
                    .offset(10.dp),
                fontSize = TextUnit(16f, TextUnitType.Sp),
                fontWeight = FontWeight.Medium
            )
        }

        items(section.stringsSection.settings) { value ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topEnd = 8.dp,
                            topStart = 8.dp,
                            bottomEnd = 8.dp,
                            bottomStart = 8.dp
                        )
                    )
                    .background(
                        colorResource(
                            id = MR.colors.textFieldBGColor.resourceId
                        ).copy(0.1f)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    value.key,
                    modifier = Modifier.weight(1f),
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    fontWeight = FontWeight.Medium
                )

                Text(value.value)
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    DialogContent(
        debugMenu = PreviewDebugMenu()
    )
}

fun Color.Companion.clear(): Color = Black.copy(0f)