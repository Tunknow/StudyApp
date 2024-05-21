package com.example.studyapp.presentation.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.repositories.ProfileRepository
import com.example.studyapp.util.Result
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val TAG = ProfileViewModel::class.simpleName

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val _state :MutableStateFlow<ProfileUIState> = MutableStateFlow(ProfileUIState())
    val state: StateFlow<ProfileUIState> = _state.asStateFlow()

    var isLogout = mutableStateOf(false)

    init {
        sendEvent(ProfileUIEvent.GetProfile)
    }

    fun sendEvent(event: ProfileUIEvent) {
        onEvent(oldState = _state.value, event = event)
    }
    private fun setState(newState: ProfileUIState) {
        _state.value = newState
    }

    fun onEvent(oldState: ProfileUIState,event: ProfileUIEvent) {
        when(event) {
            is ProfileUIEvent.LogoutButtonClicked -> {
                logout()
            }
            is ProfileUIEvent.ForgotPasswordClicked -> {
                forgotPassword()
            }

            ProfileUIEvent.GetProfile -> {
                getProfile(oldState = oldState)
            }
        }
    }

    private fun getProfile(oldState: ProfileUIState) {
        viewModelScope.launch {
            when(val result = profileRepository.getUserProfile(userId!!)) {
                is Result.Success -> {
                    val profile = result.data
                    setState(
                        oldState.copy(profile = profile)
                    )
                }

                is Result.Failure -> {
                    Log.d(TAG, "getProfile: ${result.exception}")
                }
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