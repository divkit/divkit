package com.yandex.divkit.demo.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

class DemoUriHandler(
    private val context: Context
) {

    fun handle(uri: Uri): Boolean {
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, uri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } catch (_: ActivityNotFoundException) {
            context.showToast("No one can handle: $uri")
            return false
        }
        return true
    }
}
