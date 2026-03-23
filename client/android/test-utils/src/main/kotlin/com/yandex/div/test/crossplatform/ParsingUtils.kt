package com.yandex.div.test.crossplatform

import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.internal.util.map
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
