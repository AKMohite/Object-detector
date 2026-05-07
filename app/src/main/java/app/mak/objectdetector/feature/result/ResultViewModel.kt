package app.mak.objectdetector.feature.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.mak.objectdetector.core.ml.ObjectDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val SAVED_IMAGE_PATH = "saved-image-path"
@HiltViewModel
internal class ResultViewModel @Inject constructor(
    private val detector: ObjectDetector,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    fun onImageLoad(imagePath: String) {
        viewModelScope.launch {
            savedStateHandle[SAVED_IMAGE_PATH] = imagePath
            val detectedObjects = detector.detect(imagePath)
        }
    }

}