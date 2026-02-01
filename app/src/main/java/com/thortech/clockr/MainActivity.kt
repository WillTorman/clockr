package com.thortech.clockr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.thortech.clockr.ui.ClockrApp
import com.thortech.clockr.ui.theme.ClockrTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClockrTheme {
                ClockrApp()
            }
        }
    }
}
