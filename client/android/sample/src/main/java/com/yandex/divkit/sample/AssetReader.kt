package com.yandex.divkit.sample

import android.content.Context
import org.json.JSONObject

class AssetReader(private val context: Context) {

    fun read(filename: String): JSONObject {
        return context.assets.open(filename)
            .bufferedReader()
            .use { JSONObject(it.readText()) }
    }
}
