package app.mak.objectdetector.feature.result.compo

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import app.mak.objectdetector.core.ml.DetectionResult
import coil3.compose.AsyncImage

@Composable
internal fun ObjectDetectionOverlay(
    imagePath: String,
    detectedObjects: List<DetectionResult>,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta
    )

    val context = LocalContext.current
    // Load bitmap just to get original dimensions for scaling
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    remember(imagePath) {
        context.contentResolver.openInputStream(imagePath.toUri())?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    }

    val imageWidth = options.outWidth.toFloat()
    val imageHeight = options.outHeight.toFloat()

    if (imageWidth <= 0f || imageHeight <= 0f) return

    val aspectRatio = imageWidth / imageHeight

    Box(modifier = modifier
        .fillMaxWidth()
        .aspectRatio(aspectRatio)) {
        // Use AsyncImage for consistent orientation handling
        AsyncImage(
            model = imagePath.toUri(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val scaleX = size.width / imageWidth
            val scaleY = size.height / imageHeight

            detectedObjects.forEachIndexed { index, detectedObject ->
                val boxColor = colors[index % colors.size]
                val rect = detectedObject.boundingBox

                val left = rect.left * scaleX
                val top = rect.top * scaleY
                val right = rect.right * scaleX
                val bottom = rect.bottom * scaleY

                // Draw bounding box
                drawRect(
                    color = boxColor,
                    topLeft = Offset(left, top),
                    size = Size(right - left, bottom - top),
                    style = Stroke(width = 3.dp.toPx())
                )

                // Label background and text
                val label = detectedObject.label
                
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 36f
                    isAntiAlias = true
                }

                val bgPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(180, 0, 0, 0)
                    style = android.graphics.Paint.Style.FILL
                }

                val textWidth = textPaint.measureText(label)
                val textHeight = 40f

                drawContext.canvas.nativeCanvas.drawRect(
                    left, top - textHeight, left + textWidth + 8f, top, bgPaint
                )
                drawContext.canvas.nativeCanvas.drawText(
                    label, left + 4f, top - 8f, textPaint
                )
            }
        }
    }
}
