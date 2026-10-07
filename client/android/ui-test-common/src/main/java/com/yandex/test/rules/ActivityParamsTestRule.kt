package com.yandex.test.rules

import android.app.Activity
import androidx.test.espresso.intent.rule.IntentsTestRule

open class ActivityParamsTestRule<A : Activity>(activityClass: Class<A>)
    : IntentsTestRule<A>(activityClass, true, true)
