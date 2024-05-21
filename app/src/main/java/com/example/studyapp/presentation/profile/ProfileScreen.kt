package com.example.studyapp.presentation.profile

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.presentation.auth.components.ButtonComponent
import com.example.studyapp.presentation.navigation.Screens
import com.example.studyapp.util.changeMillisToDateString

@Composable
fun ProfileScreen(
    navController: NavHostController
) {

    val profileViewModel: ProfileViewModel = hiltViewModel()

    val uiState = profileViewModel.state.collectAsState().value

    if (profileViewModel.isLogout.value) {
        navController.navigate(Screens.LoginScreenRoute.route) {
            popUpTo(Screens.ProfileScreenRoute.route) { inclusive = true }
        }
    }

    Scaffold(
        topBar = { ProfileScreenTopBar()}
    ) {paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF6F5F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, bottom = 120.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "",
                            modifier = Modifier.size(100.dp),
                            tint = Color(0xFF3C5B6F),
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = uiState.profile?.fullname ?: "Không xác định",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Normal
                            ),
                            color = Color(0xFF3C5B6F)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)) {
                            UserInfoCard(info = "Email", infoDetail = uiState.profile?.email ?: "Không xác định")
                            UserInfoCard(info = "Ngày sinh", infoDetail = uiState.profile?.dob.changeMillisToDateString()  ?: "Không xác định")
                        }

                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                ButtonComponent(

                    value = "Cập nhật hồ sơ",
                    onButtonClicked = {
                        navController.navigate(Screens.UpdateProfileScreenRoute.route)
                    },
                    isEnable = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                ButtonComponent(

                    value = "Đổi mật khẩu",
                    onButtonClicked = {
                        profileViewModel.sendEvent(event = ProfileUIEvent.ForgotPasswordClicked)
                    },
                    isEnable = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                ButtonComponent(
                    value = "Đăng xuất",
                    onButtonClicked = {
                        profileViewModel.sendEvent(event = ProfileUIEvent.LogoutButtonClicked)
                    },
                    isEnable = true
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Hồ sơ",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}

@Preview
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
}

@Composable
fun UserInfoCard(
    info: String,
    infoDetail: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = info + ": ",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal
            ))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = infoDetail)
    }
}
