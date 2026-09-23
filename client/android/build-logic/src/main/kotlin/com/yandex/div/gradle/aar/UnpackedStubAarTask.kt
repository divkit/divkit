package com.yandex.div.gradle.aar

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import org.intellij.lang.annotations.Language
import java.io.File

@DisableCachingByDefault
abstract class UnpackedStubAarTask : DefaultTask() {

    @get:Input abstract val aarPackage: Property<String>
    @get:Input abstract val minSdkVersion: Property<Int>
    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val outputDir = outputDir.asFile.get()
        outputDir.deleteRecursively()
        outputDir.mkdirs()

        val manifestFile = File("$outputDir/AndroidManifest.xml")
        val aarPackage = aarPackage.get()
        @Language("xml")
        val manifestText = """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package="$aarPackage">
                <uses-sdk android:minSdkVersion="${minSdkVersion.get()}"/>
            </manifest>""".trimIndent()

        manifestFile.writeText(manifestText)
    }
}
