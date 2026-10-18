package com.example.myappnavigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myappnavigation.ui.navigation.AppNavigation
import com.example.myappnavigation.ui.theme.MyAppNavigationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppNavigationTheme(darkTheme = false, dynamicColor = false) {
                AppNavigation()
            }
        }
    }
}
