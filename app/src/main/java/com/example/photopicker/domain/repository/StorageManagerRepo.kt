package com.example.photopicker.domain.repository

import android.graphics.Bitmap
import com.example.photopicker.data.utils.PhotoSuffix
import com.example.photopicker.domain.utils.PrivatePhoto
import com.example.photopicker.domain.utils.SharedPhoto

interface StorageManagerRepo {
    suspend fun savePrivatePhoto(
        fileName: String,
        suffix: PhotoSuffix = PhotoSuffix.PNG,
        bitmap: Bitmap,
        quality: Int = 95,
    ): Boolean
    suspend fun saveSharedPhoto (
        fileName: String,
        suffix: PhotoSuffix = PhotoSuffix.PNG,
        bitmap: Bitmap,
        quality: Int = 95,
    ): Boolean

    suspend fun loadPrivatePhotos (): List<PrivatePhoto>
    suspend fun loadSharedPhotos (): List<SharedPhoto>

    suspend fun deletePrivatePhoto (fileName: String): Boolean
    suspend fun deleteSharedPhoto (

    ): Boolean
}