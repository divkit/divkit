package com.yandex.div.core.view2.divs.tabs

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import com.yandex.div.DivDataTag
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.font.DivTypefaceType
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.divs.*
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.applyFontSize
import com.yandex.div.core.view2.divs.applyLetterSpacing
import com.yandex.div.core.view2.divs.applyLineHeight
import com.yandex.div.core.view2.divs.applyMargins
import com.yandex.div.core.view2.divs.applyPaddings
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
import com.yandex.div.internal.widget.tabs.TabsLayout
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivSize
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
    private val visibilityActionTracker: DivVisibilityActionTracker,
    private val divPatchCache: DivPatchCache,
    @Named(Names.THEMED_CONTEXT) private val context: Context,
) {
    private var oldDivSelectedTab: Long? = null

    init {
        viewPool.register(TAG_TAB_HEADER, TabTitlesLayoutView.TabViewFactory(context), 12)
        viewPool.register(TAG_TAB_ITEM, { TabItemLayout(context) }, 2)
    }

    fun bindView(view: TabsLayout, div: DivTabs, divView: Div2View, divBinder: DivBinder, path: DivStatePath) {
        val oldDiv = view.div
        val resolver = divView.expressionResolver
        view.div = div
        if (oldDiv != null) {
            baseBinder.unbindExtensions(view, oldDiv, divView)
            if (oldDiv == div) {
                view.divTabsAdapter?.applyPatch(resolver, div)?.let {
                    view.div = it
                    return@bindView
                }
            }
        }

        view.closeAllSubscription()
        val subscriber = view.expressionSubscriber

        baseBinder.bindView(view, div, oldDiv, divView)

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
        view.pagerLayout.clipToPadding = false
        div.separatorPaddings.observe(resolver, subscriber) {
            view.divider.applyMargins(div.separatorPaddings, resolver)
        }
        subscriber.addSubscription(div.separatorColor.observeAndGet(resolver) {
            view.divider.setBackgroundColor(it)
        })
        subscriber.addSubscription(div.hasSeparator.observeAndGet(resolver) { hasSeparator ->
            view.divider.visibility = if (hasSeparator) View.VISIBLE else View.GONE
        })
        view.titleLayout.setOnScrollChangedListener { div2Logger.logTabTitlesScroll(divView) }

        bindAdapter(path, divView, view, oldDiv, div, divBinder, resolver, subscriber)

        subscriber.addSubscription(div.restrictParentScroll.observeAndGet(resolver) { restrictScroll ->
            view.viewPager.onInterceptTouchEventListener = if (restrictScroll) {
                ParentScrollRestrictor(ParentScrollRestrictor.DIRECTION_HORIZONTAL)
            } else {
                null
            }
        })
    }

    private fun bindAdapter(
        path: DivStatePath,
        divView: Div2View,
        view: TabsLayout,
        oldDiv: DivTabs?,
        div: DivTabs,
        divBinder: DivBinder,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        val list = div.items.map { DivSimpleTab(it, view.resources.displayMetrics, resolver) }

        fun setupNewAdapter(selectedTab: Int) {
            val divTabsUi = createAdapter(divView, div, resolver, view, divBinder, path)
            divTabsUi.setData({ list }, selectedTab)
            view.divTabsAdapter = divTabsUi
        }

        val reusableAdapter = view.divTabsAdapter.tryReuse(div, resolver)

        if (reusableAdapter != null) {
            reusableAdapter.path = path
            reusableAdapter.divTabsEventManager.div = div
            if (oldDiv == div) {
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

    private fun createAdapter(divView: Div2View,
                              div: DivTabs,
                              resolver: ExpressionResolver,
                              view: TabsLayout,
                              divBinder: DivBinder,
                              path: DivStatePath,
    ): DivTabsAdapter {
        val eventManager = DivTabsEventManager(divView, actionBinder, div2Logger, visibilityActionTracker, view, div)
        val isDynamicHeight = div.dynamicHeight.evaluate(resolver)
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
            divView, textStyleProvider, viewCreator, divBinder,
            eventManager, path, divPatchCache
        )

    }

    private fun getDisabledScrollPages(lastPageNumber: Int, isSwipeEnabled: Boolean): MutableSet<Int> {
        return if (isSwipeEnabled) mutableSetOf() else (0..lastPageNumber).toMutableSet()
    }

    private fun TabTitlesLayoutView<*>.observeHeight(div: DivTabs, resolver: ExpressionResolver) {
        val applyHeight = { _: Any? ->
            val textPaddings = div.tabTitleStyle.paddings
            val layoutPaddings = div.titlePaddings
            val lineHeight = div.tabTitleStyle.lineHeight?.evaluate(resolver)
                ?: (div.tabTitleStyle.fontSize.evaluate(resolver) * DEFAULT_LINE_HEIGHT_COEFFICIENT).toLong()

            val height = lineHeight + textPaddings.top.evaluate(resolver) + textPaddings.bottom.evaluate(resolver) + layoutPaddings.top.evaluate(resolver) + layoutPaddings.bottom.evaluate(resolver)
            val metrics = resources.displayMetrics
            layoutParams.height = height.spToPx(metrics)
        }
        applyHeight(null)

        val subscriber = expressionSubscriber
        div.tabTitleStyle.lineHeight?.let {
            subscriber.addSubscription(it.observe(resolver, applyHeight))
        }

        subscriber.addSubscription(div.tabTitleStyle.fontSize.observe(resolver, applyHeight))
        subscriber.addSubscription(div.tabTitleStyle.paddings.top.observe(resolver, applyHeight))
        subscriber.addSubscription(div.tabTitleStyle.paddings.bottom.observe(resolver, applyHeight))
        subscriber.addSubscription(div.titlePaddings.top.observe(resolver, applyHeight))
        subscriber.addSubscription(div.titlePaddings.bottom.observe(resolver, applyHeight))
    }

    private fun TabsLayout.observeStyle(resolver: ExpressionResolver, style: DivTabs.TabTitleStyle) {
        titleLayout.applyStyle(resolver, style)

        val subscriber = expressionSubscriber
        fun Expression<*>?.addToSubscriber() =
            subscriber.addSubscription(this?.observe(resolver) {
                titleLayout.applyStyle(
                    resolver,
                    style
                )
            } ?: Disposable.NULL)

        style.activeTextColor.addToSubscriber()
        style.activeBackgroundColor.addToSubscriber()
        style.inactiveTextColor.addToSubscriber()
        style.inactiveBackgroundColor.addToSubscriber()
        style.cornerRadius?.addToSubscriber()
        style.cornersRadius?.topLeft.addToSubscriber()
        style.cornersRadius?.topRight.addToSubscriber()
        style.cornersRadius?.bottomRight.addToSubscriber()
        style.cornersRadius?.bottomLeft.addToSubscriber()
        style.itemSpacing.addToSubscriber()
        style.animationType.addToSubscriber()
        style.animationDuration.addToSubscriber()
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

    companion object {
        const val TAG_TAB_HEADER = "DIV2.TAB_HEADER_VIEW"
        const val TAG_TAB_ITEM = "DIV2.TAB_ITEM_VIEW"
        const val DEFAULT_LINE_HEIGHT_COEFFICIENT = 1.3f
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

    includeFontPadding = false
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
                ?: Disposable.NULL)
        subscriber.addSubscription(paddings.end?.observe(resolver, applyTabPaddings)
                ?: Disposable.NULL)
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
