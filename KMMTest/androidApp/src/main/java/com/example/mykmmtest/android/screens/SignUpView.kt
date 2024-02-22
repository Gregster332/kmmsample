package com.example.mykmmtest.android.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.example.authentication.Auth.SignUpComponent
import com.example.mykmmtest.MR
import com.example.mykmmtest.android.uielements.TextFieldView

@Composable
fun SignUpView(component: SignUpComponent) {
    val state by component.state.subscribeAsState()

    Surface(
        modifier =
        Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.background,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TextFieldView(
                text = state.nickname.text,
                isError = state.nickname.isError,
                isValid = state.nickname.isValid,
                title = MR.strings.nickname_field_title.getString(LocalContext.current),
                onValueChange = {
                    component.validateNickname(it)
                },
            )

            TextFieldView(
                text = state.phoneNumberState.text,
                isError = state.phoneNumberState.isError,
                isValid = state.phoneNumberState.isValid,
                title = MR.strings.phone_field_title.getString(LocalContext.current),
                onValueChange = {
                    component.validatePhone(it)
                },
            )

            TextFieldView(
                text = state.passwordState.text,
                isError = state.passwordState.isError,
                isValid = state.passwordState.isValid,
                title = MR.strings.password_field_title.getString(LocalContext.current),
                onValueChange = {
                    component.validatePassword(it)
                },
            )

            Button(
                onClick = { component.authWithPassword() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Sign in")
            }
        }
    }
}