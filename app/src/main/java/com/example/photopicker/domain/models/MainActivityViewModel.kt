package com.example.photopicker.domain.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photopicker.domain.repository.InternalStorageManagerRepo
import com.example.photopicker.domain.utils.InternalStoragePhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val internalStorageManagerRepo: InternalStorageManagerRepo
): ViewModel () {
//    @Inject lateinit var internalStorageManagerRepo: InternalStorageManagerRepo

    var internalStoragePhotos : MutableLiveData<List<InternalStoragePhoto>> = MutableLiveData()
    private var reloadingInternalStoragePhotos = false

    fun getInternalStoragePhotos() : List<InternalStoragePhoto> {
        return internalStoragePhotos.value ?: listOf()
    }
    fun reloadInternalStoragePhotos() {
        if (reloadingInternalStoragePhotos){
            return
        }
        reloadingInternalStoragePhotos = true
        GlobalScope.launch {
            val photos = withContext(Dispatchers.IO) {
                internalStorageManagerRepo.loadPhotos()
            }
            withContext(Dispatchers.Main) {
                internalStoragePhotos.value = photos
            }
            reloadingInternalStoragePhotos = false
        }
    }

}