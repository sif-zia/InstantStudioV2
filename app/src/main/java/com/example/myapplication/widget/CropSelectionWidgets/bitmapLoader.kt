package com.example.myapplication.widget.CropSelectionWidgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun loadImageBitmap(context: Context, uri: Uri?): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        BitmapFactory.decodeStream(uri?.let { resolver.openInputStream(it) }, Rect(), BitmapFactory.Options())?.asImageBitmap()
    }
}

suspend fun loadBitmap(context: Context, uri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        BitmapFactory.decodeStream(resolver.openInputStream(uri), Rect(), BitmapFactory.Options())
    }
}
