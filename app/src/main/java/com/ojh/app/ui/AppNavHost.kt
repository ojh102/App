package com.ojh.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ojh.core.navigation.AppDestination
import com.ojh.feature.album.ui.AlbumRoute
import com.ojh.feature.library.ui.LibraryRoute

@Composable
internal fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = com.ojh.core.navigation.AppDestination.Library.route,
        modifier = modifier
    ) {
        composable(route = com.ojh.core.navigation.AppDestination.Library.route) {
            LibraryRoute(
                modifier = Modifier
                    .fillMaxSize(),
                onNavigateToAlbum = {
                    navController.navigateToAlbum(it)
                }
            )
        }
        composable(
            route = com.ojh.core.navigation.AppDestination.Album.routeWithArgs,
            arguments = com.ojh.core.navigation.AppDestination.Album.arguments,
            deepLinks = com.ojh.core.navigation.AppDestination.Album.deepLinks
        ) {
            AlbumRoute(
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

private fun NavHostController.navigateToAlbum(albumId: Long) {
    this.navigateSingleTopTo("${com.ojh.core.navigation.AppDestination.Album.route}/$albumId")
}
