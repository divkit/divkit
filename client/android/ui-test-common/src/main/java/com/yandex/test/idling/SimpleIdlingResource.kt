package com.yandex.test.idling

import android.content.ContentValues.TAG
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingResource

abstract class SimpleIdlingResource(
    private val pollingIntervalMillis: Long = 1_000L,
    val description: String? = null
) : IdlingResource {

    private lateinit var resourceCallback: IdlingResource.ResourceCallback
    private lateinit var handler: Handler

    private val finishTime = System.currentTimeMillis() + with(IdlingPolicies.getDynamicIdlingResourceErrorPolicy()) {
        idleTimeoutUnit.toMillis(idleTimeout)
    }

    private var isIdle: Boolean = false

    override fun isIdleNow(): Boolean {
        handler.removeCallbacksAndMessages(null)

        if (isIdle) return true

        isIdle = checkIdle()
        Log.i(TAG, "$description idle: $isIdle timeout: $finishTime")
        if (isIdle) {
            resourceCallback.onTransitionToIdle()
        } else if (System.currentTimeMillis() + pollingIntervalMillis < finishTime) {
            handler.postDelayed({ isIdleNow }, pollingIntervalMillis)
        }
        return isIdle
    }

    protected abstract fun checkIdle(): Boolean

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        handler = Handler(Looper.getMainLooper())
        this.resourceCallback = resourceCallback
    }

    override fun getName() = description ?: toString()
}
