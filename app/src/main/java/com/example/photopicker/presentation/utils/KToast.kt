package com.example.photopicker.presentation.utils

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration

object KToast {
    private var toast: Toast? = null
    fun show(context: Context, message: String, duration: Int) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                toast?.cancel()
                toast = Toast.makeText(context, message, duration)
                toast?.show()
            }
        }
    }
    fun cancel() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                toast?.cancel()
            }
        }
    }
}