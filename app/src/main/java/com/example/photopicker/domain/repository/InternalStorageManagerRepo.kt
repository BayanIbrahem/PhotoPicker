package com.example.photopicker.domain.repository

import android.graphics.Bitmap
import com.example.photopicker.data.utils.PhotoSuffix
import com.example.photopicker.domain.utils.InternalStoragePhoto

interface InternalStorageManagerRepo {
    suspend fun savePhoto(
        fileName: String,
        suffix: PhotoSuffix = PhotoSuffix.PNG,
        bitmap: Bitmap,
        quality: Int = 95,
    ): Boolean

    suspend fun loadPhotos (
    ): List<InternalStoragePhoto>

    suspend fun deletePhoto (
        fileName: String,
    ): Boolean
}