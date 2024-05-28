package com.yandex.div.core.view2.divs.tabs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.PictureDrawable
import android.util.DisplayMetrics
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.yandex.div.DivDataTag
import com.yandex.div.R
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.font.DivTypefaceType
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.applyFontSize
import com.yandex.div.core.view2.divs.applyLetterSpacing
import com.yandex.div.core.view2.divs.applyLineHeight
import com.yandex.div.core.view2.divs.applyMargins
import com.yandex.div.core.view2.divs.applyPaddings
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.spToPx
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div.core.view2.divs.widgets.ParentScrollRestrictor
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.internal.viewpool.ViewPool
import com.yandex.div.internal.widget.tabs.BaseDivTabbedCardUi
import com.yandex.div.internal.widget.tabs.BaseIndicatorTabLayout
import com.yandex.div.internal.widget.tabs.DynamicCardHeightCalculator
import com.yandex.div.internal.widget.tabs.HeightCalculatorFactory
import com.yandex.div.internal.widget.tabs.MaxCardHeightCalculator
import com.yandex.div.internal.widget.tabs.TabItemLayout
import com.yandex.div.internal.widget.tabs.TabTextStyleProvider
import com.yandex.div.internal.widget.tabs.TabTitlesLayoutView
import com.yandex.div.internal.widget.tabs.TabView
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivTabs
import javax.inject.Inject
import javax.inject.Named

