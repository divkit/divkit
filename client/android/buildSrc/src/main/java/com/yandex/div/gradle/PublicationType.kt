package com.yandex.div.gradle

import java.text.SimpleDateFormat
import java.util.Date

private const val RELEASE_ARTIFACTORY_URL = "https://artifactory.yandex.net/artifactory/yandex_mobile_releases/"
private const val SNAPSHOT_ARTIFACTORY_URL = "https://artifactory.yandex.net/artifactory/yandex_mobile_snapshots/"

private val VERSION_DATE_FORMAT = SimpleDateFormat("yyyyMMdd.HHmmss")

enum class PublicationType(val artifactoryUrl: String) {

    dev(SNAPSHOT_ARTIFACTORY_URL) {
        override fun getVersionSuffix(properties: Map<String, Any>): String {
            val now = Date()
            return "-dev.${VERSION_DATE_FORMAT.format(now)}"
        }
    },

    release(RELEASE_ARTIFACTORY_URL) {
        override fun getVersionSuffix(properties: Map<String, Any>) = ""
    };

    // TODO: delete unused parameter
    abstract fun getVersionSuffix(properties: Map<String, Any>): String

    companion object {

        @JvmStatic
        fun fromString(string: String?): PublicationType {
            return try {
                valueOf(string!!)
            } catch (ignored: IllegalArgumentException) {
                return dev
            } catch (ignored: NullPointerException) {
                return dev
            }
        }
    }

}
