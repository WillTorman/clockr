package com.thortech.clockr

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.thortech.clockr.ui.theme.ClockrTheme

@Composable
fun ClockScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Hour Tracker",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun ClockScreenPreview() {
    ClockrTheme {
        ClockScreen()
    }
}