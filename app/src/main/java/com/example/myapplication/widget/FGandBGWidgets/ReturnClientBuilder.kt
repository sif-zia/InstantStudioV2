package com.example.myapplication.widget.FGandBGWidgets

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


fun returnClientBuilder(byteArray: ByteArray): OkHttpClient {
    var cbuilder = OkHttpClient.Builder()
    cbuilder.connectTimeout(60, TimeUnit.SECONDS)
    cbuilder.readTimeout(60, TimeUnit.SECONDS);
    cbuilder.writeTimeout(60, TimeUnit.SECONDS);
    return cbuilder.build()
}