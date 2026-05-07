package app.mak.objectdetector.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import app.mak.objectdetector.feature.home.HomeScreen

@Composable
internal fun AppNavHost(innerPadding: PaddingValues) {
    val backStack = remember { mutableStateListOf<ScreenDestination>(ScreenDestination.HomeRoute) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ScreenDestination.HomeRoute> {
                HomeScreen(
                    onNavigateToResult = { imagePath ->
                        backStack.add(ScreenDestination.ResultRoute(imagePath))
                    }
                )
            }
            entry<ScreenDestination.ResultRoute> { route ->
                Text(route.imagePath)
            }
        },
        modifier = Modifier.fillMaxSize().padding(innerPadding),
    )
}