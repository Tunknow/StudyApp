package com.example.studyapp.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.studyapp.R
import com.example.studyapp.presentation.auth.components.ButtonComponent
import com.example.studyapp.presentation.auth.components.ClickableLoginTextComponent
import com.example.studyapp.presentation.auth.components.DividerTextComponent
import com.example.studyapp.presentation.auth.components.HeadingTextComponent
import com.example.studyapp.presentation.auth.components.MyTextFieldComponent
import com.example.studyapp.presentation.auth.components.NormalTextComponent
import com.example.studyapp.presentation.auth.components.PasswordTextFieldComponent
import com.example.studyapp.presentation.auth.components.UnderlinedTextComponent
import com.example.studyapp.presentation.auth.login.LoginUIEvent
import com.example.studyapp.presentation.auth.login.LoginViewModel
import com.example.studyapp.presentation.navigation.Screens

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    navController: NavHostController
) {

    val loginResult by loginViewModel.isLoginSuccess.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                NormalTextComponent(value = stringResource(id = R.string.hello))
                HeadingTextComponent(
                    value = stringResource(id = R.string.welcome)
                )
                Spacer(modifier = Modifier.height(20.dp))

                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.email),
                    painterResource(id = R.drawable.mail_24px),
                    onTextSelected = {
                        loginViewModel.onEvent(LoginUIEvent.EmailChanged(it))
                    },
                    errorStatus = loginViewModel.loginUIState.value.emailError
                )
                PasswordTextFieldComponent(
                    labelValue = stringResource(id = R.string.password),
                    painterResource(id = R.drawable.lock_24px),
                    onTextSelected = {
                        loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it))
                    },
                    errorStatus = loginViewModel.loginUIState.value.passwordError
                )
                Spacer(modifier = Modifier.height(40.dp))
                UnderlinedTextComponent(value = stringResource(R.string.forgot_password))
                Spacer(modifier = Modifier.height(200.dp))

                ButtonComponent(
                    value = stringResource(id = R.string.login),
                    onButtonClicked = {
                        loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                    },
                    isEnable = loginViewModel.allValidationsPassed.value
                )

                Spacer(modifier = Modifier.height(20.dp))

                DividerTextComponent()
                ClickableLoginTextComponent(
                    tryingToLogin = false,
                    onTextSelected = {
                        navController.navigate(Screens.RegisterScreenRoute.route)
                    }
                )
            }
        }
        if(loginViewModel.loginInProgress.value) {
            CircularProgressIndicator()
        }
        if (loginResult) {
            navController.navigate(Screens.StudyScreenRoute.route) {
                popUpTo(Screens.LoginScreenRoute.route) { inclusive = true }
            }
        }
        if(loginViewModel.showInvalidLoginDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    loginViewModel.showInvalidLoginDialog.value = false
                },
                title = {
                    Text(
                        text = "Đăng nhập không hợp lệ",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal
                        ),
                    )
                },
                text = {
                    Text(text = "Vui lòng kiểm tra lại email và mật khẩu.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            loginViewModel.showInvalidLoginDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(72.dp)
                                .heightIn(36.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            colorResource(id = R.color.colorPrimary),
                                            colorResource(id = R.color.colorSecondary)
                                        )
                                    ),
                                    shape = RoundedCornerShape(50.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "OK")
                        }

                    }
                },
                containerColor = Color.White
            )
        }
    }
}