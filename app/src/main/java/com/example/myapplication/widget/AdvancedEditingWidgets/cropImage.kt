package com.example.myapplication.widget.AdvancedEditingWidgets

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset


fun cropImage(androidBitmap: Bitmap, top_left: Offset, cropWidth: Int, cropHeight: Int): Bitmap {
    // Convert ImageBitmap to Android Bitmap


    // Calculate the starting coordinates of the crop area
    val cropX = (top_left.x).toInt()  // Starting X position, use as is
    val cropY = (top_left.y).toInt()  // Starting Y position, use as is

    // Ensure crop area is within bounds of the original image
    val startX = cropX.coerceIn(0, androidBitmap.width - cropWidth)
    val startY = cropY.coerceIn(0, androidBitmap.height - cropHeight)

    // Crop the Bitmap
    val croppedAndroidBitmap = Bitmap.createBitmap(androidBitmap, startX, startY, cropWidth, cropHeight)

    // Convert cropped Bitmap back to ImageBitmap if necessary
    return croppedAndroidBitmap
}