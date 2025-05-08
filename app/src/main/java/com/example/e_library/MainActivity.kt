package com.example.e_library

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.e_library.navigation.NavigationGraph
import com.example.e_library.navigation.Routes
import com.example.e_library.ui.theme.ELibraryTheme
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()

        setContent {
            ELibraryTheme {
                val navController = rememberNavController()

                val startDestination = if (auth.currentUser != null) {
                    Routes.HOME
                } else {
                    Routes.LOGIN
                }

                NavigationGraph(navController = navController, startDestination = startDestination)

            }
        }
//        setContent {
//            Text("Hello")
//        }
    }
}

