package com.example.studyapp.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.studyapp.R
import com.example.studyapp.presentation.auth.signup.SignUpUIEvent
import com.example.studyapp.presentation.auth.signup.SignUpViewModel
import com.example.studyapp.presentation.auth.components.ButtonComponent
import com.example.studyapp.presentation.auth.components.ClickableLoginTextComponent
import com.example.studyapp.presentation.auth.components.DividerTextComponent
import com.example.studyapp.presentation.auth.components.HeadingTextComponent
import com.example.studyapp.presentation.auth.components.MyTextFieldComponent
import com.example.studyapp.presentation.auth.components.NormalTextComponent
import com.example.studyapp.presentation.auth.components.PasswordTextFieldComponent
import com.example.studyapp.presentation.navigation.Screens

@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel = viewModel(),
    navController: NavHostController
) {
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
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                NormalTextComponent(value = stringResource(id = R.string.hello))
                HeadingTextComponent(
                    value = stringResource(id = R.string.create_account)
                )
                Spacer(modifier = Modifier.height(20.dp))
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.first_name),
                    painterResource = painterResource(id = R.drawable.person_24px),
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.FirstNameChanged(it))
                    },
                    errorStatus = signUpViewModel.registrationUIState.value.firstNameError
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.last_name),
                    painterResource = painterResource(id = R.drawable.person_24px),
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.LastNameChanged(it))
                    },
                    errorStatus = signUpViewModel.registrationUIState.value.lastNameError
                )
                MyTextFieldComponent(
                    labelValue = stringResource(id = R.string.email),
                    painterResource = painterResource(id = R.drawable.mail_24px),
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.EmailChanged(it))
                    },
                    errorStatus = signUpViewModel.registrationUIState.value.emailError
                )
                PasswordTextFieldComponent(
                    labelValue = stringResource(id = R.string.password),
                    painterResource(id = R.drawable.lock_24px),
                    onTextSelected = {
                        signUpViewModel.onEvent(SignUpUIEvent.PasswordChanged(it))
                    },
                    errorStatus = signUpViewModel.registrationUIState.value.passwordError
                )
                Spacer(modifier = Modifier.height(80.dp))
                ButtonComponent(
                    value = stringResource(id = R.string.register),
                    onButtonClicked = {
                        signUpViewModel.onEvent(SignUpUIEvent.RegisterButtonClicked)
                    },
                    isEnable = signUpViewModel.allValidationsPassed.value
                )
                Spacer(modifier = Modifier.height(20.dp))
                DividerTextComponent()
                ClickableLoginTextComponent(
                    tryingToLogin = true,
                    onTextSelected = {
                        navController.navigate(Screens.LoginScreenRoute.route)
                    })
            }

        }

        if(signUpViewModel.signUpInProgress.value) {
            CircularProgressIndicator()
        }

        LaunchedEffect(signUpViewModel.signUpSuccess.value) {
            if (signUpViewModel.signUpSuccess.value) {
                navController.navigate(Screens.StudyScreenRoute.route) {
                    popUpTo(Screens.RegisterScreenRoute.route) { inclusive = true }
                }
            }
        }
    }

}
