package com.yandex.div.view

import android.animation.AnimatorInflater
import android.app.Activity
import android.content.ContextWrapper
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.CompoundButton
import android.widget.TextView
import androidx.annotation.AnimatorRes
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.yandex.divkit.demo.utils.coroutineScope
import com.yandex.divkit.demo.utils.noGetter
import kotlinx.coroutines.launch

const val matchParent: Int = ViewGroup.LayoutParams.MATCH_PARENT
const val wrapContent: Int = ViewGroup.LayoutParams.WRAP_CONTENT
const val matchConstraint: Int = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT

var TextView.textColor: Int
    @ColorInt get() = textColors.defaultColor
    set(@ColorInt value) = setTextColor(value)

var TextView.textColorResource: Int
    get() = noGetter()
    set(colorId) = setTextColor(context.resources.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) getColor(colorId, null) else getColor(colorId)
    })

var TextView.textColorAttr: Int
    get() = noGetter()
    set(@AttrRes value) = setTextColor(context.getThemeColor(value))

var TextView.textColorHint: Int
    get() = hintTextColors.defaultColor
    set(value) = setHintTextColor(value)

var TextView.textColorHintResource: Int
    get() = noGetter()
    set(colorId) = setHintTextColor(context.resources.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) getColor(colorId, null) else getColor(colorId)
    })

var TextView.fontResource: Int
    get() = noGetter()
    set(fontId) {
        typeface = ResourcesCompat.getFont(context, fontId)
    }

var TextView.lineSpacingAdd: Float
    get() = lineSpacingExtra
    set(value) = setLineSpacing(value, lineSpacingMultiplier)

var TextView.lineSpacingMult: Float
    get() = lineSpacingMultiplier
    set(value) = setLineSpacing(lineSpacingExtra, value)

var TextView.textResource: Int
    get() = noGetter()
    set(v) = setText(v)

var TextView.hintResource: Int
    get() = noGetter()
    set(value) = setHint(value)

var View.backgroundColor: Int
    get() = noGetter()
    set(@ColorInt value) = setBackgroundColor(value)

var View.backgroundColorResource: Int
    get() = noGetter()
    set(@ColorRes value) = setBackgroundColor(ResourcesCompat.getColor(resources, value, null))

var View.backgroundColorAttr: Int
    get() = noGetter()
    set(@AttrRes value) = setBackgroundColor(context.getThemeColor(value))

var View.backgroundResource: Int
    get() = noGetter()
    set(@DrawableRes value) = setBackgroundResource(value)


var View.backgroundTintColor: Int
    get() = noGetter()
    set(@ColorInt value) {
        background?.colorFilter = PorterDuffColorFilter(value, PorterDuff.Mode.SRC_ATOP)
    }

var View.backgroundTintColorResource: Int
    get() = noGetter()
    set(@ColorRes value) {
        backgroundTintColor = ResourcesCompat.getColor(resources, value, null)
    }

fun View.onClick(value: suspend () -> Unit) {
    setOnClickListener {
        activity.coroutineScope.launch { value() }
    }
}

fun View.clearClickListener() = setOnClickListener(null)

fun View.onLongClick(value: suspend () -> Unit) {
    setOnLongClickListener {
        activity.coroutineScope.launch { value() }
        true
    }
}

var View.leftPadding: Int
    get() = paddingLeft
    set(value) = setPadding(value, paddingTop, paddingRight, paddingBottom)

var View.rightPadding: Int
    get() = paddingRight
    set(value) = setPadding(paddingLeft, paddingTop, value, paddingBottom)

var View.topPadding: Int
    get() = paddingTop
    set(value) = setPadding(paddingLeft, value, paddingRight, paddingBottom)

var View.bottomPadding: Int
    get() = paddingBottom
    set(value) = setPadding(paddingLeft, paddingTop, paddingRight, value)

var View.padding: Int
    get() = noGetter()
    set(value) = setPadding(value, value, value, value)

var View.verticalPadding: Int
    get() = noGetter()
    set(value) = setPadding(paddingLeft, value, paddingRight, value)

var View.horizontalPadding: Int
    get() = noGetter()
    set(value) = setPadding(value, paddingTop, value, paddingBottom)

var View.stateListAnimatorResource: Int
    get() = noGetter()
    set(@AnimatorRes value) {
        stateListAnimator = AnimatorInflater.loadStateListAnimator(context, value)
    }

var View.topMargin: Int
    get() = noGetter()
    set(value) {
        withMarginLayoutParams {
            topMargin = value
        }
    }

var View.leftMargin: Int
    get() = noGetter()
    set(value) {
        withMarginLayoutParams {
            leftMargin = value
        }
    }

var View.bottomMargin: Int
    get() = noGetter()
    set(value) {
        withMarginLayoutParams {
            bottomMargin = value
        }
    }

var View.rightMargin: Int
    get() = noGetter()
    set(value) {
        withMarginLayoutParams {
            rightMargin = value
        }
    }

var View.topMarginRes: Int
    get() = noGetter()
    set(@DimenRes value) {
        withMarginLayoutParams {
            topMargin = context.resources.getDimensionPixelSize(value)
        }
    }

var View.leftMarginRes: Int
    get() = noGetter()
    set(@DimenRes value) {
        withMarginLayoutParams {
            leftMargin = context.resources.getDimensionPixelSize(value)
        }
    }

var View.bottomMarginRes: Int
    get() = noGetter()
    set(@DimenRes value) {
        withMarginLayoutParams {
            bottomMargin = context.resources.getDimensionPixelSize(value)
        }
    }

var View.rightMarginRes: Int
    get() = noGetter()
    set(@DimenRes value) {
        withMarginLayoutParams {
            rightMargin = context.resources.getDimensionPixelSize(value)
        }
    }

fun View.updateMargins(
    @Px left: Int = marginLeft,
    @Px top: Int = marginTop,
    @Px right: Int = marginRight,
    @Px bottom: Int = marginBottom
) {
    if (marginLeft == left && marginTop == top && marginBottom == bottom && marginRight == right) {
        return
    }
    withMarginLayoutParams {
        topMargin = top
        bottomMargin = bottom
        leftMargin = left
        rightMargin = right
    }
}

inline fun View.withMarginLayoutParams(block: MarginLayoutParams.() -> Unit) = withLayoutParams(block)

inline fun <reified T : ViewGroup.LayoutParams> View.withLayoutParams(block: T.() -> Unit) {
    (layoutParams as? T)?.let { lp ->
        lp.block()
        layoutParams = lp
    }
}

inline fun View.onLayoutChange(crossinline onLayoutChange: () -> Unit) =
    addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> onLayoutChange() }

inline fun CompoundButton.onCheckedChange(crossinline onCheckedChange: (Boolean) -> Unit) =
    setOnCheckedChangeListener { _, isChecked -> onCheckedChange(isChecked) }

val View.activity: Activity
    get() {
        when (val context = context) {
            is Activity -> return context
            is ContextWrapper -> {
                var contextWrapper = context
                while (contextWrapper is ContextWrapper) {
                    if (contextWrapper is Activity) {
                        return contextWrapper
                    }
                    contextWrapper = contextWrapper.baseContext
                }
                error("Unknown view context $contextWrapper")
            }
            else -> error("Unknown view context $context")
        }
    }
