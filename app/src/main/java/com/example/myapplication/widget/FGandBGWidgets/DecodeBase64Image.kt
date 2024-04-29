package com.example.myapplication.widget.FGandBGWidgets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import org.json.JSONObject


fun decodeBase64Image(jsonString: String): Bitmap? {
    try {
        // Parse the JSON string
        val jsonObject = JSONObject(jsonString)

        // Extract the modified image string
        val modifiedImageString = jsonObject.getString("modified_image")

        // Decode the Base64 string to bytes
        val imageBytes = Base64.decode(modifiedImageString, Base64.DEFAULT)

        // Decode the bytes to a bitmap
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        // Handle any exceptions, such as JSONException or decoding errors
        e.printStackTrace()
        return null
    }
}