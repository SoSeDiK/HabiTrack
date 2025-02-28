package me.sosedik.habitrack.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import me.sosedik.habitrack.presentation.screen.HabitCreationScreenRoot
import me.sosedik.habitrack.presentation.screen.HabitListScreenRoot
import me.sosedik.habitrack.presentation.theme.HabiTrackTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun App() {
    val navController: NavHostController = rememberNavController()

    HabiTrackTheme {
        NavHost(
            navController = navController,
            startDestination = Route.Home
        ) {
            navigation<Route.Home>(startDestination = Route.Home.Overview) {
                composable<Route.Home.Overview> {
                    HabitListScreenRoot(
                        onNewHabitCreation = {
                            navController.navigate(Route.Home.HabitCreation)
                        }
                    )
                }
                composable<Route.Home.HabitCreation> {
                    HabitCreationScreenRoot(
                        onDiscard = {
                            navController.popBackStack()
                        },
                        onSave = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
