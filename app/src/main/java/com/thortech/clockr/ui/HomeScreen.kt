package com.thortech.clockr.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thortech.clockr.data.TimeEntry
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(
    viewModel: TimeTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val allEntries by viewModel.allEntries.collectAsState()
    val runningEntry by viewModel.runningEntry.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Start/Stop Button
        Button(
            onClick = {
                if (runningEntry == null) {
                    viewModel.startTimer()
                } else {
                    viewModel.stopTimer()
                }
            },
            modifier = Modifier.fillMaxWidth()
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.primary,
//                contentColor = MaterialTheme.colorScheme.onPrimary
//            )
        ) {
            Text(if (runningEntry == null) "Start Timer" else "Stop Timer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of Time Entries
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allEntries) { entry ->
                TimeEntryItem(entry)
            }
        }
    }
}

@Composable
fun TimeEntryItem(entry: TimeEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = entry.projectName, style = MaterialTheme.typography.titleMedium)
                if (entry.endTime == null) {
                    Text(text = "Running...", style = MaterialTheme.typography.bodySmall)
                }
            }
            Text(
                text = formatDuration(entry.startTime, entry.endTime ?: System.currentTimeMillis()),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatDuration(start: Long, end: Long): String {
    val durationMillis = end - start
    val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
