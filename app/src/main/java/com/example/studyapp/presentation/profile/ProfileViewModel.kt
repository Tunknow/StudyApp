package com.example.studyapp.presentation.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    private val TAG = ProfileViewModel::class.simpleName

    var profileUIState = mutableStateOf(ProfileUIState())

    var isLogout = mutableStateOf(false)

    fun onEvent(event: ProfileUIEvent) {
        when(event) {
            is ProfileUIEvent.LogoutButtonClicked -> {
                logout()
            }
            is ProfileUIEvent.ForgotPasswordClicked -> {
                forgotPassword()
            }
        }
    }

    private fun forgotPassword() {
        TODO("Not yet implemented")
    }

    private fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()

        val authStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                Log.d(TAG, "Inside sign outsuccess")
                isLogout.value = true
            } else {
                Log.d(TAG, "Inside sign out is not complete")
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)
    }
}