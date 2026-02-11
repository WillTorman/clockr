package com.thortech.clockr.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.thortech.clockr.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val payPeriodDays: StateFlow<Int> = repository.payPeriodDays.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 14
    )

    val payRate: StateFlow<Double> = repository.payRate.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0
    )

    val workDays: StateFlow<Set<String>> = repository.workDays.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet()
    )

    val payPeriodStartDate: StateFlow<Long> = repository.payPeriodStartDate.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), System.currentTimeMillis()
    )

    fun updatePayPeriodDays(days: Int) {
        viewModelScope.launch { repository.updatePayPeriodDays(days) }
    }

    fun updatePayRate(rate: Double) {
        viewModelScope.launch { repository.updatePayRate(rate) }
    }

    fun updateWorkDays(days: Set<String>) {
        viewModelScope.launch { repository.updateWorkDays(days) }
    }

    fun updatePayPeriodStartDate(timestamp: Long) {
        viewModelScope.launch { repository.updatePayPeriodStartDate(timestamp) }
    }
}

class SettingsViewModelFactory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
