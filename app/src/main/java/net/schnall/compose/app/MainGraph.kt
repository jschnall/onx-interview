package net.schnall.compose.app

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import net.schnall.compose.R
import org.koin.androidx.compose.koinViewModel

enum class NavGraph(val route: String) {
    Main("mainGraph"),
}

sealed class NavScreen(
    val route: String,
    @StringRes val title: Int?,
    val hasNavigationBar: Boolean = true,
) {
    data object Map: NavScreen(route = "map", title = R.string.map)

    companion object {
        private val routeToScreen = NavScreen::class.sealedSubclasses.associate { it.objectInstance!!.route to it.objectInstance!! }
        fun fromRoute(route: String): NavScreen? = routeToScreen[route]
    }
}

fun NavGraphBuilder.mainGraph(
    navController: NavController,
    exitApp: () -> Unit,
    showSnackbar: (String, SnackbarDuration) -> Unit,
    updateScreenName: (String) -> Unit
) {
    navigation(startDestination = NavScreen.Map.route, route = NavGraph.Main.route) {
        composable(NavScreen.Map.route) { backStackEntry ->
             val viewModel: MapViewModel = koinViewModel()
             val uiState = viewModel.uiState.collectAsStateWithLifecycle()

            MapScreen(
                uiState = uiState.value,
                showSnackbar = showSnackbar,
                onMapClick = { point ->
                    viewModel.addLocation(point)
                    true
                },
                onLocationChange = { location ->
                    viewModel.updateLocation(location)
                }

            )
        }
    }
}