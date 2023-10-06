package com.yandex.div.core.view2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.view2.divs.isWrapContainer
import com.yandex.div.core.view2.divs.widgets.DivFrameLayout
import com.yandex.div.core.view2.divs.widgets.DivGifImageView
import com.yandex.div.core.view2.divs.widgets.DivGridLayout
import com.yandex.div.core.view2.divs.widgets.DivImageView
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.core.view2.divs.widgets.DivPagerIndicatorView
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivSelectView
import com.yandex.div.core.view2.divs.widgets.DivSeparatorView
import com.yandex.div.core.view2.divs.widgets.DivSliderView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.core.view2.divs.widgets.DivWrapLayout
import com.yandex.div.internal.core.DivVisitor
import com.yandex.div.internal.viewpool.ViewPool
import com.yandex.div.internal.viewpool.ViewPreCreationProfile
import com.yandex.div.internal.viewpool.optimization.ViewPreCreationProfileRepository
import com.yandex.div.internal.widget.tabs.TabsLayout
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivContainer.Orientation
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Named

@DivScope
@Mockable
internal class DivViewCreator @Inject constructor(
    @Named(Names.THEMED_CONTEXT) private val context: Context,
    private val viewPool: ViewPool,
    private val validator: DivValidator,
    viewPreCreationProfile: ViewPreCreationProfile,
    repository: ViewPreCreationProfileRepository
) : DivVisitor<View>() {

    init {
        val optimizedProfile = viewPreCreationProfile.id?.let { runBlocking { repository.get(it) } }

        with(viewPool) {
            with(optimizedProfile ?: viewPreCreationProfile) {
                register(TAG_TEXT, { DivLineHeightTextView(context) }, text.capacity)
                register(TAG_IMAGE, { DivImageView(context) }, image.capacity)
                register(TAG_GIF_IMAGE, { DivGifImageView(context) }, gifImage.capacity)
                register(TAG_OVERLAP_CONTAINER, { DivFrameLayout(context) }, overlapContainer.capacity)
                register(TAG_LINEAR_CONTAINER, { DivLinearLayout(context) }, linearContainer.capacity)
                register(TAG_WRAP_CONTAINER, { DivWrapLayout(context) }, wrapContainer.capacity)
                register(TAG_GRID, { DivGridLayout(context) }, grid.capacity)
                register(TAG_GALLERY, { DivRecyclerView(context) }, gallery.capacity)
                register(TAG_PAGER, { DivPagerView(context) }, pager.capacity)
                register(TAG_TABS, { TabsLayout(context) }, tab.capacity)
                register(TAG_STATE, { DivStateLayout(context) }, state.capacity)
                register(TAG_CUSTOM, { DivFrameLayout(context) }, custom.capacity)
                register(TAG_INDICATOR, { DivPagerIndicatorView(context) }, indicator.capacity)
                register(TAG_SLIDER, { DivSliderView(context) }, slider.capacity)
                register(TAG_INPUT, { DivInputView(context) }, input.capacity)
                register(TAG_SELECT, { DivSelectView(context) }, select.capacity)
                register(TAG_VIDEO, { DivVideoView(context) }, video.capacity)
            }
        }
    }

    fun create(div: Div, resolver: ExpressionResolver): View {
        return if (validator.validate(div, resolver)) {
            visit(div, resolver)
        } else {
            Space(context)
        }
    }

    override fun defaultVisit(data: Div, resolver: ExpressionResolver): View =
        viewPool.obtain(data.getTag(resolver))

    override fun visit(data: Div.Separator, resolver: ExpressionResolver): View =
        DivSeparatorView(context)

    override fun visit(data: Div.Container, resolver: ExpressionResolver): View {
        val view = defaultVisit(data, resolver) as ViewGroup
        data.value.items.forEach { childData ->
            view.addView(create(childData, resolver))
        }
        return view
    }

    override fun visit(data: Div.Grid, resolver: ExpressionResolver): View {
        val view = defaultVisit(data, resolver) as ViewGroup
        data.value.items.forEach { childData ->
            view.addView(create(childData, resolver))
        }
        return view
    }

    companion object {
        const val TAG_TEXT = "DIV2.TEXT_VIEW"
        const val TAG_IMAGE = "DIV2.IMAGE_VIEW"
        const val TAG_GIF_IMAGE = "DIV2.IMAGE_GIF_VIEW"
        const val TAG_OVERLAP_CONTAINER = "DIV2.OVERLAP_CONTAINER_VIEW"
        const val TAG_LINEAR_CONTAINER = "DIV2.LINEAR_CONTAINER_VIEW"
        const val TAG_WRAP_CONTAINER = "DIV2.WRAP_CONTAINER_VIEW"
        const val TAG_GRID = "DIV2.GRID_VIEW"
        const val TAG_GALLERY = "DIV2.GALLERY_VIEW"
        const val TAG_PAGER = "DIV2.PAGER_VIEW"
        const val TAG_TABS = "DIV2.TAB_VIEW"
        const val TAG_STATE = "DIV2.STATE"
        const val TAG_CUSTOM = "DIV2.CUSTOM"
        const val TAG_INDICATOR = "DIV2.INDICATOR"
        const val TAG_SLIDER = "DIV2.SLIDER"
        const val TAG_INPUT = "DIV2.INPUT"
        const val TAG_SELECT = "DIV2.SELECT"
        const val TAG_VIDEO = "DIV2.VIDEO"

        val TAGS = arrayOf(
            TAG_TEXT,
            TAG_IMAGE,
            TAG_GIF_IMAGE,
            TAG_OVERLAP_CONTAINER,
            TAG_LINEAR_CONTAINER,
            TAG_WRAP_CONTAINER,
            TAG_GRID,
            TAG_GALLERY,
            TAG_PAGER,
            TAG_TABS,
            TAG_STATE,
            TAG_CUSTOM,
            TAG_INDICATOR,
            TAG_SLIDER,
            TAG_INPUT,
            TAG_SELECT,
            TAG_VIDEO
        )

        private fun Div.getTag(resolver: ExpressionResolver) =
            when (this) {
                is Div.Container -> when {
                    value.isWrapContainer(resolver) -> TAG_WRAP_CONTAINER
                    value.orientation.evaluate(resolver) == Orientation.OVERLAP -> TAG_OVERLAP_CONTAINER
                    else -> TAG_LINEAR_CONTAINER
                }
                is Div.Custom -> TAG_CUSTOM
                is Div.Gallery -> TAG_GALLERY
                is Div.GifImage -> TAG_GIF_IMAGE
                is Div.Grid -> TAG_GRID
                is Div.Image -> TAG_IMAGE
                is Div.Indicator -> TAG_INDICATOR
                is Div.Input -> TAG_INPUT
                is Div.Pager -> TAG_PAGER
                is Div.Select -> TAG_SELECT
                is Div.Slider -> TAG_SLIDER
                is Div.State -> TAG_STATE
                is Div.Tabs -> TAG_TABS
                is Div.Text -> TAG_TEXT
                is Div.Video -> TAG_VIDEO
                is Div.Separator -> ""
            }
    }
}
