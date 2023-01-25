package com.example.photopicker.data.storage.shared_storage

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.example.photopicker.data.utils.PhotoSuffix
import com.example.photopicker.data.utils.ifSdk29DoUnless
import com.example.photopicker.domain.utils.SharedPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

const val TAG = "SHARED_STORAGE"
class SharedStorageManager(private val context: Context) {
    /** save shared photo: */
    suspend fun savePhoto(
        fileName: String,
        suffix: PhotoSuffix,
        bitmap: Bitmap,
        quality: Int
    ): Boolean {
        /** creating the uri required for the image... */
        val imageCollection = ifSdk29DoUnless {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        /** creating image values in bundle, this bundle will be saved in internal database... */
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.${suffix.suffixString}")
            put(MediaStore.Images.Media.MIME_TYPE, suffix.mimeType)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
        }
        /** saving image in the url using content resolver */
        return try {
            context.contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                context.contentResolver.openOutputStream(uri).use { outputStream ->
                    val format = when (suffix) {
                        PhotoSuffix.JPEG -> Bitmap.CompressFormat.JPEG
                        else -> Bitmap.CompressFormat.PNG
                    }
                    if (!bitmap.compress(format, quality, outputStream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore Entry")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loadPhotos(): List<SharedPhoto> {
        val contentUri = ifSdk29DoUnless {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = listOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
        )

        val photos = mutableListOf<SharedPhoto>()
        context.contentResolver.query(
            contentUri,
            projection.toTypedArray(),
            null,
            null,
            "${MediaStore.Images.Media.DISPLAY_NAME} ASC",
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                photos.add(SharedPhoto(id, name, width, height, uri))
            }
        }
        return photos.toList()
    }

    suspend fun deletePhoto(
        photo: SharedPhoto,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ): Boolean {
        delete(photo, launcher)
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
            delete(photo, launcher)
        }
        return false
    }
    private suspend fun delete(
        photo: SharedPhoto,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ): Boolean{
        try {
            context.contentResolver.delete(photo.contentUri, null, null)
        } catch (e: SecurityException) {
            val intentSender = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    MediaStore.createDeleteRequest(context.contentResolver, listOf(photo.contentUri)).intentSender
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    val recoverableSecurityException = e as? RecoverableSecurityException
                    recoverableSecurityException?.userAction?.actionIntent?.intentSender
                }
                else -> null
            }
            intentSender?.let {
                withContext(Dispatchers.Main) {
                    launcher.launch(
                        IntentSenderRequest.Builder(it).build()
                    )
                    return@withContext true
                }
            }
        }
        return false
    }
}