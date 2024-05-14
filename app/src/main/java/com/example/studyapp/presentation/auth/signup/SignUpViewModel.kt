package com.example.studyapp.presentation.auth.signup

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.studyapp.presentation.auth.rules.Validator
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpViewModel : ViewModel() {

    private val TAG = SignUpViewModel::class.simpleName
    var registrationUIState = mutableStateOf(SignUpUIState())

    var allValidationsPassed = mutableStateOf(false)

    var signUpInProgress = mutableStateOf(false)

    var signUpSuccess = mutableStateOf(false)


    fun onEvent(event: SignUpUIEvent) {
        when(event) {
            is SignUpUIEvent.FirstNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(firstName = event.firstName)
                printState()
            }
            is SignUpUIEvent.LastNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(lastName = event.lastName)
                printState()
            }
            is SignUpUIEvent.EmailChanged -> {
                registrationUIState.value = registrationUIState.value.copy(email = event.email)
                printState()
            }
            is SignUpUIEvent.PasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(password = event.password)
                printState()
            }
            is SignUpUIEvent.RegisterButtonClicked -> {
                signUp()
            }
        }
        validateDataWithRules()
    }

    private fun signUp() {
        Log.d(TAG, "Inside_signUp")
        printState()
        createUserInFirebase(
            email = registrationUIState.value.email,
            password = registrationUIState.value.password
        )
    }

    private fun validateDataWithRules() {
        val fNameResult = Validator.validateFirstName(
            fName = registrationUIState.value.firstName
        )

        val lNameResult = Validator.validateLastName(
            lName = registrationUIState.value.lastName
        )

        val emailResult = Validator.validateEmail(
            email = registrationUIState.value.email
        )


        val passwordResult = Validator.validatePassword(
            password = registrationUIState.value.password
        )


        Log.d(TAG, "Inside_validateDataWithRules")
        Log.d(TAG, "fNameResult= $fNameResult")
        Log.d(TAG, "lNameResult= $lNameResult")
        Log.d(TAG, "emailResult= $emailResult")
        Log.d(TAG, "passwordResult= $passwordResult")

        registrationUIState.value = registrationUIState.value.copy(
            firstNameError = fNameResult.status,
            lastNameError = lNameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status,
        )


        allValidationsPassed.value = fNameResult.status && lNameResult.status &&
                emailResult.status && passwordResult.status

    }

    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, registrationUIState.value.toString())
    }

    fun createUserInFirebase(email: String, password: String) {

        signUpInProgress.value = true

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                Log.d(TAG, "Inside_addOnCompleteListener")
                Log.d(TAG, "${it.isSuccessful}")

                if(it.isSuccessful) {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    val userId = firebaseUser!!.uid
                    val currentTime = Timestamp.now()
                    Log.d(TAG, "Print UserID: $userId")
                    val studyDb : FirebaseFirestore = FirebaseFirestore.getInstance()
                    studyDb.collection(("users"))
                        .document(userId)
                        .set(
                            hashMapOf(
                                "first_name" to registrationUIState.value.firstName,
                                "last_name" to registrationUIState.value.lastName,
                                "email" to registrationUIState.value.email,
                                "creat_at" to currentTime
                            )
                        )
                        .addOnSuccessListener {
                            Log.d(TAG, "Inside_addOnSuccessListener")
                            signUpInProgress.value = false
                            signUpSuccess.value = true
                        }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Inside_addOnFailureListener")
                Log.d(TAG, "Exception = ${it.message}")
                Log.d(TAG, "Exception = ${it.localizedMessage}")
            }

        }

    fun logout() {

        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()

        val authStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                Log.d(TAG, "Inside sign outsuccess")
            } else {
                Log.d(TAG, "Inside sign out is not complete")
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)

    }

}