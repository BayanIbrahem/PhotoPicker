package com.example.photopicker.domain.utils

import android.graphics.Bitmap

// TODO: make this photo contains quality and suffix.
data class PrivatePhoto (
    val name: String,
    val bitmap: Bitmap,
)
