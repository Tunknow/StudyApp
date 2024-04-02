package com.example.studyapp.presentation.navigation

sealed class Screens(var route: String) {
    object Study: Screens("study")
    object Note: Screens("note")
}