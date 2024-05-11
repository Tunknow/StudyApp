package com.example.studyapp.util

import androidx.compose.ui.graphics.Color
import com.example.studyapp.presentation.theme.Green
import com.example.studyapp.presentation.theme.Orange
import com.example.studyapp.presentation.theme.Red
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Dinh nghia tap gia tri co dinh cho Priority
enum class Priority(val title: String, val color: Color, val value: Int) {
    LOW(title = "Low", color = Green, value = 0),
    MEDIUM(title = "Medium", color = Orange, value = 1),
    HIGH(title = "High", color = Red, value = 2);

    companion object {
        fun fromInt(value: Int) = values().firstOrNull() {it.value == value} ?: MEDIUM
    }
}

fun Long?.changeMillisToDateString(): String {
    val date: LocalDate = this?.let {
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}

fun Long.toHours(): Float {
    val hours = this.toFloat() / 3600f
    return "%.2f".format(hours).toFloat()
}