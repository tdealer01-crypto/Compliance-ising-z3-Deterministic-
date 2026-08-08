package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.TaskRepository
import com.example.ui.TaskTrackerScreen
import com.example.ui.TaskViewModel
import com.example.ui.TaskViewModelFactory
import com.example.ui.theme.TaskTrackerTheme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.AuthScreen
import com.example.ui.chat.ChatScreen

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels {
        val repository = TaskRepository()
        TaskViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskTrackerTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "auth") {
                    composable("auth") {
                        AuthScreen(onAuthSuccess = {
                            navController.navigate("tasks") {
                                popUpTo("auth") { inclusive = true }
                            }
                        })
                    }
                    composable("tasks") {
                        TaskTrackerScreen(
                            viewModel = viewModel,
                            onNavigateToChat = { navController.navigate("chat") }
                        )
                    }
                    composable("chat") {
                        ChatScreen(onNavigateBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
