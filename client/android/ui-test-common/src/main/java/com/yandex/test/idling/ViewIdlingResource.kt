package com.yandex.test.idling

import android.view.View
import com.yandex.test.util.getCurrentActivity
import java.lang.ref.WeakReference

class ViewIdlingResource(private val viewId: Int) : SimpleIdlingResource() {

    internal val view: View?
        get() = activityWeakRef.get()?.findViewById(viewId)

    private val activityWeakRef
        get() = WeakReference(getCurrentActivity())

    override fun checkIdle(): Boolean {
        return view?.visibility == View.VISIBLE
    }

    override fun getName() = "ViewIdlingResource"
}
