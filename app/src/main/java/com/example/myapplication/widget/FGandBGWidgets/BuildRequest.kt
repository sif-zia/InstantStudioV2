package com.example.myapplication.widget.FGandBGWidgets

import androidx.compose.runtime.mutableStateOf
import okhttp3.FormBody
import okhttp3.Request


var selectedColors=null
val httpLink="https://c0e5-2400-adc5-458-8a00-893a-3dc2-7fa4-4861.ngrok-free.app"
val url=httpLink+"/req"

fun buildRequest(formBody: FormBody): Request {
    return Request.Builder()
        .url(url)
        .post(formBody)
        .build()
}