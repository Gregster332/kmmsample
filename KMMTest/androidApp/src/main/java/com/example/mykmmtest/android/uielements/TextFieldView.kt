package com.example.mykmmtest.android.uielements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextFieldView(
    text: String,
    isError: Boolean,
    isValid: Boolean,
    onValueChange: (String) -> Unit
) {

    val tfColor = if (isValid) {
        Color.Green.copy(0.9f)
    } else if (isError) {
        Color.Red.copy(0.9f)
    } else {
        Color.Gray.copy(0.9f)
    }

    val backgroundColor = if (isError) {
        Color.Red.copy(0.1f)
    } else if (isValid) {
        Color.Green.copy(0.1f)
    } else {
        Color.Gray.copy(0.1f)
    }

    Column(
        Modifier
            .height(IntrinsicSize.Min)
            .padding(bottom = 4.dp)
            .background(
                backgroundColor,
                RoundedCornerShape(CornerSize(8.dp))
            )
            .padding(3.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Hello",
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = text,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = tfColor,
                unfocusedBorderColor = tfColor,
                backgroundColor = Color.Transparent
            ),
            onValueChange = {
                onValueChange(it)
            },
            modifier = Modifier
                .defaultMinSize(minHeight = 26.dp)
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
        )

//        BasicTextField(value = ) {
//
//        }

        Text(
            text = "Hint",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        )
    }
}

@Preview
@Composable
fun TextFieldViewPreview() {
    TextFieldView(
        text = "Hello",
        isError = false,
        isValid = true,
        onValueChange = {}
    )
}