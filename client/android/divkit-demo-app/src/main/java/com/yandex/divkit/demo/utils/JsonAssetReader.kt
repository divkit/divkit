package com.yandex.divkit.demo.utils

import android.content.Context
import androidx.annotation.WorkerThread
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

internal class JsonAssetReader(
    private val context: Context
) {

    @WorkerThread
    @Throws(IOException::class)
    fun readBytes(filename: String): ByteArray {
        val stream = context.assets.open(filename)
        stream.use {
            return stream.readBytes()
        }
    }

    @WorkerThread
    @Throws(IOException::class)
    fun readText(filename: String): String {
        return readBytes(filename).toString(charset = Charsets.UTF_8)
    }

    @WorkerThread
    @Throws(IOException::class, JSONException::class)
    fun readJson(filename: String): JSONObject {
        return JSONObject(readText(filename))
    }
}
