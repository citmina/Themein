package io.citmina.themein

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.citmina.themein.ui.screens.MainScreen
import io.citmina.themein.ui.theme.ThemeinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThemeinTheme {
                MainScreen()
            }
        }
    }
}