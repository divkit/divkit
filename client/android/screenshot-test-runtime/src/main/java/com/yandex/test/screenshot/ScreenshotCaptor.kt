package com.yandex.test.screenshot

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import com.yandex.test.util.performOnMain

object ScreenshotCaptor {
    /**
     * @param fromScreen crop the view area from a screenshot to include popups above the activity
     * @return collection of relative screenshot paths
     */
    fun takeScreenshots(
        view: View,
        suiteName: String,
        name: String,
        fromScreen: Boolean = false
    ): Collection<String> {
        val bitmap = when {
            fromScreen -> ViewRasterizer.screenCopy(view)
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> performOnMain {
                ViewRasterizer.rasterize(view)
            }
            else -> performOnMain {
                ViewRasterizer.pixelCopy(view.context.asActivity().window, view)
            }
        }
        val type = if (fromScreen || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ScreenshotType.ViewPixelCopy
        } else {
            ScreenshotType.ViewRender
        }
        bitmap.save(type.asFile(suiteName, name))
        return listOf(type.relativeScreenshotPath(suiteName, name))
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
