package app.mak.objectdetector.feature.result.compo

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import app.mak.objectdetector.R
import app.mak.objectdetector.core.ml.DetectionResult
import coil3.compose.AsyncImage
import com.google.mlkit.vision.objects.DetectedObject
import java.util.Locale
import kotlin.math.abs

@Composable
fun ObjectDetectionOverlay(
    imagePath: String,
    detectedObjects: List<DetectedObject>,
    modifier: Modifier = Modifier
) {

    val colors = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Yellow,
        Color.Cyan,
        Color.Magenta
    )

//    val bitmap = remember(imagePath) {
//        BitmapFactory.decodeFile(imagePath)
//    }
    val context = LocalContext.current

    val bitmap = remember(imagePath) {
        context.contentResolver.openInputStream(imagePath.toUri())?.use {
            BitmapFactory.decodeStream(it)
        }
    } ?: return

    Box(modifier = modifier) {

        // Background image
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // Overlay canvas
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val imageWidth = bitmap.width.toFloat()
            val imageHeight = bitmap.height.toFloat()

            val scaleX = size.width / imageWidth
            val scaleY = size.height / imageHeight

            detectedObjects.forEach { detectedObject ->

                val colorId =
                    if (detectedObject.trackingId == null) 0
                    else abs(
                        detectedObject.trackingId!! % colors.size
                    )

                val boxColor = colors[colorId]

                val rect = detectedObject.boundingBox

                val left = rect.left * scaleX
                val top = rect.top * scaleY
                val right = rect.right * scaleX
                val bottom = rect.bottom * scaleY

                // Draw bounding box
                drawRect(
                    color = boxColor,
                    topLeft = Offset(left, top),
                    size = Size(
                        width = right - left,
                        height = bottom - top
                    ),
                    style = Stroke(width = 4.dp.toPx())
                )

                // Labels
                val labels = buildList {

                    detectedObject.labels.forEach { label ->

                        add(label.text)

                        add(
                            String.format(
                                Locale.US,
                                "%.2f%%",
                                label.confidence * 100
                            )
                        )
                    }
                }

                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 42f
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.FILL
                }

                val bgPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(180, 0, 0, 0)
                    style = android.graphics.Paint.Style.FILL
                }

                val lineHeight = 50f

                val maxTextWidth = labels.maxOfOrNull {
                    textPaint.measureText(it)
                } ?: 0f

                val labelHeight =
                    labels.size * lineHeight + 20f

                // Label background
                drawContext.canvas.nativeCanvas.drawRect(
                    left,
                    top - labelHeight,
                    left + maxTextWidth + 20f,
                    top,
                    bgPaint
                )

                // Draw label text
                var textY =
                    top - labelHeight + lineHeight

                labels.forEach { text ->

                    drawContext.canvas.nativeCanvas.drawText(
                        text,
                        left + 10f,
                        textY,
                        textPaint
                    )

                    textY += lineHeight
                }
            }
        }
    }
}