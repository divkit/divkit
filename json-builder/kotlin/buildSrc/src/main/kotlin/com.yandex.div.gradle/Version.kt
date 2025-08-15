package com.yandex.div.gradle

import org.gradle.api.Project

class Version private constructor(
    val majorVersion: Int,
    val minorVersion: Int,
    val fixVersion: Int,
    val buildNumber: Int
) {

    val versionCode = computeVersionCode(majorVersion, minorVersion, fixVersion)
    val versionName =  "$majorVersion.$minorVersion.$fixVersion"

    constructor(
        project: Project,
        majorVersion: Int,
        minorVersion: Int,
        fixVersion: Int
    ) : this(majorVersion, minorVersion, fixVersion, buildNumber(project))

    companion object {

        fun buildNumber(project: Project): Int {
            val buildNumber = project.findProperty("buildNumber")?.toString() ?: System.getenv("BUILD_NUMBER")
            return buildNumber?.toInt() ?: 0
        }

        fun computeVersionCode(majorVersion: Int, minorVersion: Int, fixVersion: Int): Int {
            require(minorVersion in 0..999) { "Minor version value must be between 0 and 999" }
            require(fixVersion in 0..999) { "Fix version value must be between 0 and 999" }
            return majorVersion * 1_000_000 + minorVersion * 1_000 + fixVersion
        }
    }
}
