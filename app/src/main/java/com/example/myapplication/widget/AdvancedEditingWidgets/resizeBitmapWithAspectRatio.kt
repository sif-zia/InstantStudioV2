package com.example.myapplication.widget.AdvancedEditingWidgets

import android.graphics.Bitmap

fun resizeBitmapWithAspectRatio(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {

    val (scaledWidth, scaledHeight) = calculateScaledDimensions(bitmap.width.toFloat(), bitmap.height.toFloat(), newWidth, newHeight)
    return Bitmap.createScaledBitmap(bitmap, scaledWidth.toInt(), scaledHeight.toInt(), true)
}
