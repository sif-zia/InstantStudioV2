package com.example.myapplication.widget.FGandBGWidgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.myapplication.imageUri
import java.io.ByteArrayOutputStream


fun compressImageToByteArray(context: Context): ByteArray? {
    return try {
        // Open input stream for the image
        val imageStream = imageUri?.let { context.contentResolver.openInputStream(it) }

        // Decode the stream into a bitmap
        val selectedImage = BitmapFactory.decodeStream(imageStream)

        // Compress the bitmap into a PNG format with 100% quality
        val byteArrayOutputStream = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        // Convert compressed bitmap to byte array
        byteArrayOutputStream.toByteArray()
    } catch (e: Exception) {
        // Handle any exceptions, such as IOException or decoding errors
        e.printStackTrace()
        null
    }
}