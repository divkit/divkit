package com.yandex.divkit.demo.screenshot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.div.divContext
import com.yandex.divkit.demo.settings.Preferences
import java.io.File

private const val TAG = "DivScreenshotActivity"

/**
 * Run:
adb shell am start -n com.yandex.divkit.demo/com.yandex.divkit.demo.screenshot.DivScreenshotActivity \
 -e DivScreenshotActivity.EXTRA_DIV_ASSET_NAME interactive_snapshot_test_data/div-container/base-properties.json \
 -e DivScreenshotActivity.EXTRA_SUITE_NAME com.yandex.morda.div.Div2InteractiveScreenshotTest/div-container/base-properties
 */
class DivScreenshotActivity : AppCompatActivity() {

    private val assetReader = DivAssetReader(this)
    private lateinit var divContext: Div2Context

    private val cardAssetName: String
        get() = intent.extras?.getString(EXTRA_DIV_ASSET_NAME) ?: throw IllegalArgumentException("Missing div asset name")

    private val templatesAssetName: String
        get() = cardAssetName.substringBeforeLast(File.separator) + "${File.separator}templates.json"

    private val imageLoaderName: String?
        get() = intent?.extras?.getString(EXTRA_DIV_IMAGE_LOADER_NAME)

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                REBIND_DIV_WITH_SAME_DATA_ACTION -> rebindDivWithSameData()
            }
        }
    }

    private val receiverIntentFilter = IntentFilter().apply {
        addAction(REBIND_DIV_WITH_SAME_DATA_ACTION)
    }

    lateinit var divView: Div2View
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        setImageLoader()
        divContext = divContext(activity = this)
        super.onCreate(savedInstanceState)

        var divJson = getTestCaseJson()
        if (divJson.has("div_data")) {
            divJson = divJson.getJSONObject("div_data")
        }

        val onBound: (Div2View) -> Unit = { divView ->
            this.divView = divView
            divView.apply {
                val divViewHeight = if (getChildAt(0)?.layoutParams?.height == ViewGroup.LayoutParams.MATCH_PARENT)
                    ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, divViewHeight)
                id = R.id.morda_screenshot_div
                removeAutofocusForOldApis()
                hideCursor()
            }
            applyConfiguration(divView)
            setContentView(divView)
        }

        if (divJson.has("card")) {
            val templateJson = divJson.optJSONObject("templates")
            val cardJson = divJson.getJSONObject("card")
            Div2ViewFactory(divContext, templateJson).createViewByConfig(cardJson, onBound)
        } else {
            Div2ViewFactory(divContext, assetReader.tryRead(templatesAssetName)).createViewByConfig(divJson, onBound)
        }
    }

    private fun setImageLoader() {
        val loader = when (imageLoaderName) {
            IMAGE_LOADER_PICASSO -> Preferences.ImageLoaderOption.PICASSO
            IMAGE_LOADER_GLIDE -> Preferences.ImageLoaderOption.GLIDE
            IMAGE_LOADER_COIL -> Preferences.ImageLoaderOption.COIL
            else -> return
        }
        Container.preferences.imageLoader = loader
    }

    fun getTestCaseJson() = assetReader.read(cardAssetName)

    private fun View.removeAutofocusForOldApis() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            isFocusableInTouchMode = true
        }
    }

    private fun ViewGroup.hideCursor() {
        for (child in children) {
            if (child is EditText) {
                child.isCursorVisible = false
                child.inputType = child.inputType or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            } else {
                (child as? ViewGroup)?.hideCursor()
            }
        }
    }

    private fun applyConfiguration(view: View) {
        val divJson = getTestCaseJson()
        val configuration = if (divJson.has("configuration")) {
            divJson.getJSONObject("configuration")
        } else {
            return
        }

        if (configuration.has("layout_direction")) {
            if (configuration.getString("layout_direction") == "rtl") {
                view.layoutDirection = View.LAYOUT_DIRECTION_RTL
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(
            this,
            broadcastReceiver,
            receiverIntentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun rebindDivWithSameData() {
        when (val view = findViewById<View>(R.id.morda_screenshot_div)) {
            is Div2View -> {
                view.setData(view.divData, view.dataTag)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    companion object {
        const val REBIND_DIV_WITH_SAME_DATA_ACTION = "DivScreenshotActivity.REBIND_DIV_WITH_SAME_DATA"
        const val EXTRA_DIV_ASSET_NAME = "DivScreenshotActivity.EXTRA_DIV_ASSET_NAME"
        const val EXTRA_DIV_IMAGE_LOADER_NAME = "DivScreenshotActivity.EXTRA_DIV_IMAGE_LOADER_NAME"

        const val IMAGE_LOADER_PICASSO = "picasso"
        const val IMAGE_LOADER_GLIDE = "glide"
        const val IMAGE_LOADER_COIL = "coil"
    }
}
