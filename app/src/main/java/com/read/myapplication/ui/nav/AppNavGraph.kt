package com.read.myapplication.ui.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.read.myapplication.ui.features.details.DetailUIScreen
import com.read.myapplication.ui.features.posts.PostsUIScreen
import com.read.myapplication.ui.features.details.DetailsViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Destination.List.route
    ) {
        composable(Destination.List.route) { backStackEntry ->
            PostsUIScreen(navController)
        }

        composable(
            Destination.Detail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vm: DetailsViewModel = hiltViewModel(backStackEntry)
            DetailUIScreen(onBack = { navController.popBackStack() }, vm = vm)
        }

    }
}