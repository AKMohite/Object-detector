package app.mak.objectdetector.core.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal interface ObjectDetector {
    suspend fun detect(imagePath: String): List<String>

}

internal class MLObjectDetector @Inject constructor(
    @param:ApplicationContext private val context: Context
): ObjectDetector {
    override suspend fun detect(imagePath: String): List<String> {
        return processImage(imagePath.toUri())
    }

    private suspend fun processImage(uri: Uri): List<String> {
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

    private fun detectObjects(bitmap: Bitmap): List<String> {
        return emptyList()
    }

}