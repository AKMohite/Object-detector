package app.mak.objectdetector.feature.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.mak.objectdetector.core.ml.DetectionResult
import app.mak.objectdetector.core.ml.ImageDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val SAVED_IMAGE_PATH = "saved-image-path"

internal data class ResultState(
    val imagePath: String = "",
    val results: List<DetectionResult> = emptyList()
)

@HiltViewModel
internal class ResultViewModel @Inject constructor(
    private val detector: ImageDetector,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state = _state.asStateFlow()

    fun onImageLoad(imagePath: String) {
        viewModelScope.launch {
            savedStateHandle[SAVED_IMAGE_PATH] = imagePath
            val detectedObjects = detector.detect(imagePath)
            _state.update { it.copy(results = detectedObjects) }
        }
    }

}