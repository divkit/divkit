<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';
    import { derived, type Readable } from 'svelte/store';

    import css from './Gallery.module.css';
    import rootCss from '../Root.module.css';
    import arrowsCss from '../utilities/Arrows.module.css';

    import type { Align, LayoutParams } from '../../types/layoutParams';
    import type { DivGalleryData } from '../../types/gallery';
    import type { DivBaseData } from '../../types/base';
    import type { SwitchElements } from '../../types/switch-elements';
    import type { Orientation } from '../../types/orientation';
    import type { MaybeMissing } from '../../expressions/json';
    import type { Style } from '../../types/general';
    import type { ComponentContext, ComponentKey } from '../../types/componentContext';
    import type { Variable } from '../../expressions/variable';
    import type { Overflow } from '../../../typings/common';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { genClassName } from '../../utils/genClassName';
    import { pxToEm } from '../../utils/pxToEm';
    import { makeStyle } from '../../utils/makeStyle';
    import { correctGeneralOrientation } from '../../utils/correctGeneralOrientation';
    import { correctAlignment } from '../../utils/correctAlignment';
    import { assignIfDifferent } from '../../utils/assignIfDifferent';
    import { correctNonNegativeNumber } from '../../utils/correctNonNegativeNumber';
    import { correctEdgeInserts } from '../../utils/correctEdgeInserts';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { debounce } from '../../utils/debounce';
    import { Truthy } from '../../utils/truthy';
    import { nonNegativeModulo } from '../../utils/nonNegativeModulo';
    import { constStore } from '../../utils/constStore';
    import { getItemsFromItemBuilder } from '../../utils/itemBuilder';
    import { isDeepEqual } from '../../utils/isDeepEqual';
    import { wrapError } from '../../utils/wrapError';
    import Outer from '../utilities/Outer.svelte';
    import Unknown from '../utilities/Unknown.svelte';

    export let componentContext: ComponentContext<DivGalleryData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    interface ChildInfo {
        visibility?: string;
    }

    interface Item {
        index: number;
        hasGapBefore: boolean;
        componentContext: ComponentContext;
    }

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const direction = rootCtx.direction;

    let scroller: HTMLElement;
    let galleryItemsWrappers: HTMLElement[] = [];
    let hasScrollLeft = false;
    let hasScrollRight = false;

    let resizeObserver: ResizeObserver | null = null;
    let itemsGridElem: HTMLElement;
    let mounted = false;

    const leftClass = rootCtx.getCustomization('galleryLeftClass');
    const rightClass = rootCtx.getCustomization('galleryRightClass');

    let prevId: string | undefined;
    let columns = 1;
    let orientation: Orientation = 'horizontal';
    let align: Align = 'start';
    let gridGap: string | undefined;
    let itemSpacing = 8;
    let crossGridGap: string | undefined;
    let crossSpacing;
    let padding = '';
    let lastPaddingSize: {
        width: string;
        height: string;
        'margin-left'?: string;
        'margin-right'?: string;
        'margin-bottom'?: string;
    } | undefined;
    let childStore: Readable<ChildInfo[]>;
    let scrollerStyle: Style = {};
    let scrollSnap = false;
    let childLayoutParams: LayoutParams = {};
    let defaultItem = 0;

    $: origJson = componentContext.origJson;

    function rebind(): void {
        columns = 1;
        orientation = 'horizontal';
        align = 'start';
        itemSpacing = 8;
        padding = '';
    }

    $: if (origJson) {
        rebind();
    }

    $: jsonItems = Array.isArray(componentContext.json.items) && componentContext.json.items || [];
    // eslint-disable-next-line no-nested-ternary
    $: jsonItemBuilderData = typeof componentContext.json.item_builder?.data === 'string' ? componentContext.getDerivedFromVars(
        componentContext.json.item_builder?.data, undefined, true
    ) : (componentContext.json.item_builder?.data ? constStore(componentContext.json.item_builder.data) : undefined);

    $: jsonColumnCount = componentContext.getDerivedFromVars(componentContext.json.column_count);
    $: jsonOrientation = componentContext.getDerivedFromVars(componentContext.json.orientation);
    $: jsonCrossContentAlignment = componentContext.getDerivedFromVars(componentContext.json.cross_content_alignment);
    $: jsonItemSpacing = componentContext.getDerivedFromVars(componentContext.json.item_spacing);
    $: jsonCrossSpacing = componentContext.getDerivedFromVars(componentContext.json.cross_spacing);
    $: jsonPaddings = componentContext.getDerivedFromVars(componentContext.json.paddings);
    $: jsonScrollMode = componentContext.getDerivedFromVars(componentContext.json.scroll_mode);
    $: jsonRestrictParentScroll = componentContext.getDerivedFromVars(componentContext.json.restrict_parent_scroll);
    $: jsonScrollbar = componentContext.getDerivedFromVars(componentContext.json.scrollbar);
    $: jsonDefaultItem = componentContext.getDerivedFromVars(componentContext.json.default_item);

    function replaceItems(items: (MaybeMissing<DivBaseData> | undefined)[]): void {
        componentContext = prevContext = {
            ...componentContext,
            json: {
                ...componentContext.json,
                items: items.filter(Truthy)
            }
        };
    }

    const isDesktop = rootCtx.isDesktop;
    let items: ComponentContext[] = [];
    let prevContext: ComponentContext<DivGalleryData> | undefined;

    $: {
        let newItems: {
            div: MaybeMissing<DivBaseData>;
            id?: string | undefined;
            vars?: Map<string, Variable> | undefined;
            key: ComponentKey;
        }[] = [];
        if (
            componentContext.json.item_builder &&
            Array.isArray($jsonItemBuilderData) &&
            Array.isArray(componentContext.json.item_builder.prototypes)
        ) {
            const builder = componentContext.json.item_builder;
            newItems = getItemsFromItemBuilder($jsonItemBuilderData, rootCtx, componentContext, builder);
        } else {
            newItems = (Array.isArray(jsonItems) && jsonItems || []).map((it, index) => {
                return {
                    div: it,
                    key: it.id || { index, data: it }
                };
            });
        }

        const unusedContexts = new Set(items);
        const keyToContextMap = new Map<unknown, ComponentContext>();
        let hasDuplicateKeys = false;

        if (prevContext === componentContext) {
            items.forEach(context => {
                if (context.key) {
                    if (typeof context.key === 'string' && keyToContextMap.has(context.key)) {
                        if (!hasDuplicateKeys) {
                            hasDuplicateKeys = true;
                            componentContext.logError(wrapError(new Error('Duplicate key for child elements inside item_builder'), {
                                additional: {
                                    key: context.key
                                }
                            }));
                        }
                    } else {
                        keyToContextMap.set(
                            typeof context.key === 'string' ? context.key : context.key.index,
                            context
                        );
                    }
                }
            });
        }

        items = newItems.map((item, index) => {
            let found = !hasDuplicateKeys && keyToContextMap.get(item.id);
            let foundByData = keyToContextMap.get(index);
            if (
                !found &&
                !item.id &&
                typeof item.key === 'object' &&
                typeof foundByData?.key === 'object' &&
                isDeepEqual(foundByData.key.data, item.key.data)
            ) {
                found = foundByData;
            }
            if (found) {
                unusedContexts.delete(found);
                return found;
            }

            return componentContext.produceChildContext(item.div, {
                path: index,
                variables: item.vars,
                id: item.id,
                key: item.key
            });
        });

        for (const ctx of unusedContexts) {
            ctx.destroy();
        }
        prevContext = componentContext;
    }

    $: shouldCheckArrows = $isDesktop && mounted;
    $: if (shouldCheckArrows) {
        if (typeof ResizeObserver !== 'undefined') {
            // Gallery can contain a dynamic content (e.g. loading images with auto-size)
            resizeObserver = new ResizeObserver(() => {
                updateArrowsVisibilityDebounced();
            });
            resizeObserver.observe(itemsGridElem);
        }
    } else if (resizeObserver) {
        resizeObserver.disconnect();
        resizeObserver = null;
    }

    $: {
        columns = correctPositiveNumber($jsonColumnCount, columns);
    }

    function rebuildItemsGrid(items: ComponentContext[], info: ChildInfo[], columns: number): Item[][] {
        let column = 0;
        let res: Item[][] = [];
        let wasFirstVisibleItem = [];

        for (let i = 0; i < items.length; ++i) {
            if (!res[column]) {
                res[column] = [];
            }
            res[column].push({
                index: i,
                hasGapBefore: wasFirstVisibleItem[column] && info[i].visibility !== 'gone',
                componentContext: items[i]
            });
            if (!wasFirstVisibleItem[column] && info[i].visibility !== 'gone') {
                wasFirstVisibleItem[column] = true;
            }
            if (++column >= columns) {
                column = 0;
            }
        }

        return res;
    }

    $: {
        orientation = correctGeneralOrientation($jsonOrientation, orientation);
    }

    $: {
        align = correctAlignment($jsonCrossContentAlignment, align);
    }

    $: {
        itemSpacing = correctNonNegativeNumber($jsonItemSpacing, itemSpacing);
        gridGap = pxToEm(itemSpacing);
    }

    $: {
        crossSpacing = correctNonNegativeNumber($jsonCrossSpacing, itemSpacing);
        crossGridGap = pxToEm(crossSpacing);
    }

    $: {
        padding = correctEdgeInserts($jsonPaddings, $direction, padding);
        const size = orientation === 'horizontal' ?
            ($jsonPaddings?.end ?? $jsonPaddings?.[($direction === 'ltr' ? 'right' : 'left')] ?? 0) :
            ($jsonPaddings?.bottom ?? 0);
        const calcedSize = pxToEm(size);
        lastPaddingSize = {
            width: orientation === 'horizontal' ? calcedSize : '1px',
            height: orientation === 'horizontal' ? '1px' : calcedSize,
            'margin-right': orientation === 'horizontal' && $direction === 'ltr' ? '-' + calcedSize : undefined,
            'margin-left': orientation === 'horizontal' && $direction === 'rtl' ? '-' + calcedSize : undefined,
            'margin-bottom': orientation === 'vertical' ? '-' + calcedSize : undefined,
        };
    }

    $: {
        let children: Readable<ChildInfo>[] = [];

        items.forEach(item => {
            children.push(item.getDerivedFromVars({
                visibility: item.json.visibility
            }));
        });

        // Create a new array every time so it is not equal to the previous one
        childStore = derived(children, val => [...val]);
    }

    $: itemsGrid = rebuildItemsGrid(items, $childStore, columns);

    $: {
        const newScrollerStyle: Style = {};
        let newChildLayoutParams: LayoutParams = {};
        scrollSnap = false;

        newChildLayoutParams.treatMatchParentAs100 = true;

        if (orientation === 'horizontal') {
            newChildLayoutParams.parentVAlign = align;
            newChildLayoutParams.parentContainerOrientation = 'horizontal';
        } else {
            newChildLayoutParams.parentHAlign = align;
            newChildLayoutParams.parentContainerOrientation = 'vertical';
        }

        if ($jsonScrollMode === 'paging') {
            scrollSnap = true;
            newChildLayoutParams.scrollSnap = 'start';
            const scrollPadding = orientation === 'horizontal' ? 'scroll-padding-left' : 'scroll-padding-top';
            newScrollerStyle[scrollPadding] = pxToEm(itemSpacing / 2);
        }

        // todo multiple columns
        if (columns === 1) {
            newChildLayoutParams.parentLayoutOrientation = orientation;
        }

        scrollerStyle = assignIfDifferent(newScrollerStyle, scrollerStyle);
        childLayoutParams = assignIfDifferent(newChildLayoutParams, childLayoutParams);
    }

    $: gridStyle = {
        padding,
        'grid-gap': crossGridGap
    };

    $: mods = {
        orientation,
        'scroll-snap': scrollSnap,
        scrollbar: $jsonScrollbar === 'auto' ? 'auto' : 'none'
    };

    $: {
        defaultItem = correctNonNegativeNumber($jsonDefaultItem, defaultItem);
    }

    function updateArrowsVisibility(): void {
        if (!scroller) {
            return;
        }

        let scrollLeft = scroller.scrollLeft;
        if ($direction === 'rtl') {
            scrollLeft *= -1;
        }
        const scrollWidth = scroller.scrollWidth;
        const containerWidth = scroller.offsetWidth;

        if ($direction === 'ltr') {
            hasScrollLeft = scrollLeft > 2;
            hasScrollRight = scrollLeft + containerWidth < scrollWidth - 2;
        } else {
            hasScrollRight = scrollLeft > 2;
            hasScrollLeft = scrollLeft + containerWidth < scrollWidth - 2;
        }
    }

    const updateArrowsVisibilityDebounced = debounce(updateArrowsVisibility, 50);

    $: if (componentContext.json) {
        updateArrowsVisibilityDebounced();
    }

    function scroll(type: 'left' | 'right'): void {
        scroller.scroll({
            left: scroller.scrollLeft + (scroller.offsetWidth * .75) * (type === 'right' ? 1 : -1),
            behavior: 'smooth'
        });
    }

    function getItems(): HTMLElement[] {
        let res: HTMLElement[] = [];
        let maxLen = galleryItemsWrappers[0].children.length;

        for (let j = 0; j < maxLen; j += 2) {
            for (let i = 0; i < columns; ++i) {
                const elem = galleryItemsWrappers[i].children[j] as HTMLElement;
                if (elem) {
                    res.push(elem);
                }
            }
        }

        return res;
    }

    function scrollTo(offset: number, animated = true): void {
        const isHorizontal = orientation === 'horizontal';
        const scrollDirection: keyof ScrollToOptions = isHorizontal ? 'left' : 'top';

        scroller.scroll({
            [scrollDirection]: offset,
            behavior: animated ? 'smooth' : 'instant'
        });
    }

    function scrollToGalleryItem(galleryElements: HTMLElement[], index: number, {
        animated = true,
        extraOffset = 0,
        overflow = 'clamp'
    }: {
        animated?: boolean;
        extraOffset?: number;
        overflow?: Overflow;
    } = {}): void {
        const isHorizontal = orientation === 'horizontal';
        const elementOffset: keyof HTMLElement = isHorizontal ? 'offsetLeft' : 'offsetTop';

        // 0.01 forces Chromium to use scroll-snap (exact correct scroll position will not trigger it)
        // Chromium will save scroll-snapped value and will not save exact one
        // Saved scroll position is used on resnapping (e.g. content change)

        if (index > galleryElements.length - 1) {
            index = overflow === 'ring' ? nonNegativeModulo(index, galleryElements.length) : galleryElements.length - 1;
        } else if (index < 0) {
            index = overflow === 'ring' ? nonNegativeModulo(index, galleryElements.length) : 0;
        }

        const elem = galleryElements[index];

        if (elem) {
            let offset;
            if ($direction === 'ltr' || !isHorizontal) {
                offset = elem[elementOffset] + .01 - itemSpacing / 2;
            } else {
                const scrollWrapperSize = scroller.offsetWidth;
                offset = (elem[elementOffset] + elem.offsetWidth + .01 - itemSpacing / 2) - scrollWrapperSize;
            }

            if (extraOffset) {
                offset += extraOffset;

                const maxOffset = isHorizontal ?
                    scroller.scrollWidth - scroller.offsetWidth :
                    scroller.scrollHeight - scroller.offsetHeight;
                if (offset > maxOffset) {
                    if (overflow === 'clamp') {
                        offset = maxOffset;
                    } else if (overflow === 'ring') {
                        offset = nonNegativeModulo(offset, maxOffset);
                    }
                }
                if (offset < 0) {
                    if (overflow === 'clamp') {
                        offset = 0;
                    } else if (overflow === 'ring') {
                        offset = nonNegativeModulo(offset, maxOffset);
                    }
                }
            }

            scrollTo(offset, animated);
        }
    }

    function scrollOffset(offset: number, {
        overflow = 'clamp',
        animated = true
    }: {
        overflow?: Overflow;
        animated?: boolean;
    } = {}): void {
        const isHorizontal = orientation === 'horizontal';
        const directionMultiplier = ($direction === 'ltr' || !isHorizontal) ? 1 : -1;
        const currentOffset = isHorizontal ?
            scroller.scrollLeft :
            scroller.scrollTop;
        const maxOffset = isHorizontal ?
            scroller.scrollWidth - scroller.offsetWidth :
            scroller.scrollHeight - scroller.offsetHeight;
        let newOffset = currentOffset * directionMultiplier + offset;
        if (newOffset > maxOffset) {
            if (overflow === 'clamp') {
                newOffset = maxOffset;
            } else if (overflow === 'ring') {
                newOffset = nonNegativeModulo(newOffset, maxOffset);
            }
        } else if (newOffset < 0) {
            if (overflow === 'clamp') {
                newOffset = 0;
            } else if (overflow === 'ring') {
                newOffset = nonNegativeModulo(newOffset, maxOffset);
            }
        }
        scrollTo(newOffset * directionMultiplier, animated);
    }

    function checkIsIntersecting(scroller: DOMRect, item: DOMRect): boolean {
        if (orientation === 'horizontal') {
            return item.right > scroller.left && scroller.right > item.left;
        }

        return item.bottom > scroller.top && scroller.bottom > item.top;
    }

    function checkIsFullyIntersecting(scroller: DOMRect, item: DOMRect): boolean {
        if (orientation === 'horizontal') {
            return item.left >= scroller.left && item.right <= scroller.right;
        }

        return item.top >= scroller.top && item.bottom <= scroller.bottom;
    }

    function calculateCurrentElementIndex(action: 'prev' | 'next'): number {
        const galleryElements = getItems();
        const scrollerRect = scroller.getBoundingClientRect();

        // Try to find the most left fully visible element
        const firstFullyVisibleElement = galleryElements.findIndex(el =>
            checkIsFullyIntersecting(scrollerRect, el.getBoundingClientRect())
        );

        if (firstFullyVisibleElement !== -1) {
            return firstFullyVisibleElement;
        }

        // If there is no fully visible elements, it means that:
        // - only one element is partly visible and its width bigger than gallery width
        // - two elements are partly visible
        const visibleElementsMap = galleryElements.map(el =>
            checkIsIntersecting(scrollerRect, el.getBoundingClientRect())
        );
        const firstVisibleElement = visibleElementsMap.findIndex(Boolean);

        if (firstVisibleElement !== -1) {
            // If two elements are partly visible, we should scroll to current element on "set_item_previous" action
            const isPreviousEqualCurrent = action === 'prev' && visibleElementsMap.filter(Boolean).length === 2;
            return isPreviousEqualCurrent ? firstVisibleElement + 1 : firstVisibleElement;
        }

        return action === 'prev' ? 1 : galleryElements.length - 2;
    }

    $: if (componentContext.json) {
        if (prevId) {
            rootCtx.unregisterInstance(prevId);
            prevId = undefined;
        }

        if (componentContext.id && !componentContext.fakeElement) {
            prevId = componentContext.id;
            rootCtx.registerInstance<SwitchElements>(prevId, {
                setCurrentItem(item: number, animated: boolean) {
                    const galleryElements = getItems();
                    if (item < 0 || item > galleryElements.length - 1) {
                        throw new Error('Item is out of range in "set-current-item" action');
                    }

                    scrollToGalleryItem(galleryElements, item, { animated });
                },
                setPreviousItem(step: number, overflow: Overflow, animated: boolean) {
                    const currentElementIndex = calculateCurrentElementIndex('prev');
                    const galleryElements = getItems();
                    let previousItem = currentElementIndex - step;

                    scrollToGalleryItem(galleryElements, previousItem, { animated, overflow });
                },
                setNextItem(step: number, overflow: Overflow, animated: boolean) {
                    const isHorizontal = orientation === 'horizontal';
                    const directionMultiplier = ($direction === 'ltr' || !isHorizontal) ? 1 : -1;
                    // Go to scroller start, if we reached right/bottom edge of scroller
                    const isEdgeScroll = isHorizontal ? (
                        scroller.scrollLeft * directionMultiplier + scroller.offsetWidth === scroller.scrollWidth
                    ) : (
                        scroller.scrollTop + scroller.offsetHeight === scroller.scrollHeight
                    );
                    const galleryElements = getItems();
                    if (isEdgeScroll && overflow === 'ring') {
                        scrollToGalleryItem(galleryElements, 0, { animated });
                        return;
                    }

                    const currentElementIndex = calculateCurrentElementIndex('next');
                    let nextItem = currentElementIndex + step;

                    scrollToGalleryItem(galleryElements, nextItem, { animated, overflow });
                },
                scrollToStart(animated: boolean) {
                    scrollTo(0, animated);
                },
                scrollToEnd(animated: boolean) {
                    scrollTo(($direction === 'ltr' || orientation !== 'horizontal') ? 1e6 : -1e6, animated);
                },
                scrollToPosition(step, animated: boolean) {
                    scrollTo(($direction === 'ltr' || orientation !== 'horizontal') ? step : -step, animated);
                },
                scrollCombined({
                    step,
                    offset,
                    overflow,
                    animated
                }) {
                    if (step) {
                        const currentElementIndex = calculateCurrentElementIndex(step > 0 ? 'next' : 'prev');
                        const nextItem = currentElementIndex + step;
                        scrollToGalleryItem(getItems(), nextItem, { animated, extraOffset: offset, overflow });
                    } else if (offset) {
                        scrollOffset(offset, {
                            overflow,
                            animated
                        });
                    }
                }
            });
        }
    }

    onMount(() => {
        mounted = true;

        updateArrowsVisibility();

        if (defaultItem) {
            const galleryElements = getItems();
            scrollToGalleryItem(galleryElements, defaultItem, { animated: false });
        }
    });

    onDestroy(() => {
        mounted = false;

        items.forEach(context => {
            context.destroy();
        });

        if (prevId && !componentContext.fakeElement) {
            rootCtx.unregisterInstance(prevId);
            prevId = undefined;
        }
    });
