package com.yandex.test.util

import android.annotation.SuppressLint
import androidx.test.espresso.BaseLayerComponent
import androidx.test.espresso.Espresso
import androidx.test.espresso.Root
import androidx.test.espresso.matcher.RootMatchers
import org.fest.reflect.core.Reflection
import org.junit.Assert

fun assertNoPopupsAreDisplayed() {
    Assert.assertTrue("Popup view must not exist",
                      activeRoots.none { RootMatchers.isPlatformPopup().matches(it) })
}

fun assertPopupDisplayed() {
    Assert.assertNotNull("Exactly one popup view must exist",
        activeRoots.singleOrNull { RootMatchers.isPlatformPopup().matches(it) }
    )
}

internal val activeRoots: List<Root>
    @SuppressLint("RestrictedApi")
    get() = performOnMain {
        Reflection.staticField("BASE")
            .ofType(BaseLayerComponent::class.java)
            .`in`(Espresso::class.java).get()
            .activeRootLister()
            .listActiveRoots()
    }
