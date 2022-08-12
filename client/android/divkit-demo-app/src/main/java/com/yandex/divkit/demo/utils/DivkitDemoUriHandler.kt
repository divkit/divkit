package com.yandex.divkit.demo.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import com.yandex.div.core.util.KLog
import com.yandex.div.core.interfaces.UriHandler
import com.yandex.div.video.custom.VideoCustomUriHandler
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.uri.CustomTabColors
import com.yandex.divkit.demo.uri.startBrowserActivity
import org.jetbrains.anko.newTask
import java.lang.ref.WeakReference

private const val TAG = "UriHandler"

open class DivkitDemoUriHandler(context: Context) :
    UriHandler {

    private val defaultUriHandler = DefaultUriHandler(context)
    private val browserUriHandler = BrowserUriHandler(context)
    private val httpUriHandler = HttpUriHandler(context)
    private val viewportUriHandler = ViewportUriHandler(this)
    private val yellowSkinUriHandler = YellowSkinUriHandler(context)
    private val videoCustomUriHandler = VideoCustomUriHandler(Container.videoCustomViewController)

    private var _handlingActivity: WeakReference<Activity?>? = null

    var handlingActivity
        get() = _handlingActivity?.get()
        set(value) {
            _handlingActivity = WeakReference(value)
        }

    fun handle(uri: Uri, returnIntent: Intent? = null): Boolean {
        KLog.d(TAG) { uri.toString() }

        val isHandled = when (uri.scheme) {
            "browser" -> browserUriHandler.handle(uri, returnIntent)
            "http", "https" -> httpUriHandler.handle(uri, returnIntent)
            "viewport" -> viewportUriHandler.handle(uri, returnIntent)
            "yellowskin" -> yellowSkinUriHandler.handle(uri, returnIntent)
            "custom_video" -> videoCustomUriHandler.handleUri(uri)
            else -> false
        }

        if (isHandled) {
            handlingActivity?.finish()
        }

        return isHandled || defaultUriHandler.handle(uri)
    }

    override fun handleUri(uri: Uri): Boolean {
        return handle(uri)
    }
}

class DefaultUriHandler(private val context: Context) {

    fun handle(uri: Uri): Boolean {
        val url = uri.toString()
        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME).newTask()
        val resolveInfo = context.packageManager.resolveActivity(intent, 0)
        if (resolveInfo == null) {
            context.showToast("No one can handle:\n$url")
            return false
        }
        context.startActivity(intent)
        return true
    }
}

class BrowserUriHandler(private val context: Context) {

    fun handle(uri: Uri, returnIntent: Intent?): Boolean {
        val innerUrl = uri.getQueryParameter("url")
        if (innerUrl.isNullOrEmpty()) {
            return false
        }

        context.startBrowserActivity(Uri.parse(innerUrl), returnIntent)

        return true
    }
}

class HttpUriHandler(private val context: Context) {

    fun handle(uri: Uri, returnIntent: Intent?): Boolean {
        context.startBrowserActivity(uri, returnIntent)
        return true
    }
}

class ViewportUriHandler(private val uriHandler: DivkitDemoUriHandler) {

    fun handle(uri: Uri, returnIntent: Intent?): Boolean {
        val text = uri.getQueryParameterSafe("text")
        if (text.isNullOrEmpty()) {
            return false
        }

        val newUri = Uri.parse("https://yandex.ru/search/touch/?text=$text")
        return uriHandler.handle(newUri, returnIntent)
    }

    private fun Uri.getQueryParameterSafe(key: String) = if (isOpaque) null else getQueryParameter(key)
}

class YellowSkinUriHandler(private val context: Context) {

    fun handle(uri: Uri, returnIntent: Intent?): Boolean {
        val innerUrl = uri.getQueryParameterSafe("url")
        if (innerUrl.isNullOrEmpty()) {
            return false
        }

        val colors = CustomTabColors(
            toolbarColor = uri.getQueryParameterSafe("omnibox_color")?.let { Color.parseColor(it) },
            secondaryToolbarColor = uri.getQueryParameterSafe("background_color")?.let { Color.parseColor(it) }
        )

        context.startBrowserActivity(Uri.parse(innerUrl), returnIntent, colors)

        return true
    }

    private fun Uri.getQueryParameterSafe(key: String) = if (isOpaque) null else getQueryParameter(key)
}
