package com.thortech.clockr.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.thortech.clockr.data.TimeEntry
import com.thortech.clockr.data.TimeEntryDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimeTrackerViewModel(private val timeEntryDao: TimeEntryDao) : ViewModel() {

    // Exposes the list of all time entries
    val allEntries: StateFlow<List<TimeEntry>> = timeEntryDao.getAllTimeEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Holds the currently running time entry (if any)
    val runningEntry: StateFlow<TimeEntry?> = timeEntryDao.getRunningTimeEntry()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun startTimer(projectName: String = "New Project") {
        viewModelScope.launch {
            // Check if there is already a running entry; if not, start a new one
            if (runningEntry.value == null) {
                val newEntry = TimeEntry(
                    startTime = System.currentTimeMillis(),
                    projectName = projectName
                )
                timeEntryDao.insertTimeEntry(newEntry)
            }
        }
    }

    fun stopTimer() {
        viewModelScope.launch {
            runningEntry.value?.let { entry ->
                val updatedEntry = entry.copy(endTime = System.currentTimeMillis())
                timeEntryDao.updateTimeEntry(updatedEntry)
            }
        }
    }
}

class TimeTrackerViewModelFactory(private val dao: TimeEntryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeTrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimeTrackerViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
