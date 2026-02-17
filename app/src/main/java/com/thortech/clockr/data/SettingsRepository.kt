package com.thortech.clockr.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val PAY_PERIOD_DAYS = intPreferencesKey("pay_period_days")
        val PAY_RATE = doublePreferencesKey("pay_rate")
        val WORK_DAYS = stringSetPreferencesKey("work_days")
        val PAY_PERIOD_START_DATE = longPreferencesKey("pay_period_start_date")
        val DEFAULT_PROJECT_LABEL = stringPreferencesKey("default_project_label")
    }

    val payPeriodDays: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PAY_PERIOD_DAYS] ?: 14 // Default 2 weeks
    }

    val payRate: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PAY_RATE] ?: 0.0
    }

    val workDays: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.WORK_DAYS] ?: setOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY")
    }

    val payPeriodStartDate: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PAY_PERIOD_START_DATE] ?: System.currentTimeMillis()
    }

    val defaultProjectLabel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_PROJECT_LABEL] ?: "New Project"
    }

    suspend fun updatePayPeriodDays(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAY_PERIOD_DAYS] = days
        }
    }

    suspend fun updatePayRate(rate: Double) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAY_RATE] = rate
        }
    }

    suspend fun updateWorkDays(days: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WORK_DAYS] = days
        }
    }

    suspend fun updatePayPeriodStartDate(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAY_PERIOD_START_DATE] = timestamp
        }
    }

    suspend fun updateDefaultProjectLabel(label: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_PROJECT_LABEL] = label
        }
    }
}
