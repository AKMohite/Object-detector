package app.mak.objectdetector.feature.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.mak.objectdetector.utils.ext.getTempUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val SAVED_URI_PATH = "saved-uri-path"

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val savedState: SavedStateHandle
): ViewModel() {

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    fun launchCamera(context: Context) {
        viewModelScope.launch {
            val tempUri = withContext(Dispatchers.IO) {
                context.getTempUri()
            } ?: return@launch
            savedState[SAVED_URI_PATH] = tempUri.toString()
            _effect.emit(HomeEffect.LaunchCamera(tempUri))
        }

    }

    fun onImageCaptured() {
        viewModelScope.launch {
            val imagePath: String = savedState[SAVED_URI_PATH]
                ?: throw IllegalStateException("The image was captured but path was not found")
            _effect.emit(HomeEffect.NavigateToResult(imagePath))
        }
    }
}

sealed interface HomeEffect {
    data class LaunchCamera(val uri: Uri): HomeEffect
    data class NavigateToResult(val path: String): HomeEffect
}
