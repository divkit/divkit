package com.yandex.div.core.view2.divs.widgets

import android.view.View
import androidx.annotation.VisibleForTesting
import com.yandex.div.R
import com.yandex.div.core.DivCustomViewAdapter
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.util.releasableList
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.Releasable
import com.yandex.div2.DivBase
import com.yandex.div2.DivCustom
import javax.inject.Inject

@DivViewScope
@Mockable
internal class ReleaseViewVisitor @Inject constructor(
    private val divView: Div2View,
    private val divCustomViewAdapter: DivCustomViewAdapter?,
    private val divExtensionController: DivExtensionController
) : DivViewVisitor() {

    override fun visit(view: DivWrapLayout) = releaseInternal(view, view.div)

    override fun visit(view: DivFrameLayout) = releaseInternal(view, view.div)

    override fun visit(view: DivGifImageView) = releaseInternal(view, view.div)

    override fun visit(view: DivGridLayout) = releaseInternal(view, view.div)

    override fun visit(view: DivImageView) = releaseInternal(view, view.div)

    override fun visit(view: DivLinearLayout) = releaseInternal(view, view.div)

    override fun visit(view: DivLineHeightTextView) = releaseInternal(view, view.div)

    override fun visit(view: DivPagerIndicatorView) = releaseInternal(view, view.div)

    override fun visit(view: DivPagerView) = releaseInternal(view, view.div)

    override fun visit(view: DivRecyclerView) = releaseInternal(view, view.div)

    override fun visit(view: DivSeparatorView) = releaseInternal(view, view.div)

    override fun visit(view: DivSnappyRecyclerView) = releaseInternal(view, view.div)

    override fun visit(view: DivStateLayout) = releaseInternal(view, view.divState)

    override fun visit(view: DivSliderView) = releaseInternal(view, view.div)

    override fun visit(view: View) {
        release(view)
        val divCustom: DivCustom? = view.getTag(R.id.div_custom_tag) as? DivCustom
        if (divCustom != null) {
            divCustomViewAdapter?.release(view, divCustom)
        }
    }

    private fun releaseInternal(view: View, div: DivBase?) {
        if (div != null) {
            divExtensionController.unbindView(divView, view, div)
        }
        release(view)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun release(view: View) {
        if (view is Releasable) {
            view.release()
        }

        view.releasableList?.forEach { releasable ->
            releasable.release()
        }
    }
}
