package me.sosedik.habitrack.app

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import me.sosedik.habitrack.presentation.screen.GeneralSettingsScreenRoot
import me.sosedik.habitrack.presentation.screen.HabitCreationScreenRoot
import me.sosedik.habitrack.presentation.screen.HabitListScreenRoot
import me.sosedik.habitrack.presentation.screen.SettingsScreenRoot
import me.sosedik.habitrack.presentation.theme.HabiTrackTheme
import me.sosedik.habitrack.presentation.theme.IconCache
import me.sosedik.habitrack.presentation.theme.NerdSymbolsFontFamily
import me.sosedik.habitrack.presentation.viewmodel.AppViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Preview
@Composable
fun App(
    viewModel: AppViewModel = koinViewModel(),
    iconsFontFamily: FontFamily = NerdSymbolsFontFamily(),
    iconCache: IconCache = getKoin().get { parametersOf(iconsFontFamily, "nerd_symbols") }
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
                        iconCache = iconCache,
                        onSettings = {
                            navController.navigate(Route.Settings.Overview)
                        },
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
                        slideInVertically(initialOffsetY = { it }, animationSpec = tween(500))
                    },
                    popExitTransition = {
                        slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500))
                    }
                ) {
                    HabitCreationScreenRoot(
                        iconCache = iconCache,
                        onDiscard = {
                            navController.popBackStack()
                        },
                        onSave = {
                            navController.popBackStack()
                        }
                    )
                }
            }
            navigation<Route.Settings>(startDestination = Route.Settings.Overview) {
                composable<Route.Settings.Overview>(
                    enterTransition = {
                        slideInVertically(initialOffsetY = { it }, animationSpec = tween(500))
                    },
                    popEnterTransition = {
                        EnterTransition.None
                    },
                    popExitTransition = {
                        slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500))
                    }
                ) {
                    SettingsScreenRoot(
                        onDiscard = {
                            navController.popBackStack()
                        },
                        onNavigate = {
                            navController.navigate(it)
                        }
                    )
                }
                composable<Route.Settings.General>(
                    enterTransition = {
                        scaleIn(
                            initialScale = 0.7F,
                            animationSpec = tween(durationMillis = 200)
                        )
                    },
                    popExitTransition = {
                        scaleOut(
                            targetScale = 0.9F,
                            animationSpec = tween(durationMillis = 100)
                        ) + fadeOut(
                            targetAlpha = 0F,
                            animationSpec = tween(durationMillis = 100)
                        )
                    }
                ) {
                    GeneralSettingsScreenRoot(
                        onDiscard = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
