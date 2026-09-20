package com.yandex.div.core.view2

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.Disposable
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.accessibility
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container as containerData
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivText
import com.yandex.div2.DivVideo
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivAccessibilityBinderTest {

    private val binder = DivAccessibilityBinder(
        enabled = true,
        accessibilityStateProvider = AccessibilityStateProvider(true),
    )
    private val view = TextView(ApplicationProvider.getApplicationContext())

    @BeforeTest
    fun setUp() {
        AccessibilityStateProvider.touchExplorationEnabled = true
    }

    @AfterTest
    fun tearDown() {
        AccessibilityStateProvider.touchExplorationEnabled = null
    }

    @Test
    fun `described button is an important accessibility node`() {
        bind(accessibility(description = "Element 2", type = DivAccessibility.Type.BUTTON))

        assertEquals(
            listOf("Element 2", View.IMPORTANT_FOR_ACCESSIBILITY_YES, "android.widget.Button"),
            listOf(
                view.contentDescription,
                view.importantForAccessibility,
                view.createAccessibilityNodeInfo().className,
            ),
        )
    }

    @Test
    fun `hint makes view important for accessibility`() {
        bind(accessibility(hint = "Activate button"))

        assertEquals(
            listOf("Activate button", View.IMPORTANT_FOR_ACCESSIBILITY_YES),
            listOf(view.contentDescription, view.importantForAccessibility),
        )
    }

    @Test
    fun `description and hint make view important for accessibility`() {
        bind(accessibility(description = "Element 2", hint = "Activate button"))

        assertEquals(
            listOf("Element 2\nActivate button", View.IMPORTANT_FOR_ACCESSIBILITY_YES),
            listOf(view.contentDescription, view.importantForAccessibility),
        )
    }

    @Test
    fun `view without description or hint keeps automatic accessibility importance`() {
        bind(custom(accessibility()))

        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, view.importantForAccessibility)
    }

    @Test
    fun `exclude mode hides described view and descendants`() {
        bind(accessibility(description = "Element", mode = DivAccessibility.Mode.EXCLUDE))

        assertEquals(
            View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS,
            view.importantForAccessibility,
        )
    }

    @Test
    fun `merge mode makes described view a screen reader focusable node`() {
        bind(accessibility(description = "Element", mode = DivAccessibility.Mode.MERGE))

        assertTrue(ViewCompat.isScreenReaderFocusable(view))
        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_YES, view.importantForAccessibility)
    }

    @Test
    fun `described container keeps automatic accessibility importance`() {
        val containerView = FrameLayout(ApplicationProvider.getApplicationContext())

        bind(
            container(accessibility(description = "Group")),
            targetView = containerView,
        )

        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, containerView.importantForAccessibility)
    }

    @Test
    fun `merge mode makes described container a screen reader focusable node`() {
        val containerView = FrameLayout(ApplicationProvider.getApplicationContext())

        bind(
            container(accessibility(description = "Group", mode = DivAccessibility.Mode.MERGE)),
            targetView = containerView,
        )

        assertEquals(
            listOf(true, View.IMPORTANT_FOR_ACCESSIBILITY_YES),
            listOf(
                ViewCompat.isScreenReaderFocusable(containerView),
                containerView.importantForAccessibility,
            ),
        )
    }

    @Test
    fun `described video view is an important accessibility node`() {
        val videoView = DivVideoView(ApplicationProvider.getApplicationContext())

        bind(
            DivVideo(
                accessibility = accessibility(description = "Video"),
                videoSources = emptyList(),
            ),
            targetView = videoView,
        )

        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_YES, videoView.importantForAccessibility)
    }

    @Test
    fun `described custom container keeps accessible child as the important node`() {
        val childDiv = text(accessibility = accessibility(description = "Child"), text = "Child")
        val containerView = FrameLayout(ApplicationProvider.getApplicationContext())
        val childView = TextView(ApplicationProvider.getApplicationContext())
        containerView.addView(childView)

        bind(
            DivCustom(
                accessibility = accessibility(description = "Group"),
                customType = "container",
                items = listOf(childDiv),
            ),
            targetView = containerView,
        )
        bind((childDiv as Div.Text).value, targetView = childView)

        assertEquals(
            listOf(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, View.IMPORTANT_FOR_ACCESSIBILITY_YES),
            listOf(containerView.importantForAccessibility, childView.importantForAccessibility),
        )
    }

    @Test
    fun `described custom without items is an important accessibility node`() {
        val customView = FrameLayout(ApplicationProvider.getApplicationContext())

        bind(
            DivCustom(
                accessibility = accessibility(description = "Custom"),
                customType = "leaf",
            ),
            targetView = customView,
        )

        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_YES, customView.importantForAccessibility)
    }

    @Test
    fun `adding description on rebind makes view important for accessibility`() {
        val oldDiv = div(accessibility())
        bind(oldDiv)

        bind(accessibility(description = "Element"), oldDiv)

        assertEquals(
            listOf("Element", View.IMPORTANT_FOR_ACCESSIBILITY_YES),
            listOf(view.contentDescription, view.importantForAccessibility),
        )
    }

    @Test
    fun `description expression update does not change importance when binder is disabled`() {
        val description = MutableDescriptionExpression("")
        val disabledBinder = DivAccessibilityBinder(
            enabled = false,
            accessibilityStateProvider = AccessibilityStateProvider(true),
        )
        view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        bind(
            div(dynamicAccessibility(description)),
            targetBinder = disabledBinder,
        )

        description.update("Element")

        assertEquals(
            listOf("Element", View.IMPORTANT_FOR_ACCESSIBILITY_NO),
            listOf(view.contentDescription, view.importantForAccessibility),
        )
    }

    @Test
    fun `description expression update makes view important for accessibility`() {
        val description = MutableDescriptionExpression("")
        bind(custom(dynamicAccessibility(description)))
        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, view.importantForAccessibility)

        description.update("Element")

        assertEquals(
            listOf("Element", View.IMPORTANT_FOR_ACCESSIBILITY_YES),
            listOf(view.contentDescription, view.importantForAccessibility),
        )
    }

    @Test
    fun `widget content description does not change text importance on rebind without accessibility`() {
        val lineHeightView = DivLineHeightTextView(ApplicationProvider.getApplicationContext())
        val div = div()
        bind(div, targetView = lineHeightView)
        lineHeightView.contentDescription = "Generated by widget"

        bind(div, oldDiv = div, targetView = lineHeightView)

        assertEquals(
            listOf("Generated by widget", View.IMPORTANT_FOR_ACCESSIBILITY_YES),
            listOf(lineHeightView.contentDescription, lineHeightView.importantForAccessibility),
        )
    }

    @Test
    fun `removing description on rebind restores automatic accessibility importance`() {
        val oldDiv = custom(accessibility(description = "Element"))
        bind(oldDiv)

        bind(custom(accessibility()), oldDiv)

        assertEquals(null, view.contentDescription)
        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO, view.importantForAccessibility)
        assertFalse(ViewCompat.isScreenReaderFocusable(view))
    }

    private fun bind(accessibility: DivAccessibility, oldDiv: DivText? = null) {
        bind(div(accessibility), oldDiv)
    }

    private fun bind(
        div: DivBase,
        oldDiv: DivBase? = null,
        targetView: View = view,
        targetBinder: DivAccessibilityBinder = binder,
    ) {
        targetBinder.bind(
            targetView,
            div,
            oldDiv,
            ExpressionResolver.EMPTY,
            targetView.expressionSubscriber,
        )
    }

    private fun div(accessibility: DivAccessibility? = null): DivText {
        return (text(accessibility = accessibility, text = "Text") as Div.Text).value
    }

    private fun container(accessibility: DivAccessibility): DivContainer {
        return (containerData(accessibility = accessibility) as Div.Container).value
    }

    private fun custom(accessibility: DivAccessibility): DivCustom {
        return DivCustom(accessibility = accessibility, customType = "custom")
    }

    private fun dynamicAccessibility(description: Expression<String>): DivAccessibility {
        return DivAccessibility(
            description = description,
            mode = constant(DivAccessibility.Mode.DEFAULT),
            type = DivAccessibility.Type.AUTO,
        )
    }

    private class MutableDescriptionExpression(initialValue: String) : Expression<String>() {

        override val rawValue = "mutable description"

        private var value = initialValue
        private var callback: ((String) -> Unit)? = null

        override fun evaluate(resolver: ExpressionResolver): String = value

        override fun observe(resolver: ExpressionResolver, callback: (String) -> Unit): Disposable {
            this.callback = callback
            return Disposable.NULL
        }

        fun update(value: String) {
            this.value = value
            callback?.invoke(value)
        }
    }
}
