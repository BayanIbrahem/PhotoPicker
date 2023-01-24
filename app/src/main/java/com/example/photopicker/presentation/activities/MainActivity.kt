package com.example.photopicker.presentation.activities

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photopicker.databinding.ActivityMainBinding
import com.example.photopicker.domain.adapters.InternalStoragePhotoAdapter
import com.example.photopicker.domain.models.MainActivityViewModel
import com.example.photopicker.domain.utils.InternalStoragePhoto
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var internalStoragePhotoAdapter: InternalStoragePhotoAdapter
    private var internalStoragePhotos: List<InternalStoragePhoto> = listOf()

    private lateinit var viewModel: MainActivityViewModel
    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        bitmap ->
        if (binding.switchPrivate.isChecked && bitmap != null) { // internal storage:
            lifecycleScope.launchWhenCreated() {
                withContext(Dispatchers.IO) {
                    viewModel.internalStorageManagerRepo.savePhoto(UUID.randomUUID().toString(), bitmap = bitmap)
                    viewModel.reloadInternalStoragePhotos()
                }
            }
        } else { // external storage

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()

        loadPrivatePhotosToRv()

        setViewClickListeners()
    }
    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.internalStoragePhotos.observe(this) {
            newPhotos ->
            internalStoragePhotos = newPhotos
            internalStoragePhotoAdapter.photos = internalStoragePhotos
            internalStoragePhotoAdapter.notifyDataSetChanged()
        }
    }
    private fun loadPrivatePhotosToRv() {
        internalStoragePhotoAdapter = InternalStoragePhotoAdapter {
            photo ->
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.internalStorageManagerRepo.deletePhoto(photo.name)
                withContext(Dispatchers.Main) {
                    loadPrivatePhotosToRv()
                }
            }
        }
        internalStoragePhotoAdapter.photos = internalStoragePhotos
        viewModel.reloadInternalStoragePhotos()
        binding.rvPrivatePhotos.adapter = internalStoragePhotoAdapter
        binding.rvPrivatePhotos.layoutManager = GridLayoutManager(this, 3)
    }
    private fun setViewClickListeners() {
        binding.btnTakePhoto.setOnClickListener {
            takePicturePreview.launch(null)
        }
    }
}