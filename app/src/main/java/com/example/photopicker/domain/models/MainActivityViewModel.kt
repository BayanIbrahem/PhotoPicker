package com.example.photopicker.domain.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photopicker.data.storage.shared_storage.TAG
import com.example.photopicker.domain.repository.StorageManagerRepo
import com.example.photopicker.domain.utils.PrivatePhoto
import com.example.photopicker.domain.utils.SharedPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val storageManagerRepo: StorageManagerRepo
): ViewModel () {
    var privatePhotos : MutableLiveData<List<PrivatePhoto>> = MutableLiveData()
    var sharedPhotos : MutableLiveData<List<SharedPhoto>> = MutableLiveData()

    private var reloadingPrivatePhotos = false
    private var reloadingSharedPhotos = false

    var externalStorageReadPermission = false
    var externalStorageWritePermission = false

    fun reloadPrivatePhotos() {
        if (reloadingPrivatePhotos){
            return
        }
        reloadingPrivatePhotos = true
        viewModelScope.launch {
            val photos = withContext(Dispatchers.IO) {
                storageManagerRepo.loadPrivatePhotos()
            }
            withContext(Dispatchers.Main) {
                privatePhotos.value = photos
            }
            reloadingPrivatePhotos = false
        }
    }

    fun reloadSharedPhotos() {
        if (reloadingSharedPhotos){
            return
        }
        if (externalStorageReadPermission.not()) {
            Log.e(TAG, "Couldn't load photos without read permission!")
            return
        }
        reloadingSharedPhotos = true
        viewModelScope.launch {
            val photos = withContext(Dispatchers.IO) {
                storageManagerRepo.loadSharedPhotos()
            }
            withContext(Dispatchers.Main) {
                sharedPhotos.value = photos
            }
            reloadingSharedPhotos = false
        }

    }

}