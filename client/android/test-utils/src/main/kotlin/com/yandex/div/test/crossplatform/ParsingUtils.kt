package com.yandex.div.test.crossplatform

import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.internal.util.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.collections.filter
import kotlin.collections.flatMap
import kotlin.collections.forEach
import kotlin.collections.orEmpty
import kotlin.collections.partition
import kotlin.io.extension

private const val TEST_DATA_PATH = "../../../test_data/"

object ParsingUtils {

    fun <T : Any> parseFiles(
        relativePath: String,
        parseAction: (file: File, json: String) -> List<ParsingResult<T>>
    ): List<ParsingResult<T>> {
        return parseFiles(directory = File(TEST_DATA_PATH, relativePath), parseAction)
    }

    fun <T : Any> parseFiles(
        directory: File,
        parseAction: (file: File, json: String) -> List<ParsingResult<T>>
    ): List<ParsingResult<T>> {
        val results = mutableListOf<ParsingResult<T>>()
        getFiles(directory).forEach { file ->
            try {
                val json = file.readText(Charsets.UTF_8)
                results.addAll(parseAction(file, json))
            } catch (e: Exception) {
                results.add(ParsingResult.Error(fileName = file.name, error = e))
                return@forEach
            }
        }
        return results
    }

    private fun getFiles(dir: File): List<File> {
        val (directories, files) = dir.listFiles().orEmpty()
            .partition { it.isDirectory }
        return arrayListOf<File>().apply {
            addAll(files.filter { file -> file.extension == "json" })
            addAll(directories.flatMap { getFiles(it) })
        }
    }
}

val JSONObject.platforms: List<String>
    get() = getJSONArray("platforms").map { it as String }

val JSONObject.isForAndroid: Boolean
    get() = platforms.contains("android")

fun JSONArray?.toObjectList(): List<JSONObject> {
    if (this == null) {
        return emptyList()
    }
    val result = mutableListOf<JSONObject>()
    for (i in 0 until this.length()) {
        result.add(this.getJSONObject(i))
    }
    return result
}

fun parseDateTime(utcString: String): DateTime {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
    val date: Date = dateFormat.parse(utcString)!!
    return DateTime(
        timestampMillis = date.time + Calendar.getInstance().timeZone.rawOffset,
        timezone = TimeZone.getTimeZone("UTC")
    )
}
