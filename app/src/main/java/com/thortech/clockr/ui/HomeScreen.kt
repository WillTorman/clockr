package com.thortech.clockr.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thortech.clockr.data.TimeEntry
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(
    viewModel: TimeTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val allEntries by viewModel.allEntries.collectAsState()
    val runningEntry by viewModel.runningEntry.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()

    var editingEntry by remember { mutableStateOf<TimeEntry?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Active Timer Display
        if (runningEntry != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Active: ${runningEntry?.projectName}", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = formatDuration(elapsedTime),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }

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
        ) {
            Text(if (runningEntry == null) "Start Clockr" else "Stop Clockr")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of Time Entries
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allEntries) { entry ->
                TimeEntryItem(
                    entry = entry,
                    onEditClick = { editingEntry = entry },
                    onDeleteClick = { viewModel.deleteEntry(entry) }
                )
            }
        }
    }

    // Edit Dialog
    editingEntry?.let { entry ->
        EditEntryDialog(
            entry = entry,
            onDismiss = { editingEntry = null },
            onConfirm = { updatedName ->
                viewModel.updateEntry(entry.copy(projectName = updatedName))
                editingEntry = null
            }
        )
    }
}

@Composable
fun TimeEntryItem(
    entry: TimeEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entry.projectName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${dateFormatter.format(Date(entry.startTime))} • ${timeFormatter.format(Date(entry.startTime))}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatDuration(entry.startTime, entry.endTime ?: System.currentTimeMillis()),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun EditEntryDialog(
    entry: TimeEntry,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(entry.projectName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Project Name") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Project Name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDuration(durationMillis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

private fun formatDuration(start: Long, end: Long): String {
    return formatDuration(end - start)
}
