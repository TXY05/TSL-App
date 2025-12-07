package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.screen.Dashboard
import com.example.myapplication.screen.Post
import com.example.myapplication.screen.Search
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController, startDestination = "dashboard"
                ) {
                    composable("dashboard") {
                        Dashboard(
                            onAddClick = { date ->
                            navController.navigate("addItem/$date")
                        },
                            onEditClick = { postId -> navController.navigate("editItem/$postId") },
                            onSearchClick = { navController.navigate("searchPost") })
                    }

                    composable("searchPost") { Search(navController) }

                    composable(
                        route = "addItem/{date}",
                        arguments = listOf(navArgument("date") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val dateString = backStackEntry.arguments?.getString("date")
                        val date = dateString?.let { LocalDate.parse(it) } ?: LocalDate.now()

                        Post(
                            postId = null,
                            onBack = { navController.popBackStack() },
                            defaultDate = date // <- you can use this in Post composable
                        )
                    }


                    composable(
                        route = "editItem/{postId}",
                        arguments = listOf(navArgument("postId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val postId = backStackEntry.arguments?.getInt("postId") ?: return@composable
                        Post(
                            postId = postId,
                            onBack = { navController.popBackStack() } // ✅ Add this
                        )
                    }


                }

            }
        }
    }
}

