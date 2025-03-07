package me.sosedik.habitrack.app

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
                composable<Route.Home.HabitCreation>(
                    enterTransition = {
                        slideInVertically(initialOffsetY = { it }, animationSpec = tween(900))
                    },
                    exitTransition = {
                        slideOutVertically(targetOffsetY = { it }, animationSpec = tween(900))
                    }
                ) {
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
