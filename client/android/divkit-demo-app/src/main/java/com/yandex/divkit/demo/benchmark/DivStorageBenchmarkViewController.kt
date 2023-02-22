package com.yandex.divkit.demo.benchmark

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.yandex.div.internal.util.dp
import com.yandex.divkit.demo.utils.padding
import com.yandex.divkit.demo.utils.textColor

internal class DivStorageBenchmarkViewController(
    private val profilingLayout: ProfilingLayout,
) : BenchmarkViewController {

    private val context = profilingLayout.context

    override fun showMessage(message: CharSequence) {
        profilingLayout.run {
            removeAllViews()
            addView(messageView(message), messageLayoutParams())
        }
    }

    private fun messageView(message: CharSequence): TextView {
        return AppCompatTextView(context).apply {
            gravity = Gravity.CENTER
            padding = dp(32)
            textColor = Color.DKGRAY
            textSize = 24.0f
            text = message
        }
    }

    private fun messageLayoutParams(): ViewGroup.LayoutParams {
        return FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}
