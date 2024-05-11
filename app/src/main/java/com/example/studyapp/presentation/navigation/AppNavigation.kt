package com.example.studyapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studyapp.presentation.auth.LoginScreen
import com.example.studyapp.presentation.auth.SignUpScreen
import com.example.studyapp.presentation.note.NoteScreen
import com.example.studyapp.presentation.profile.ProfileScreen
import com.example.studyapp.presentation.study.StudyScreen
import com.example.studyapp.presentation.study.session.SessionScreen
import com.example.studyapp.presentation.study.subject.SubjectScreen
import com.example.studyapp.presentation.study.task.TaskScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    Scaffold(
        bottomBar = {
            if( currentRoute == Screens.StudyScreenRoute.route ||
                currentRoute == Screens.NoteScreenRoute.route ||
                currentRoute == Screens.ProfileScreenRoute.route
            ) {
                BottomBarNav(navController)
            }
        }
    ) {paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.LoginScreenRoute.route,
            modifier = Modifier
                .padding(paddingValues)
        ) {
            composable(Screens.LoginScreenRoute.route) {
                LoginScreen(navController = navController)
            }

            composable(Screens.RegisterScreenRoute.route) {
                SignUpScreen(navController = navController)
            }

            composable(Screens.StudyScreenRoute.route) {
                StudyScreen(navController)
            }
            composable(Screens.NoteScreenRoute.route) {
                NoteScreen()
            }
            composable(Screens.ProfileScreenRoute.route) {
                ProfileScreen(navController = navController)
            }
            composable(
                route = Screens.SubjectScreenRoute.route,
                arguments = listOf(
                    navArgument(SUBJECT_ARG_KEY) { type = NavType.StringType }
                )
            ) {backStackEntry ->
                SubjectScreen(
                    subjectId = backStackEntry.arguments?.getString("subjectId") ?: "",
                    navController = navController
                )
            }
            composable(
                route = Screens.TaskScreenRoute.route,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType

                        defaultValue = ""
                    },
                    navArgument("subjectId") {
                        type = NavType.StringType

                        defaultValue = ""
                    }
                )
            ) {backStackEntry ->
                TaskScreen(
                    taskId = backStackEntry.arguments?.getString("id"),
                    subjectId = backStackEntry.arguments?.getString("subjectId"),
                    navController = navController
                )
            }
            composable(Screens.SessionScreenRoute.route) {
                SessionScreen(navController = navController)
            }
        }
    }

}

@Composable
private fun BottomBarNav(
    navController: NavHostController
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val listOfNavItems = listOf(
            NavItem(
                label = "Học tập",
                icon = Icons.Default.DateRange,
                route = Screens.StudyScreenRoute.route
            ),
            NavItem(
                label = "Ghi chú",
                icon = Icons.Default.Edit,
                route = Screens.NoteScreenRoute.route
            ),
            NavItem(
                label = "Hồ sơ",
                icon = Icons.Default.Person,
                route = Screens.ProfileScreenRoute.route
            )
        )

        listOfNavItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                onClick = {
                          navController.navigate(navItem.route) {
                              popUpTo(navController.graph.findStartDestination().id) {
                                  saveState = true
                              }
                              launchSingleTop = true
                              restoreState = true
                          }
                },
                icon = { 
                    Icon(imageVector = navItem.icon, contentDescription = null)
                },
                label = { Text(text = navItem.label) }
            )
        }
    }
}

