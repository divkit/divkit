<script lang="ts">
    import { getContext, onDestroy, tick } from 'svelte';
    import { writable } from 'svelte/store';

    import css from './Tabs.module.css';
    import rootCss from '../Root.module.css';

    import type { Mods } from '../../types/general';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivTabsData } from '../../types/tabs';
    import type { Action } from '../../../typings/common';
    import type { EdgeInsets } from '../../types/edgeInserts';
    import type { SwitchElements, Overflow } from '../../types/switch-elements';
    import type { TabItem } from '../../types/tabs';
    import type { MaybeMissing } from '../../expressions/json';
    import type { DivBaseData } from '../../types/base';
    import type { ComponentContext } from '../../types/componentContext';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import Outer from '../utilities/Outer.svelte';
    import Unknown from '../utilities/Unknown.svelte';
    import Actionable from '../utilities/Actionable.svelte';
    import { wrapError } from '../../utils/wrapError';
    import { genClassName } from '../../utils/genClassName';
    import { makeStyle } from '../../utils/makeStyle';
    import { ARROW_LEFT, ARROW_RIGHT, END, HOME } from '../../utils/keyboard/codes';
    import { pxToEm, pxToEmWithUnits } from '../../utils/pxToEm';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { correctEdgeInserts } from '../../utils/correctEdgeInserts';
    import { correctBorderRadius } from '../../utils/correctBorderRadius';
    import { correctColor } from '../../utils/correctColor';
    import { correctFontWeight } from '../../utils/correctFontWeight';
    import { isNonNegativeNumber } from '../../utils/isNonNegativeNumber';
    import { assignIfDifferent } from '../../utils/assignIfDifferent';
    import { Coords, getTouchCoords } from '../../utils/getTouchCoords';
    import { correctEdgeInsertsObject } from '../../utils/correctEdgeInsertsObject';
    import { correctNonNegativeNumber } from '../../utils/correctNonNegativeNumber';
    import { edgeInsertsToCss } from '../../utils/edgeInsertsToCss';
    import { filterEnabledActions } from '../../utils/filterEnabledActions';
    import { nonNegativeModulo } from '../../utils/nonNegativeModulo';

    export let componentContext: ComponentContext<DivTabsData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    interface ChildInfo {
        index: number;
        title: MaybeMissing<string> | undefined;
        title_click_action?: MaybeMissing<Action> | undefined;
    }

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const direction = rootCtx.direction;

    const instId = rootCtx.genId('tabs');

    let prevId: string | undefined;
    let hasError = false;
    let childStore = writable<ChildInfo[]>([]);
    let childLayoutParams: LayoutParams = {};

    let tabsElem: HTMLElement;
    let panelsWrapper: HTMLElement;
    let swiperElem: HTMLElement;
    let mods: Mods = {};

    let tabFontSize = 12;
    let tabPaddings = '';
    let tabLineHeight = '';
    let tabLetterSpacing = '';
    let tabBorderRadius = '';
    let tabActiveFontWeight: number | undefined = undefined;
    let tabActiveFontFamily = '';
    let tabInactiveFontWeight: number | undefined = undefined;
    let tabInactiveFontFamily = '';
    let tabActiveTextColor = '';
    let tabInactiveTextColor = '';
    let tabActiveBackground = '';
    let tabInactiveBackground = '';
    let tabItemSpacing = 0;
    let separatorBackground = '';
    let separatorMargins = '';
    let titlePadding: EdgeInsets | null = null;
    let isSwipeInitialized = false;
    let isAnimated = false;
    let previousSelected: number | undefined;
    let showedPanels: (ComponentContext | undefined)[] = [];
    let visiblePanels: boolean[] = [];
    let hidePanelsTimeout: number | null = null;
    let startCoords: Coords | null = null;
    let moveCoords: Coords | null = null;
    let swipeStartTime: number;
    let isSwipeStarted = false;
    let isSwipeCanceled = false;
    let startTransform: number;
    let currentTransform: number;

    $: origJson = componentContext.origJson;

    function rebind(): void {
        tabFontSize = 12;
        tabPaddings = '';
        tabBorderRadius = '';
        tabActiveFontWeight = undefined;
        tabActiveFontFamily = '';
        tabInactiveFontWeight = undefined;
        tabInactiveFontFamily = '';
        tabActiveTextColor = '';
        tabInactiveTextColor = '';
        tabActiveBackground = '';
        tabInactiveBackground = '';
        tabItemSpacing = 0;
        separatorBackground = '';
        separatorMargins = '';
        titlePadding = null;
    }

    $: if (origJson) {
        rebind();
    }

    $: items = Array.isArray(componentContext.json.items) && componentContext.json.items || [];
    $: parentOfItems = items.map(it => {
        return it.div;
    });

    $: jsonWidth = componentContext.getDerivedFromVars(componentContext.json.width);
    $: jsonHeight = componentContext.getDerivedFromVars(componentContext.json.height);
    $: jsonSelectedTab = componentContext.getJsonWithVars(componentContext.json.selected_tab);
    $: jsonTabStyle = componentContext.getDerivedFromVars(componentContext.json.tab_title_style);
    $: jsonSeparator = componentContext.getDerivedFromVars(componentContext.json.has_separator);
    $: jsonSeparatorColor = componentContext.getDerivedFromVars(componentContext.json.separator_color);
    $: jsonSeparatorPaddings = componentContext.getDerivedFromVars(componentContext.json.separator_paddings);
    $: jsonSwipeEnabled = componentContext.getDerivedFromVars(
        componentContext.json.switch_tabs_by_content_swipe_enabled
    );
    $: jsonRestrictParentScroll = componentContext.getDerivedFromVars(componentContext.json.restrict_parent_scroll);
    $: jsonTitlePaddings = componentContext.getDerivedFromVars(componentContext.json.title_paddings);

    $: selected = jsonSelectedTab && Number(jsonSelectedTab) || 0;

    $: if (Array.isArray(items) && items.length) {
        let children: ChildInfo[] = [];

        items.forEach((item, index) => {
            const part = componentContext.getJsonWithVars({
                index,
                title: item.title,
                title_click_action: item.title_click_action,
            });
            if (part.title && typeof part.title === 'string') {
                children.push(part as ChildInfo);
            } else {
                componentContext.logError(wrapError(new Error('Incorrect title for the tab'), {
                    additional: {
                        index
                    }
                }));
            }
        });

        childStore.set(children);
    } else {
        childStore.set([]);
    }

    function replaceItems(items: (MaybeMissing<DivBaseData> | undefined)[]): void {
        if (!componentContext.json.items) {
            return;
        }

        componentContext = {
            ...componentContext,
            json: {
                ...componentContext.json,
                items: componentContext.json.items.map((it, index) => {
                    return {
                        ...it,
                        div: items[index] as DivBaseData
                    };
                })
            }
        };
    }

    $: {
        if (!$childStore?.length) {
            hasError = true;
            componentContext.logError(wrapError(new Error('Incorrect or empty "items" prop for div "tabs"')));
        } else {
            hasError = false;
        }
    }

    $: {
        let newLayoutParams: LayoutParams = {
            parentContainerOrientation: 'horizontal'
        };

        if ($jsonWidth?.type === 'wrap_content') {
            newLayoutParams.parentHorizontalWrapContent = true;
        }
        if (!$jsonHeight || $jsonHeight.type === 'wrap_content') {
            newLayoutParams.parentVerticalWrapContent = true;
        }

        childLayoutParams = assignIfDifferent(newLayoutParams, childLayoutParams);
    }

    $: if (!hasError && (selected < 0 || selected >= items.length)) {
        componentContext.logError(wrapError(new Error('Incorrect "selected_tab" prop for div "tabs"'), {
            additional: {
                selected: componentContext.json.selected_tab,
                length: items.length
            }
        }));
        selected = selected < 0 ? 0 : items.length - 1;
    }

    $: if (!hasError && !$childStore.some(it => selected === it.index)) {
        componentContext.logError(wrapError(new Error('Incorrect "selected_tab" prop for div "tabs"'), {
            additional: {
                selected: componentContext.json.selected_tab
            }
        }));
        selected = $childStore[0]?.index || 0;
    }

    $: tabStyle = $jsonTabStyle || {};

    $: {
        tabFontSize = correctPositiveNumber(tabStyle.font_size, tabFontSize);
    }

    $: {
        if (tabStyle.font_size || tabStyle.paddings) {
            const paddings: EdgeInsets = tabStyle.paddings || {
                top: 6,
                right: 8,
                bottom: 6,
                left: 8
            };

            const adjustedPaddings: EdgeInsets = {
                top: (Number(paddings.top) || 0) / tabFontSize * 10,
                right: (Number($direction === 'ltr' ? paddings.end : paddings.start) || Number(paddings.right) || 0) / tabFontSize * 10,
                bottom: (Number(paddings.bottom) || 0) / tabFontSize * 10,
                left: (Number($direction === 'ltr' ? paddings.start : paddings.end) || Number(paddings.left) || 0) / tabFontSize * 10
            };

            tabPaddings = correctEdgeInserts(adjustedPaddings, $direction, tabPaddings);
        }
    }

    $: {
        const lineHeight = tabStyle.line_height;
        if (lineHeight !== undefined && isPositiveNumber(lineHeight)) {
            tabLineHeight = pxToEm(lineHeight / tabFontSize * 10);
        }
    }

    $: {
        const letterSpacing = tabStyle.letter_spacing;
        if (letterSpacing !== undefined && isNonNegativeNumber(letterSpacing)) {
            tabLetterSpacing = pxToEm(letterSpacing / tabFontSize * 10);
        }
    }

    $: {
        if (tabStyle.corner_radius || tabStyle.corners_radius || tabStyle.font_size) {
            const defaultRadius = tabStyle.corner_radius ?? 1000;

            if (tabStyle.corners_radius) {
                tabBorderRadius = correctBorderRadius(
                    tabStyle.corners_radius,
                    defaultRadius,
                    tabFontSize,
                    tabBorderRadius
                );
            } else if (isNonNegativeNumber(defaultRadius)) {
                tabBorderRadius = pxToEm(defaultRadius / tabFontSize * 10);
            }
        }
    }

    $: {
        tabActiveFontWeight = correctFontWeight(
            tabStyle.active_font_weight || tabStyle.font_weight,
            undefined,
            tabActiveFontWeight
        );
        if (tabStyle.font_family && typeof tabStyle.font_family === 'string') {
            tabActiveFontFamily = rootCtx.typefaceProvider(tabStyle.font_family, {
                fontWeight: tabActiveFontWeight || 400
            });
        } else {
            tabActiveFontFamily = '';
        }
    }

    $: {
        tabInactiveFontWeight = correctFontWeight(
            tabStyle.inactive_font_weight || tabStyle.font_weight,
            undefined,
            tabInactiveFontWeight
        );
        if (tabStyle.font_family && typeof tabStyle.font_family === 'string') {
            tabInactiveFontFamily = rootCtx.typefaceProvider(tabStyle.font_family, {
                fontWeight: tabInactiveFontWeight || 400
            });
        } else {
            tabInactiveFontFamily = '';
        }
    }

    $: {
        tabActiveTextColor = correctColor(tabStyle.active_text_color, 1, tabActiveTextColor);
    }

    $: {
        tabInactiveTextColor = correctColor(tabStyle.inactive_text_color, 1, tabInactiveTextColor);
    }

    $: {
        tabActiveBackground = correctColor(tabStyle.active_background_color, 1, tabActiveBackground);
    }

    $: {
        tabInactiveBackground = correctColor(tabStyle.inactive_background_color, 1, tabInactiveBackground);
    }

    $: {
        tabItemSpacing = correctNonNegativeNumber(tabStyle.item_spacing, tabItemSpacing);
    }

    $: {
        if ($jsonSeparator) {
            if ($jsonSeparatorColor) {
                separatorBackground = correctColor($jsonSeparatorColor, 1, separatorBackground);
            }
            if ($jsonSeparatorPaddings) {
                separatorMargins = correctEdgeInserts($jsonSeparatorPaddings, $direction, separatorMargins);
            }
        }
    }
    $: separatorStyle = {
        background: separatorBackground,
        margin: separatorMargins
    };

    $: isSwipeEnabled = typeof $jsonSwipeEnabled === 'undefined' ?
        true :
        Boolean($jsonSwipeEnabled);

    $: {
        titlePadding = correctEdgeInsertsObject($jsonTitlePaddings ? $jsonTitlePaddings : undefined, titlePadding);
    }

    function updateItems(items: MaybeMissing<TabItem>[]): void {
        if (hasError) {
            return;
        }

        showedPanels = items.map((item, i) => {
            if (i === selected && item?.div) {
                return componentContext.produceChildContext(item.div, {
                    path: i
                });
            }
        });
        visiblePanels = items.map((_, i) => i === selected);
    }
    $: updateItems(items);

    async function setSelected(val: number, focus = false): Promise<void> {
        previousSelected = selected;
        selected = val;
        initTabsSwipe();
        changeTab();

        if (focus) {
            await tick();

            const selectedTab = tabsElem.querySelector(`.${css.tabs__item_selected}`) as HTMLElement | null;
            if (selectedTab) {
                selectedTab.focus();
            }
        }
    }

    function moveSelected(shift: number, focus = false): void {
        const len = $childStore?.length;
        if (!len) {
            return;
        }
        const indices = $childStore.map(it => it.index);
        const selectedIndex = indices.indexOf(selected);

        let newSelectedIndex = selectedIndex + shift;

        if (newSelectedIndex >= len) {
            newSelectedIndex = 0;
        } else if (newSelectedIndex < 0) {
            newSelectedIndex = len - 1;
        }
        const newSelected = indices[newSelectedIndex];

        setSelected(newSelected, focus);
    }

    function selectItem(_event: Event, index: number): boolean {
        if (selected !== index) {
            setSelected(index);

            return false;
        }

        return true;
    }

    function changeTab(): void {
        isAnimated = true;
        updateTransform(-selected * 100);
        updateShowedPanels();
        updateWrapperHeight();
        hideNonVisiblePanels();
        currentTransform = -selected * panelsWrapper.clientWidth;
    }

    async function updateTransform(transform: number): Promise<void> {
        await tick();
        swiperElem.style.transform = `translate3d(${transform}%,0,0)`;
    }

    function updateShowedPanels(around = false): void {
        const start = around ?
            Math.max(0, selected - 1) :
            Math.min(selected, previousSelected ?? selected);
        const end = around ?
            Math.min(items.length - 1, selected + 1) :
            Math.max(selected, previousSelected ?? selected);

        showedPanels = showedPanels.map((context, index) => {
            if (context) {
                return context;
            }
            if (index >= start && index <= end && items[index]?.div) {
                return componentContext.produceChildContext(items[index].div, {
                    path: index
                });
            }
            return undefined;
        });
        visiblePanels = visiblePanels.map((_, index) => index >= start && index <= end);
    }

    async function updateWrapperHeight(): Promise<void> {
        if ($jsonHeight?.type === 'match_parent') {
            return;
        }

        await tick();
        const activePanel = document.getElementById(`${instId}-panel-${selected}`);

        if (activePanel) {
            panelsWrapper.style.height = pxToEm(activePanel.offsetHeight);
        }
    }

    function hideNonVisiblePanels(): void {
        if (hidePanelsTimeout) {
            clearTimeout(hidePanelsTimeout);
        }
        hidePanelsTimeout = window.setTimeout(() => {
            visiblePanels = items.map((_, i) => i === selected);
        }, 400);
    }

    function onTabKeydown(event: KeyboardEvent): void {
        if (event.ctrlKey || event.shiftKey || event.altKey || event.metaKey) {
            return;
        }
        if (!items) {
            return;
        }

        if (event.which === ARROW_LEFT) {
            moveSelected(-1, true);
        } else if (event.which === ARROW_RIGHT) {
            moveSelected(1, true);
        } else if (event.which === HOME) {
            setSelected(0, true);
        } else if (event.which === END) {
            setSelected(items.length - 1, true);
        } else {
            return;
        }

        event.preventDefault();
    }

    // todo desktop arrows

    function initTabsSwipe(): void {
        if (isSwipeInitialized) {
            return;
        }

        isSwipeInitialized = true;
        panelsWrapper.style.height = pxToEm(panelsWrapper.clientHeight);
        swiperElem.style.transform = `translate3d(${-(previousSelected ?? selected) * 100}%,0,0)`;
    }

    function onTouchStart(event: TouchEvent): void {
        const target = event.target as HTMLElement | null;
        const restrictClosest = target?.closest?.(`.${rootCss['root_restrict-scroll']}`);

        if (
            items.length < 2 ||
            event.touches.length > 1 ||
            (restrictClosest && restrictClosest !== panelsWrapper)
        ) {
            return;
        }

        isSwipeStarted = false;
        isSwipeCanceled = false;
        startCoords = getTouchCoords(event);
        moveCoords = null;
        swipeStartTime = Date.now();
        startTransform = currentTransform || -selected * panelsWrapper.clientWidth;
        isAnimated = false;

        if (hidePanelsTimeout) {
            clearTimeout(hidePanelsTimeout);
        }
    }

    function onTouchMove(event: TouchEvent): void {
        const coords = getTouchCoords(event);
        if (
            !startCoords ||
            moveCoords && moveCoords.x === coords.x && moveCoords.y === coords.y
        ) {
            return;
        }

        moveCoords = coords;
        const panelsWrapperWidth = panelsWrapper.clientWidth;
        if (isSwipeStarted) {
            currentTransform = coords.x - startCoords.x + startTransform;

            // Slowing down scroll on edges
            const scrollWidth = panelsWrapperWidth * items.length;
            if (currentTransform > 0) {
                currentTransform = currentTransform * panelsWrapperWidth / (currentTransform + panelsWrapperWidth * 3);
            } else if (-currentTransform + panelsWrapperWidth > scrollWidth) {
                let space = -currentTransform + panelsWrapperWidth - scrollWidth;
                space = space * panelsWrapperWidth / (space + panelsWrapperWidth * 3);
                currentTransform = panelsWrapperWidth - scrollWidth - space;
            }

            updateTransform(currentTransform * 100 / panelsWrapperWidth);
        } else if (Math.abs(coords.y - startCoords.y) > 10) {
            // Swipe is not started, so we cancel it because of vertical scroll
            isSwipeCanceled = true;
        } else if (!isSwipeCanceled && Math.abs(coords.x - startCoords.x) > 8) {
            initTabsSwipe();
            isSwipeStarted = true;
            startCoords = coords;
            updateTransform(-selected * 100);
            updateShowedPanels(true);
        }

        if (isSwipeStarted && event.cancelable) {
            event.preventDefault();
        }
    }

    function onTouchEnd(): void {
        isSwipeCanceled = false;
        startCoords = null;
        let newSelected = selected;
        if (!isSwipeStarted) {
            return;
        }
        isSwipeStarted = false;

        // 512px limit for big screens
        const panelsWrapperWidth = Math.min(512, panelsWrapper.clientWidth);
        const swipeDist = Math.abs(startTransform - currentTransform);
        const swipeCoefficient = Math.min(1, (Date.now() - swipeStartTime) / 750);
        if (swipeDist > (panelsWrapperWidth / 4) * swipeCoefficient) {
            newSelected += (startTransform > currentTransform) ? 1 : -1;
        }

        if (newSelected >= items.length) {
            newSelected = items.length - 1;
        } else if (newSelected < 0) {
            newSelected = 0;
        }

        if (newSelected === selected) {
            isAnimated = true;
            // Scroll back to current tab
            currentTransform = -newSelected * panelsWrapperWidth;
            updateTransform(-newSelected * 100);
            hideNonVisiblePanels();
        } else {
            setSelected(newSelected);
        }
    }

    $: if (componentContext.json) {
        if (prevId) {
            rootCtx.unregisterInstance(prevId);
            prevId = undefined;
        }

        if (componentContext.json.id && !hasError && !componentContext.fakeElement) {
            prevId = componentContext.json.id;
            rootCtx.registerInstance<SwitchElements>(prevId, {
                setCurrentItem(item: number) {
                    if (item < 0 || item > items.length - 1) {
                        throw new Error('Item is out of range in "set-current-item" action');
                    }

                    setSelected(item);
                },
                setPreviousItem(step: number, overflow: Overflow) {
                    let previousItem = selected - step;

                    if (previousItem < 0) {
                        previousItem = overflow === 'ring' ? nonNegativeModulo(previousItem, items.length) : 0;
                    }

                    setSelected(previousItem);
                },
                setNextItem(step: number, overflow: Overflow) {
                    let nextItem = selected + step;

                    if (nextItem > items.length - 1) {
                        nextItem = overflow === 'ring' ? nonNegativeModulo(nextItem, items.length) : items.length - 1;
                    }

                    setSelected(nextItem);
                },
                scrollToStart() {
                    setSelected(0);
                },
                scrollToEnd() {
                    setSelected(items.length - 1);
                },
            });
        }
    }

    $: mods = {
        'height-parent': $jsonHeight?.type === 'match_parent' ? 'yes' : ''
    };

    onDestroy(() => {
        if (prevId) {
            rootCtx.unregisterInstance(prevId);
            prevId = undefined;
        }
    });
