package com.yandex.div.core.view2.divs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.transition.Fade
import com.yandex.div.core.DivCompatibilityFeatures
import com.yandex.div.core.asExpression
import com.yandex.div.core.dagger.Div2ViewComponent
import com.yandex.div.core.dagger.DivDataComponent
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTransitionBuilder
import com.yandex.div.core.view2.animations.DivTransitionHandler
import com.yandex.div.core.view2.divs.widgets.DivGifImageView
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.internal.core.toBlock
import com.yandex.div.internal.view.DivImageView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.gifImage
import com.yandex.div.test.data.image
import com.yandex.div2.Div
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFadeTransition
import com.yandex.div2.DivFocus
import com.yandex.div2.DivText
import com.yandex.div2.DivTransitionTrigger
import com.yandex.div2.DivVisibility
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class DivBaseBinderTest {

    private val view = spy(DivLineHeightTextView(context()).apply {
        layoutParams = defaultLayoutParams()
    })

    private val paddingsBottom1 = DivEdgeInsets(bottom = 1L.asExpression())
    private val paddingsBottom2 = DivEdgeInsets(bottom = 1L.asExpression())
    private val paddingsTop = DivEdgeInsets(top = 1L.asExpression())

    private val divBackgroundBinder = mock<DivBackgroundBinder>()
    private val baseBinder = DivBaseBinder(divBackgroundBinder, mock(), mock(), mock(), mock())
    private val hidingBinder = DivBaseBinder(
        divBackgroundBinder, mock(), mock(), mock(), mock(),
        compatibilityFeatures = DivCompatibilityFeatures(hideImagesWithoutUrl = true),
    )
    private val dataComponent = mock<DivDataComponent> {
        on { layoutProviderBinder } doReturn mock()
    }
    private val transitionHandler = mock<DivTransitionHandler>()
    private val transitionBuilder = mock<DivTransitionBuilder> {
        on { createAndroidTransition(any(), any(), any()) } doReturn Fade()
    }
    private val viewComponent = mock<Div2ViewComponent> {
        on { transitionHandler } doReturn transitionHandler
        on { transitionBuilder } doReturn transitionBuilder
    }
    private val divView = mock<Div2View> {
        on { dataComponent } doReturn dataComponent
        on { viewComponent } doReturn viewComponent
    }
    private val resolver = mock<ExpressionResolver>()
    private val path = DivStatePath.fromState(0)

    @Test
    fun `do not apply paddings when same`() {
        val div = createDiv(paddings = paddingsBottom1).toBlock(resolver, path)
        val oldDiv = createDiv(paddings = paddingsBottom1).toBlock(resolver, path)

        baseBinder.bindView(view, oldDiv, null, divView)
        clearInvocations(view)

        baseBinder.bindView(view, div, oldDiv, divView)

        verify(view, never()).setPadding(any(), any(), any(), any())
        verify(view, never()).requestLayout()
    }

    @Test
    fun `do not apply paddings when equals`() {
        val div = createDiv(paddings = paddingsBottom1).toBlock(resolver, path)
        val oldDiv = createDiv(paddings = paddingsBottom2).toBlock(resolver, path)

        baseBinder.bindView(view, oldDiv, null, divView)
        clearInvocations(view)

        baseBinder.bindView(view, div, oldDiv, divView)

        verify(view, never()).setPadding(any(), any(), any(), any())
        verify(view, never()).requestLayout()
    }

    @Test
    fun `apply paddings when value changed`() {
        val div = createDiv(paddings = paddingsBottom1).toBlock(resolver, path)
        val oldDiv = createDiv(paddings = paddingsTop).toBlock(resolver, path)

        baseBinder.bindView(view, div, oldDiv, divView)

        verify(view).setPadding(any(), any(), any(), any())
        verify(view, atLeastOnce()).requestLayout()
    }

    @Test
    fun `bind background of a div without background when an underlay is installed`() {
        val div = createDiv().toBlock(resolver, path)
        val underlay = ColorDrawable(Color.RED)
        view.backgroundUnderlay = underlay

        baseBinder.bindView(view, div, div, divView)

        verify(divBackgroundBinder).bindBackground(
            any(), any(), any(), any(), any(), any(), any(), any(), eq(underlay), anyOrNull()
        )
    }

    @Test
    fun `do not bind background when an overlay is bound and nothing changed`() {
        val div = createDiv().toBlock(resolver, path)
        view.boundBackgroundOverlay = ColorDrawable(Color.GREEN)

        baseBinder.bindView(view, div, div, divView)

        verify(divBackgroundBinder, never()).bindBackground(
            any(), any(), any(), any(), any(), any(), any(), any(), anyOrNull(), anyOrNull()
        )
    }

    @Test
    fun `do not bind background of a div without background when nothing changed`() {
        val div = createDiv().toBlock(resolver, path)

        baseBinder.bindView(view, div, div, divView)

        verify(divBackgroundBinder, never()).bindBackground(
            any(), any(), any(), any(), any(), any(), any(), any(), anyOrNull(), anyOrNull()
        )
    }

    @Test
    fun `not clear animation when view is visible`() {
        val div = createDiv(transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)).toBlock(resolver, path)

        baseBinder.bindView(view, div, null, divView)

        verify(view, never()).clearAnimation()
    }

    @Test
    fun `clear animation when view is invisible`() {
        val div = createDiv(
            visibility = DivVisibility.INVISIBLE,
            transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)
        ).toBlock(resolver, path)

        baseBinder.bindView(view, div, null, divView)

        verify(view).clearAnimation()
    }

    @Test
    fun `clear animation when view is gone`() {
        val div = createDiv(
            visibility = DivVisibility.GONE,
            transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)
        ).toBlock(resolver, path)

        baseBinder.bindView(view, div, null, divView)

        verify(view).clearAnimation()
    }

    @Test
    fun `set isFocusableInTouchMode true when div has focus block`() {
        val div = createDiv(focus = DivFocus()).toBlock(resolver, path)

        baseBinder.bindView(view, div, null, divView)

        assert(view.isFocusableInTouchMode)
    }

    @Test
    fun `set isFocusableInTouchMode false when div has no focus block`() {
        val div = createDiv(focus = null).toBlock(resolver, path)

        baseBinder.bindView(view, div, null, divView)

        assert(!view.isFocusableInTouchMode)
    }

    @Test
    fun `image without url is gone when hideImagesWithoutUrl is set`() {
        assertImageVisibility(View.GONE, hidingBinder, image())
    }

    @Test
    fun `image with empty scheme url is visible when hideImagesWithoutUrl is set`() {
        assertImageVisibility(View.VISIBLE, hidingBinder, image(imageUrl = "empty://"))
    }

    @Test
    fun `image without url is visible by default`() {
        assertImageVisibility(View.VISIBLE, baseBinder, image())
    }

    @Test
    fun `gif without url is gone when hideImagesWithoutUrl is set`() {
        assertGifVisibility(View.GONE, hidingBinder, gifImage())
    }

    @Test
    fun `gif with empty scheme url is visible when hideImagesWithoutUrl is set`() {
        assertGifVisibility(View.VISIBLE, hidingBinder, gifImage(url = "empty://"))
    }

    @Test
    fun `gif without url is visible by default`() {
        assertGifVisibility(View.VISIBLE, baseBinder, gifImage())
    }

    @Test
    fun `image hidden by hideImagesWithoutUrl ignores visibility`() {
        assertImageVisibility(View.GONE, hidingBinder, image(visibility = DivVisibility.INVISIBLE))
    }

    @Test
    fun `image hidden by hideImagesWithoutUrl becomes visible when rebound with url`() {
        val view = DivImageView(context())
        val oldDiv = image().toBlock(resolver, path)
        val div = image(imageUrl = "empty://").toBlock(resolver, path)
        hidingBinder.bindView(view, oldDiv, null, divView)

        hidingBinder.bindView(view, div, oldDiv, divView)

        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun `image losing url is hidden without transition when hideImagesWithoutUrl is set`() {
        val view = DivImageView(context())
        val fade = DivAppearanceTransition.Fade(DivFadeTransition())
        val oldDiv = image(imageUrl = "empty://", transitionOut = fade).toBlock(resolver, path)
        val div = image(transitionOut = fade).toBlock(resolver, path)
        hidingBinder.bindView(view, oldDiv, null, divView)

        hidingBinder.bindView(view, div, oldDiv, divView)

        assertEquals(View.GONE, view.visibility)
        verify(transitionHandler, never()).putTransition(any(), any(), any())
    }

    @Test
    fun `image becoming gone by visibility is hidden with transition`() {
        val view = DivImageView(context())
        val fade = DivAppearanceTransition.Fade(DivFadeTransition())
        val oldDiv = image(imageUrl = "empty://", transitionOut = fade).toBlock(resolver, path)
        val div = image(imageUrl = "empty://", transitionOut = fade, visibility = DivVisibility.GONE)
            .toBlock(resolver, path)
        hidingBinder.bindView(view, oldDiv, null, divView)

        hidingBinder.bindView(view, div, oldDiv, divView)

        verify(transitionHandler).putTransition(any(), eq(view), any())
    }

    private fun assertImageVisibility(expected: Int, binder: DivBaseBinder, div: Div) {
        val view = DivImageView(context())

        binder.bindView(view, div.toBlock(resolver, path), null, divView)

        assertEquals(expected, view.visibility)
    }

    private fun assertGifVisibility(expected: Int, binder: DivBaseBinder, div: Div) {
        val view = DivGifImageView(context())

        binder.bindView(view, div.toBlock(resolver, path), null, divView)

        assertEquals(expected, view.visibility)
    }

    private fun createDiv(
        paddings: DivEdgeInsets? = null,
        visibility: DivVisibility = DivVisibility.VISIBLE,
        transitionTriggers: List<DivTransitionTrigger>? = null,
        focus: DivFocus? = null
    ) = Div.Text(DivText(
        text = "text".asExpression(),
        paddings = paddings,
        visibility = visibility.asExpression(),
        transitionTriggers = transitionTriggers,
        focus = focus,
    ))
}
