package com.yandex.divkit.demo.screenshot

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.EditText
import androidx.core.view.children
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.DivKit
import com.yandex.div.core.view2.Div2View
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.div.divContext
import com.yandex.divkit.demo.settings.Preferences
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject

class DivScreenshotActivity : DivDataScreenshotActivity() {

    private lateinit var divContext: Div2Context
    private var assertionsEnabled = false

    var imageLoaderName = IMAGE_LOADER_LOCAL

    lateinit var divView: Div2View
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assertionsEnabled = DivKit.isAssertionsEnabled()
        DivKit.enableAssertions(false)
    }

    override fun prepare() {
        setImageLoader()
        divContext = divContext(activity = this, isRiveEnabled = false) {
            animationsEnabledProvider(DisabledAnimationsProvider)
        }
        divView = Div2View(divContext)
        setContentView(divView)
    }

    override fun setDivData(json: JSONObject) {
        val configuration = ScreenshotTestConfiguration.from(json)
        val divJson = json.optJSONObject("div_data") ?: json
        val cardJson = divJson.getJSONObject("card")
        Div2ViewFactory(
            context = divContext,
            templatesJson = divJson.optJSONObject("templates"),
            parsingErrorLogger = configuration.parsingErrorLogger,
        ).bindViewByConfig(divView, cardJson) { it.onBound(json) }
    }

    override fun cleanup() {
        divView.cleanup()
        Container.imageLoaderOverride = null
    }

    override fun onDestroy() {
        super.onDestroy()
        DivKit.enableAssertions(assertionsEnabled)
    }

    private fun setImageLoader() {
        val loader = when (imageLoaderName) {
            IMAGE_LOADER_COIL, IMAGE_LOADER_LOCAL -> Preferences.ImageLoaderOption.COIL
            IMAGE_LOADER_GLIDE -> Preferences.ImageLoaderOption.GLIDE
            else -> return
        }
        Container.imageLoaderOverride = Container.createImageLoader(
            loader = loader,
            useOnlyLocalImages = imageLoaderName == IMAGE_LOADER_LOCAL,
        )
    }

    private fun Div2View.onBound(testCase: JSONObject) {
        val matchParentWidth = getChildAt(0)?.layoutParams?.width == LayoutParams.MATCH_PARENT
        layoutParams?.width = if (matchParentWidth) {
            LayoutParams.MATCH_PARENT
        } else {
            LayoutParams.WRAP_CONTENT
        }

        val matchParentHeight = getChildAt(0)?.layoutParams?.height == LayoutParams.MATCH_PARENT
        layoutParams?.height = if (matchParentHeight) {
            LayoutParams.MATCH_PARENT
        } else {
            LayoutParams.WRAP_CONTENT
        }

        tag = SCREENSHOT_VIEW_TAG
        removeAutofocusForOldApis()
        hideCursor()
        applyConfiguration(testCase)
        requestLayout()
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

    private fun View.applyConfiguration(testCase: JSONObject) {
        ScreenshotTestConfiguration.from(testCase).applyTo(this)
    }

    companion object {
        const val SCREENSHOT_VIEW_TAG = "screenshot_view"

        const val IMAGE_LOADER_GLIDE = "glide"
        const val IMAGE_LOADER_COIL = "coil"
        const val IMAGE_LOADER_LOCAL = "local"
    }
}

private object DisabledAnimationsProvider : DivAnimationsEnabledProvider {
    override val animationsEnabled = MutableStateFlow(false)
}
