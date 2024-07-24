package net.schnall.compose.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import net.schnall.compose.app.theme.OnXInterviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OnXInterviewTheme {
                MyApp(exitApp = { moveTaskToBack(false) })
            }
        }
    }
}