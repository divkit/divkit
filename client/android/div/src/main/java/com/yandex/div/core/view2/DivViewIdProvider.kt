package com.yandex.div.core.view2

import android.view.View
import androidx.core.view.ViewCompat
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.internal.util.arrayMap
import javax.inject.Inject

@DivScope
internal class DivViewIdProvider @Inject constructor() {

    private val cache = arrayMap<String, Int>()

    fun getViewId(id: String?): Int {
        if (id == null) return View.NO_ID

        return cache.getOrPut(id) {
            ViewCompat.generateViewId()
        }
    }

    fun reset() = cache.clear()
}
