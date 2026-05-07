package app.mak.objectdetector.feature.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import app.mak.objectdetector.R
import app.mak.objectdetector.ui.theme.ObjectDetectorTheme

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier
) {

    val viewModel: HomeViewModel = hiltViewModel()
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isCaptured ->
        if (isCaptured) {

        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            viewModel.launchCamera()
        }) {
            Text(stringResource(R.string.capture_image))
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    ObjectDetectorTheme {
        HomeScreen()
    }
}

