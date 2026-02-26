package com.imagecounter.game

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.imagecounter.game.navigation.AppNavGraph
import com.imagecounter.game.ui.theme.ImageCounterTheme

@Composable
fun ImageCounterApp() {
    ImageCounterTheme {
        val navController = rememberNavController()
        AppNavGraph(navController = navController)
    }
}
