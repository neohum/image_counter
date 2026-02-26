package com.imagecounter.game.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.imagecounter.game.ui.gameplay.GamePlayScreen
import com.imagecounter.game.ui.home.HomeScreen
import com.imagecounter.game.ui.stageselect.StageSelectScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Home,
    ) {
        composable<Home> {
            HomeScreen(
                onStartClick = { navController.navigate(StageSelect) }
            )
        }
        composable<StageSelect> {
            StageSelectScreen(
                onStageClick = { stageId ->
                    navController.navigate(GamePlay(stageId = stageId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<GamePlay> { backStackEntry ->
            val route = backStackEntry.toRoute<GamePlay>()
            GamePlayScreen(
                stageId = route.stageId,
                startLevel = route.startLevel,
                onBackClick = { navController.popBackStack() },
                onStageComplete = {
                    navController.popBackStack(route = Home, inclusive = false)
                }
            )
        }
    }
}
