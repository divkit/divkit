<script lang="ts">
    import { getContext, onMount } from 'svelte';
    import { derived, Readable } from 'svelte/store';

    import css from './gallery.module.css';
    import rootCss from './root.module.css';

    import type { Align, LayoutParams } from '../types/layoutParams';
    import type { DivGalleryData } from '../types/gallery';
    import type { DivBase, TemplateContext } from '../../typings/common';
    import type { DivBaseData } from '../types/base';
    import type { Overflow, SwitchElements } from '../types/switch-elements';
    import type { Orientation } from '../types/orientation';
    import type { MaybeMissing } from '../expressions/json';
    import type { Size } from '../types/sizes';
    import type { Style } from '../types/general';
    import { ROOT_CTX, RootCtxValue } from '../context/root';
    import Outer from './outer.svelte';
    import Unknown from './unknown.svelte';
    import { wrapError } from '../utils/wrapError';
    import { genClassName } from '../utils/genClassName';
    import { pxToEm } from '../utils/pxToEm';
    import { makeStyle } from '../utils/makeStyle';
    import { correctGeneralOrientation } from '../utils/correctGeneralOrientation';
    import { correctAlignment } from '../utils/correctAlignment';
    import { assignIfDifferent } from '../utils/assignIfDifferent';
    import { correctNonNegativeNumber } from '../utils/correctNonNegativeNumber';
    import { correctEdgeInserts } from '../utils/correctEdgeInserts';
    import { correctPositiveNumber } from '../utils/correctPositiveNumber';
    import { joinTemplateSizes } from '../utils/joinTemplateSizes';

    export let json: Partial<DivGalleryData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    let scroller: HTMLElement;
    let galleryItemsWrappers: HTMLElement[] = [];
    let hasScrollLeft = false;
    let hasScrollRight = false;

    let hasError = false;
    $: jsonItems = json.items;
    $: {
        if (!jsonItems?.length || !Array.isArray(jsonItems)) {
            hasError = true;
            rootCtx.logError(wrapError(new Error('Incorrect or empty "items" prop for div "gallery"')));
        } else {
            hasError = false;
        }
    }

    interface Item {
        json: DivBaseData;
        templateContext: TemplateContext;
        origJson: DivBaseData;
    }

    $: items = (!hasError && jsonItems || []).map(item => {
        let childJson: DivBaseData = item as DivBaseData;
        let childContext: TemplateContext = templateContext;

        ({
            templateContext: childContext,
            json: childJson
        } = rootCtx.processTemplate(childJson, childContext));

        return {
            json: childJson,
            templateContext: childContext,
            origJson: item
        };
    });

    $: jsonColumnCount = rootCtx.getDerivedFromVars(json.column_count);
    let columns = 1;
    $: {
        columns = correctPositiveNumber($jsonColumnCount, columns);
    }

    function rebuildItemsGrid(items: Item[], columns: number): Item[][] {
        let column = 0;
        let res: Item[][] = [];

        for (let i = 0; i < items.length; ++i) {
            if (!res[column]) {
                res[column] = [];
            }
            res[column].push(items[i]);
            if (++column >= columns) {
                column = 0;
            }
        }

        return res;
    }
    $: itemsGrid = rebuildItemsGrid(items, columns);

    $: jsonOrientation = rootCtx.getDerivedFromVars(json.orientation);
    let orientation: Orientation = 'horizontal';
    $: {
        orientation = correctGeneralOrientation($jsonOrientation, orientation);
    }

    let align: Align = 'start';
    $: jsonCrossContentAlignment = rootCtx.getDerivedFromVars(json.cross_content_alignment);
    $: {
        align = correctAlignment($jsonCrossContentAlignment, align);
    }

    let gridGap: string | undefined;
    let itemSpacing = 8;
    $: jsonItemSpacing = rootCtx.getDerivedFromVars(json.item_spacing);
    $: {
        itemSpacing = correctNonNegativeNumber($jsonItemSpacing, itemSpacing);
        gridGap = pxToEm(itemSpacing);
    }

    $: jsonPaddings = rootCtx.getDerivedFromVars(json.paddings);
    let padding = '';
    $: {
        padding = correctEdgeInserts($jsonPaddings, padding);
    }

    $: gridTemplate = orientation === 'horizontal' ? 'grid-template-columns' : 'grid-template-rows';
    let templateSizes: string[] = [];

    let childStore: Readable<(MaybeMissing<Size> | undefined)[]>;
    $: {
        let childs: Readable<MaybeMissing<Size> | undefined>[] = [];

        items.forEach(item => {
            const itemSize = orientation === 'horizontal' ? 'width' : 'height';
            childs.push(rootCtx.getDerivedFromVars(item.json[itemSize]));
        });

        childStore = derived(childs, val => val);
    }
    $: {
        templateSizes = [];
        if (columns > 1) {
            // TODO: think about match_parent in this task DIVKIT-307
            templateSizes.push('auto');
        } else {
            $childStore.forEach(childInfo => {
                if ((!childInfo && orientation === 'horizontal') || childInfo?.type === 'match_parent') {
                    templateSizes.push('100%');
                } else {
                    templateSizes.push('max-content');
                }
            });
        }
    }

    let scrollerStyle: Style = {};
    let scrollSnap = false;
    $: jsonScrollMode = rootCtx.getDerivedFromVars(json.scroll_mode);
    let childLayoutParams: LayoutParams = {};
    $: {
        const newScrollerStyle: Style = {};
        let newChildLayoutParams: LayoutParams = {};
        scrollSnap = false;

        if (orientation === 'horizontal') {
            newChildLayoutParams.parentVAlign = align;
        } else {
            newChildLayoutParams.parentHAlign = align;
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
        'grid-gap': gridGap
    };

    $: columnStyle = {
        'grid-gap': gridGap,
        [gridTemplate]: joinTemplateSizes(templateSizes)
    };

    $: mods = {
        orientation,
        'scroll-snap': scrollSnap
    };

    $: jsonDefaultItem = rootCtx.getDerivedFromVars(json.default_item);
    let defaultItem = 0;
    $: {
        defaultItem = correctNonNegativeNumber($jsonDefaultItem, defaultItem);
    }

    function updateArrowsVisibility(): void {
        const scrollLeft = scroller.scrollLeft;
        const scrollWidth = scroller.scrollWidth;
        const containerWidth = scroller.offsetWidth;

        hasScrollLeft = scrollLeft > 2;
        hasScrollRight = scrollLeft + containerWidth < scrollWidth - 2;
    }

    function scroll(type: 'left' | 'right'): void {
        scroller.scroll({
            left: scroller.scrollLeft + (scroller.offsetWidth * .75) * (type === 'right' ? 1 : -1),
            behavior: 'smooth'
        });
    }

    function onScroll(): void {
        updateArrowsVisibility();
    }

    function getItems(): HTMLElement[] {
        let res: HTMLElement[] = [];
        let maxLen = galleryItemsWrappers[0].children.length;

        for (let j = 0; j < maxLen; ++j) {
            for (let i = 0; i < columns; ++i) {
                const elem = galleryItemsWrappers[i].children[j] as HTMLElement;
                if (elem) {
                    res.push(elem);
                }
            }
        }

        return res;
    }

    function scrollToGalleryItem(galleryElements: HTMLElement[], index: number, behavior: ScrollBehavior = 'smooth'): void {
        const isHorizontal = orientation === 'horizontal';
        const elementOffset: keyof HTMLElement = isHorizontal ? 'offsetLeft' : 'offsetTop';
        const scrollDirection: keyof ScrollToOptions = isHorizontal ? 'left' : 'top';

        scroller.scroll({
            [scrollDirection]: Math.max(0, galleryElements[index][elementOffset] - itemSpacing / 2),
            behavior
        });
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

    onMount(() => {
        if (!hasError) {
            updateArrowsVisibility();

            if (defaultItem) {
                const galleryElements = getItems();
                scrollToGalleryItem(galleryElements, defaultItem, 'auto');
            }
        }
    });

    if (json.id && !hasError) {
        rootCtx.registerInstance<SwitchElements>(json.id, {
            setCurrentItem(item: number) {
                const galleryElements = getItems();
                if (item < 0 || item > galleryElements.length - 1) {
                    throw new Error('Item is out of range in "set-current-item" action');
                }

                scrollToGalleryItem(galleryElements, item);
            },
            setPreviousItem(overflow: Overflow) {
                const currentElementIndex = calculateCurrentElementIndex('prev');
                const galleryElements = getItems();
                let previousItem = currentElementIndex - 1;

                if (previousItem < 0) {
                    previousItem = overflow === 'ring' ? galleryElements.length - 1 : currentElementIndex;
                }

                scrollToGalleryItem(galleryElements, previousItem);
            },
            setNextItem(overflow: Overflow) {
                // Go to scroller start, if we reached right/bottom edge of scroller
                const isEdgeScroll = orientation === 'horizontal' ? (
                    scroller.scrollLeft + scroller.offsetWidth === scroller.scrollWidth
                ) : (
                    scroller.scrollTop + scroller.offsetHeight === scroller.scrollHeight
                );
                const galleryElements = getItems();
                if (isEdgeScroll && overflow === 'ring') {
                    scrollToGalleryItem(galleryElements, 0);
                    return;
                }

                const currentElementIndex = calculateCurrentElementIndex('next');
                let nextItem = currentElementIndex + 1;

                if (nextItem > galleryElements.length - 1) {
                    nextItem = overflow === 'ring' ? 0 : currentElementIndex;
                }

                scrollToGalleryItem(galleryElements, nextItem);
            }
        });
    }
</script>

{#if !hasError}
    <Outer
        cls={genClassName('gallery', css, mods)}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
        customPaddings={true}
        customActions={'gallery'}
        forceHeight={orientation === 'vertical'}
        forceWidth={orientation === 'horizontal'}
    >
        <div
            class={css.gallery__scroller}
            bind:this={scroller}
            on:scroll={onScroll}
            style={makeStyle(scrollerStyle)}
        >
            <div
                class={css['gallery__items-grid']}
                style={makeStyle(gridStyle)}
            >
                {#each itemsGrid as itemsRow, rowIndex}
                    <div
                        class={css.gallery__items}
                        style={makeStyle(columnStyle)}
                        bind:this={galleryItemsWrappers[rowIndex]}
                    >
                        {#each itemsRow as item}
                            <Unknown
                                layoutParams={childLayoutParams}
                                div={item.json}
                                templateContext={item.templateContext}
                                origJson={item.origJson}
                            />
                        {/each}
                    </div>
                {/each}
            </div>
        </div>
        {#if orientation === 'horizontal'}
            {#if hasScrollLeft}
                <div class="{css.gallery__arrow} {css.gallery__arrow_left} {rootCss['root__only-desktop']}" on:click={() => scroll('left')}>
                    <svg class={css['gallery__arrow-icon']} xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32" fill="none">
                        <path class={css['gallery__arrow-icon-path']} d="m10 16 8.3 8 1.03-1-4-6-.7-1 .7-1 4-6-1.03-1z"/>
                    </svg>
                </div>
            {/if}
            {#if hasScrollRight}
                <div class="{css.gallery__arrow} {css.gallery__arrow_right} {rootCss['root__only-desktop']}" on:click={() => scroll('right')}>
                    <svg class={css['gallery__arrow-icon']} xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32" fill="none">
                        <path class={css['gallery__arrow-icon-path']} d="M22 16l-8.3 8-1.03-1 4-6 .7-1-.7-1-4-6 1.03-1 8.3 8z"/>
                    </svg>
                </div>
            {/if}
        {/if}
    </Outer>
{/if}
