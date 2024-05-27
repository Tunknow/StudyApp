package com.example.studyapp.presentation.profile.update

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.studyapp.R
import com.example.studyapp.presentation.auth.components.ButtonComponent
import com.example.studyapp.presentation.auth.components.MyTextFieldComponent
import com.example.studyapp.presentation.auth.components.NormalTextComponent
import com.example.studyapp.presentation.navigation.Screens
import com.example.studyapp.presentation.profile.components.DobPicker
import com.example.studyapp.presentation.profile.components.LoadingComponent
import com.example.studyapp.presentation.profile.components.UpdateProfileUIEffect
import com.example.studyapp.util.changeMillisToDateString
import kotlinx.coroutines.flow.onEach
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    navController: NavHostController
) {
    val profileUpdateViewModel: UpdateProfileViewModel = hiltViewModel()

    val uiState = profileUpdateViewModel.state.collectAsState().value
    val effectFlow = profileUpdateViewModel.effect

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        effectFlow.onEach { effect ->
            when (effect) {
                is UpdateProfileUIEffect.ShowSnackBarMessage -> {
                    snackBarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short,
                        actionLabel = "DISMISS",
                    )
                }
            }
        }
    }

    var isDatePickerDialogOpen by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )

    DobPicker(
        state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = { isDatePickerDialogOpen = false },
        onConfirmButtonClicked = {
            profileUpdateViewModel.sendEvent(
                event = UpdateProfileUIEvent.OnDobChanged(datePickerState.selectedDateMillis)
            )
            isDatePickerDialogOpen = false
        }
    )

    Scaffold(
        snackbarHost = {
                       SnackbarHost(snackBarHostState)
        },
        topBar = {
            UpdateProfileScreenTopBar(
                onBackButtonClick = {navController.navigate(Screens.ProfileScreenRoute.route)}
            )
        }
    ) {paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    LoadingComponent()
                }
            }
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
                    NormalTextComponent(
                        value = stringResource(id = R.string.update_profile)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MyTextFieldComponent(
                        labelValue = stringResource(id = R.string.full_name),
                        painterResource = painterResource(id = R.drawable.person_24px),
                        onTextSelected = {
                            profileUpdateViewModel.sendEvent(
                                event = UpdateProfileUIEvent.OnFullnameChanged(it)
                            )
                        },
                        errorStatus = true
                    )
                    MyTextFieldComponent(
                        labelValue = stringResource(id = R.string.study_at),
                        painterResource = painterResource(id = R.drawable.person_24px),
                        onTextSelected = {
                            profileUpdateViewModel.sendEvent(
                                event = UpdateProfileUIEvent.OnStudyAtChanged(it)
                            )
                        },
                        errorStatus = true
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "Ngày sinh", style = MaterialTheme.typography.bodySmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.dob.changeMillisToDateString(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(onClick = { isDatePickerDialogOpen = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Chọn ngày sinh"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    ButtonComponent(
                        value = stringResource(id = R.string.update_profile_btn),
                        onButtonClicked = {
                            profileUpdateViewModel.sendEvent(
                                event = UpdateProfileUIEvent.UpdateProfileButtonClicked
                            )
                        },
                        isEnable = true
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateProfileScreenTopBar(
    onBackButtonClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        title = {
            Text(text = "Cập nhật thông tin", style = MaterialTheme.typography.headlineSmall)
        }
    )
}