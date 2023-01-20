package com.yandex.div.gradle

import org.gradle.api.Project

class Version private constructor(
    val majorVersion: Int,
    val minorVersion: Int,
    val fixVersion: Int
) {

    val versionCode = computeVersionCode(majorVersion, minorVersion, fixVersion)
    val versionName =  "$majorVersion.$minorVersion.$fixVersion"

    var buildNumber = 0
        private set

    constructor(
        project: Project,
        majorVersion: Int,
        minorVersion: Int,
        fixVersion: Int
    ) : this(majorVersion, minorVersion, fixVersion) {
        if (project.hasProperty("teamcity.version")) {
            buildNumber = Integer.parseInt(project.properties["build.number"] as String)
        } else {
            val tsrBuildNumber = System.getenv("BUILD_NUMBER")
            buildNumber = tsrBuildNumber?.toInt() ?: 0
        }
    }

    companion object {

        fun computeVersionCode(majorVersion: Int, minorVersion: Int, fixVersion: Int): Int {
            require(minorVersion in 0..999) { "Minor version value must be between 0 and 999" }
            require(fixVersion in 0..999) { "Fix version value must be between 0 and 999" }
            return majorVersion * 1_000_000 + minorVersion * 1_000 + fixVersion
        }
    }
}
