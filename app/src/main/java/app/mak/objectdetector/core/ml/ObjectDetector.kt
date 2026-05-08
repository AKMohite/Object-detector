package app.mak.objectdetector.core.ml

import android.graphics.Rect
import com.google.mlkit.vision.objects.DetectedObject

internal interface ImageDetector {
    suspend fun detect(imagePath: String): List<DetectedObject>

}

internal data class DetectionResult(
    val boundingBox: Rect,
    val label: String
)