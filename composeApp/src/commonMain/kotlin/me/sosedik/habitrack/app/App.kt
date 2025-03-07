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
import me.sosedik.habitrack.presentation.viewmodel.AppViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Preview
@Composable
fun App(
    viewModel: AppViewModel = koinViewModel()
) {
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
                        },
                        onHabitEdit = { habit ->
                            viewModel.cachedHabit = habit
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
