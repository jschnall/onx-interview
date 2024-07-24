package net.schnall.compose.app

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import net.schnall.compose.app.theme.ComposeStarterTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext

@Composable
fun MyApp(
    exitApp: () -> Unit
) {
    val appViewModel: AppViewModel = koinViewModel()
    val screenNameState = appViewModel.screenName.collectAsStateWithLifecycle()

    val appState: AppState = rememberAppState()

    val backStackEntry by appState.navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentScreen = NavScreen.fromRoute(currentDestination?.route ?: "")
        ?: NavScreen.Map

    currentScreen.title?.let {
        appViewModel.updateScreenName(stringResource(it))
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = appState.snackbarHostState)
        },
        topBar = {
            MyAppBar(
                canNavigateBack = appState.navController.previousBackStackEntry != null,
                navigateUp = { appState.navController.navigateUp() },
                screenName = screenNameState.value
            )
        },
        bottomBar = {}
    ) { innerPadding ->
        NavHost(
            navController = appState.navController,
            startDestination = NavGraph.Main.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            mainGraph(
                navController = appState.navController,
                exitApp = exitApp,
                showSnackbar = { message, duration -> appState.showSnackbar(message, duration) },
                updateScreenName = { title -> appViewModel.updateScreenName(title)}
            )
        }
    }
}

@Preview(showSystemUi = false, device = Devices.NEXUS_6, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MyAppPreview() {
    KoinContext {
        ComposeStarterTheme {
            MyApp(exitApp = {})
        }
    }
}

@Preview(showSystemUi = false, device = Devices.NEXUS_6, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MyAppNightPreview() {
    KoinContext {
        ComposeStarterTheme {
            MyApp(exitApp = {})
        }
    }
}