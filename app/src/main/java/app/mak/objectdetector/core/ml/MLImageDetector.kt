package app.mak.objectdetector.core.ml

import android.content.Context
import androidx.core.net.toUri
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class MLImageDetector @Inject constructor(
    @param:ApplicationContext private val context: Context
): ImageDetector {

    private var objectDetector: ObjectDetector? = null
    private var useCustomOptions = true

    private fun setupDetector() {
        if (objectDetector != null) return

        val options = getMLOptions()

        objectDetector = ObjectDetection.getClient(options)
    }

    private fun getMLOptions(): ObjectDetectorOptionsBase {

        return if (useCustomOptions) {
            val localModel =
                LocalModel.Builder().setAssetFilePath("custom_models/object_labeler.tflite").build()
            CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .build()
        } else {
            ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .build()
        }
    }

    override suspend fun detect(imagePath: String): List<DetectionResult> =
        withContext(Dispatchers.IO) {
        return@withContext detectObjects(imagePath)
    }

    private suspend fun detectObjects(imagePath: String) = suspendCancellableCoroutine { continuation ->
        setupDetector()
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, imagePath.toUri())
            objectDetector?.let { detector ->
                detector.process(image)
                .addOnSuccessListener { detectedObjects ->
                    val results: List<DetectionResult> = detectedObjects?.map { detection ->
                        val firstLabel = detection.labels.firstOrNull()
                        val label =
                            "${firstLabel?.text ?: "Unknown Object"} (${((firstLabel?.confidence ?: 0f) * 100).toInt()}%)"
                        DetectionResult(
                            boundingBox = detection.boundingBox,
                            label = label,
                        )
                    } ?: emptyList()
                    continuation.resume(results)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }

            }
                ?: continuation.resumeWithException(IllegalStateException("The object detector was not initialised"))
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
}
