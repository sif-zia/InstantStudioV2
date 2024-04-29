package com.example.myapplication.widget.AdvancedEditingWidgets

import android.graphics.Bitmap

fun resizeBitmapWithAspectRatio(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
    val scaledWidth: Int
    val scaledHeight: Int

    if (ratio > 1) {
        // Landscape image
        scaledWidth = newWidth
        scaledHeight = (newWidth / ratio).toInt()
    } else {
        // Portrait or square image
        scaledWidth = (newHeight * ratio).toInt()
        scaledHeight = newHeight
    }

    return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
}