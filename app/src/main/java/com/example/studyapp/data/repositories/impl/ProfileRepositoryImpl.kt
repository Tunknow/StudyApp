package com.example.studyapp.data.repositories.impl

import com.example.studyapp.data.repositories.ProfileRepository
import com.example.studyapp.di.IoDispatcher
import com.example.studyapp.domain.model.ProfileUser
import com.example.studyapp.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject


class ProfileRepositoryImpl @Inject constructor(
    private val studyAppDB: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProfileRepository {

    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val email = FirebaseAuth.getInstance().currentUser!!.email
    override suspend fun getUserProfile(userId: String): Result<ProfileUser> {
        return try {
            withContext(ioDispatcher) {
                val fetchingProfileUser = withTimeoutOrNull(10000L) {
                    studyAppDB.collection("users").document(userId)
                        .get()
                        .await()
                }
                if(fetchingProfileUser == null) {
                    Result.Failure(IllegalStateException("Kiểm tra kết nối internet."))
                }
                Result.Success(
                    ProfileUser(
                        id = fetchingProfileUser!!.id,
                        fullname = fetchingProfileUser.getString("fullname")!!,
                        email = email!!,
                        dob = fetchingProfileUser.getLong("dob")!!,
                        school = fetchingProfileUser.getString("studyAt")!!
                    )
                )
            }
        } catch (e: Exception) {
            Result.Failure(exception = e)
        }
    }

    override suspend fun updateUserProfile(profileUser: ProfileUser): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val profileUpdate: Map<String, Any> = hashMapOf(
                    "fullname" to profileUser.fullname,
                    "email" to email!!,
                    "dob" to profileUser.dob!!,
                    "studyAt" to profileUser.school,
                )

                val addProfile = withTimeoutOrNull(10000L) {
                    studyAppDB.collection("users").document(userId)
                        .update(profileUpdate)
                        .await()
                }

                if(addProfile == null) {
                    Result.Failure(IllegalStateException("Kiểm tra kết nối internet."))
                }
                Result.Success(Unit)
            }
        }
        catch (e: Exception) {
            Result.Failure(exception = e)
        }
    }
}