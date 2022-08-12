<script lang="ts">
    import { getContext, tick } from 'svelte';

    import css from './tabs.module.css';

    import type { Mods } from '../types/general';
    import type { LayoutParams } from '../types/layoutParams';
    import type { DivTabsData } from '../types/tabs';
    import type { DivBase, TemplateContext } from '../../typings/common';
    import type { EdgeInsets } from '../types/edgeInserts';
    import type { SwitchElements, Overflow } from '../types/switch-elements';
    import { ROOT_CTX, RootCtxValue } from '../context/root';
    import Outer from './outer.svelte';
    import Unknown from './unknown.svelte';
    import { wrapError } from '../utils/wrapError';
    import { genClassName } from '../utils/genClassName';
    import Actionable from './actionable.svelte';
    import { makeStyle } from '../utils/makeStyle';
    import { ARROW_LEFT, ARROW_RIGHT, END, HOME } from '../utils/keyboard/codes';
    import { pxToEm } from '../utils/pxToEm';
    import { isPositiveNumber } from '../utils/isPositiveNumber';
    import { correctPositiveNumber } from '../utils/correctPositiveNumber';
    import { correctEdgeInserts } from '../utils/correctEdgeInserts';
    import { correctBorderRadius } from '../utils/correctBorderRadius';
    import { correctColor } from '../utils/correctColor';
    import { correctFontWeight } from '../utils/correctFontWeight';
    import { TabItem } from '../types/tabs';
    import { isNonNegativeNumber } from '../utils/isNonNegativeNumber';

    export let json: Partial<DivTabsData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const instId = rootCtx.genId('tabs');

    let hasError = false;
    $: items = json.items || [];
    $: {
        if (!items?.length) {
            hasError = true;
            rootCtx.logError(wrapError(new Error('Empty "items" prop for div "tabs"')));
        } else {
            hasError = false;
        }
    }

    let tabsElem: HTMLElement;
    let panelsWrapper: HTMLElement;
    let swiperElem: HTMLElement;
    let mods: Mods = {};
    const jsonSelectedTab = rootCtx.getJsonWithVars(json.selected_tab);
    let selected = jsonSelectedTab && Number(jsonSelectedTab) || 0;

    $: {
        if (items && (selected < 0 || selected >= items.length)) {
            rootCtx.logError(wrapError(new Error('Incorrect "selected_tab" prop for div "tabs"'), {
                additional: {
                    selected: json.selected_tab,
                    length: items.length
                }
            }));
            selected = selected < 0 ? 0 : items.length - 1;
        }
    }

    $: jsonTabStyle = rootCtx.getDerivedFromVars(json.tab_title_style);
    $: tabStyle = $jsonTabStyle || {};

    let tabFontSize = 12;
    $: {
        tabFontSize = correctPositiveNumber(tabStyle.font_size, tabFontSize);
    }

    let tabPaddings = '';

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
                right: (Number(paddings.right) || 0) / tabFontSize * 10,
                bottom: (Number(paddings.bottom) || 0) / tabFontSize * 10,
                left: (Number(paddings.left) || 0) / tabFontSize * 10
            };

            tabPaddings = correctEdgeInserts(adjustedPaddings, tabPaddings);
        }
    }

    let tabLineHeight = '';
    $: {
        const lineHeight = tabStyle.line_height;
        if (lineHeight !== undefined && isPositiveNumber(lineHeight)) {
            tabLineHeight = pxToEm(lineHeight / tabFontSize * 10);
        }
    }

    let tabLetterSpacing = '';
    $: {
        const letterSpacing = tabStyle.letter_spacing;
        if (letterSpacing !== undefined && isNonNegativeNumber(letterSpacing)) {
            tabLetterSpacing = pxToEm(letterSpacing / tabFontSize * 10);
        }
    }

    let tabBorderRadius = '';
    $: {
        if (tabStyle.corner_radius || tabStyle.corners_radius || tabStyle.font_size) {
            const defaultRadius = tabStyle.corner_radius ?? 1000;

            if (tabStyle.corners_radius) {
                tabBorderRadius = correctBorderRadius(tabStyle.corners_radius, defaultRadius, tabFontSize, tabBorderRadius);
            } else if (isNonNegativeNumber(defaultRadius)) {
                tabBorderRadius = pxToEm(defaultRadius / tabFontSize * 10);
            }
        }
    }

    let tabActiveFontWeight: number | undefined = undefined;
    $: {
        tabActiveFontWeight = correctFontWeight(tabStyle.active_font_weight || tabStyle.font_weight, tabActiveFontWeight);
    }

    let tabInactiveFontWeight: number | undefined = undefined;
    $: {
        tabInactiveFontWeight = correctFontWeight(
            tabStyle.inactive_font_weight || tabStyle.font_weight,
            tabInactiveFontWeight
        );
    }

    let tabActiveTextColor = '';
    $: {
        tabActiveTextColor = correctColor(tabStyle.active_text_color, 1, tabActiveTextColor);
    }

    let tabInactiveTextColor = '';
    $: {
        tabInactiveTextColor = correctColor(tabStyle.inactive_text_color, 1, tabInactiveTextColor);
    }

    let tabActiveBackground = '';
    $: {
        tabActiveBackground = correctColor(tabStyle.active_background_color, 1, tabActiveBackground);
    }

    let tabInactiveBackground = '';
    $: {
        tabInactiveBackground = correctColor(tabStyle.inactive_background_color, 1, tabInactiveBackground);
    }

    $: jsonSeparator = rootCtx.getDerivedFromVars(json.has_separator);
    $: jsonSeparatorColor = rootCtx.getDerivedFromVars(json.separator_color);
    $: jsonSeparatorPaddings = rootCtx.getDerivedFromVars(json.separator_paddings);
    let separatorBackground = '';
    let separatorMargins = '';
    $: {
        if ($jsonSeparator) {
            if ($jsonSeparatorColor) {
                separatorBackground = correctColor($jsonSeparatorColor, 1, separatorBackground);
            }
            if ($jsonSeparatorPaddings) {
                separatorMargins = correctEdgeInserts($jsonSeparatorPaddings, separatorMargins);
            }
        }
    }
    $: separatorStyle = {
        background: separatorBackground,
        margin: separatorMargins
    };

    $: jsonSwipeEnabled = rootCtx.getDerivedFromVars(json.switch_tabs_by_content_swipe_enabled);
    $: isSwipeEnabled = typeof $jsonSwipeEnabled === 'undefined' ?
        true :
        Boolean($jsonSwipeEnabled);

    let isSwipeInitialized = false;
    let isAnimated = false;
    let previousSelected = selected;
    let showedPanels: boolean[] = [];
    let visiblePanels: boolean[] = [];
    function updateItems(_items: TabItem[]): void {
        showedPanels = items.map((_, i) => i === selected);
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
        const len = items?.length;
        if (!len) {
            return;
        }

        let newSelected = selected + shift;

        if (newSelected >= len) {
            newSelected = 0;
        } else if (newSelected < 0) {
            newSelected = len - 1;
        }

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
            Math.min(selected, previousSelected);
        const end = around ?
            Math.min(items.length - 1, selected + 1) :
            Math.max(selected, previousSelected);

        showedPanels = showedPanels.map((isShowed, index) => isShowed || index >= start && index <= end);
        visiblePanels = visiblePanels.map((_, index) => index >= start && index <= end);
    }

    async function updateWrapperHeight(): Promise<void> {
        await tick();
        const activePanel = document.getElementById(`${instId}-panel-${selected}`);

        if (activePanel) {
            panelsWrapper.style.height = pxToEm(activePanel.offsetHeight);
        }
    }

    let hidePanelsTimeout: number | null = null;

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
        swiperElem.style.transform = `translate3d(${-previousSelected * 100}%,0,0)`;
    }

    type Coords = { x: number; y: number };
    let startCoords: Coords | null = null;
    let moveCoords: Coords | null = null;
    let swipeStartTime: number;
    let isSwipeStarted = false;
    let isSwipeCanceled = false;
    let startTransform: number;
    let currentTransform: number;

    function onTouchStart(event: TouchEvent): void {
        if (items.length < 2 || event.touches.length > 1) {
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
            // Scroll back to current tab
            currentTransform = -newSelected * panelsWrapperWidth;
            updateTransform(-newSelected * 100);
            hideNonVisiblePanels();
        } else {
            setSelected(newSelected);
        }
    }

    function getTouchCoords(event: TouchEvent): Coords {
        const firstEvent = event.touches[0];
        const x = firstEvent.clientX || firstEvent.pageX;
        const y = firstEvent.clientY || firstEvent.pageY;

        return { x, y };
    }

    if (json.id) {
        rootCtx.registerInstance<SwitchElements>(json.id, {
            setCurrentItem(item: number) {
                if (item < 0 || item > items.length - 1) {
                    throw new Error('Item is out of range in "set-current-item" action');
                }

                setSelected(item);
            },
            setPreviousItem(overflow: Overflow) {
                let previousItem = selected - 1;

                if (previousItem < 0) {
                    previousItem = overflow === 'ring' ? items.length - 1 : selected;
                }

                setSelected(previousItem);
            },
            setNextItem(overflow: Overflow) {
                let nextItem = selected + 1;

                if (nextItem > items.length - 1) {
                    nextItem = overflow === 'ring' ? 0 : selected;
                }

                setSelected(nextItem);
            }
        });
    }
