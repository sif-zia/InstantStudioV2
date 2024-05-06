package com.example.myapplication.widget.FGandBGWidgets

import androidx.compose.runtime.mutableStateOf
import okhttp3.FormBody
import okhttp3.Request


var selectedColors=null
val httpLink="https://183a-2400-adc5-458-8a00-8918-3c29-6c5f-9880.ngrok-free.app"
val url=httpLink+"/req"

fun buildRequest(formBody: FormBody): Request {
    return Request.Builder()
        .url(url)
        .post(formBody)
        .build()
}