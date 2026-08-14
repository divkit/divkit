package com.yandex.divkit.regression.utils

import android.content.Context
import org.json.JSONObject

class AssetReader(private val context: Context) {

    fun readBytes(fileName: String): ByteArray {
        return context.assets.open(fileName)
            .use { stream -> stream.readBytes() }
    }

    fun readString(fileName: String): String {
        return readBytes(fileName).toString(charset = Charsets.UTF_8)
    }

    fun readJson(fileName: String): JSONObject {
        return JSONObject(readString(fileName))
    }
}
