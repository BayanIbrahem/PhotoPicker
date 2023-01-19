package com.example.photopicker.data.utils

enum class PhotoSuffix (val suffixString: String){
    JPEG(suffixString = "jpeg"),
    PNG(suffixString = "png"),
    ALL("*"),
    NONE(""),
}