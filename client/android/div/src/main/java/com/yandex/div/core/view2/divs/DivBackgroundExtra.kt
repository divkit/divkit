package com.yandex.div.core.view2.divs

import android.graphics.drawable.Drawable
import android.view.View
import com.yandex.div.R
import com.yandex.div.core.annotations.InternalApi

private class DivBackgroundExtra {
    var underlayDrawable: Drawable? = null
    var boundUnderlayDrawable: Drawable? = null
    var boundOverlayDrawable: Drawable? = null
}

private var View.backgroundExtra: DivBackgroundExtra?
    get() = getTag(R.id.div_background_extra_tag) as? DivBackgroundExtra?
    set(value) {
        setTag(R.id.div_background_extra_tag, value)
    }

private fun View.obtainBackgroundExtra(): DivBackgroundExtra {
    val extra = backgroundExtra
    return extra ?: DivBackgroundExtra().also { backgroundExtra = it }
}

@InternalApi
var View.backgroundUnderlay: Drawable?
    get() = backgroundExtra?.underlayDrawable
    set(value) {
        obtainBackgroundExtra().underlayDrawable = value
    }

internal var View.boundBackgroundUnderlay: Drawable?
    get() = backgroundExtra?.boundUnderlayDrawable
    set(value) {
        obtainBackgroundExtra().boundUnderlayDrawable = value
    }

internal var View.boundBackgroundOverlay: Drawable?
    get() = backgroundExtra?.boundOverlayDrawable
    set(value) {
        obtainBackgroundExtra().boundOverlayDrawable = value
    }
