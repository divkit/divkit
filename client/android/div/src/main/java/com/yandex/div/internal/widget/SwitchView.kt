package com.yandex.div.internal.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.yandex.div.core.view2.drawable.NoOpDrawable

internal open class SwitchView(context: Context) : FrameLayout(context) {
    private val switch = SwitchCompat(context)

    var colorOn: Int? = null
        set(value) {
            field = value
            changeTints()
        }

    var isChecked: Boolean
        get() = switch.isChecked
        set(value) {
            switch.isChecked = value
        }

    private val trackTintColors = IntArray(3)
    private val thumbTintColors = IntArray(3)

    init {
        switch.apply {
            showText = false
            background = NoOpDrawable
        }
        this.addView(switch, LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER))
        this.setOnClickListener { forwardClicksToSwitch() }

        fillDefaultColors()
        updateTints()
    }

    override fun isEnabled(): Boolean = switch.isEnabled
    override fun setEnabled(enabled: Boolean) {
        switch.isEnabled = enabled
    }

    fun setOnCheckedChangeListener(listener: (Boolean) -> Unit) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            listener.invoke(isChecked)
        }
    }

    private fun changeTints() {
        val checkedColor = colorOn ?: return

        thumbTintColors[CHECKED_INDEX] = checkedColor
        trackTintColors[CHECKED_INDEX] = checkedColor.applyAlpha(0.3f)

        updateTints()
    }

    private fun updateTints() {
        switch.trackTintList = ColorStateList(STATES_ARRAY, trackTintColors)
        switch.thumbTintList = ColorStateList(STATES_ARRAY, thumbTintColors)
    }

    private fun fillDefaultColors() {
        val typedValue = TypedValue()

        val trackColor = getColorFromTheme(android.R.attr.colorForeground, typedValue, false)
        val accentColor = getColorFromTheme(android.R.attr.colorControlActivated, typedValue, false)
        @SuppressLint("PrivateResource")
        val thumbColor = getColorFromTheme(androidx.appcompat.R.attr.colorSwitchThumbNormal, typedValue, true)

        trackTintColors[CHECKED_INDEX] = accentColor.applyAlpha(0.3f)
        trackTintColors[UNCHECKED_INDEX] = trackColor.setAlpha(0.3f)
        trackTintColors[DISABLED_INDEX] = trackColor.setAlpha(0.1f)

        thumbTintColors[CHECKED_INDEX] = accentColor
        thumbTintColors[UNCHECKED_INDEX] = thumbColor
        thumbTintColors[DISABLED_INDEX] = thumbColor.lightenColor(0.5f)
    }

    private fun getColorFromTheme(resId: Int, typedValue: TypedValue, outputIsResource: Boolean): Int {
        val resolved = context.theme.resolveAttribute(resId, typedValue, true)
        return when {
            !resolved -> 0
            outputIsResource && typedValue.resourceId != 0 -> ContextCompat.getColor(context, typedValue.resourceId)
            else -> typedValue.data
        }
    }

    private fun Int.applyAlpha(value: Float): Int {
        val newAlpha = (Color.alpha(this) * value).toInt()
        return this.setAlpha(newAlpha)
    }

    private fun Int.setAlpha(value: Int): Int {
        return Color.argb(value, Color.red(this), Color.green(this), Color.blue(this))
    }

    private fun Int.setAlpha(value: Float): Int {
        return this.setAlpha((value * 255).toInt())
    }

    private fun Int.lightenColor(value: Float): Int {
        return ColorUtils.blendARGB(this, Color.WHITE, value)
    }

    private fun forwardClicksToSwitch() {
        if (isEnabled) {
            switch.performClick()
        }
    }

    @VisibleForTesting
    internal val thumbTintList: ColorStateList?
        get() = switch.thumbTintList

    @VisibleForTesting
    internal val trackTintList: ColorStateList?
        get() = switch.trackTintList

    companion object {
        private val DISABLED = intArrayOf(-android.R.attr.state_enabled)
        private val CHECKED = intArrayOf(android.R.attr.state_checked)
        private val DEFAULT = intArrayOf()

        private const val CHECKED_INDEX = 1
        private const val UNCHECKED_INDEX = 2
        private const val DISABLED_INDEX = 0

        private val STATES_ARRAY = arrayOf(DISABLED, CHECKED, DEFAULT)
    }

}
