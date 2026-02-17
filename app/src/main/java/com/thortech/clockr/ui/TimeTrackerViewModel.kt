package com.thortech.clockr.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.thortech.clockr.data.SettingsRepository
import com.thortech.clockr.data.TimeEntry
import com.thortech.clockr.data.TimeEntryDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimeTrackerViewModel(
    private val timeEntryDao: TimeEntryDao,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

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

    // A ticker flow that emits the current time every second
    private val ticker = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000L)
        }
    }

    // Combines the running entry and the ticker to provide a live duration
    val elapsedTime: StateFlow<Long> = combine(runningEntry, ticker) { entry, now ->
        if (entry != null) {
            now - entry.startTime
        } else {
            0L
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0L
    )

    fun startTimer() {
        viewModelScope.launch {
            // Check if there is already a running entry; if not, start a new one
            if (runningEntry.value == null) {
                val defaultLabel = settingsRepository.defaultProjectLabel.first()
                val newEntry = TimeEntry(
                    startTime = System.currentTimeMillis(),
                    projectName = defaultLabel
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

    fun deleteEntry(entry: TimeEntry) {
        viewModelScope.launch {
            timeEntryDao.deleteTimeEntry(entry)
        }
    }

    fun updateEntry(entry: TimeEntry) {
        viewModelScope.launch {
            timeEntryDao.updateTimeEntry(entry)
        }
    }
}

class TimeTrackerViewModelFactory(
    private val dao: TimeEntryDao,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeTrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimeTrackerViewModel(dao, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
