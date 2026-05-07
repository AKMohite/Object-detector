package app.mak.objectdetector.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface ScreenDestination: NavKey {
    @Serializable
    data object HomeRoute: ScreenDestination
    @Serializable
    data class ResultRoute(val imagePath: String): ScreenDestination
}