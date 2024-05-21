package com.example.studyapp.presentation.navigation


const val SUBJECT_ARG_KEY = "subjectId"

sealed class Screens(val route: String) {
    object LoginScreenRoute: Screens(route = "Login")

    object RegisterScreenRoute: Screens(route = "Register")
    object StudyScreenRoute: Screens(route = "Study")
    object NoteScreenRoute: Screens(route = "Note")
    object ProfileScreenRoute: Screens(route = "Profile")
    object SessionScreenRoute: Screens(route = "Session")
    object TaskScreenRoute: Screens(route = "Task?id={id}&subjectId={subjectId}") {
        fun passTaskId(
            taskId: String = "",
            subjectId: String = ""
        ): String {
            return "Task?id=${taskId}&subjectId=${subjectId}"
        }
    }
    object SubjectScreenRoute: Screens(route = "Subject/{$SUBJECT_ARG_KEY}") {
        fun passSubjectId(subjectId: String): String {
            return "Subject/$subjectId"
        }
    }
    object UpdateProfileScreenRoute: Screens(route = "UpdateProfile")
}