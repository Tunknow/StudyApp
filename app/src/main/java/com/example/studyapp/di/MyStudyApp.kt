package com.example.studyapp.di

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyStudyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}