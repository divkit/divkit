package com.yandex.test.screenshot.tasks

import com.yandex.test.screenshot.ScreenshotTestPluginExtension
import com.yandex.test.util.FileOutput
import com.yandex.test.util.Logger
import com.yandex.test.util.StreamOutput
import com.yandex.test.util.reportDir
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.work.DisableCachingByDefault
import java.io.File
import java.io.IOException
import java.util.Properties
import kotlin.io.path.name

private typealias ActualPath = String
private typealias ReferencePath = String

@DisableCachingByDefault
abstract class CompareScreenshotsTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val referencesDir: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val screenshotDir: DirectoryProperty

    @get:Input
    abstract val comparableCategories: ListProperty<String>

    @get:Input
    abstract val strictComparison: Property<Boolean>

    @get:Internal
    abstract val reportDir: DirectoryProperty

    @get:OutputDirectory
    abstract val comparisonDir: DirectoryProperty

    @get:OutputDirectory
    abstract val collectedDir: DirectoryProperty

    private val logger: Logger by lazy {
        val logFile = reportDir.file("screenshot-comparison.log").get().asFile.apply { delete() }
        Logger(TAG, StreamOutput(), FileOutput(logFile))
    }

    init {
        group = "verification"
    }

    @TaskAction
    fun perform() {
        collectedDir.asFile.get().deleteRecursively()
        screenshotDir.asFile.get().listFiles()?.forEach { it.copyRecursively(collectedDir.asFile.get()) }

        comparisonDir.asFile.get().deleteRecursively()

        val screenshotDirs = screenshotDir.asFile.get().listFiles { file -> file.isDirectory }!!

        screenshotDirs.forEach { screenshotDirFile ->
            val device = screenshotDirFile.toPath().last().name
            val deviceReferenceDir =
                referencesDir.dir(deviceDescription(screenshotDirFile)).get().asFile

            logger.i("Screenshots comparison for $device started")
            logger.i("\tscreenshots from: $screenshotDirFile")
            logger.i("\treferences from: $deviceReferenceDir")

            val referenceOverrides = ReferenceFileReader(screenshotDirFile)
            val comparator = ImageComparator(logger)

            loadExplicitScreenshotMatchMap(referenceOverrides)

            val successful = listOf(
                processNewScreenshots(
                    referenceOverrides,
                    screenshotDirFile,
                    deviceReferenceDir,
                    comparableCategories.get()
                ),
                processSkippedReferences(
                    screenshotDirFile,
                    deviceReferenceDir,
                    comparableCategories.get()
                ),
                processDifferentScreenshots(
                    referenceOverrides,
                    comparator,
                    screenshotDirFile,
                    deviceReferenceDir,
                    comparableCategories.get()
                )
            ).all { it }

            if (!successful) {
                throw GradleException("error processing images, see log messages above")
            }
            logger.i("Screenshot comparison for $device finished successfully")
        }
    }

    private fun loadExplicitScreenshotMatchMap(referenceOverrides: ReferenceFileReader) {
        try {
            referenceOverrides.load()
        } catch (e: IOException) {
            throw GradleException("Failed to read references file!", e)
        }
    }

    private fun processNewScreenshots(
        referenceOverrides: ReferenceFileReader,
        screenshotDir: File,
        referenceDir: File,
        categories: List<String>,
    ): Boolean {
        val newScreenshotDir = comparisonDir.dir("new").get().asFile

        val newScreenshots = mutableListOf<String>()
        categories.forEach { category ->
            val src = File(screenshotDir, category)
            val dst = File(referenceDir, category)
            newScreenshots += enumerateNewImages(src, dst)
                .map { image -> "$category/$image" }
                .filter { !hasManualReference( referenceOverrides, it, referenceDir) }
        }

        newScreenshots.forEach { image ->
            File(screenshotDir, image).copyIfExists(File(newScreenshotDir, image))
        }

        if (newScreenshots.isNotEmpty()) {
            logger.w("${newScreenshots.size} new images:\n\t${newScreenshots.joinToString("\n\t")}")
            if (strictComparison.get()) {
                return false
            }
        }
        return true
    }

    private fun hasManualReference(referenceOverrides: ReferenceFileReader, relativePath: String, referenceDir: File): Boolean {
        val match = referenceOverrides.resolveReferencePath(relativePath) ?: return false
        return File(referenceDir, match).exists()
    }

    private fun processSkippedReferences(screenshotDir: File, referenceDir: File, categories: List<String>): Boolean {
        val skippedScreenshotDir = comparisonDir.dir("skipped").get().asFile

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
            if (strictComparison.get()) {
                return false
            }
        }
        return true
    }

    private fun processDifferentScreenshots(
        referenceOverrides: ReferenceFileReader,
        comparator: ImageComparator,
        screenshotDir: File,
        referenceDir: File,
        categories: List<String>,
    ): Boolean {
        val differentScreenshotDir = comparisonDir.dir("diff").get().asFile

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

        differentScreenshots.forEach { pair ->
            val actualFile = File(screenshotDir, pair.actual)
            val expectedFile = File(referenceDir, pair.reference)

            createDiff(comparator, actualFile, expectedFile, differentScreenshotDir, pair.actual)
        }

        if (differentScreenshots.isNotEmpty()) {
            logger.w("${differentScreenshots.size} images differs:\n\t${differentScreenshots.joinToString("\n\t")}")
            return false
        }
        return true
    }

    private fun createDiff(
        comparator: ImageComparator,
        actualFile: File,
        expectedFile: File,
        diffDir: File,
        imagePath: String,
    ) {
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

    private fun deviceDescription(
        screenshotDir: File
    ): String {
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

        private const val TAG = "CompareScreenshotsTask"

        fun register(
            project: Project,
            extension: ScreenshotTestPluginExtension,
            variant: String,
            ): TaskProvider<CompareScreenshotsTask> =
            project.tasks.register("compare${variant}Screenshots", CompareScreenshotsTask::class.java) {
                it.referencesDir.set(project.file(extension.referencesDir))
                it.comparableCategories.set(extension.comparableCategories)
                it.strictComparison.set(extension.strictComparison)
                it.screenshotDir.set(project.layout.buildDirectory.dir(extension.screenshotDir))
                it.reportDir.set(project.reportDir)
                it.comparisonDir.set(project.reportDir.map { it.dir(extension.comparisonDir.get()) })
                it.collectedDir.set(project.reportDir.map { it.dir(extension.collectedDir.get()) })

                it.onlyIf { extension.enableComparison.get() }
            }
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
