package com.example.photopicker.domain.utils

import android.net.Uri

data class SharedPhoto(
    val id: Long,
    val name: String,
    val width: Int,
    val height: Int,
    val contentUri: Uri
)
