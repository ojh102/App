package com.ojh.core.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

sealed interface AppDestination {
    val route: String

    data object Library : AppDestination {
        override val route = "library"
    }

    data object Album : AppDestination {
        override val route = "album"
        const val albumIdArg = "album_id"
        val routeWithArgs = "$route/{$albumIdArg}"
        val arguments = listOf(
            navArgument(albumIdArg) { type = NavType.LongType }
        )
        val deepLinks = listOf(
            navDeepLink { uriPattern = "$SCHEME://$route/{$albumIdArg}" }
        )
    }
}


val appScreens = listOf(AppDestination.Library, AppDestination.Album)

private const val SCHEME = "ojh"
