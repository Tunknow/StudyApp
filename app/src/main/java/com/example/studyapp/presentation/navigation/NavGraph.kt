package com.example.studyapp.presentation.navigation

import androidx.compose.animation.expandIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studyapp.presentation.screens.Dashboard
import com.example.studyapp.presentation.screens.NoteScreen

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Study.route,
    ) {
        composable(
            route = Screens.Study.route,
            enterTransition = {
                expandIn()
            },
            exitTransition = {
                shrinkOut()
            }
        ) {
            Dashboard(innerPadding = innerPadding)
        }
        composable(
            route = Screens.Note.route,
            enterTransition = {
                scaleIn()
            },
            exitTransition = {
                scaleOut()
            }
        ) {
            NoteScreen(innerPadding = innerPadding)
        }
    }
}