package app.mak.objectdetector.feature.result.compo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.core.net.toUri
import app.mak.objectdetector.R
import app.mak.objectdetector.core.ml.DetectionResult
import coil3.compose.AsyncImage

@Composable
internal fun OverlayView(
    imagePath: String,
    results: List<DetectionResult>,
    modifier: Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    var imageSize by remember { mutableStateOf<Size?>(null) }

    Box(modifier = modifier) {
        AsyncImage(
            model = imagePath.toUri(),
            contentDescription = stringResource(R.string.result_image),
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Fit,
            onSuccess = { state ->
                val painter = state.painter
                imageSize = painter.intrinsicSize
            }
        )

        Canvas(modifier = Modifier.matchParentSize()) {
            results.forEach { detectedObject ->

                val rect = detectedObject.boundingBox

                // Bounding box
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(rect.left.toFloat(), rect.top.toFloat()),
                    size = Size(
                        rect.width().toFloat(),
                        rect.height().toFloat()
                    ),
                    style = Stroke(width = 4f)
                )

                // Label text
                val label = detectedObject.label

                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    rect.left.toFloat(),
                    rect.top - 12f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 40f
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}