package app.mak.objectdetector.core.ml

import android.graphics.Rect

internal interface ImageDetector {
    suspend fun detect(imagePath: String): List<DetectionResult>
}

internal data class DetectionResult(
    val boundingBox: Rect,
    val label: String
)
