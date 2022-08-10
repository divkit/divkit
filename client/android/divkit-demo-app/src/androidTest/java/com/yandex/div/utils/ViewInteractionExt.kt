package com.yandex.div.utils

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

inline fun <reified V : View, T> ViewInteraction.runOnView(crossinline block: (V) -> T?): T? {
    var result: T? = null
    perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isAssignableFrom(V::class.java)
        }

        override fun getDescription(): String {
            return "Perform on ${V::class.java.canonicalName}"
        }

        override fun perform(uiController: UiController, view: View) {
            result = block(view as V)
        }
    })
    return result
}
