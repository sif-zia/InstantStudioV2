package com.example.myapplication.widget.AdvancedEditingWidgets

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset

fun pasteBitmapOverAnother(largeBitmap: Bitmap, smallBitmap: Bitmap, offset: Offset): Bitmap {
    // Create a mutable bitmap to draw the combined image
    val resultBitmap = Bitmap.createBitmap(largeBitmap.width, largeBitmap.height, largeBitmap.config)

    // Canvas to draw the new combined bitmap
    val canvas = android.graphics.Canvas(resultBitmap)
    canvas.drawBitmap(largeBitmap, 0f, 0f, null)  // Draw the large bitmap first
    canvas.drawBitmap(smallBitmap, offset.x, offset.y, null)  // Draw the small bitmap at the specified offset

    return resultBitmap
}