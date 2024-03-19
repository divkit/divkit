package com.yandex.div.gradle

import org.gradle.api.Project

class Version private constructor(
    val majorVersion: Int,
    val minorVersion: Int,
    val fixVersion: Int,
) {

    val versionCode = computeVersionCode(majorVersion, minorVersion, fixVersion)
    val baseVersionName =  "$majorVersion.$minorVersion.$fixVersion"

    var buildNumber = 0
        private set

    var releaseLibraryVersion: String = baseVersionName
        private set

    fun getVersionNameForBuildType(buildType: String): String {
        return if (buildType == "debug") {
            // releaseLibraryVersion can contains build start time (see PublicationType.dev)
            // which will be different for each build. This will cause recompilation on each build.
            baseVersionName
        } else {
            releaseLibraryVersion
        }
    }

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

        val publicationType = PublicationType.fromString(project.findProperty("publicationType") as String?)
        val regularVersion = project.findProperty("regularVersion") as String?
        releaseLibraryVersion = if (regularVersion != null) {
            "${baseVersionName}-regular-$regularVersion"
        } else {
            "${baseVersionName}${publicationType.getVersionSuffix()}"
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
