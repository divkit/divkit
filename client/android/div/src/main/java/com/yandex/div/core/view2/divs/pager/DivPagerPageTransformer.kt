package com.yandex.div.core.view2.divs.pager

import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.util.ViewProperty
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivPageTransformation
import com.yandex.div2.DivPageTransformationOverlap
import com.yandex.div2.DivPageTransformationSlide
import kotlin.math.abs
import kotlin.math.min

internal class DivPagerPageTransformer(
    private val recyclerView: RecyclerView,
    private val resolver: ExpressionResolver,
    private val pageTranslations: SparseArray<Float>,
    private val parentSize: ViewProperty<Int>,
    private val pageTransformation: DivPageTransformation?,
    private val offsetProvider: DivPagerPageOffsetProvider,
    private val isHorizontal: Boolean,
) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        when (val transformation = pageTransformation?.value()) {
            is DivPageTransformationSlide -> transformation.apply(page, position)
            is DivPageTransformationOverlap -> transformation.apply(page, position)
            else -> page.applyOffset(position)
        }
    }

    private fun DivPageTransformationSlide.apply(page: View, position: Float) {
        page.applyAlphaAndScale(
            position, interpolator,
            nextPageAlpha, nextPageScale,
            previousPageAlpha, previousPageScale
        )
        page.applyOffset(position)
    }

    private fun DivPageTransformationOverlap.apply(page: View, position: Float) {
        page.applyAlphaAndScale(
            position, interpolator,
            nextPageAlpha, nextPageScale,
            previousPageAlpha, previousPageScale
        )

        if (position > 0 || (position < 0 && reversedStackingOrder.evaluate(resolver))) {
            page.applyOffset(position)
            page.translationZ = 0f
        } else {
            page.applyOffset(position, overlap = true)
            page.translationZ = -abs(position)
        }
    }

    private fun View.applyAlphaAndScale(
        position: Float,
        interpolator: Expression<DivAnimationInterpolator>,
        nextPageAlpha: Expression<Double>,
        nextPageScale: Expression<Double>,
        previousPageAlpha: Expression<Double>,
        previousPageScale: Expression<Double>,
    ) {
        val coercedPosition = abs(position.coerceAtLeast(-1f).coerceAtMost(1f))
        val androidInterpolator = interpolator.evaluate(resolver).androidInterpolator
        val interpolatedValue = 1 - androidInterpolator.getInterpolation(coercedPosition)

        if (position > 0) {
            applyPageAlpha(interpolatedValue, nextPageAlpha.evaluate(resolver))
            applyPageScale(interpolatedValue, nextPageScale.evaluate(resolver))
        } else {
            applyPageAlpha(interpolatedValue, previousPageAlpha.evaluate(resolver))
            applyPageScale(interpolatedValue, previousPageScale.evaluate(resolver))
        }
    }

    private fun View.applyOffset(position: Float, overlap: Boolean = false) {
        val pagePosition = recyclerView.getChildAdapterPosition(this)
        if (pagePosition == RecyclerView.NO_POSITION) return

        /**
         * This initial values is used to stick edge items to the edges of the pager.
         */
        var offset = - if (overlap) {
            parentSize.get() * position
        } else {
            offsetProvider.getPageOffset(position, pagePosition, pageTransformation is DivPageTransformation.Overlap)
        }

        if (isHorizontal && recyclerView.isLayoutRtl()) {
            offset = -offset
        }
        applyEvaluatedOffset(pagePosition, offset)
    }

    private fun View.applyEvaluatedOffset(pagePosition: Int, offset: Float) {
        pageTranslations.put(pagePosition, offset)

        if (isHorizontal) {
            translationX = offset
        } else {
            translationY = offset
        }
    }

    private fun View.applyPageAlpha(interpolatedValue: Float, cornerAlpha: Double) {
        val adapterPosition = recyclerView.getChildAdapterPosition(this)
        val adapter = recyclerView.adapter as? DivPagerAdapter ?: return
        val div = adapter.itemsToShow[adapterPosition].div
        val pageAlpha = div.value().alpha.evaluate(resolver)

        alpha = getInterpolation(pageAlpha, cornerAlpha, interpolatedValue).toFloat()
    }

    private fun View.applyPageScale(interpolatedValue: Float, cornerScale: Double) {
        if (cornerScale == 1.0) return

        getInterpolation(1.0, cornerScale, interpolatedValue).toFloat().let {
            scaleX = it
            scaleY = it
        }
    }

    private fun getInterpolation(value1: Double, value2: Double, interpolatedValue: Float) =
        min(value1, value2) + abs(value2 - value1) * interpolatedValue
}
