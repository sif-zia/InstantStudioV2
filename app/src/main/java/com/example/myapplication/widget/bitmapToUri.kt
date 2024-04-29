package com.example.myapplication.widget

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.example.myapplication.R

fun bitmapToUri(context: Context, bitmap: ImageBitmap): Uri {
    // Generate a unique file name for the cropped image
    val timestamp = System.currentTimeMillis()
    val fileName = "instant_studio_$timestamp.png"

    // Create the file in the cache directory
    val file = File(context.cacheDir, fileName)

    // Convert ImageBitmap to Bitmap (assuming ImageBitmap is equivalent to Bitmap)
    val bitmapImage = bitmap.asAndroidBitmap()

    try {
        // Create an output stream to write the bitmap to the file
        val outputStream = FileOutputStream(file)
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // Compress and save the bitmap
        outputStream.close() // Close the output stream after saving

        // Return the URI for the saved cropped image
        return FileProvider.getUriForFile(context, "com.example.myapplication.provider", file)
    } catch (e: IOException) {
        // Handle any errors that occur during saving
        Log.e("bitmapToUri Function", "Error saving cropped image: ${e.message}")
        // Return a default URI or handle the error as appropriate for your app
        return Uri.EMPTY
    }
}
