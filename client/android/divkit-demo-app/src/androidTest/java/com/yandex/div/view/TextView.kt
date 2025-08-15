package com.yandex.div.view

import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.text.getSpans
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.notNullValue

object TextViewActions {

    /**
     * Clear text in TextView
     */
    fun clearText(): ViewAction {
        return object : ViewAction {

            override fun getConstraints(): Matcher<View> = allOf(isDisplayed(), isAssignableFrom(EditText::class.java))

            override fun getDescription(): String = "Delete all text in EditText"

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadUntilIdle()
                (view as EditText).text.clear()
            }
        }
    }

    /**
     * Taps a first found link if it is recognized.
     */
    fun tapLink(): ViewAction {
        return object : ViewAction {

            override fun getConstraints(): Matcher<View> = allOf(isDisplayed(), isAssignableFrom(TextView::class.java))

            override fun getDescription(): String = "Tap on link inside text"

            override fun perform(uiController: UiController, view: View) {
                val spannable = (view as TextView).text as? Spanned
                assertThat("Unknown text format found in $spannable", spannable, notNullValue())
                val span = spannable?.getSpans<ClickableSpan>()?.firstOrNull()
                assertThat("No link found in $spannable", spannable, notNullValue())
                span?.onClick(view)
            }
        }
    }
}
