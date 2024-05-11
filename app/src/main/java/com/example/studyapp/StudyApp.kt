package com.example.studyapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.studyapp.presentation.navigation.AppNavigation

@Composable
fun StudyApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        AppNavigation()
    }
}