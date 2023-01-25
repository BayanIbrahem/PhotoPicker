package com.example.photopicker.presentation.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photopicker.databinding.ActivityMainBinding
import com.example.photopicker.domain.utils.adapters.PrivatePhotoAdapter
import com.example.photopicker.domain.models.MainActivityViewModel
import com.example.photopicker.domain.utils.PrivatePhoto
import com.example.photopicker.domain.utils.SharedPhoto
import com.example.photopicker.domain.utils.adapters.SharedPhotoAdapter
import com.example.photopicker.presentation.utils.KToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    /** binding: */
    private lateinit var binding: ActivityMainBinding

    /**viewModel: */
    private lateinit var viewModel: MainActivityViewModel

    /** adapters: */
    private lateinit var privatePhotoAdapter: PrivatePhotoAdapter
    private lateinit var sharedPhotoAdapter: SharedPhotoAdapter

    /** photos list: */
    private var privatePhotos: List<PrivatePhoto> = listOf()
    private var sharedPhotos: List<SharedPhoto> = listOf()

    /** Activity result launchers: */
    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        bitmap ->
        if (bitmap != null) {
            if (binding.switchPrivate.isChecked) { // internal storage:
                lifecycleScope.launchWhenCreated {
                    withContext(Dispatchers.IO) {
                        viewModel.storageManagerRepo.savePrivatePhoto(UUID.randomUUID().toString(), bitmap = bitmap)
                        viewModel.reloadPrivatePhotos()
                    }
                }
            } else if (viewModel.externalStorageWritePermission){ // external storage
                lifecycleScope.launchWhenCreated {
                    withContext(Dispatchers.IO) {
                        viewModel.storageManagerRepo.saveSharedPhoto(UUID.randomUUID().toString(), bitmap = bitmap)
                        KToast.show(this@MainActivity, "image saved", Toast.LENGTH_SHORT)
                        viewModel.reloadSharedPhotos()
                    }
                }
            }
        }
    }
    private val grandPermissionsLauncher = registerForActivityResult (ActivityResultContracts.RequestMultiplePermissions()) {
        permissionsResult ->
        permissionsResult.forEach { entry ->
            if (entry.key == Manifest.permission.READ_EXTERNAL_STORAGE) {
                viewModel.externalStorageReadPermission = entry.value
            }
            else if (entry.key == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                viewModel.externalStorageWritePermission = entry.value
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()

        loadPhotosToRv()

        checkExternalStoragePermissions()

        grandNeededPermissions()

        setViewClickListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        setModelLiveDataObservers()
    }
    private fun setModelLiveDataObservers() {
        /** private photos live data observer: */
        viewModel.privatePhotos.observe(this) {
                newPhotos ->
            privatePhotos = newPhotos
            privatePhotoAdapter.photos = privatePhotos
            privatePhotoAdapter.notifyDataSetChanged()
        }

        /** shared photos live data observer: */
        viewModel.sharedPhotos.observe(this) {
                newPhotos ->
            sharedPhotos = newPhotos
            sharedPhotoAdapter.photos = sharedPhotos
            sharedPhotoAdapter.notifyDataSetChanged()
        }
    }
    private fun loadPhotosToRv() {
        loadPrivatePhotosToRv()
        loadSharedPhotosToRv()
    }
    private fun loadPrivatePhotosToRv() {
        privatePhotoAdapter = PrivatePhotoAdapter {
            photo ->
            lifecycleScope.launchWhenCreated {
                viewModel.storageManagerRepo.deletePrivatePhoto(photo.name)
                withContext(Dispatchers.Main) {
                    loadPrivatePhotosToRv()
                }
            }
        }
        privatePhotoAdapter.photos = privatePhotos
        viewModel.reloadPrivatePhotos()
        binding.rvPrivatePhotos.adapter = privatePhotoAdapter
        binding.rvPrivatePhotos.layoutManager = GridLayoutManager(this, 3)
    }
    private fun loadSharedPhotosToRv() {
        sharedPhotoAdapter = SharedPhotoAdapter { photo ->
            lifecycleScope.launchWhenCreated {
                viewModel.storageManagerRepo.deleteSharedPhoto()
            }
        }
        sharedPhotoAdapter.photos = sharedPhotos
        viewModel.reloadSharedPhotos()
        binding.rvPublicPhotos.adapter = sharedPhotoAdapter
        binding.rvPublicPhotos.layoutManager = GridLayoutManager(this, 3)
    }

    private fun checkExternalStoragePermissions() {
        val readPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val writePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val isSdk29OrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        viewModel.externalStorageReadPermission = readPermission
        viewModel.externalStorageWritePermission = writePermission.or(isSdk29OrHigher)
    }
    private fun grandNeededPermissions() {
        val permissions = mutableListOf<String>()
        if (viewModel.externalStorageReadPermission.not()) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (viewModel.externalStorageWritePermission.not()) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissions.isNotEmpty()) {
            grandPermissionsLauncher.launch(permissions.toTypedArray())
        }
    }

    private fun setViewClickListeners() {
        binding.btnTakePhoto.setOnClickListener {
            takePicturePreview.launch(null)
        }
    }
}