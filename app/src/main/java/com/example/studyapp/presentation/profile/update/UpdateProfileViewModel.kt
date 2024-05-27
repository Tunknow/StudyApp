package com.example.studyapp.presentation.profile.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.repositories.ProfileRepository
import com.example.studyapp.domain.model.ProfileUser
import com.example.studyapp.presentation.profile.components.UpdateProfileUIEffect
import com.example.studyapp.util.Result
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
): ViewModel() {
    private val _state: MutableStateFlow<UpdateProflieUIState> =
        MutableStateFlow(UpdateProflieUIState())
    val state: StateFlow<UpdateProflieUIState> = _state.asStateFlow()

    private val _effect: Channel<UpdateProfileUIEffect> = Channel()
    val effect = _effect.receiveAsFlow()


    init {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val currentProfile = profileRepository.getUserProfile(userId)
            when(currentProfile) {
                is Result.Success -> {
                    val profileUser = currentProfile.data
                    sendEvent(UpdateProfileUIEvent.OnFullnameChanged(profileUser.fullname))
                    sendEvent(UpdateProfileUIEvent.OnStudyAtChanged(profileUser.school))
                    sendEvent(UpdateProfileUIEvent.OnDobChanged(profileUser.dob))
                }
                is Result.Failure -> {
                    setEffect { UpdateProfileUIEffect.ShowSnackBarMessage("Có lỗi xảy ra, vui lòng thử lại sau.") }
                }
            }
        }
    }
    fun sendEvent(event: UpdateProfileUIEvent) {
        reduce(oldState = _state.value, event = event)
    }

    private fun setEffect(builder: () -> UpdateProfileUIEffect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun setState(newState: UpdateProflieUIState) {
        _state.value = newState
    }

    private fun reduce(oldState: UpdateProflieUIState, event: UpdateProfileUIEvent) {
        when (event) {
            is UpdateProfileUIEvent.OnFullnameChanged -> {
                setState(oldState.copy(fullname = event.fullname))
            }

            is UpdateProfileUIEvent.OnStudyAtChanged -> {
                setState(oldState.copy(studyAt = event.studyAt))
            }

            is UpdateProfileUIEvent.OnDobChanged -> {
                setState(oldState.copy(dob = event.dob))
            }

            UpdateProfileUIEvent.UpdateProfileButtonClicked -> {
                updateProfile(oldState = oldState)
            }
        }
    }

    private fun updateProfile(oldState: UpdateProflieUIState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            val fullname = oldState.fullname
            val studyAt = oldState.studyAt
            val dob = oldState.dob

            when(val result = profileRepository.updateUserProfile(
                ProfileUser(fullname = fullname, school = studyAt, dob = dob)
            )) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))
                    val errorMessage =
                        result.exception.message ?: "Có lỗi xảy ra, vui lòng thử lại sau."
                    setEffect { UpdateProfileUIEffect.ShowSnackBarMessage(errorMessage) }

                }
                is Result.Success -> {
                    setState(oldState.copy(isLoading = false))

                    setEffect {UpdateProfileUIEffect.ShowSnackBarMessage("Cập nhật thành công")}
                }
            }
        }
    }
}