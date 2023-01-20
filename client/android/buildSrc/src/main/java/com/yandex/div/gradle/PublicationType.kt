package com.yandex.div.gradle

import java.text.SimpleDateFormat
import java.util.Date

private val VERSION_DATE_FORMAT = SimpleDateFormat("yyyyMMdd.HHmmss")

enum class PublicationType {

    dev {
        override fun getVersionSuffix() = "-dev.${VERSION_DATE_FORMAT.format(Date())}"
    },

    release {
        override fun getVersionSuffix() = ""
    };

    abstract fun getVersionSuffix(): String

    companion object {

        @JvmStatic
        fun fromString(string: String?): PublicationType {
            return try {
                valueOf(string!!)
            } catch (ignored: IllegalArgumentException) {
                return PublicationType.dev
            } catch (ignored: NullPointerException) {
                return PublicationType.dev
            }
        }
    }
}
