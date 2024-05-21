package com.example.studyapp.data.repositories

import com.example.studyapp.domain.model.ProfileUser
import com.example.studyapp.util.Result

interface ProfileRepository {

    suspend fun getUserProfile(userId: String) : Result<ProfileUser>
    suspend fun updateUserProfile(profileUser: ProfileUser) : Result<Unit>
}