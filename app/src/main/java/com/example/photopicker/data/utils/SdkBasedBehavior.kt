package com.example.photopicker.data.utils

import android.os.Build

inline fun <T> ifSdk29DoUnless (requiredSdkAction: () -> T): T? {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return requiredSdkAction()
    } else null
}