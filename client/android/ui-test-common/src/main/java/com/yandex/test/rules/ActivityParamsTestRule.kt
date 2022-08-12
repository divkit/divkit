package com.yandex.test.rules

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.test.espresso.intent.rule.IntentsTestRule

open class ActivityParamsTestRule<A : Activity>(
    activityClass: Class<A>,
    launchActivity: Boolean = true,
    private val action: String? = null,
    private val params: Bundle? = null
) : IntentsTestRule<A>(activityClass, false, launchActivity) {

    constructor(activityClass: Class<A>, vararg params: Pair<String, Any?>) : this(
        activityClass = activityClass,
        action = null,
        params = Bundle().fill(params)
    )

    override fun getActivityIntent(): Intent? {
        if (params == null && action == null) return null

        return createIntent(params)
    }

    fun launchActivity(overrideParams: Bundle? = null) = launchActivity(overrideParams?.let { createIntent(it) })

    private fun createIntent(params: Bundle?): Intent {
        val intent = Intent()
        intent.action = action
        params?.let { intent.putExtras(params) }
        return intent
    }
}

private fun Bundle.fill(params: Array<out Pair<String, Any?>>): Bundle {
    params.forEach {
        when (val value = it.second) {
            null -> putSerializable(it.first, null)
            is Int -> putInt(it.first, value)
            is Long -> putLong(it.first, value)
            is CharSequence -> putCharSequence(it.first, value)
            is String -> putString(it.first, value)
            is Float -> putFloat(it.first, value)
            is Double -> putDouble(it.first, value)
            is Char -> putChar(it.first, value)
            is Short -> putShort(it.first, value)
            is Boolean -> putBoolean(it.first, value)
            is Bundle -> putBundle(it.first, value)
            is Parcelable -> putParcelable(it.first, value)
            is IntArray -> putIntArray(it.first, value)
            is LongArray -> putLongArray(it.first, value)
            is FloatArray -> putFloatArray(it.first, value)
            is DoubleArray -> putDoubleArray(it.first, value)
            is CharArray -> putCharArray(it.first, value)
            is ShortArray -> putShortArray(it.first, value)
            is BooleanArray -> putBooleanArray(it.first, value)
            else -> throw RuntimeException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
        }
    }
    return this
}
