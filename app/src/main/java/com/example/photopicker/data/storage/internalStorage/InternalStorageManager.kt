package com.example.photopicker.data.storage.internalStorage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.photopicker.data.utils.PhotoSuffix
import com.example.photopicker.domain.unit_classes.InternalStoragePhoto
import kotlinx.coroutines.*
import java.io.IOException

object InternalStorageManager {
    suspend fun savePhoto (
        context: Context,
        fileName: String,
        suffix: PhotoSuffix = PhotoSuffix.JPEG,
        bitmap: Bitmap,
        quality: Int = 95,
    ): Boolean {
        val handler = CoroutineExceptionHandler {
                _, trowable ->
            Log.e("InternalStorageManager", "failed to save $fileName")
            trowable.printStackTrace()
        }
        return withContext(Dispatchers.IO) {
            /*
            `use` method closes the stream automatically after finishing.
             */
            context.openFileOutput("$fileName.${suffix.suffixString}", MODE_PRIVATE).use {
                outputStream ->
                val compressFormat = when (suffix) {
                    PhotoSuffix.PNG -> Bitmap.CompressFormat.PNG
                    else -> Bitmap.CompressFormat.JPEG
                }
                if (bitmap.compress(compressFormat, quality, outputStream)) {
                    Log.d("InternalStorageSave", "successfully saving $fileName")
                    true
                } else {
                    this.cancel("save failure", IOException("can't save bitmap"))
                    false
                }
            }
        }
    }

    suspend fun loadPhotos (
        context: Context,
    ): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            (context.filesDir.listFiles()?.filter { file ->
                val name = file.name
                file.isFile &&
                        file.canRead() &&
                        name.endsWith("jpeg")
                            .or(name.endsWith("png"))
                            .or(name.endsWith("jpg"))
            }?.map { file ->
                try {
                    val bytes = file.readBytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    InternalStoragePhoto(name = file.name, bitmap = bitmap)
                } catch (e: IOException) {
                    null
                }
            }?.filter{
                it != null
            }.also {
                Log.d("InternalStorageLoad", "successfully loading photos")
            } ?: listOf<InternalStoragePhoto>().also {
                Log.e("InternalStorageLoad", "failed loading photos")
            }) as List<InternalStoragePhoto>
        }
    }

    suspend fun deletePhoto (
        context: Context,
        fileName: String,
    ): Boolean {
        val handler = CoroutineExceptionHandler {
                _, trowable ->
            Log.e("InternalStorageManager", "failed to delete $fileName")
            trowable.printStackTrace()
        }
        return withContext(Dispatchers.IO) {
            async (handler) {
                context.deleteFile(fileName)
            }.await()
        }
    }
}