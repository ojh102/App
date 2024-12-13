package com.ojh.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ojh.core.navigation.AppDestination
import com.ojh.core.navigation.appScreens
import com.ojh.feature.player.ui.CollapsedPlayerLayoutHeight
import com.ojh.feature.player.ui.PlayerBottomSheet


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun App(
    navController: NavHostController = rememberNavController()
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen =
        appScreens.find { it.route == currentDestination?.route } ?: AppDestination.Album

    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            PlayerBottomSheet(scaffoldState)
        },
        sheetShape = RectangleShape,
        sheetPeekHeight = CollapsedPlayerLayoutHeight + WindowInsets.navigationBars.asPaddingValues()
            .calculateBottomPadding(),
        sheetDragHandle = { },
        sheetSwipeEnabled = false,
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AppNavHost(navController, modifier = Modifier.fillMaxSize())
        }
    }
}
