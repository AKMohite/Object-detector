package app.mak.objectdetector.utils.ext

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File

internal fun Context.getTempUri(): Uri? {
    val imageFileName = "JPEG_Temp_image"
    val storagePath = (
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                filesDir.path
            } else {
                Environment.getExternalStorageDirectory().path
            }
            ) + "/images"
    val storageDir = File(storagePath)
    if (!storageDir.exists()) storageDir.mkdirs()
    val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
    return FileProvider.getUriForFile(
        this,
        "${packageName}.provider",
        imageFile
    )
}