package com.example.studyapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studyapp.R
import com.example.studyapp.domain.model.Session

@OptIn(ExperimentalMaterial3Api::class)
fun LazyListScope.studySessionsList(
    sectionTitle: String,
    emptyListText: String,
    sessions: List<Session>,
    onDeleteIconClick: (Session) -> Unit
) {
    item {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(12.dp)
        )
    }
    if (sessions.isEmpty()) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier
                        .size(120.dp),
                    painter = painterResource(id = R.drawable.history_24px),
                    contentDescription = emptyListText,
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = emptyListText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    items(sessions) {session ->

//        val state = rememberDismissState(
//            confirmValueChange = {
//                if (it == DismissValue.DismissedToStart) {
//                    onDeleteIconClick(session)
//                }
//                true
//            }
//        )
//
//        SwipeToDismiss(
//            state = state,
//            background = {
//                         val color = when(state.dismissDirection) {
//                             DismissDirection.EndToStart -> Color.Red
//                             DismissDirection.StartToEnd -> Color.Green
//                             null -> Color.Transparent
//                         }
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(color)
//                        .padding(horizontal = 12.dp, vertical = 4.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = "Xóa phiên học tập",
//                        modifier = Modifier.align(Alignment.CenterEnd)
//                    )
//                }
//            },
//            dismissContent = {
//                StudySessionCard(
//                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
//                    session = session,
//                    onDeleteIconClick = {onDeleteIconClick(session)}
//                )
//            })
        StudySessionCard(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            session = session,
            onDeleteIconClick = {onDeleteIconClick(session)}
        )
    }
}

@Composable
private fun StudySessionCard(
    modifier: Modifier = Modifier,
    session: Session,
    onDeleteIconClick: () -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = session.relatedToSubject,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${session.date}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Đã học ${session.duration} giờ",
                style = MaterialTheme.typography.bodySmall
            )
            IconButton(onClick = { onDeleteIconClick() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa phiên học tập"
                )
            }
        }
    }
}