@DivScope
internal class DivTabsBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val viewCreator: DivViewCreator,
    private val viewPool: ViewPool,
    private val textStyleProvider: TabTextStyleProvider,
    private val actionBinder: DivActionBinder,
    private val div2Logger: Div2Logger,
    private val imageLoader: DivImageLoader,
    private val visibilityActionTracker: DivVisibilityActionTracker,
    private val divPatchCache: DivPatchCache,
    @Named(Names.THEMED_CONTEXT) private val context: Context,
) {
    private var oldDivSelectedTab: Long? = null

    init {
        viewPool.register(TAG_TAB_HEADER, TabTitlesLayoutView.TabViewFactory(context), 12)
        viewPool.register(TAG_TAB_ITEM, { TabItemLayout(context) }, 2)
    }

    fun bindView(context: BindingContext, view: DivTabsLayout, div: DivTabs, divBinder: DivBinder, path: DivStatePath) {
        val oldDiv = view.div
        val resolver = context.expressionResolver
        if (oldDiv === div) {
            view.divTabsAdapter?.applyPatch(resolver, div)?.let {
                view.div = it
                return@bindView
            }
        }

        val divView = context.divView
        baseBinder.bindView(context, view, div, oldDiv)

        view.clipToPadding = false

        val applyPaddings = { _: Any? ->
            view.titleLayout.applyPaddings(div.titlePaddings, resolver)
        }
        applyPaddings(null)
        div.titlePaddings.left.observe(resolver, applyPaddings)
        div.titlePaddings.right.observe(resolver, applyPaddings)
        div.titlePaddings.top.observe(resolver, applyPaddings)
        div.titlePaddings.bottom.observe(resolver, applyPaddings)

        view.titleLayout.observeHeight(div, resolver)
        view.observeStyle(resolver, div.tabTitleStyle)
        view.observeDividerStyle(resolver, div.tabTitleDelimiter, context)
        view.pagerLayout.clipToPadding = false
        div.separatorPaddings.observe(resolver, view) {
            view.divider.applyMargins(div.separatorPaddings, resolver)
        }
        view.addSubscription(div.separatorColor.observeAndGet(resolver) {
            view.divider.setBackgroundColor(it)
        })
        view.addSubscription(div.hasSeparator.observeAndGet(resolver) { hasSeparator ->
            view.divider.visibility = if (hasSeparator) View.VISIBLE else View.GONE
        })
        view.titleLayout.setOnScrollChangedListener { div2Logger.logTabTitlesScroll(divView) }

        bindAdapter(path, context, view, oldDiv, div, divBinder, view)

        view.addSubscription(div.restrictParentScroll.observeAndGet(resolver) { restrictScroll ->
            view.viewPager.onInterceptTouchEventListener = if (restrictScroll) {
                ParentScrollRestrictor
            } else {
                null
            }
        })
    }

    private fun bindAdapter(
        path: DivStatePath,
        bindingContext: BindingContext,
        view: DivTabsLayout,
        oldDiv: DivTabs?,
        div: DivTabs,
        divBinder: DivBinder,
        subscriber: ExpressionSubscriber
    ) {
        val resolver = bindingContext.expressionResolver
        val list = div.items.map { DivSimpleTab(it, view.resources.displayMetrics, resolver) }

        fun setupNewAdapter(selectedTab: Int) {
            val divTabsUi = createAdapter(bindingContext, div, view, divBinder, path)
            divTabsUi.setData({ list }, selectedTab)
            view.divTabsAdapter = divTabsUi
        }

        val reusableAdapter = view.divTabsAdapter.tryReuse(div, resolver)

        if (reusableAdapter != null) {
            reusableAdapter.path = path
            reusableAdapter.divTabsEventManager.div = div
            if (oldDiv === div) {
                // rebind tabs only
                reusableAdapter.notifyStateChanged()
            } else {
                reusableAdapter.setData({ list }, resolver, subscriber)
            }
        } else {
            setupNewAdapter(div.selectedTab.evaluate(resolver).toIntSafely())
        }

        div.items.observeFixedHeightChange(resolver, subscriber) {
            view.divTabsAdapter?.notifyStateChanged()
        }

        val selectTab = { selectedTab: Long ->
            oldDivSelectedTab = selectedTab
            view.divTabsAdapter?.pager?.let {
                val selectedTabInt = selectedTab.toIntSafely()
                if (it.currentItemIndex != selectedTabInt) {
                    it.smoothScrollTo(selectedTabInt)
                }
            }
            Unit
        }

        subscriber.addSubscription(div.dynamicHeight.observe(resolver) { dynamicHeight ->
            if (view.divTabsAdapter?.isDynamicHeight == dynamicHeight) {
                return@observe
            }

            setupNewAdapter(
                view.divTabsAdapter?.pager?.currentItemIndex
                ?: div.selectedTab.evaluate(resolver).toIntSafely()
            )
        })

        subscriber.addSubscription(div.selectedTab.observe(resolver, selectTab))

        val divView = bindingContext.divView
        val isDataTagTheSame = divView.prevDataTag == DivDataTag.INVALID
                || divView.dataTag == divView.prevDataTag

        val selectedTab = div.selectedTab.evaluate(resolver)
        val keepSelectedTab = isDataTagTheSame && oldDivSelectedTab == selectedTab

        if (!keepSelectedTab) {
            selectTab(selectedTab)
        }

        subscriber.addSubscription(div.switchTabsByContentSwipeEnabled.observeAndGet(resolver) {
            view.divTabsAdapter?.setDisabledScrollPages(
                getDisabledScrollPages(
                    lastPageNumber = div.items.size - 1,
                    isSwipeEnabled = it
                )
            )
        })
    }

    private fun createAdapter(
        bindingContext: BindingContext,
        div: DivTabs,
        view: DivTabsLayout,
        divBinder: DivBinder,
        path: DivStatePath,
    ): DivTabsAdapter {
        val eventManager =
            DivTabsEventManager(bindingContext, actionBinder, div2Logger, visibilityActionTracker, view, div)
        val isDynamicHeight = div.dynamicHeight.evaluate(bindingContext.expressionResolver)
        val heightCalculatorFactory = if (isDynamicHeight) {
            HeightCalculatorFactory(::DynamicCardHeightCalculator)
        } else {
            HeightCalculatorFactory(::MaxCardHeightCalculator)
        }
        val selectedTab = view.viewPager.currentItem
        val currentTab = view.viewPager.currentItem
        if (currentTab == selectedTab) {
            // Tab view could be not laid out at this point, so post display event
            UiThreadHandler.postOnMainThread {
                eventManager.onPageDisplayed(currentTab)
            }
        }
        return DivTabsAdapter(
            viewPool, view, getTabbedCardLayoutIds(), heightCalculatorFactory, isDynamicHeight,
            bindingContext, textStyleProvider, viewCreator, divBinder,
            eventManager, path, divPatchCache
        )

    }

    private fun getDisabledScrollPages(lastPageNumber: Int, isSwipeEnabled: Boolean): MutableSet<Int> {
        return if (isSwipeEnabled) mutableSetOf() else (0..lastPageNumber).toMutableSet()
    }

    private fun TabTitlesLayoutView<*>.observeHeight(div: DivTabs, resolver: ExpressionResolver) {
        val applyHeight = { _: Any? ->
            val tabTitleStyle = div.tabTitleStyle ?: DEFAULT_TAB_TITLE_STYLE
            val textPaddings = tabTitleStyle.paddings
            val layoutPaddings = div.titlePaddings
            val lineHeight = tabTitleStyle.lineHeight?.evaluate(resolver)
                ?: (tabTitleStyle.fontSize.evaluate(resolver) * DEFAULT_LINE_HEIGHT_COEFFICIENT).toLong()

            val height = lineHeight + textPaddings.top.evaluate(resolver) + textPaddings.bottom.evaluate(resolver) + layoutPaddings.top.evaluate(resolver) + layoutPaddings.bottom.evaluate(resolver)
            val metrics = resources.displayMetrics
            layoutParams.height = height.spToPx(metrics)
        }
        applyHeight(null)

        val subscriber = expressionSubscriber
        subscriber.addSubscription(div.tabTitleStyle?.lineHeight?.observe(resolver, applyHeight))
        subscriber.addSubscription(div.tabTitleStyle?.fontSize?.observe(resolver, applyHeight))
        subscriber.addSubscription(div.tabTitleStyle?.paddings?.top?.observe(resolver, applyHeight))
        subscriber.addSubscription(div.tabTitleStyle?.paddings?.bottom?.observe(resolver, applyHeight))
        subscriber.addSubscription(div.titlePaddings.top.observe(resolver, applyHeight))
        subscriber.addSubscription(div.titlePaddings.bottom.observe(resolver, applyHeight))
    }

    private fun DivTabsLayout.observeStyle(resolver: ExpressionResolver, style: DivTabs.TabTitleStyle?) {
        titleLayout.applyStyle(resolver, style ?: DEFAULT_TAB_TITLE_STYLE)

        val callback = { _: Any? ->
            titleLayout.applyStyle(resolver, style ?: DEFAULT_TAB_TITLE_STYLE)
        }

        style?.activeTextColor?.observe(resolver, callback)
        style?.activeBackgroundColor?.observe(resolver, callback)
        style?.inactiveTextColor?.observe(resolver, callback)
        style?.inactiveBackgroundColor?.observe(resolver, callback)
        style?.cornerRadius?.observe(resolver, callback)
        style?.cornersRadius?.topLeft?.observe(resolver, callback)
        style?.cornersRadius?.topRight?.observe(resolver, callback)
        style?.cornersRadius?.bottomRight?.observe(resolver, callback)
        style?.cornersRadius?.bottomLeft?.observe(resolver, callback)
        style?.itemSpacing?.observe(resolver, callback)
        style?.animationType?.observe(resolver, callback)
        style?.animationDuration?.observe(resolver, callback)
    }

    private fun TabTitlesLayoutView<*>.applyStyle(resolver: ExpressionResolver, style: DivTabs.TabTitleStyle) {
        setTabColors(
            style.activeTextColor.evaluate(resolver),
            style.activeBackgroundColor.evaluate(resolver),
            style.inactiveTextColor.evaluate(resolver),
            style.inactiveBackgroundColor?.evaluate(resolver) ?: Color.TRANSPARENT
        )
        val metrics = resources.displayMetrics
        setTabIndicatorCornersRadii(style.getCornerRadii(metrics, resolver))
        setTabItemSpacing(style.itemSpacing.evaluate(resolver).dpToPx(metrics))
        setAnimationType(when(style.animationType.evaluate(resolver)) {
            DivTabs.TabTitleStyle.AnimationType.SLIDE -> BaseIndicatorTabLayout.AnimationType.SLIDE
            DivTabs.TabTitleStyle.AnimationType.FADE -> BaseIndicatorTabLayout.AnimationType.FADE
            DivTabs.TabTitleStyle.AnimationType.NONE -> BaseIndicatorTabLayout.AnimationType.NONE
        })
        setAnimationDuration(style.animationDuration.evaluate(resolver))
        setTabTitleStyle(style)
    }

    private fun DivTabsLayout.observeDividerStyle(
        resolver: ExpressionResolver,
        style: DivTabs.TabTitleDelimiter?,
        bindingContext: BindingContext,
    ) {
        style ?: return
        titleLayout.applyDelimiterStyle(resolver, style, bindingContext)
        val callback = { _: Any? -> titleLayout.applyDelimiterStyle(resolver, style, bindingContext) }

        style.width.value.observe(resolver, callback)
        style.width.unit.observe(resolver, callback)
        style.height.value.observe(resolver, callback)
        style.height.unit.observe(resolver, callback)
        style.imageUrl.observe(resolver, callback)
    }

    private fun TabTitlesLayoutView<*>.applyDelimiterStyle(
        resolver: ExpressionResolver,
        style: DivTabs.TabTitleDelimiter,
        bindingContext: BindingContext,
    ) {
        val metrics = resources.displayMetrics
        val evaluatedWidth = style.width.let { width ->
            width.value.evaluate(resolver).toPx(width.unit.evaluate(resolver), metrics)
        }
        val evaluatedHeight = style.height.let { height ->
            height.value.evaluate(resolver).toPx(height.unit.evaluate(resolver), metrics)
        }

        val reference = imageLoader.loadImage(
            style.imageUrl.evaluate(resolver).toString(),
            object : DivIdLoggingImageDownloadCallback(bindingContext.divView) {
                override fun onSuccess(cachedBitmap: CachedBitmap) {
                    super.onSuccess(cachedBitmap)
                    setTabDelimiter(cachedBitmap.bitmap, evaluatedWidth, evaluatedHeight)
                }

                override fun onSuccess(pictureDrawable: PictureDrawable) {
                    super.onSuccess(pictureDrawable)
                    setTabDelimiter(pictureDrawable.toBitmap(), evaluatedWidth, evaluatedHeight)
                }

                override fun onError() {
                    super.onError()
                    setTabDelimiter(null, 0, 0)
                }
            }
        )

        bindingContext.divView.addLoadReference(reference, this)
    }

    private fun DivTabs.TabTitleStyle.getCornerRadii(
        metrics: DisplayMetrics,
        resolver: ExpressionResolver
    ): FloatArray {
        fun Expression<Long>.toCornerRadii() = this.evaluate(resolver).dpToPx(metrics).toFloat()

        val defaultRadius = cornerRadius?.toCornerRadii()
            ?: if (cornersRadius == null) BaseIndicatorTabLayout.UNDEFINED_RADIUS else 0f

        val topLeft = cornersRadius?.topLeft?.toCornerRadii() ?: defaultRadius
        val topRight = cornersRadius?.topRight?.toCornerRadii() ?: defaultRadius
        val bottomLeft = cornersRadius?.bottomLeft?.toCornerRadii() ?: defaultRadius
        val bottomRight = cornersRadius?.bottomRight?.toCornerRadii() ?: defaultRadius

        return floatArrayOf(
            topLeft, topLeft,
            topRight, topRight,
            bottomRight, bottomRight,
            bottomLeft, bottomLeft
        )
    }

    private fun getTabbedCardLayoutIds(): BaseDivTabbedCardUi.TabbedCardConfig {
        return BaseDivTabbedCardUi.TabbedCardConfig(
            R.id.base_tabbed_title_container_scroller,
            R.id.div_tabs_pager_container,
            R.id.div_tabs_container_helper,
            true,
            false,
            TAG_TAB_HEADER,
            TAG_TAB_ITEM
        )
    }

    private companion object {
        private const val TAG_TAB_HEADER = "DIV2.TAB_HEADER_VIEW"
        private const val TAG_TAB_ITEM = "DIV2.TAB_ITEM_VIEW"
        private const val DEFAULT_LINE_HEIGHT_COEFFICIENT = 1.3f

        private val DEFAULT_TAB_TITLE_STYLE = DivTabs.TabTitleStyle()
    }
}

