package app.mak.objectdetector.core.ml

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class MLImageDetector @Inject constructor(
    @param:ApplicationContext private val context: Context
): ImageDetector {

    // Multiple object detection in static images
    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
        .enableMultipleObjects()
        .enableClassification()  // Optional
        .build()

    private val objectDetector = ObjectDetection.getClient(options)

    override suspend fun detect(imagePath: String): List<DetectionResult> = withContext(Dispatchers.IO) {
        return@withContext detectObjects(imagePath)
    }

    private suspend fun detectObjects(imagePath: String) = suspendCancellableCoroutine { continuation ->
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, imagePath.toUri())
            objectDetector.process(image)
                .addOnSuccessListener { detectedObjects ->
                    val results: List<DetectionResult> = detectedObjects?.mapNotNull { detection ->
                        Log.d("MLImageDetector", "_______________________________________________________")
                        Log.d("MLImageDetector", "yelllo: ${detection.labels}")
                        Log.d("MLImageDetector", "yelllo: ${detection.boundingBox}")
                        Log.d("MLImageDetector", "_______________________________________________________")
                        var label = ""
                        detection.labels.forEach { objectLabel ->
                            label = "${objectLabel.text}: ${objectLabel.confidence}"
                        }
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
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
        continuation.invokeOnCancellation {
            it?.printStackTrace()
            objectDetector.close()
        }
    }
}