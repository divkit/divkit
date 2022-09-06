package com.yandex.div.core.resources

import android.content.Context
import android.content.res.Resources
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper

/**
 *  ContextThemeWrapper that caches primitive resources.
 */
internal class ContextThemeWrapperWithResourceCache(
    baseContext: Context,
    @StyleRes themeResId: Int
) : ContextThemeWrapper(baseContext, themeResId) {

    private val resourceCache: Resources by lazy {
        PrimitiveResourceCache(super.getResources())
    }

    override fun getResources() = resourceCache
}
