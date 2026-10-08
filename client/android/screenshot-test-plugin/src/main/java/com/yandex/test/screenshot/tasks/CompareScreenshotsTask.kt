package com.yandex.test.screenshot.tasks

import com.android.build.api.variant.Variant
import com.android.builder.core.BuilderConstants
import com.google.gson.Gson
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

    @get:Input
    abstract val ignoreFailures: Property<Boolean>

    @get:Input
    abstract val selectedReferencePrefix: Property<String>

    @get:Internal
    abstract val reportDir: DirectoryProperty

    @get:OutputDirectory
    abstract val allureResultsDir: DirectoryProperty

    @get:OutputDirectory
    abstract val comparisonDir: DirectoryProperty

    @get:OutputDirectory
    abstract val collectedDir: DirectoryProperty

    private lateinit var allureResults: ScreenshotAllureResults

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
        allureResultsDir.asFile.get().apply {
            deleteRecursively()
            mkdirs()
        }

        val updates = mutableMapOf<ReferencePath, String>()
        val screenshotDirs = screenshotDir.asFile.get().listFiles { file -> file.isDirectory }!!
        val allSuccessful = screenshotDirs.map { screenshotDirFile ->
            val device = screenshotDirFile.toPath().last().name
            val properties = if (File(screenshotDirFile, "device.properties").isFile) {
                readDeviceProperties(screenshotDirFile)
            } else {
                null
            }
            val apiLevel = properties?.getProperty("apiLevel")
            allureResults = ScreenshotAllureResults(screenshotDirFile, allureResultsDir.get().asFile, apiLevel)
            val testFailures = allureResults.failedTests
            testFailures.forEach { result ->
                logger.e("${result.fullName ?: result.name}: ${result.statusDetails?.message.orEmpty()}")
            }
            if (properties == null) {
                allureResults.write()
                val message = "No screenshots were produced for $device"
                logger.e(message)
                if (!ignoreFailures.get()) throw GradleException(message)
                return@map false
            }
            val deviceReferenceDir = referencesDir.dir(deviceDescription(properties)).get().asFile

            logger.i("Screenshots comparison for $device started")
            logger.i("\tscreenshots from: $screenshotDirFile")
            logger.i("\treferences from: $deviceReferenceDir")

            val referenceOverrides = ReferenceFileReader(screenshotDirFile)
            val comparator = ImageComparator(logger)

            loadExplicitScreenshotMatchMap(referenceOverrides)

            val successful = listOf(
                testFailures.isEmpty(),
                processNewScreenshots(
                    referenceOverrides,
                    screenshotDirFile,
                    deviceReferenceDir,
                    comparableCategories.get(),
                    updates,
                ),
                processSkippedReferences(
                    referenceOverrides,
                    screenshotDirFile,
                    deviceReferenceDir,
                    comparableCategories.get(),
                    updates,
                ),
                processDifferentScreenshots(
                    referenceOverrides,
                    comparator,
                    screenshotDirFile,
                    deviceReferenceDir,
                    comparableCategories.get(),
                    updates,
                )
            ).all { it }

            allureResults.write()
            logger.i("Screenshot comparison for $device finished: successful=$successful")
            successful
        }.all { it }

        comparisonDir.file("updates.json").get().asFile.apply {
            parentFile.mkdirs()
            writeText(Gson().toJson(updates))
        }
        if (!allSuccessful && !ignoreFailures.get()) {
            throw GradleException("error processing images, see log messages above")
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
        updates: MutableMap<ReferencePath, String>,
    ): Boolean {
        val newScreenshots = mutableListOf<String>()
        categories.forEach { category ->
            val src = File(screenshotDir, category)
            newScreenshots += enumerateImagesRelative(src)
                .map { image -> "$category/$image" }
                .filter { image ->
                    val reference = referenceOverrides.resolveReferencePath(image) ?: image
                    !File(referenceDir, reference).exists()
                }
        }

        newScreenshots.forEach { image ->
            addReferenceUpdate(referenceOverrides, image, updates)
            allureResults.addMissingReference(image, File(screenshotDir, image))
        }

        if (newScreenshots.isNotEmpty()) {
            logger.w("${newScreenshots.size} new images:\n\t${newScreenshots.joinToString("\n\t")}")
            if (strictComparison.get()) {
                return false
            }
        }
        return true
    }

    private fun processSkippedReferences(
        referenceOverrides: ReferenceFileReader,
        screenshotDir: File,
        referenceDir: File,
        categories: List<String>,
        updates: MutableMap<ReferencePath, String>,
    ): Boolean {
        val producedReferences = categories.flatMap { category ->
            enumerateImagesRelative(File(screenshotDir, category)).flatMap { image ->
                val path = "$category/$image"
                val reference = referenceOverrides.resolveReferencePath(path) ?: path
                listOf(path, reference)
            }
        }.toSet()
        val skippedScreenshots = categories.flatMap { category ->
            enumerateImagesRelative(File(referenceDir, category)).map { image -> "$category/$image" }
        }.filterNot { it in producedReferences }

        val requiredReferences = requiredSkippedReferences(skippedScreenshots, selectedReferencePrefix.get())
        requiredReferences.forEach { image ->
            val skippedPath = "skipped/$image"
            File(referenceDir, image).copyTo(comparisonDir.file(skippedPath).get().asFile)
            updates[image] = skippedPath
        }
        skippedScreenshots.forEach { image ->
            allureResults.addMissingScreenshot(image, required = image in requiredReferences)
        }

        if (skippedScreenshots.isNotEmpty()) {
            logger.w("${skippedScreenshots.size} skipped references")
        }
        if (requiredReferences.isNotEmpty()) {
            logger.w("${requiredReferences.size} required references were not produced:\n\t" +
                requiredReferences.joinToString("\n\t"))
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
        updates: MutableMap<ReferencePath, String>,
    ): Boolean {
        val differentScreenshotDir = comparisonDir.dir("diff").get().asFile

        val differentScreenshots = mutableListOf<ScreenshotPair>()
        val referenceMap = mutableMapOf<ActualPath, ReferencePath>()
        val actualScreenshotPaths = mutableSetOf<String>()

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
            } else {
                // compare success
                allureResults.addMatch(pair.actual)
            }
        }

        differentScreenshots.forEach { pair ->
            addReferenceUpdate(referenceOverrides, pair.actual, updates)
            val actualFile = File(screenshotDir, pair.actual)
            val expectedFile = File(referenceDir, pair.reference)

            createDiff(comparator, actualFile, expectedFile, differentScreenshotDir, pair.actual)
            allureResults.addDifference(
                pair.actual,
                actualFile,
                expectedFile,
                File(differentScreenshotDir, pair.actual.withSuffix("_diff"))
            )
        }

        if (differentScreenshots.isNotEmpty()) {
            logger.w("${differentScreenshots.size} images differs:\n\t${differentScreenshots.joinToString("\n\t")}")
            return false
        }
        return true
    }

    private fun addReferenceUpdate(
        referenceOverrides: ReferenceFileReader,
        actualPath: ActualPath,
        updates: MutableMap<ReferencePath, String>,
    ) {
        val reference = referenceOverrides.resolveReferencePath(actualPath) ?: actualPath
        if (File(actualPath).parent == File(reference).parent) {
            val firstScreenshot = referenceOverrides.resolveFirstScreenshotPath(reference) ?: actualPath
            updates[reference] = collectedDir.file(firstScreenshot).get().asFile
                .relativeTo(comparisonDir.get().asFile).path
        }
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

    private fun deviceDescription(properties: Properties): String {
        val apiLevel = properties.getProperty("apiLevel")
        val displayWidth = properties.getProperty("displayWidth")
        val displayHeight = properties.getProperty("displayHeight")
        val displayDensity = properties.getProperty("displayDensity")
        return "API${apiLevel}_${displayDensity}_${displayWidth}x${displayHeight}"
    }

    private fun readDeviceProperties(
        screenshotDir: File
    ): Properties {
        val propertiesFile = File(screenshotDir, "device.properties")
        try {
            val properties = Properties()
            propertiesFile.bufferedReader().use { properties.load(it) }
            return properties
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

        /**
         * Location of the additional test output of `connected<Variant>AndroidTest`, relative to the
         * build directory. Mirrors what AGP composes for
         * `InternalArtifactType.CONNECTED_ANDROID_TEST_ADDITIONAL_OUTPUT`: an `OUTPUTS` artifact
         * named after the [BuilderConstants.CONNECTED] device provider, under the android test
         * component directory.
         *
         * It cannot be read from the connected test task itself: AGP wires the corresponding output
         * property from its own configuration action, which runs after the actions this plugin is
         * able to register, so the property is still unset by then.
         */
        private fun additionalTestOutputDir(variant: Variant) =
            "outputs/connected_android_test_additional_output/" +
                "${variant.name}AndroidTest/${BuilderConstants.CONNECTED}"

        @Suppress("UnstableApiUsage")
        fun register(
            project: Project,
            variant: Variant,
            extension: ScreenshotTestPluginExtension,
        ): TaskProvider<CompareScreenshotsTask> =
            project.tasks.register(
                variant.computeTaskName(action = "compare", subject = "screenshots"),
                CompareScreenshotsTask::class.java,
            ) {
                it.referencesDir.set(project.file(extension.referencesDir))
                it.comparableCategories.set(extension.comparableCategories)
                it.strictComparison.set(extension.strictComparison)
                it.ignoreFailures.set(extension.ignoreFailures)
                it.selectedReferencePrefix.set(extension.selectedReferencePrefix)
                it.screenshotDir.set(
                    project.layout.buildDirectory.dir(additionalTestOutputDir(variant))
                )
                it.reportDir.set(project.reportDir)
                it.allureResultsDir.set(project.layout.buildDirectory.dir("allure-comparison-results"))
                it.comparisonDir.set(project.reportDir.map { it.dir(extension.comparisonDir.get()) })
                it.collectedDir.set(project.reportDir.map { it.dir(extension.collectedDir.get()) })

                it.onlyIf { extension.enableComparison.get() }
            }
    }
}

internal fun requiredSkippedReferences(
    skippedReferences: Collection<String>,
    selectedReferencePrefix: String,
): List<String> {
    if (selectedReferencePrefix.isBlank()) return skippedReferences.toList()

    val selectedFile = "$selectedReferencePrefix.png"
    val selectedDirectory = "$selectedReferencePrefix/"
    return skippedReferences.filter { reference ->
        val pathWithoutCategory = reference.substringAfter('/')
        pathWithoutCategory == selectedFile || pathWithoutCategory.startsWith(selectedDirectory)
    }
}

private class ScreenshotPair(
    val actual: String,
    val reference: String,
) {
    override fun toString(): String {
        return if (actual == reference) actual else "$actual (from $reference)"
    }
}
