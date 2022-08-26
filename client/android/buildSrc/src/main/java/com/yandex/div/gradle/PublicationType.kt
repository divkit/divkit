package com.yandex.div.gradle

import java.text.SimpleDateFormat
import java.util.Date

private const val RELEASE_MAVEN_CENTRAL_URL = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
private const val SNAPSHOT_MAVEN_CENTRAL_URL = "https://oss.sonatype.org/content/repositories/snapshots/"
private const val RELEASE_ARTIFACTORY_URL = "https://artifactory.yandex.net/artifactory/yandex_mobile_releases/"
private const val SNAPSHOT_ARTIFACTORY_URL = "https://artifactory.yandex.net/artifactory/yandex_mobile_snapshots/"

private val VERSION_DATE_FORMAT = SimpleDateFormat("yyyyMMdd.HHmmss")

enum class PublicationType(
    val mavenCentralUrl: String,
    val artifactoryUrl: String
) {

    dev(SNAPSHOT_MAVEN_CENTRAL_URL, SNAPSHOT_ARTIFACTORY_URL) {
        override fun getVersionSuffix(): String {
            val now = Date()
            return "-dev.${VERSION_DATE_FORMAT.format(now)}"
        }
    },

    release(RELEASE_MAVEN_CENTRAL_URL, RELEASE_ARTIFACTORY_URL) {
        override fun getVersionSuffix() = ""
    };

    abstract fun getVersionSuffix(): String

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
