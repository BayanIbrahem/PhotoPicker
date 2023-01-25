package com.example.photopicker.data.utils

enum class PhotoSuffix (
    val suffixString: String,
    val mimeType: String,
){
    JPEG(suffixString = "jpeg", "image/jpg"),
    PNG(suffixString = "png", "image/png"),
    ALL("*", "image/*"),
    NONE("", "*/*"),
}