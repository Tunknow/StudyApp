package com.example.studyapp.domain.model

data class Session(
    var id: String,
    var sid: String,
    var relatedToSubject: String = "",
    var date: Long,
    var duration: Long

)
