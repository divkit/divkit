@file:JvmName("DivTestUtils")

package com.yandex.div.core

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.core.dagger.Div2ViewComponent
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.widgets.ReleaseViewVisitor
import com.yandex.div.core.view2.state.DivStateSwitcher
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.Expression
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

internal fun View.viewEquals(other: View): Boolean {
    if (this::class.java == other::class.java) {
        if (this is ViewGroup && other is ViewGroup) {
            return if (this.childCount == other.childCount) {
                this.children
                    .zip(other.children)
                    .map {
                        it.first.viewEquals(it.second)
            }
                    .reduceOrNull { a, b ->
                        a && b
                    } ?: true
            } else {
                false
    }
        }
        return compareViews(this, other)
    }
    return false
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

internal val String.path: DivStatePath get() = DivStatePath.parse(this)

internal fun ViewGroup.childrenToFlatList(): List<View> {
    val viewList = mutableListOf<View>()
    children.forEach { childView: View ->
        viewList.add(childView)
        if (childView is ViewGroup) {
            viewList.addAll(childView.childrenToFlatList())
        }
    }
    return viewList
}

internal class TestComponent(
    private val wrapped: Div2Component,
    private val divBinder: DivBinder = wrapped.divBinder,
    private val viewComponentBuilder: Div2ViewComponent.Builder = wrapped.viewComponent()
) : Div2Component by wrapped {

    override fun getDivBinder(): DivBinder = divBinder

    override fun viewComponent(): Div2ViewComponent.Builder = viewComponentBuilder
}

internal class TestViewComponentBuilder(
    private val wrapped: Div2ViewComponent.Builder,
    private val releaseViewVisitor: ReleaseViewVisitor? = null,
    private val stateSwitcher: DivStateSwitcher? = null
) : Div2ViewComponent.Builder {

    override fun divView(divView: Div2View): Div2ViewComponent.Builder {
        wrapped.divView(divView)
        return this
    }

    override fun build(): Div2ViewComponent {
        val wrappedViewComponent = wrapped.build()
        return TestViewComponent(
            wrappedViewComponent,
            releaseViewVisitor ?: wrappedViewComponent.releaseViewVisitor,
            stateSwitcher ?: wrappedViewComponent.stateSwitcher
        )
    }
}

internal class TestViewComponent(
    private val wrapped: Div2ViewComponent,
    override val releaseViewVisitor: ReleaseViewVisitor = wrapped.releaseViewVisitor,
    override val stateSwitcher: DivStateSwitcher = wrapped.stateSwitcher
) : Div2ViewComponent by wrapped

internal fun <T: Any> T.asExpression(): Expression<T> {
    return Expression.constant(this)
}
