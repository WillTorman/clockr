package com.thortech.clockr.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    timeViewModel: TimeTrackerViewModel,
    settingsViewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val allEntries by timeViewModel.allEntries.collectAsState()
    val payRate by settingsViewModel.payRate.collectAsState()
    val payPeriodDays by settingsViewModel.payPeriodDays.collectAsState()
    
    // For simplicity, let's calculate for the last 'payPeriodDays'
    val currentTime = System.currentTimeMillis()
    val payPeriodStart = currentTime - TimeUnit.DAYS.toMillis(payPeriodDays.toLong())
    
    val periodEntries = allEntries.filter { it.startTime >= payPeriodStart }
    
    val totalMillis = periodEntries.sumOf { entry ->
        val end = entry.endTime ?: currentTime
        end - entry.startTime
    }
    
    val totalHours = totalMillis.toDouble() / (1000 * 60 * 60)
    val grossPay = totalHours * payRate
    
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Earnings & Stats") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Current Pay Period",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currencyFormatter.format(grossPay),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 48.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Estimated Gross Pay",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatBox(
                    label = "Total Hours",
                    value = String.format("%.2f", totalHours),
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Entries",
                    value = periodEntries.size.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Divider()

            // Info Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Details",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(label = "Pay Rate", value = "${currencyFormatter.format(payRate)}/hr")
                InfoRow(label = "Period Length", value = "$payPeriodDays days")
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            Text(text = label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
