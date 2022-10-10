package com.yandex.divkit.demo.uri

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

private const val EXTRA_URI = "EXTRA_URI"
private const val EXTRA_RETURN_INTENT = "EXTRA_RETURN_INTENT"
private const val EXTRA_COLORS = "EXTRA_COLORS"

class BrowserActivity : AppCompatActivity() {

    private var customTabHelper: CustomTabHelper? = null
    private var wasBrowserOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            wasBrowserOpened = true
        }
    }

    override fun onResume() {
        super.onResume()

        if (wasBrowserOpened) {
            goBack()
            return
        }

        customTabHelper = CustomTabHelper(this).also {
            val uri = intent.getParcelableExtra<Uri>(EXTRA_URI)
            val colors = intent.getParcelableExtra<CustomTabColors>(EXTRA_COLORS)
            if (uri != null) {
                it.open(uri, colors)
            }
        }

        wasBrowserOpened = true
    }

    override fun onDestroy() {
        super.onDestroy()

        customTabHelper?.unbind()
    }

    private fun goBack() {
        val returnIntent = intent.getParcelableExtra<Intent>(EXTRA_RETURN_INTENT)
        if (returnIntent != null) {
            startActivity(returnIntent)
        }
        finish()
    }
}

fun Context.startBrowserActivity(uri: Uri, returnIntent: Intent? = null, colors: CustomTabColors? = null) {
    val intent = Intent(this, BrowserActivity::class.java).apply {
        putExtra(EXTRA_URI, uri)
        putExtra(EXTRA_RETURN_INTENT, returnIntent)
        putExtra(EXTRA_COLORS, colors)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}
