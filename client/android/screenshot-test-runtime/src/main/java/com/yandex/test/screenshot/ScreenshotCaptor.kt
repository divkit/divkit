package com.yandex.test.screenshot

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.MainThread
import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean

class ScreenshotCaptor {
    /**
     * @return collection of relative screenshot paths
     */
    @MainThread
    fun takeScreenshots(
        view: View,
        suiteName: String,
        name: String
    ): Collection<String> {
        val alreadySaved = propertiesSaved.getAndSet(true)
        if (!alreadySaved) {
            saveDeviceProperties(view.context)
        }

        takeViewRender(view, outputFile = ScreenshotType.ViewRender.asFile(suiteName, name))
        takeViewPixelCopy(view, outputFile = ScreenshotType.ViewPixelCopy.asFile(suiteName, name))

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

        val propertiesWriter = TestFile("device.properties").open().bufferedWriter()
        propertiesWriter.use {
            properties.store(it, null)
        }
    }

    @MainThread
    fun takeViewRender(view: View, outputFile: TestFile) {
        val bitmap = ViewRasterizer.rasterize(view)
        Log.i(TAG, "saving view render to ${outputFile.path}")

        bitmap.save(outputFile)
    }

    @MainThread
    fun takeViewPixelCopy(view: View, outputFile: TestFile) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val window = view.context.asActivity().window
        val pixelCopy = ViewRasterizer.pixelCopy(window, view)
        Log.i(TAG, "saving view pixel copy to ${outputFile.path}")

        pixelCopy.save(outputFile)
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

        private fun ScreenshotType.asFile(suiteName: String, fileName: String): TestFile {
            return TestFile(relativeScreenshotPath(suiteName, fileName))
        }

        private val propertiesSaved = AtomicBoolean(false)

        private fun Bitmap.save(outputFile: TestFile) {
            val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSLESS
            } else {
                Bitmap.CompressFormat.PNG
            }

            outputFile.open().use {
                compress(format, 100, it)
            }
        }
    }
}
