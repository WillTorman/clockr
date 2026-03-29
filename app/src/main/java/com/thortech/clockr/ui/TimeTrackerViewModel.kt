package com.thortech.clockr.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.thortech.clockr.data.AuthRepository
import com.thortech.clockr.data.FirestoreRepository
import com.thortech.clockr.data.SettingsRepository
import com.thortech.clockr.data.TimeEntry
import com.thortech.clockr.data.TimeEntryDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimeTrackerViewModel(
    private val timeEntryDao: TimeEntryDao,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    val currentUser = authRepository.currentUser

    @OptIn(ExperimentalCoroutinesApi::class)
    val allEntries: StateFlow<List<TimeEntry>> = currentUser.flatMapLatest { user ->
        user?.let { timeEntryDao.getAllTimeEntries(it.uid) } ?: flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val runningEntry: StateFlow<TimeEntry?> = currentUser.flatMapLatest { user ->
        user?.let { timeEntryDao.getRunningTimeEntry(it.uid) } ?: flowOf(null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val ticker = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000L)
        }
    }

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
            val user = currentUser.value ?: return@launch
            if (runningEntry.value == null) {
                val defaultLabel = settingsRepository.defaultProjectLabel.first()
                val newEntry = TimeEntry(
                    userId = user.uid,
                    startTime = System.currentTimeMillis(),
                    projectName = defaultLabel
                )
                val id = timeEntryDao.insertTimeEntry(newEntry)
                val insertedEntry = newEntry.copy(id = id)
                
                // Sync to Firestore
                firestoreRepository.syncTimeEntry(insertedEntry)
                timeEntryDao.updateTimeEntry(insertedEntry.copy(synced = true))
            }
        }
    }

    fun stopTimer() {
        viewModelScope.launch {
            runningEntry.value?.let { entry ->
                val updatedEntry = entry.copy(endTime = System.currentTimeMillis())
                timeEntryDao.updateTimeEntry(updatedEntry)
                
                // Sync to Firestore
                firestoreRepository.syncTimeEntry(updatedEntry)
                timeEntryDao.updateTimeEntry(updatedEntry.copy(synced = true))
            }
        }
    }

    fun deleteEntry(entry: TimeEntry) {
        viewModelScope.launch {
            timeEntryDao.deleteTimeEntry(entry)
            firestoreRepository.deleteTimeEntry(entry.id)
        }
    }

    fun updateEntry(entry: TimeEntry) {
        viewModelScope.launch {
            timeEntryDao.updateTimeEntry(entry)
            firestoreRepository.syncTimeEntry(entry)
            timeEntryDao.updateTimeEntry(entry.copy(synced = true))
        }
    }
    
    fun signIn() {
        authRepository.signInAnonymously { success ->
            // Handle success/failure if needed
        }
    }
}

class TimeTrackerViewModelFactory(
    private val dao: TimeEntryDao,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeTrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimeTrackerViewModel(dao, settingsRepository, authRepository, firestoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
