package com.yandex.test.screenshot

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.annotation.MainThread
import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean

object ScreenshotCaptor {
    private val propertiesSaved = AtomicBoolean(false)

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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ViewRasterizer
                .rasterize(view)
                .save(ScreenshotType.ViewRender.asFile(suiteName, name))
            return listOf(ScreenshotType.ViewRender.relativeScreenshotPath(suiteName, name))
        } else {
            val window = view.context.asActivity().window
            ViewRasterizer
                .pixelCopy(window, view)
                .save(ScreenshotType.ViewPixelCopy.asFile(suiteName, name))
            return listOf(ScreenshotType.ViewPixelCopy.relativeScreenshotPath(suiteName, name))
        }
    }

    private fun saveDeviceProperties(context: Context) {
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

    private fun Context.asActivity(): Activity {
        return when (this) {
            is Activity -> this
            is ContextWrapper -> baseContext.asActivity()
            else -> throw ClassCastException()
        }
    }

    private fun ScreenshotType.asFile(suiteName: String, fileName: String): TestFile {
        return TestFile(relativeScreenshotPath(suiteName, fileName))
    }

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
