package com.example.courseapplication.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.courseapplication.ui.screens.HomeScreen
import com.example.courseapplication.ui.screens.CourseEntryScreen
import com.example.courseapplication.ui.screens.CourseDetailScreen

enum class CourseScreen(val title: String) {
    Home(title = "Courses"),
    Entry(title = "Add Course"),
    Detail(title = "Course Details"),
    Edit(title = "Edit Course")
}

@Composable
fun CourseApp(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = CourseScreen.Home.name
    ) {
        composable(route = CourseScreen.Home.name) {
            HomeScreen(
                navigateToItemEntry = { navController.navigate(CourseScreen.Entry.name) },
                navigateToItemUpdate = { navController.navigate("${CourseScreen.Detail.name}/$it") }
            )
        }
        composable(route = CourseScreen.Entry.name) {
            CourseEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = "${CourseScreen.Detail.name}/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) { backStackEntry ->
            CourseDetailScreen(
                navigateBack = { navController.navigateUp() },
                navigateToEditItem = { navController.navigate("${CourseScreen.Edit.name}/$it") },
                courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
            )
        }
        composable(
            route = "${CourseScreen.Edit.name}/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) { backStackEntry ->
            CourseEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                isEdit = true,
                courseId = backStackEntry.arguments?.getInt("courseId")
            )
        }
    }
}
