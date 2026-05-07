package app.mak.objectdetector.feature.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ResultViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    fun detectObjects() {

    }

}