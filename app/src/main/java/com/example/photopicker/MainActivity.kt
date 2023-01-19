package com.example.photopicker

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photopicker.data.storage.internalStorage.InternalStorageManager
import com.example.photopicker.databinding.ActivityMainBinding
import com.example.photopicker.domain.adapters.InternalStoragePhotoAdapter
import com.example.photopicker.domain.unit_classes.InternalStoragePhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var internalStoragePhotoAdapter: InternalStoragePhotoAdapter
    private var internalStoragePhotos: List<InternalStoragePhoto> = listOf()

    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        bitmap ->
        if (binding.switchPrivate.isChecked && bitmap != null) { // internal storage:
            lifecycleScope.launchWhenCreated {
                withContext(Dispatchers.IO) {
                    InternalStorageManager.savePhoto(this@MainActivity, UUID.randomUUID().toString(), bitmap = bitmap)
                    loadPrivatePhotosFromStorage()
                }
            }
        } else { // external storage

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPrivatePhotosToRv() // internalStoragePhotos will be loaded here too.

        setViewClickListeners()
    }
    private fun loadPrivatePhotosFromStorage() {
        lifecycleScope.launchWhenCreated {
            internalStoragePhotos = withContext(Dispatchers.IO) {
                InternalStorageManager.loadPhotos(context = this@MainActivity.applicationContext)
            }.also {
                internalStoragePhotoAdapter.photos = internalStoragePhotos
                internalStoragePhotoAdapter.notifyDataSetChanged()
            }
        }
    }
    private fun setupPrivatePhotosToRv() {
        internalStoragePhotoAdapter = InternalStoragePhotoAdapter {
            // onLongClickListener - delete photos...
                photo ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    InternalStorageManager.deletePhoto(this@MainActivity, photo.name)
                }
            }
            loadPrivatePhotosFromStorage()
        }
        internalStoragePhotoAdapter.photos = internalStoragePhotos

        binding.rvPrivatePhotos.adapter = internalStoragePhotoAdapter
        binding.rvPrivatePhotos.layoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
//        binding.rvPrivatePhotos.layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
    }
    private fun setViewClickListeners() {
        binding.btnTakePhoto.setOnClickListener {
            view ->
            takePicturePreview.launch(null)
        }

    }
}