package com.thortech.clockr.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val payPeriodDays by viewModel.payPeriodDays.collectAsState()
    val payRate by viewModel.payRate.collectAsState()
    val workDays by viewModel.workDays.collectAsState()
    val defaultProjectLabel by viewModel.defaultProjectLabel.collectAsState()

    var payRateText by remember { mutableStateOf(if (payRate == 0.0) "" else payRate.toString()) }
    var defaultProjectLabelText by remember(defaultProjectLabel) { mutableStateOf(defaultProjectLabel) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Default Project Label
            OutlinedTextField(
                value = defaultProjectLabelText,
                onValueChange = { newValue ->
                    defaultProjectLabelText = newValue
                    viewModel.updateDefaultProjectLabel(newValue)
                },
                label = { Text("Default Project Label") },
                placeholder = { Text("New Project") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Pay Rate
            OutlinedTextField(
                value = payRateText,
                onValueChange = { newValue ->
                    payRateText = newValue
                    if (newValue.isEmpty()) {
                        viewModel.updatePayRate(0.0)
                    } else {
                        newValue.toDoubleOrNull()?.let {
                            viewModel.updatePayRate(it)
                        }
                    }
                },
                label = { Text("Hourly Pay Rate ($)") },
                placeholder = { Text("0.0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            // Pay Period
            Text(text = "Pay Period Length: $payPeriodDays days", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = payPeriodDays.toFloat(),
                onValueChange = { viewModel.updatePayPeriodDays(it.toInt()) },
                valueRange = 1f..31f,
                steps = 30
            )

            // Work Days
            Text(text = "Work Days", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DayOfWeek.entries.forEach { day ->
                    val dayName = day.name
                    val isSelected = workDays.contains(dayName)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newDays = if (isSelected) {
                                workDays - dayName
                            } else {
                                workDays + dayName
                            }
                            viewModel.updateWorkDays(newDays)
                        },
                        label = { 
                            Text(day.getDisplayName(TextStyle.SHORT, Locale.getDefault())) 
                        }
                    )
                }
            }
        }
    }
}
