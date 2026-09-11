package com.yandex.div.test

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider

/**
 * A lightweight replacement for `Robolectric.buildActivity(Activity::class.java).get()`
 * in tests that only need a themed [Context] (e.g. to construct [com.yandex.div.core.Div2Context]
 * or Android views) and don't rely on the Activity lifecycle (onCreate/attach/window).
 *
 * `Robolectric.buildActivity(...).get()` spins up a full Activity lifecycle, which is
 * unnecessary overhead for most binder/view unit tests. Prefer this helper unless the test
 * specifically exercises Activity lifecycle behavior.
 */
fun testContextThemeWrapper(): ContextThemeWrapper =
    ContextThemeWrapper(ApplicationProvider.getApplicationContext<Context>(), 0)
