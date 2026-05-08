package app.mak.objectdetector.core.ml

import android.content.Context
import androidx.core.net.toUri
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
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
                .enableClassification()  // Optional
                .build()
        }
    }

    override suspend fun detect(imagePath: String): List<DetectedObject> = withContext(Dispatchers.IO) {
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
                    val results: List<DetectionResult> = detectedObjects?.mapNotNull { detection ->
                        var label = ""
                        detection.labels.forEach { objectLabel ->
                            label = "${objectLabel.text}: ${objectLabel.confidence}"
                        }
                        DetectionResult(
                            boundingBox = detection.boundingBox,
                            label = label,
                        )
                    } ?: emptyList()
                    continuation.resume(detectedObjects.mapNotNull { it })
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }

            }
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
        continuation.invokeOnCancellation {
            it?.printStackTrace()
            objectDetector?.close()
        }
    }
}