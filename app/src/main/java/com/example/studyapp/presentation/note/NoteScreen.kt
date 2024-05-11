package com.example.studyapp.presentation.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination

@Composable
fun NoteScreen() {
    Scaffold(
        topBar = { NoteScreenTopBar()}
    ) {paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Text(text = "Note Screen")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Ghi chú",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}