</script>

<svelte:window on:resize={shouldCheckArrows ? updateArrowsVisibilityDebounced : null} />

<Outer
    cls={genClassName('gallery', css, mods)}
    {componentContext}
    {layoutParams}
    customPaddings={true}
    customActions={'gallery'}
    parentOf={items}
    {replaceItems}
>
    <div
        class="{css.gallery__scroller} {$jsonRestrictParentScroll ? rootCss['root_restrict-scroll'] : ''}"
        bind:this={scroller}
        on:scroll={shouldCheckArrows ? updateArrowsVisibility : null}
        style={makeStyle(scrollerStyle)}
    >
        <div
            bind:this={itemsGridElem}
            class={css['gallery__items-grid']}
            style={makeStyle(gridStyle)}
        >
            {#each itemsGrid as itemsRow, rowIndex}
                <div
                    class={css.gallery__items}
                    bind:this={galleryItemsWrappers[rowIndex]}
                >
                    {#each itemsRow as item}
                        {#if item.hasGapBefore}
                            <div
                                class={css.gallery__gap}
                                style:width={orientation === 'horizontal' ? gridGap : undefined}
                                style:height={orientation !== 'horizontal' ? gridGap : undefined}
                            ></div>
                        {/if}

                        <Unknown
                            componentContext={item.componentContext}
                            layoutParams={childLayoutParams}
                        />
                    {/each}

                    <div class={css.gallery__gap} style={makeStyle(lastPaddingSize)}></div>
                </div>
            {/each}
        </div>
    </div>
    {#if orientation === 'horizontal'}
        {#if hasScrollLeft && shouldCheckArrows}
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-static-element-interactions -->
            <div class="{leftClass || `${css.gallery__arrow} ${arrowsCss.arrow} ${arrowsCss.arrow_left}`}" on:click={() => scroll('left')}>
                {#if !leftClass}
                    <svg class={arrowsCss.arrow__icon} xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32" fill="none">
                        <path class={css['gallery__arrow-icon-path']} d="m10 16 8.3 8 1.03-1-4-6-.7-1 .7-1 4-6-1.03-1z"/>
                    </svg>
                {/if}
            </div>
        {/if}
        {#if hasScrollRight && shouldCheckArrows}
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-static-element-interactions -->
            <div class="{rightClass || `${css.gallery__arrow} ${arrowsCss.arrow} ${arrowsCss.arrow_right}`}" on:click={() => scroll('right')}>
                {#if !rightClass}
                    <svg class={arrowsCss.arrow__icon} xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32" fill="none">
                        <path class={css['gallery__arrow-icon-path']} d="M22 16l-8.3 8-1.03-1 4-6 .7-1-.7-1-4-6 1.03-1 8.3 8z"/>
                    </svg>
                {/if}
            </div>
        {/if}
    {/if}
</Outer>
