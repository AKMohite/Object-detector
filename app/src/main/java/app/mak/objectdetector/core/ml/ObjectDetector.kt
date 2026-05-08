package app.mak.objectdetector.core.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import javax.inject.Inject

internal interface ImageDetector {
    suspend fun detect(imagePath: String): List<DetectionResult>

}

internal class TensorFlowImageDetector @Inject constructor(
    @param:ApplicationContext private val context: Context
): ImageDetector {

    private var objectDetector: ObjectDetector? = null

    init {
        setupObjectDetector()
    }

    private fun setupObjectDetector() {
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(0.5f)
            .setMaxResults(5)

        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(
                context, "efficientdet_lite0.tflite", optionsBuilder.build()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun detect(imagePath: String): List<DetectionResult> {
        return processImage(imagePath.toUri())
    }

    private suspend fun processImage(uri: Uri): List<DetectionResult> {
        val bitmap = loadBitmapFromUri(uri) ?: return emptyList()
        val detections = detectObjects(bitmap)
        return detections
    }

    private suspend fun loadBitmapFromUri(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun detectObjects(bitmap: Bitmap): List<DetectionResult> = withContext(Dispatchers.IO) {
        if (objectDetector == null) {
            setupObjectDetector()
        }
        val image = TensorImage.fromBitmap(bitmap)
//        objectDetector?.detect(image)?.map { detection ->
//            DetectionResult(
//                boundingBox = detection.boundingBox,
//                label = detection.categories.firstOrNull()?.label ?: "Unknown",
//                score = detection.categories.firstOrNull()?.score ?: 0f
//            )
//        } ?: emptyList()
        emptyList()
    }

}

internal data class DetectionResult(
    val boundingBox: Rect,
    val label: String
)