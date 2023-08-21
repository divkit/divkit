<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';

    import css from './Pager.module.css';
    import rootCss from '../Root.module.css';

    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivBaseData } from '../../types/base';
    import type { DivPagerData } from '../../types/pager';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { Orientation } from '../../types/orientation';
    import type { PagerData } from '../../stores/pagers';
    import type { Overflow, SwitchElements } from '../../types/switch-elements';

    import Outer from '../utilities/Outer.svelte';
    import Unknown from '../utilities/Unknown.svelte';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';
    import { genClassName } from '../../utils/genClassName';
    import { pxToEm, pxToEmWithUnits } from '../../utils/pxToEm';
    import { makeStyle } from '../../utils/makeStyle';
    import { correctGeneralOrientation } from '../../utils/correctGeneralOrientation';
    import { correctEdgeInserts } from '../../utils/correctEdgeInserts';
    import { isNonNegativeNumber } from '../../utils/isNonNegativeNumber';
    import { debounce } from '../../utils/debounce';

    export let json: Partial<DivPagerData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const instId = rootCtx.genId('pager');

    let pagerItemsWrapper: HTMLElement;
    let mounted = false;

    let hasItemsError = false;
    $: {
        if (!json.items?.length || !Array.isArray(json.items)) {
            hasItemsError = true;
            rootCtx.logError(wrapError(new Error('Incorrect or empty "items" prop for div "pager"')));
        } else {
            hasItemsError = false;
        }
    }

    function replaceItems(items: DivBaseData[]): void {
        json = {
            ...json,
            items
        };
    }

    $: items = (!hasItemsError && json.items || []).map(item => {
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

    let currentItem = 0;
    let prevSelectedItem = 0;

    let hasLayoutModeError = false;
    $: jsonLayoutMode = rootCtx.getDerivedFromVars(json.layout_mode);
    $: {
        if (!$jsonLayoutMode) {
            hasLayoutModeError = true;
            rootCtx.logError(wrapError(new Error('Empty "layout_mode" prop for div "pager"')));
        } else if ($jsonLayoutMode.type !== 'percentage' && $jsonLayoutMode.type !== 'fixed') {
            hasLayoutModeError = true;
            rootCtx.logError(wrapError(new Error('Incorrect value of "layout_mode.type" for div "pager"')));
        } else {
            hasLayoutModeError = false;
        }
    }

    let orientation: Orientation = 'horizontal';
    $: jsonOrientation = rootCtx.getDerivedFromVars(json.orientation);
    $: {
        orientation = correctGeneralOrientation($jsonOrientation, orientation);
    }

    let itemSpacing = '';
    $: jsonItemSpacing = rootCtx.getDerivedFromVars(json.item_spacing);
    $: {
        const val = $jsonItemSpacing?.value;
        if (val && isNonNegativeNumber(val)) {
            itemSpacing = pxToEm(val || 0);
        }
    }

    $: jsonPaddings = rootCtx.getDerivedFromVars(json.paddings);
    let padding = '';
    $: {
        padding = correctEdgeInserts($jsonPaddings, padding);
    }

    $: jsonRestrictParentScroll = rootCtx.getDerivedFromVars(json.restrict_parent_scroll);

    $: gridAuto = orientation === 'horizontal' ? 'grid-auto-columns' : 'grid-auto-rows';
    let sizeVal = '';

    $: {
        if ($jsonLayoutMode?.type === 'fixed') {
            const paddings = orientation === 'horizontal' ?
                pxToEmWithUnits((json.paddings?.left || 0) + (json.paddings?.right || 0)) :
                pxToEmWithUnits((json.paddings?.top || 0) + (json.paddings?.bottom || 0));
            const neighbourPageWidth = $jsonLayoutMode.neighbour_page_width?.value;
            sizeVal = neighbourPageWidth ?
                `calc(100% + ${paddings} - 2 * ${pxToEmWithUnits(neighbourPageWidth)} - 2 * ${itemSpacing})` :
                '100%';
        } else if ($jsonLayoutMode?.type === 'percentage') {
            const pageWidth = $jsonLayoutMode.page_width?.value;
            sizeVal = `${Number(pageWidth)}%`;
        }
    }

    $: style = {
        'grid-gap': itemSpacing,
        padding,
        [gridAuto]: sizeVal
    };

    $: mods = {
        orientation
    };

    $: hasError = hasItemsError || hasLayoutModeError;

    function checkIsFullyIntersecting(scroller: DOMRect, item: DOMRect): boolean {
        if (orientation === 'horizontal') {
            return item.left >= scroller.left && item.right <= scroller.right;
        }

        return item.top >= scroller.top && item.bottom <= scroller.bottom;
    }

    function calculateCurrentElementIndex(): number {
        const pagerElements = Array.from(pagerItemsWrapper.children) as HTMLElement[];
        const wrapperRect = pagerItemsWrapper.getBoundingClientRect();

        const firstFullyVisibleElement = pagerElements.findIndex(el =>
            checkIsFullyIntersecting(wrapperRect, el.getBoundingClientRect())
        );

        if (firstFullyVisibleElement !== -1) {
            return firstFullyVisibleElement;
        }

        return currentItem;
    }

    function onScroll(): void {
        if (!mounted) {
            // Already destroyed
            return;
        }

        const nextItem = calculateCurrentElementIndex();
        if (nextItem !== currentItem) {
            currentItem = nextItem;
        }
    }

    const onScrollDebounced = debounce(onScroll, 50);

    $: pagers = rootCtx.getStore<Map<string, PagerData>>('pagers');

    function pagerDataUpdate(size: number, currentItem: number): void {
        const pagerId = json.id;
        if (pagerId) {
            const newPagersMap = new Map($pagers);
            $pagers = newPagersMap.set(pagerId, { instId, size, currentItem, scrollToPagerItem });
        }
    }

    function runSelectedActions(currentItem: number): void {
        // prevent initial actions execution
        if (currentItem === prevSelectedItem) {
            return;
        }
        prevSelectedItem = currentItem;

        const actions = rootCtx.getJsonWithVars(items[currentItem].json?.selected_actions);
        if (!actions?.length) {
            return;
        }
        rootCtx.execAnyActions(actions);
    }

    $: pagerDataUpdate(items.length, currentItem);

    $: runSelectedActions(currentItem);

    function scrollToPagerItem(index: number, behavior: ScrollBehavior = 'smooth'): void {
        const isHorizontal = orientation === 'horizontal';
        const nextPagerItem = pagerItemsWrapper.children[index] as HTMLElement;
        const elementOffset: keyof HTMLElement = isHorizontal ? 'offsetLeft' : 'offsetTop';
        const scrollDirection: keyof ScrollToOptions = isHorizontal ? 'left' : 'top';

        pagerItemsWrapper.scroll({
            [scrollDirection]: nextPagerItem[elementOffset],
            behavior
        });
        currentItem = index;
    }

    if (json.id && !layoutParams?.fakeElement) {
        rootCtx.registerInstance<SwitchElements>(json.id, {
            setCurrentItem(item: number) {
                if (item < 0 || item > items.length - 1) {
                    throw new Error('Item is out of range in "set-current-item" action');
                }

                scrollToPagerItem(item);
            },
            setPreviousItem(overflow: Overflow) {
                let previousItem = currentItem - 1;

                if (previousItem < 0) {
                    previousItem = overflow === 'ring' ? items.length - 1 : currentItem;
                }

                scrollToPagerItem(previousItem);
            },
            setNextItem(overflow: Overflow) {
                let nextItem = currentItem + 1;

                if (nextItem > items.length - 1) {
                    nextItem = overflow === 'ring' ? 0 : currentItem;
                }

                scrollToPagerItem(nextItem);
            }
        });
    }

    onMount(() => {
        mounted = true;

        const isIndicatorExist = Boolean(document.getElementById(`${instId}-tab-0`));

        if (isIndicatorExist) {
            const pagerItems = [...pagerItemsWrapper.children] as HTMLElement[];

            for (const [index, item] of pagerItems.entries()) {
                item.setAttribute('role', 'tabpanel');
                item.setAttribute('id', `${instId}-panel-${index}`);
                item.setAttribute('aria-labelledby', `${instId}-tab-${index}`);
            }
        }
    });

    onDestroy(() => {
        mounted = false;

        if (json.id && !layoutParams?.fakeElement) {
            rootCtx.unregisterInstance(json.id);
        }
    });
</script>

{#if !hasError}
    <Outer
        cls={genClassName('pager', css, mods)}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
        customPaddings={true}
        parentOf={json.items}
        {replaceItems}
    >
        <div
            class="{css.pager__items} {$jsonRestrictParentScroll ? rootCss['root_restrict-scroll'] : ''}"
            style={makeStyle(style)}
            bind:this={pagerItemsWrapper}
            on:scroll={onScrollDebounced}
        >
            {#key items}
                {#each items as item}
                    <div class={css.pager__item}>
                        <Unknown
                            div={item.json}
                            templateContext={item.templateContext}
                            origJson={item.origJson}
                            layoutParams={layoutParams?.fakeElement ? { fakeElement: true } : undefined}
                        />
                    </div>
                {/each}
            {/key}
        </div>
    </Outer>
{/if}
