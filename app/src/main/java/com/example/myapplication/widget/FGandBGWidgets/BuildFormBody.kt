package com.example.myapplication.widget.FGandBGWidgets

import okhttp3.FormBody


fun buildFormBody(encoded: String, rgbArray: String, type: String): FormBody {
    val builder = FormBody.Builder()
    val map: HashMap<String, String> = hashMapOf(
        "image" to encoded,
        "rgb" to rgbArray,
        "background" to type
    )
    map.forEach { (key, value) ->
        builder.add(key, value)
    }
    return builder.build()
    /*
                val builder = FormBody.Builder()
                val map: HashMap<String, String> =
                    hashMapOf("image" to encoded, "rgb" to rgbArray, "background" to "False")
                val it = map.entries.iterator()
                while (it.hasNext()) {
                    val pair = it.next() as Map.Entry<*, *>
                    builder.add(pair.key.toString(), pair.value.toString())
                }*/
}