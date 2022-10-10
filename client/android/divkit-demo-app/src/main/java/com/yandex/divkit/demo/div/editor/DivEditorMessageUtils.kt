package com.yandex.divkit.demo.div.editor

import android.content.Context
import android.view.Surface
import com.yandex.divkit.demo.BuildConfig
import com.yandex.divkit.demo.utils.windowManager
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

private const val DIV_EDITOR_PREFS = "div_editor_prefs"
private const val DIV_EDITOR_FINGERPRINT_KEY = "fingerprint"

object DivEditorMessageUtils {

    fun JSONObject.addMetadata(context: Context): JSONObject {
        val display = context.windowManager.defaultDisplay
        val orientation = when (display.rotation) {
            Surface.ROTATION_90, Surface.ROTATION_270 -> "album"
            else -> "portrait"
        }
        val metaDataObject = JSONObject()
            .put("app", BuildConfig.APPLICATION_ID)
            .put("version", BuildConfig.VERSION_NAME)
            .put("os", "android")
            .put("device", "${android.os.Build.MANUFACTURER}, ${android.os.Build.MODEL}")
            .put("sdk_name", android.os.Build.VERSION.RELEASE)
            .put("sdk", "${android.os.Build.VERSION.SDK_INT}")
            .put("width", display.width)
            .put("height", display.height)
            .put("density", context.resources.displayMetrics.density)
            .put("orientation", orientation)
            .put("client_id", context.getFingerprint())

        return this.put("device", metaDataObject)
    }

    fun JSONObject.addExceptions(exceptions: List<Exception>): JSONObject {
        val errorArray = JSONArray()
        exceptions.forEach { exception: Exception ->
            val exceptionObject = JSONObject()
            exceptionObject.put("message", exception.message)
                .put("stack", exception.getStacktraceAsJsonArray())
            errorArray.put(exceptionObject)
        }

        return put("errors", errorArray)
    }

    private fun Throwable.getStacktraceAsJsonArray(stack: JSONArray = JSONArray()): JSONArray {
        stackTrace.forEach { stackTraceElement: StackTraceElement ->
            val clazz = stackTraceElement.className
            val func = stackTraceElement.methodName
            val line = "${stackTraceElement.fileName}:${stackTraceElement.lineNumber}"
            stack.put("at $clazz$$func($line)")
        }
        val cause: Throwable = cause ?: return stack
        stack.put("Caused by ${cause.message}")
        return cause.getStacktraceAsJsonArray(stack)
    }

    private fun Context.getFingerprint(): String {
        val prefs = getSharedPreferences(DIV_EDITOR_PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(DIV_EDITOR_FINGERPRINT_KEY)) {
            prefs.edit()
                .putString(DIV_EDITOR_FINGERPRINT_KEY, UUID.randomUUID().toString())
                .apply()
        }
        return prefs.getString(DIV_EDITOR_FINGERPRINT_KEY, "") as String
    }
}
