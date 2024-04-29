package com.example.myapplication.widget.AdvancedEditingWidgets

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.InputStream

fun uriToBitmap(activity: Activity, imageUri: Uri): Bitmap? {
    return try {
        val inputStream: InputStream? = activity.contentResolver.openInputStream(imageUri)
        BitmapFactory.decodeStream(inputStream).also {
            inputStream?.close()  // Ensure the inputStream is closed after use
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}