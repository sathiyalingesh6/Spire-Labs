package com.example.spire_labs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.spire_labs.presentation.navigation.AppNavGraph
import com.example.spire_labs.ui.theme.SpireLabsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpireLabsTheme {
                AppNavGraph()
            }
        }
    }
}