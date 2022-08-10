@file:JvmName("DivTestUtils")

package com.yandex.div.legacy

import android.view.View
import android.widget.TextView
import com.yandex.alicekit.core.utils.Assert
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

internal fun runAsync(action: () -> Unit) {
    val executor = Executors.newSingleThreadExecutor()
    var error: Throwable? = null
    executor.execute {
        try {
            action.invoke()
        } catch (e: Throwable) {
            error = e
        }
    }

    executor.shutdown()
    val finished = executor.awaitTermination(10, TimeUnit.SECONDS)
    if (!finished) throw TimeoutException("Test execution takes more than 10 seconds.")

    error?.let { throw it }
}

internal fun compareViews(first: View, second: View): Boolean {
    Assert.assertEquals(first.width, second.width)
    Assert.assertEquals(first.height, second.height)

    Assert.assertEquals(first.paddingTop, second.paddingTop)
    Assert.assertEquals(first.paddingLeft, second.paddingLeft)
    Assert.assertEquals(first.paddingRight, second.paddingRight)
    Assert.assertEquals(first.paddingBottom, second.paddingBottom)

    Assert.assertEquals(first.alpha, second.alpha)

    Assert.assertEquals(first.hasOnClickListeners(), second.hasOnClickListeners())
    when (first) {
        is TextView -> {
            Assert.assertEquals(first.textSize, (second as TextView).textSize)
            Assert.assertEquals(first.text, second.text)
            Assert.assertEquals(first.maxLines, second.maxLines)
        }
    }
    return true
}
