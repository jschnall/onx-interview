package net.schnall.compose.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import net.schnall.compose.app.theme.ComposeStarterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeStarterTheme {
                MyApp(exitApp = { moveTaskToBack(false) })
            }
        }
    }
}