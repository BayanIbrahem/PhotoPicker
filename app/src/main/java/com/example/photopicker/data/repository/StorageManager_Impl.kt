package com.example.photopicker.data.repository

import android.graphics.Bitmap
import com.example.photopicker.data.storage.shared_storage.SharedStorageManager
import com.example.photopicker.data.storage.private_storage.PrivateStorageManager
import com.example.photopicker.data.utils.PhotoSuffix
import com.example.photopicker.domain.repository.StorageManagerRepo
import com.example.photopicker.domain.utils.PrivatePhoto
import com.example.photopicker.domain.utils.SharedPhoto
import javax.inject.Inject

class StorageManager_Impl @Inject constructor(
    val privateStorage: PrivateStorageManager,
    val sharedStorage: SharedStorageManager,
): StorageManagerRepo{
    override suspend fun savePrivatePhoto(
        fileName: String,
        suffix: PhotoSuffix,
        bitmap: Bitmap,
        quality: Int
    ): Boolean {
        return privateStorage.savePhoto(fileName, suffix, bitmap, quality)
    }

    override suspend fun saveSharedPhoto(
        fileName: String,
        suffix: PhotoSuffix,
        bitmap: Bitmap,
        quality: Int
    ): Boolean {
        return sharedStorage.savePhoto(fileName, suffix, bitmap, quality)
    }

    override suspend fun loadPrivatePhotos(): List<PrivatePhoto> {
        return privateStorage.loadPrivatePhotos()
    }

    override suspend fun loadSharedPhotos(): List<SharedPhoto> {
        return sharedStorage.loadPhotos()
    }

    override suspend fun deletePrivatePhoto(fileName: String): Boolean {
        return privateStorage.deletePhoto(fileName)
    }

    override suspend fun deleteSharedPhoto(fileName: String): Boolean {
        return sharedStorage.deletePhoto(fileName)
    }

}