package app.mak.objectdetector.feature.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import app.mak.objectdetector.R
import app.mak.objectdetector.ui.theme.ObjectDetectorTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun HomeScreen(
    onNavigateToResult: (String) -> Unit
) {

    val viewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isCaptured ->
        if (isCaptured) {
            viewModel.onImageCaptured()
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.launchCamera(context)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { event ->
            when(event) {
                is HomeEffect.LaunchCamera -> cameraLauncher.launch(event.uri)
                is HomeEffect.NavigateToResult -> onNavigateToResult(event.path)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }) {
            Text(stringResource(R.string.capture_image))
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    ObjectDetectorTheme {
        HomeScreen(
            onNavigateToResult = {}
        )
    }
}