</script>

{#if !hasError}
    <Outer
        cls={genClassName('tabs', css, mods)}
        {componentContext}
        {layoutParams}
        customActions={'tabs'}
        parentOf={parentOfItems}
        parentOfSimpleMode={true}
        {replaceItems}
    >
        <!-- svelte-ignore a11y-interactive-supports-focus -->
        <div
            bind:this={tabsElem}
            class="{css.tabs__list} {$jsonRestrictParentScroll ? rootCss['root_restrict-scroll'] : ''}"
            role="tablist"
            style:--divkit-tabs-title-padding={titlePadding ? edgeInsertsToCss(titlePadding, $direction) : ''}
            style:--divkit-tabs-font-size={pxToEm(tabFontSize)}
            style:--divkit-tabs-paddings={tabPaddings}
            style:--divkit-tabs-line-height={tabLineHeight}
            style:--divkit-tabs-letter-spacing={tabLetterSpacing}
            style:--divkit-tabs-active-font-weight={tabActiveFontWeight || ''}
            style:--divkit-tabs-inactive-font-weight={tabInactiveFontWeight || ''}
            style:--divkit-tabs-active-font-family={tabActiveFontFamily || ''}
            style:--divkit-tabs-inactive-font-family={tabInactiveFontFamily || ''}
            style:--divkit-tabs-active-text-color={tabActiveTextColor}
            style:--divkit-tabs-inactive-text-color={tabInactiveTextColor}
            style:--divkit-tabs-active-background-color={tabActiveBackground}
            style:--divkit-tabs-inactive-background-color={tabInactiveBackground}
            style:--divkit-tabs-border-radius={tabBorderRadius}
            style:--divkit-tabs-items-spacing={tabItemSpacing ? pxToEmWithUnits(tabItemSpacing * 10 / tabFontSize) : ''}
            on:keydown={onTabKeydown}
        >
            {#each $childStore as item}
                {@const index = item.index}
                {@const isSelected = index === selected}

                <Actionable
                    {componentContext}
                    cls={genClassName('tabs__item', css, {
                        selected: isSelected,
                        actionable: Boolean(item.title_click_action)
                    })}
                    actions={
                        item.title_click_action && !componentContext.fakeElement ?
                            [item.title_click_action].filter(filterEnabledActions) :
                            []
                    }
                    attrs={{
                        id: `${instId}-tab-${index}`,
                        'aria-controls': `${instId}-panel-${index}`,
                        role: 'tab',
                        // eslint-disable-next-line no-nested-ternary
                        tabindex: isSelected && !componentContext.fakeElement ? (item.title_click_action ? undefined : '0') : '-1',
                        'aria-selected': isSelected ? 'true' : 'false'
                    }}
                    customAction={componentContext.fakeElement ? null : (event => selectItem(event, index))}
                >{item.title}</Actionable>
            {/each}
        </div>
        {#if $jsonSeparator}
            <div
                class={css.tabs__separator}
                style={makeStyle(separatorStyle)}
            ></div>
        {/if}
        <div
            class="{css.tabs__panels} {$jsonRestrictParentScroll ? rootCss['root_restrict-scroll'] : ''}"
            bind:this={panelsWrapper}
            on:touchstart={isSwipeEnabled ? onTouchStart : undefined}
            on:touchmove={isSwipeEnabled ? onTouchMove : undefined}
            on:touchend={isSwipeEnabled ? onTouchEnd : undefined}
            on:touchcancel={isSwipeEnabled ? onTouchEnd : undefined}
        >
            <div
                class={genClassName('tabs__swiper', css, {
                    inited: isSwipeInitialized,
                    animated: isAnimated
                })}
                bind:this={swiperElem}
            >
                {#each $childStore as item}
                    {@const index = item.index}
                    {@const childComponentContext = showedPanels[index]}

                    <div
                        class={genClassName('tabs__panel', css, {
                            visible: visiblePanels[index]
                        })}
                        role="tabpanel"
                        id="{instId}-panel-{index}"
                        aria-labelledby="{instId}-tab-{index}"
                        style="left: {index * 100}%"
                    >
                        {#if childComponentContext}
                            <Unknown
                                componentContext={childComponentContext}
                                layoutParams={childLayoutParams}
                            />
                        {/if}
                    </div>
                {/each}
            </div>
        </div>
    </Outer>
{/if}
