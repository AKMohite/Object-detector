package app.mak.objectdetector.feature.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.mak.objectdetector.R
import app.mak.objectdetector.core.ml.DetectionResult
import app.mak.objectdetector.ui.theme.ObjectDetectorTheme
import coil3.compose.AsyncImage

@Composable
internal fun ResultScreen(
    imagePath: String
) {
    val viewModel: ResultViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onImageLoad(imagePath)
    }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        ImageSection(
            title = stringResource(R.string.original_image),
            imagePath = imagePath,
            isOriginal = true,
        )
        ImageSection(
            title = stringResource(R.string.result_image),
            imagePath = imagePath,
            isOriginal = false,
            results = state.results
        )
    }
}

@Composable
internal fun ImageSection(
    title: String,
    imagePath: String,
    isOriginal: Boolean,
    modifier: Modifier = Modifier,
    results: List<DetectionResult> = emptyList()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)

        if (isOriginal) {
            AsyncImage(
                model = imagePath.toUri(),
                contentDescription = title,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        } else {
            AsyncImage(
                model = imagePath.toUri(),
                contentDescription = title,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview
@Composable
private fun ResultScreenPreview() {
    ObjectDetectorTheme {
        ResultScreen("")
    }
}

