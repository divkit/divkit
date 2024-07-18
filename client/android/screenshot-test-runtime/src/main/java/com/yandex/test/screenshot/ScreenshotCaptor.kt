package com.yandex.test.screenshot

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.MainThread
import androidx.test.uiautomator.UiDevice
import java.io.File
import java.io.FileOutputStream
import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean

class ScreenshotCaptor {
    /**
     * @return collection of relative screenshot paths
     */
    @MainThread
    fun takeScreenshots(
        device: UiDevice,
        view: View,
        suiteName: String,
        name: String
    ): Collection<String> {
        val alreadySaved = propertiesSaved.getAndSet(true)
        if (!alreadySaved) {
            saveDeviceProperties(view.context)
        }

        prepareDirectories(suiteName)

        takeViewRender(view, outputFile = ScreenshotType.ViewRender.asFile(suiteName, name))
        takeViewPixelCopy(view, outputFile = ScreenshotType.ViewPixelCopy.asFile(suiteName, name))
        takeDeviceScreenshot(device, outputFile = ScreenshotType.Device.asFile(suiteName, name))

        return listOf(
            ScreenshotType.ViewRender.relativeScreenshotPath(suiteName, name),
            ScreenshotType.ViewPixelCopy.relativeScreenshotPath(suiteName, name)
        )
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun saveDeviceProperties(context: Context) {
        val specs = DeviceSpecs(context)
        val properties = Properties().apply {
            put("apiLevel", Build.VERSION.SDK_INT.toString())
            put("displayWidth", specs.displayWidth.toString())
            put("displayHeight", specs.displayHeight.toString())
            put("displayDensity", specs.density.toString())
        }

        val propertiesWriter = File(rootDir, "device.properties").bufferedWriter()
        propertiesWriter.use {
            properties.store(it, null)
        }
    }

    @MainThread
    fun takeViewRender(view: View, outputFile: File) {
        val bitmap = ViewRasterizer.rasterize(view)
        Log.i(TAG, "saving view render to ${outputFile.absolutePath}")

        bitmap.save(outputFile)
    }

    @MainThread
    fun takeViewPixelCopy(view: View, outputFile: File) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val window = view.context.asActivity().window
        val pixelCopy = ViewRasterizer.pixelCopy(window, view)
        Log.i(TAG, "saving view pixel copy to ${outputFile.absolutePath}")

        pixelCopy.save(outputFile)
    }

    fun takeDeviceScreenshot(device: UiDevice, outputFile: File) {
        device.waitForIdle(IDLE_TIMEOUT)
        Log.i(TAG, "saving device screenshot to ${outputFile.absolutePath}")
        device.takeScreenshot(outputFile)
    }

    private fun Context.asActivity(): Activity {
        return when (this) {
            is Activity -> this
            is ContextWrapper -> baseContext.asActivity()
            else -> throw ClassCastException()
        }
    }

    companion object {

        private const val TAG = "ScreenshotCapture"
        private const val IDLE_TIMEOUT = 2000L

        val rootDir by lazy {
            File("${Environment.getExternalStorageDirectory()}/${ScreenshotBuildSpecs.screenshotRootDir}").apply {
                mkdirs()
            }
        }

        private fun ScreenshotType.asFile(suiteName: String, fileName: String): File {
            return File(rootDir, relativeScreenshotPath(suiteName, fileName))
        }

        private fun prepareDirectories(suiteName: String) {
            ScreenshotType.values()
                .map { File(rootDir, it.relativeDirPath(suiteName)) }
                .forEach { it.mkdirs() }
        }

        private val propertiesSaved = AtomicBoolean(false)

        private fun Bitmap.save(outputFile: File) {
            val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSLESS
            } else {
                Bitmap.CompressFormat.PNG
            }

            outputFile.outputStream().use {
                compress(format, 100, it)
            }
        }
    }
}
