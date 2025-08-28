package com.yandex.divkit.gradle

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val VERSION_DATE_FORMAT = SimpleDateFormat("yyyyMMdd.HHmmss", Locale.US)

enum class PublicationType(val isRelease: Boolean) {

    snapshot(isRelease = false) {
        override fun getVersionSuffix() = "-${VERSION_DATE_FORMAT.format(Date())}-SNAPSHOT"
    },

    release(isRelease = true) {
        override fun getVersionSuffix() = ""
    };

    abstract fun getVersionSuffix(): String

    companion object {

        @JvmStatic
        fun fromString(string: String?): PublicationType {
            return try {
                valueOf(string!!)
            } catch (ignored: IllegalArgumentException) {
                return snapshot
            } catch (ignored: NullPointerException) {
                return snapshot
            }
        }
    }
}
