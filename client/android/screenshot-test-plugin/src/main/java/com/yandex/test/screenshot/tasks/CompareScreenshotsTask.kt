package com.yandex.test.screenshot.tasks

import com.yandex.test.screenshot.ScreenshotTestPluginExtension
import com.yandex.test.util.FileOutput
import com.yandex.test.util.Logger
import com.yandex.test.util.StreamOutput
import com.yandex.test.util.reportDir
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException
import java.util.Properties

private typealias ActualPath = String
private typealias ReferencePath = String

open class CompareScreenshotsTask : DefaultTask() {

    private val screenshots = project.extensions.getByType(ScreenshotTestPluginExtension::class.java)

    private val screenshotDir = File(project.reportDir, screenshots.collectedDir)
    private val comparisonDir = File(project.reportDir, screenshots.comparisonDir)
    private val logFile = File(project.reportDir, "screenshot-comparison.log").apply { delete() }

    private val logger = Logger(TAG, StreamOutput(), FileOutput(logFile))
    private val comparator = ImageComparator(logger)
    private val referenceOverrides = ReferenceFileReader(screenshotDir)

    init {
        group = "verification"
        this.dependsOn(PullScreenshotsTask.NAME)
    }

    @TaskAction
    fun perform() {
        val referenceDir = File(screenshots.referencesDir, deviceDescription())

        logger.i("Screenshots comparison started")
        logger.i("\tscreenshots from: $screenshotDir")
        logger.i("\treferences from: $referenceDir")

        loadExplicitScreenshotMatchMap()

        processNewScreenshots(screenshotDir, referenceDir, screenshots.comparableCategories)
        processSkippedReferences(screenshotDir, referenceDir, screenshots.comparableCategories)
        processDifferentScreenshots(screenshotDir, referenceDir, screenshots.comparableCategories)

        logger.i("Screenshot comparison finished successfully")
    }

    private fun loadExplicitScreenshotMatchMap() {
        try {
            referenceOverrides.load()
            referenceOverrides.deleteReferenceFile()
        } catch (e: IOException) {
            throw GradleException("Failed to read references file!", e)
        }
    }

    private fun processNewScreenshots(screenshotDir: File, referenceDir: File, categories: List<String>) {
        val newScreenshotDir = File(comparisonDir, "new").apply { deleteRecursively() }

        val newScreenshots = mutableListOf<String>()
        categories.forEach { category ->
            val src = File(screenshotDir, category)
            val dst = File(referenceDir, category)
            newScreenshots += enumerateNewImages(src, dst)
                .map { image -> "$category/$image" }
                .filter { !hasManualReference(it, referenceDir) }
        }

        newScreenshots.forEach { image ->
            File(screenshotDir, image).copyIfExists(File(newScreenshotDir, image))
        }

        if (newScreenshots.isNotEmpty()) {
            logger.w("${newScreenshots.size} new images:\n\t${newScreenshots.joinToString("\n\t")}")
            if (screenshots.strictComparison) {
                throw GradleException("error processing images, see log messages above")
            }
        }
    }

    private fun hasManualReference(relativePath: String, referenceDir: File): Boolean {
        val match = referenceOverrides.resolveReferencePath(relativePath) ?: return false
        return File(referenceDir, match).exists()
    }

    private fun processSkippedReferences(screenshotDir: File, referenceDir: File, categories: List<String>) {
        val skippedScreenshotDir = File(comparisonDir, "skipped").apply { deleteRecursively() }

        val skippedScreenshots = mutableListOf<String>()
        categories.forEach { category ->
            val src = File(screenshotDir, category)
            val dst = File(referenceDir, category)
            skippedScreenshots += enumerateSkippedImages(src, dst).map { image ->
                "$category/$image"
            }
        }

        skippedScreenshots.forEach { image ->
            File(screenshotDir, image).copyIfExists(File(skippedScreenshotDir, image))
        }

        if (skippedScreenshots.isNotEmpty()) {
            logger.w("${skippedScreenshots.size} skipped references:\n\t${skippedScreenshots.joinToString("\n\t")}")
            if (screenshots.strictComparison) {
                throw GradleException("error processing images, see log messages above")
            }
        }
    }

