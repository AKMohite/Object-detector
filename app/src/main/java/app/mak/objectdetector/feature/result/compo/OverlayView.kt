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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            val imgSize = imageSize ?: return@Canvas
            if (imgSize.width <= 0f || imgSize.height <= 0f) return@Canvas

            val canvasSize = size

            // Calculate the scaling and offset for ContentScale.Fit
            val scaleX = canvasSize.width / imgSize.width
            val scaleY = canvasSize.height / imgSize.height
            val scale = minOf(scaleX, scaleY)

            val offsetX = (canvasSize.width - imgSize.width * scale) / 2f
            val offsetY = (canvasSize.height - imgSize.height * scale) / 2f

            results.forEach { result ->
                val box = result.boundingBox

                // Map coordinates from original image to canvas
                val mappedLeft = box.left * scale + offsetX
                val mappedTop = box.top * scale + offsetY
                val mappedWidth = box.width() * scale
                val mappedHeight = box.height() * scale

                // Draw bounding box
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(mappedLeft, mappedTop),
                    size = Size(mappedWidth, mappedHeight),
                    style = Stroke(width = 4.dp.toPx())
                )

                // Draw label and score
                val textLayoutResult = textMeasurer.measure(
                    text = result.label,
                    style = TextStyle(color = Color.White, fontSize = 14.sp)
                )

                // Draw text above the box, or inside if not enough space
                val textTop = if (mappedTop - textLayoutResult.size.height > 0) {
                    mappedTop - textLayoutResult.size.height
                } else {
                    mappedTop
                }

                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(mappedLeft, textTop)
                )
            }
        }
    }
}