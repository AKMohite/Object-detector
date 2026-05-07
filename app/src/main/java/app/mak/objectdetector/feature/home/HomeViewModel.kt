package app.mak.objectdetector.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val savedState: SavedStateHandle
): ViewModel() {
    fun launchCamera() {
        // create file path and name for image
    }
}