private fun DivTabsAdapter?.tryReuse(div: DivTabs, resolver: ExpressionResolver): DivTabsAdapter? {
    if (this == null) {
        return null
    }

    if (this.isDynamicHeight != div.dynamicHeight.evaluate(resolver)) {
        return null
    }

    return this
}

private fun List<DivTabs.Item>.observeFixedHeightChange(
    resolver: ExpressionResolver,
    subscriber: ExpressionSubscriber,
    observer: (_: Any?) -> Unit
) {
    this.forEach { item ->
        val height = item.div.value().height
        if (height is DivSize.Fixed) {
            subscriber.addSubscription(height.value.unit.observe(resolver, observer))
            subscriber.addSubscription(height.value.value.observe(resolver, observer))
        }
    }
}

private fun DivEdgeInsets.observe(resolver: ExpressionResolver, subscriber: ExpressionSubscriber,
                                  observer: (Any?) -> Unit) {
    subscriber.addSubscription(this.left.observe(resolver, observer))
    subscriber.addSubscription(this.right.observe(resolver, observer))
    subscriber.addSubscription(this.top.observe(resolver, observer))
    subscriber.addSubscription(this.bottom.observe(resolver, observer))
    observer.invoke(null)
}

internal fun TabView.observeStyle(style: DivTabs.TabTitleStyle, resolver: ExpressionResolver, subscriber: ExpressionSubscriber) {
    val applyStyle = { _: Any? ->
        val fontSize = style.fontSize.evaluate(resolver).toIntSafely()
        applyFontSize(fontSize, style.fontSizeUnit.evaluate(resolver))
        applyLetterSpacing(style.letterSpacing.evaluate(resolver), fontSize)
        applyLineHeight(style.lineHeight?.evaluate(resolver), style.fontSizeUnit.evaluate(resolver))
    }
    subscriber.addSubscription(style.fontSize.observe(resolver, applyStyle))
    subscriber.addSubscription(style.fontSizeUnit.observe(resolver, applyStyle))
    style.lineHeight?.observe(resolver, applyStyle)?.let {
        subscriber.addSubscription(it)
    }

    applyStyle(null)

    val paddings = style.paddings
    val metrics = resources.displayMetrics
    val applyTabPaddings = { _: Any? ->
        if (paddings.start != null || paddings.end != null) {
            setTabPadding(
                    paddings.start?.evaluate(resolver).dpToPx(metrics),
                    paddings.top.evaluate(resolver).dpToPx(metrics),
                    paddings.end?.evaluate(resolver).dpToPx(metrics),
                    paddings.bottom.evaluate(resolver).dpToPx(metrics)
            )
        } else {
            setTabPadding(
                    paddings.left.evaluate(resolver).dpToPx(metrics),
                    paddings.top.evaluate(resolver).dpToPx(metrics),
                    paddings.right.evaluate(resolver).dpToPx(metrics),
                    paddings.bottom.evaluate(resolver).dpToPx(metrics)
            )
        }
    }

    subscriber.addSubscription(paddings.top.observe(resolver, applyTabPaddings))
    subscriber.addSubscription(paddings.bottom.observe(resolver, applyTabPaddings))
    if (paddings.start != null || paddings.end != null) {
       subscriber.addSubscription(paddings.start?.observe(resolver, applyTabPaddings)
               )
        subscriber.addSubscription(paddings.end?.observe(resolver, applyTabPaddings)
               )
    } else {
        subscriber.addSubscription(paddings.left.observe(resolver, applyTabPaddings))
        subscriber.addSubscription(paddings.right.observe(resolver, applyTabPaddings))
    }
    applyTabPaddings(null)

    fun Expression<DivFontWeight>.addToSubscriber(callback: (DivFontWeight) -> Unit) {
        subscriber.addSubscription(this.observeAndGet(resolver, callback))
    }

    (style.inactiveFontWeight ?: style.fontWeight).addToSubscriber { divFontWeight ->
        setInactiveTypefaceType(divFontWeight.toTypefaceType())
    }
    (style.activeFontWeight ?: style.fontWeight).addToSubscriber { divFontWeight ->
        setActiveTypefaceType(divFontWeight.toTypefaceType())
    }
}

private fun DivFontWeight.toTypefaceType() = when (this) {
    DivFontWeight.MEDIUM -> DivTypefaceType.MEDIUM
    DivFontWeight.REGULAR -> DivTypefaceType.REGULAR
    DivFontWeight.LIGHT -> DivTypefaceType.LIGHT
    DivFontWeight.BOLD -> DivTypefaceType.BOLD
}
