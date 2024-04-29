package com.example.myapplication.widget.AdvancedEditingWidgets

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import io.ak1.drawbox.DrawBoxPayLoad

fun modifyBitmap(sourceImgBitmap: Bitmap, drawPath: DrawBoxPayLoad?, penColor: Color, penSize: Float): Bitmap {
    val mutableBitmap = sourceImgBitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)
    val paint = Paint().apply {
        color = android.graphics.Color.argb(
            255,
            (penColor.red * 255).toInt(),
            (penColor.green * 255).toInt(),
            (penColor.blue * 255).toInt()
        )
        strokeWidth = penSize
        style = Paint.Style.STROKE
    }

    drawPath?.let { path ->
        if(path.path.isNotEmpty()) {
            path.path.forEach { pathLines ->
                val path = android.graphics.Path().apply {
                    moveTo(pathLines.points.first().x, pathLines.points.first().y)
                    pathLines.points.forEach {
                        lineTo(it.x, it.y)
                    }
                }
                canvas.drawPath(path, paint)
            }
        }
    }

    return mutableBitmap
}