    private fun processDifferentScreenshots(screenshotDir: File, referenceDir: File, categories: List<String>) {
        val differentScreenshotDir = File(comparisonDir, "diff").apply { deleteRecursively() }

        val differentScreenshots = mutableListOf<ScreenshotPair>()
        val referenceMap = mutableMapOf<ActualPath, ReferencePath>()
        val actualScreenshotPaths = mutableSetOf<String>()
        val statusLogger = TestCaseStatuses(screenshotDir)

        categories.forEach { category ->
            val categoryImages = enumerateImagesRelative(File(screenshotDir, category))
            actualScreenshotPaths.addAll(categoryImages.map { "$category/$it" })
        }

        actualScreenshotPaths.forEach {
            val reference = referenceOverrides.resolveReferencePath(it) ?: it
            if (File(referenceDir, reference).exists()) {
                referenceMap[it] = reference
            }
        }

        referenceMap.entries.forEach { entry ->
            val pair = ScreenshotPair(actual = entry.key, reference = entry.value)
            val actualFile = File(screenshotDir, pair.actual)
            val referenceFile = File(referenceDir, pair.reference)
            if (!comparator.compareImages(actualFile, referenceFile, pair.actual)) {
                // compare failed
                differentScreenshots += pair
                statusLogger.notifyFailed(pair.actual)
            } else {
                // compare success
                statusLogger.notifyPassed(pair.actual)
            }
        }

        PassedTestCasesWriter(screenshotDir).log(statusLogger.getPassedCases())

        differentScreenshots.forEach{ pair ->
            val actualFile = File(screenshotDir, pair.actual)
            val expectedFile = File(referenceDir, pair.reference)

            createDiff(actualFile, expectedFile, differentScreenshotDir, pair.actual)
        }

        if (differentScreenshots.isNotEmpty()) {
            logger.w("${differentScreenshots.size} images differs:\n\t${differentScreenshots.joinToString("\n\t")}")
            throw GradleException("error processing images, see log messages above")
        }
    }

    private fun createDiff(actualFile: File, expectedFile: File, diffDir: File, imagePath: String) {
        val diffFile = File(diffDir, imagePath.withSuffix("_diff"))

        comparator.createDiff(actualFile, expectedFile, diffFile, imagePath)
        actualFile.copyIfExists(File(diffDir, imagePath.withSuffix("_actual")))
        expectedFile.copyIfExists(File(diffDir, imagePath.withSuffix("_expected")))
    }

    private fun enumerateNewImages(src: File, dst: File): List<String> {
        val scrFiles = enumerateImagesRelative(src)
        val dstFiles = enumerateImagesRelative(dst)
        return (scrFiles - dstFiles).toList()
    }

    private fun enumerateSkippedImages(src: File, dst: File): List<String> {
        val scrFiles = enumerateImagesRelative(src)
        val dstFiles = enumerateImagesRelative(dst)
        return (dstFiles - scrFiles).toList()
    }

    private fun enumerateImagesRelative(dir: File): Set<String> {
        return enumerateFiles(dir) {
            if (!it.endsWith(".png")) null else it.substringAfter("${dir.absolutePath}/")
        }
    }

    private fun enumerateFiles(rootDir: File, transform: (String) -> String? = { it }): Set<String> {
        return rootDir.walk()
            .mapNotNull { transform(it.absolutePath) }
            .toSet()
    }

    private fun deviceDescription(): String {
        val propertiesFile = File(screenshotDir, "device.properties")
        try {
            val properties = Properties()
            properties.load(propertiesFile.bufferedReader())
            val apiLevel = properties.getProperty("apiLevel")
            val displayWidth = properties.getProperty("displayWidth")
            val displayHeight = properties.getProperty("displayHeight")
            val displayDensity = properties.getProperty("displayDensity")
            return "API${apiLevel}_${displayDensity}_${displayWidth}x${displayHeight}"
        } catch (e: IOException) {
            throw GradleException("Failed to read $propertiesFile", e)
        }
    }

    private fun String.withSuffix(suffix: String) = "${substringBeforeLast(".")}$suffix.${substringAfterLast(".")}"

    private fun File.copyIfExists(dst: File) {
        if (exists()) copyTo(dst)
    }

    companion object {

        const val NAME = "compareScreenshots"

        private const val TAG = "CompareScreenshotsTask"
    }
}

private class ScreenshotPair(
    val actual: String,
    val reference: String,
) {
    override fun toString(): String {
        return if (actual == reference) "" else "$actual (from $reference)"
    }
}
