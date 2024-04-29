package com.example.myapplication.widget.AdvancedEditingWidgets

fun calculateScaledDimensions(imageWidth: Float, imageHeight: Float, screenWidth: Int, screenHeight: Int): Pair<Float, Float> {
    val aspectRatio = imageWidth / imageHeight
    val scaledWidth: Float
    val scaledHeight: Float

    if (aspectRatio > screenWidth.toFloat() / screenHeight.toFloat()) {
        // Image is wider than screen
        scaledWidth = screenWidth.toFloat()
        scaledHeight = screenWidth.toFloat() / aspectRatio
    } else {
        // Image is taller than or equal to screen
        scaledWidth = screenHeight.toFloat() * aspectRatio
        scaledHeight = screenHeight.toFloat()
    }

    return Pair(scaledWidth, scaledHeight)
}