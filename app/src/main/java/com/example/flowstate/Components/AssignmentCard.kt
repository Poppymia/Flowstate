package com.example.flowstate.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.flowstate.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.flowstate.models.Assignment
import androidx.compose.ui.res.stringResource
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun AssignmentCard(
    assignment: Assignment,
    onClick: () -> Unit
) {
    // displays assignment details
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // tappable card
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = assignment.color
        )

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // due date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.assignment_due_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black.copy(alpha = 0.7f)
                        )
                    )

                    // due date formatted
                    Text(
                        text = formatDueDate(assignment.dueDate),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // assignment title
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                maxLines = 2, // prevents overflow
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // course code
            Text(
                text = assignment.courseId,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Black.copy(alpha = 0.7f)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// formats due date to show month and date
private fun formatDueDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
