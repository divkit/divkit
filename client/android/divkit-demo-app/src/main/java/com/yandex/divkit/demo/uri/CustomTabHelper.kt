package com.yandex.divkit.demo.uri

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.utils.longToast
import kotlinx.parcelize.Parcelize

/**
 * Opens URI in a custom tab.
 */
class CustomTabHelper(private val context: Context) {

    private var serviceConnection: CustomTabsServiceConnection? = CustomTabsServiceConnectionImpl().also {
        CustomTabsClient.bindCustomTabsService(context, context.packageName, it)
    }

    private var customTabSession: CustomTabsSession? = null

    fun open(uri: Uri, colors: CustomTabColors? = null) {
        if (serviceConnection == null) {
            throw IllegalStateException("Custom tabs service is disconnected")
        }

        val intentBuilder = CustomTabsIntent.Builder(customTabSession)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intentBuilder.setToolbarColor(context.resources.getColor(R.color.colorPrimary, null))
        } else {
            intentBuilder.setToolbarColor(context.resources.getColor(R.color.colorPrimary))
        }

        if (colors != null) {
            colors.toolbarColor?.let { intentBuilder.setToolbarColor(it) }
            colors.secondaryToolbarColor?.let { intentBuilder.setSecondaryToolbarColor(it) }
        }

        try {
            intentBuilder.build().launchUrl(context, uri)
        } catch (e: ActivityNotFoundException) {
            context.longToast("Unable to open browser tab")
        }
    }

    fun unbind() {
        serviceConnection?.let {
            context.unbindService(it)
            serviceConnection = null
        }
    }

    private inner class CustomTabsServiceConnectionImpl : CustomTabsServiceConnection() {

        override fun onCustomTabsServiceConnected(componentName: ComponentName, client: CustomTabsClient) {
            client.warmup(0)
            customTabSession = client.newSession(null)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            customTabSession = null
        }
    }
}

@Parcelize
data class CustomTabColors(@ColorInt val toolbarColor: Int?, @ColorInt val secondaryToolbarColor: Int?) : Parcelable
