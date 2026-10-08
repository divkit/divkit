package com.yandex.test.idling

import android.view.View
import androidx.core.view.isVisible
import com.yandex.test.util.getCurrentActivity
import java.lang.ref.WeakReference

class ViewIdlingResource(private val viewTag: String) : SimpleIdlingResource() {

    internal val view: View?
        get() = activityWeakRef.get()?.window?.decorView?.findViewWithTag(viewTag)

    private val activityWeakRef
        get() = WeakReference(getCurrentActivity())

    override fun checkIdle(): Boolean {
        val view = view ?: return false
        return view.isVisible && !view.isLayoutRequested
    }

    override fun getName() = "ViewIdlingResource"
}
