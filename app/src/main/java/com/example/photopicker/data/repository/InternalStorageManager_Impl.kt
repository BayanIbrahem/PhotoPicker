package com.example.photopicker.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.photopicker.data.utils.PhotoSuffix
import com.example.photopicker.domain.repository.InternalStorageManagerRepo
import com.example.photopicker.domain.utils.InternalStoragePhoto
import kotlinx.coroutines.*
import java.io.IOException

const val TAG = "ISM_IMPL"
class InternalStorageManager_Impl(
    private val context: Context,
): InternalStorageManagerRepo {
    override suspend fun savePhoto(
        fileName: String,
        suffix: PhotoSuffix,
        bitmap: Bitmap,
        quality: Int,
    ): Boolean {
        val coroutineHandler = CoroutineExceptionHandler {
            _, throwable ->
            Log.e(TAG, "failed to save $fileName")
            throwable.printStackTrace()
        }
        return withContext<Boolean>(Dispatchers.IO) {
            var result = false
            this.launch(coroutineHandler) {
                /*
                `use` method closes the stream automatically after finishing.
                 */
                result = context.openFileOutput("$fileName.${suffix.suffixString}", Context.MODE_PRIVATE).use {
                        outputStream ->
                    val compressFormat = when (suffix) {
                        PhotoSuffix.PNG -> Bitmap.CompressFormat.PNG
                        else -> Bitmap.CompressFormat.JPEG
                    }
                    if (bitmap.compress(compressFormat, quality, outputStream)) {
                        Log.d(TAG, "successfully saving $fileName")
                        true
                    } else {
                        this.cancel("save failure", IOException("can't save bitmap"))
                        false
                    }
                }
            }
            Log.d(TAG, "saving result: $result, file name: $fileName")
            result

        }
    }

    override suspend fun loadPhotos(): List<InternalStoragePhoto> {
        val photos = withContext(Dispatchers.IO) {
            context.filesDir.listFiles()?.filter { file ->
                val name = file.name
                file.isFile &&
                        file.canRead() &&
                        name.endsWith("jpeg")
                            .or(name.endsWith("png"))
                            .or(name.endsWith("jpg"))
            }?.mapNotNull { file ->
                try {
                    val bytes = file.readBytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    InternalStoragePhoto(name = file.name, bitmap = bitmap)
                } catch (e: IOException) {
                    null
                }
            }.also {
                Log.d(TAG, "successfully loading photos")
            } ?: listOf<InternalStoragePhoto>().also {
                Log.e(TAG, "failed loading photos")
            }
        }
        Log.d(TAG, "load: result is null or empty: ${photos.isNotEmpty()}, content: $photos")
        return photos
    }

    override suspend fun deletePhoto(fileName: String): Boolean {
        val handler = CoroutineExceptionHandler {
                _, trowable ->
            Log.e(TAG, "failed to delete $fileName")
            trowable.printStackTrace()
        }
        return withContext(Dispatchers.IO) {
            var result = false
            this.launch(handler) {
                result = context.deleteFile(fileName)
            }
            Log.d(TAG, "delete: result: $result, file name: $fileName")
            result
        }
    }
}