package com.example.myapplication.widget.AdvancedEditingWidgets

import android.graphics.Bitmap

fun resizeBitmapWithAspectRatio(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {

    var ratio: Float
    var scaledWidth: Float
    var scaledHeight: Float

    if(bitmap.height > bitmap.width){
        ratio = bitmap.height.toFloat() / bitmap.width.toFloat()

        scaledWidth = newWidth.toFloat()
        scaledHeight = newWidth.toFloat()*ratio

        if(scaledHeight.toInt() > newHeight){
            scaledHeight = newHeight.toFloat()
            scaledWidth = newHeight.toFloat()*(1/ratio)
        }

    }
    else{
        ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
        scaledWidth = newWidth.toFloat()*ratio
        scaledHeight = newWidth.toFloat()
    }

    return Bitmap.createScaledBitmap(bitmap, scaledWidth.toInt() , scaledHeight.toInt() , true)
}