</script>

{#if !hasError}
    <Outer
        cls={genClassName('tabs', css, mods)}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
        customActions={'tabs'}
    >
        <div
            bind:this={tabsElem}
            class={css.tabs__list}
            role="tablist"
            style:--divkit-tabs-font-size={pxToEm(tabFontSize)}
            style:--divkit-tabs-paddings={tabPaddings}
            style:--divkit-tabs-line-height={tabLineHeight}
            style:--divkit-tabs-letter-spacing={tabLetterSpacing}
            style:--divkit-tabs-active-font-weight={tabActiveFontWeight || ''}
            style:--divkit-tabs-inactive-font-weight={tabInactiveFontWeight || ''}
            style:--divkit-tabs-active-text-color={tabActiveTextColor}
            style:--divkit-tabs-inactive-text-color={tabInactiveTextColor}
            style:--divkit-tabs-active-background-color={tabActiveBackground}
            style:--divkit-tabs-inactive-background-color={tabInactiveBackground}
            style:--divkit-tabs-border-radius={tabBorderRadius}
        >
            {#each items as item, index}
                {@const isSelected = index === selected}

                <Actionable
                    cls={genClassName('tabs__item', css, {
                        selected: isSelected,
                        actionable: Boolean(item.title_click_action)
                    })}
                    actions={item.title_click_action ? [item.title_click_action] : []}
                    attrs={{
                        id: `${instId}-tab-${index}`,
                        'aria-controls': `${instId}-panel-${index}`,
                        role: 'tab',
                        // eslint-disable-next-line no-nested-ternary
                        tabindex: isSelected ? (item.title_click_action ? undefined : '0') : '-1',
                        'aria-selected': isSelected ? 'true' : 'false'
                    }}
                    customAction={event => selectItem(event, index)}
                    on:keydown={onTabKeydown}
                >
                    {item.title}
                </Actionable>
            {/each}
        </div>
        {#if $jsonSeparator}
            <div
                class={css.tabs__separator}
                style={makeStyle(separatorStyle)}
            ></div>
        {/if}
        <div
            class={css.tabs__panels}
            bind:this={panelsWrapper}
        >
            <div
                class={genClassName('tabs__swiper', css, {
                    inited: isSwipeInitialized,
                    animated: isAnimated
                })}
                bind:this={swiperElem}
                on:touchstart={isSwipeEnabled ? onTouchStart : undefined}
                on:touchmove={isSwipeEnabled ? onTouchMove : undefined}
                on:touchend={isSwipeEnabled ? onTouchEnd : undefined}
                on:touchcancel={isSwipeEnabled ? onTouchEnd : undefined}
            >
                {#each items as item, index}
                    <div
                        class={genClassName('tabs__panel', css, {
                            visible: visiblePanels[index]
                        })}
                        role="tabpanel"
                        id="{instId}-panel-{index}"
                        aria-labelledby="{instId}-tab-{index}"
                        style="left: {index * 100}%"
                    >
                        {#if showedPanels[index]}
                            <Unknown div={item.div} templateContext={templateContext} />
                        {/if}
                    </div>
                {/each}
            </div>
        </div>
    </Outer>
{/if}
