package com.yandex.divkit.demo.div

import android.os.Environment
import org.json.JSONArray
import java.io.File
import java.io.OutputStream
import java.util.concurrent.TimeUnit

/**
 * Use to save/load div data to json.
 */
class DivFileStorage {

    fun readJsonFromFile(fileName: String) =
            File(fileName)
                    .inputStream()
                    .use { it.reader().use { it.readText() } }
                    .parseJson()

    fun saveJsonToFile(divData: JSONArray) =
            TimeUnit.MILLISECONDS
                    .toSeconds(System.currentTimeMillis())
                    .toString(radix = 16)
                    .let { timeStamp -> "$filesPath/$timeStamp$JSON_FILE_EXT" }
                    .let { fileName -> File(fileName) }
                    .apply { createNewFile() }
                    .outputStream()
                    .use { divData.toStream(it) }

    private fun JSONArray.toStream(stream: OutputStream) = stream.write(toString().toByteArray())

    private val filesPath
        get() = FILES_PATH.also {
            File(it).apply { if (!exists()) mkdir() }
        }

    companion object {
        private const val JSON_FILE_EXT = ".json"
        private const val FILES_DIR = "div"
        private val EXT_STORAGE_PATH = Environment.getExternalStorageDirectory().path
        private val FILES_PATH = "$EXT_STORAGE_PATH/$FILES_DIR"
    }
}
