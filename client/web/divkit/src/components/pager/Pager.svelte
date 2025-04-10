<script lang="ts" context="module">
    import type { Mods } from '../../types/general';
    import { correctBooleanInt } from '../../utils/correctBooleanInt';

    interface ChildInfo {
        width?: MaybeMissing<Size>;
        height?: MaybeMissing<Size>;
    }

    const SIZE_MAP: Record<Size['type'], string> = {
        wrap_content: 'content',
        fixed: 'fixed',
        match_parent: 'parent'
    };

    function getItemMods(orientation: Orientation, childInfo: ChildInfo): Mods {
        if (orientation === 'horizontal') {
            const heightType = childInfo.height?.type || '';

            return {
                height: heightType in SIZE_MAP ? SIZE_MAP[heightType as Size['type']] : 'content',
                'height-constrained': childInfo.height?.type === 'wrap_content' ? correctBooleanInt(childInfo.height.constrained, false) : false
            };
        }

        const widthType = childInfo.width?.type || '';

        return {
            width: widthType in SIZE_MAP ? SIZE_MAP[widthType as Size['type']] : 'parent',
            'width-constrained': childInfo.width?.type === 'wrap_content' ? correctBooleanInt(childInfo.width.constrained, false) : false
        };
    }
</script>

<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';
    import { derived, type Readable } from 'svelte/store';

    import css from './Pager.module.css';
    import rootCss from '../Root.module.css';
    import arrowsCss from '../utilities/Arrows.module.css';

    import type { DivBaseData } from '../../types/base';
    import type { DivPagerData } from '../../types/pager';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { Orientation } from '../../types/orientation';
    import type { SwitchElements } from '../../types/switch-elements';
    import type { ComponentContext, PagerRegisterData } from '../../types/componentContext';
    import type { MaybeMissing } from '../../expressions/json';
    import type { Size } from '../../types/sizes';
    import type { Variable } from '../../expressions/variable';
    import type { Overflow } from '../../../typings/common';
    import type { EdgeInsets } from '../../types/edgeInserts';

    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';
    import { genClassName } from '../../utils/genClassName';
    import { pxToEm, pxToEmWithUnits } from '../../utils/pxToEm';
    import { makeStyle } from '../../utils/makeStyle';
    import { correctGeneralOrientation } from '../../utils/correctGeneralOrientation';
    import { isNonNegativeNumber } from '../../utils/isNonNegativeNumber';
    import { debounce } from '../../utils/debounce';
    import { Truthy } from '../../utils/truthy';
    import { nonNegativeModulo } from '../../utils/nonNegativeModulo';
    import { getItemsFromItemBuilder } from '../../utils/itemBuilder';
    import { constStore } from '../../utils/constStore';
    import { correctEdgeInsertsObject } from '../../utils/correctEdgeInsertsObject';
    import { edgeInsertsToCss } from '../../utils/edgeInsertsToCss';
    import Outer from '../utilities/Outer.svelte';
    import Unknown from '../utilities/Unknown.svelte';
    import DevtoolHolder from '../utilities/DevtoolHolder.svelte';

    export let componentContext: ComponentContext<DivPagerData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const direction = rootCtx.direction;

    const instId = rootCtx.genId('pager');

    const leftClass = rootCtx.getCustomization('pagerLeftClass');
    const rightClass = rootCtx.getCustomization('pagerRightClass');

    const isDesktop = rootCtx.isDesktop;

    const onScrollDebounced = debounce(onScroll, 50);

    let prevId: string | undefined;

    let pagerItemsWrapper: HTMLElement;
    let mounted = false;

    let childStore: Readable<ChildInfo[]>;

    let currentItem = 0;
    let prevSelectedItem = 0;

    let hasLayoutModeError = false;

    let orientation: Orientation = 'horizontal';
    let itemSpacing = '0em';
    let paddingObj: EdgeInsets = {};
    let padding = '';
    let sizeVal = '';

    let childLayoutParams: LayoutParams = {};
    let crossAxisAlignment: 'start' | 'center' | 'end' = 'start';
    let scrollAxisAlignment: 'start' | 'center' | 'end' = 'center';

    let scrollPaddings: EdgeInsets = {};

    let items: ComponentContext[] = [];
    let prevContext: ComponentContext<DivPagerData> | undefined;

    let registerData: PagerRegisterData | undefined;

    $: origJson = componentContext.origJson;

    function rebind(): void {
        paddingObj = {};
        childLayoutParams = {};
        crossAxisAlignment = 'start';
        scrollAxisAlignment = 'center';
        scrollPaddings = {};
    }

    $: if (origJson) {
        rebind();
    }

    // eslint-disable-next-line no-nested-ternary
    $: jsonItemBuilderData = typeof componentContext.json.item_builder?.data === 'string' ? componentContext.getDerivedFromVars(
        componentContext.json.item_builder?.data, undefined, true
    ) : (componentContext.json.item_builder?.data ? constStore(componentContext.json.item_builder.data) : undefined);

    $: jsonLayoutMode = componentContext.getDerivedFromVars(componentContext.json.layout_mode);
    $: jsonOrientation = componentContext.getDerivedFromVars(componentContext.json.orientation);
    $: jsonItemSpacing = componentContext.getDerivedFromVars(componentContext.json.item_spacing);
    $: jsonPaddings = componentContext.getDerivedFromVars(componentContext.json.paddings);
    $: jsonRestrictParentScroll = componentContext.getDerivedFromVars(componentContext.json.restrict_parent_scroll);
    $: jsonCrossAxisAlignment = componentContext.getDerivedFromVars(componentContext.json.cross_axis_alignment);
    $: jsonScrollAxisAlignment = componentContext.getDerivedFromVars(componentContext.json.scroll_axis_alignment);

    function replaceItems(items: (MaybeMissing<DivBaseData> | undefined)[]): void {
        componentContext = prevContext = {
            ...componentContext,
            json: {
                ...componentContext.json,
                items: items.filter(Truthy)
            }
        };
    }

    $: {
        let newItems: {
            div: MaybeMissing<DivBaseData>;
            id?: string | undefined;
            vars?: Map<string, Variable> | undefined;
        }[] = [];
        if (
            componentContext.json.item_builder &&
            Array.isArray($jsonItemBuilderData) &&
            Array.isArray(componentContext.json.item_builder.prototypes)
        ) {
            const builder = componentContext.json.item_builder;
            newItems = getItemsFromItemBuilder($jsonItemBuilderData, rootCtx, componentContext, builder);
        } else {
            newItems = (Array.isArray(componentContext.json.items) && componentContext.json.items || []).map(it => {
                return {
                    div: it
                };
            });
        }

        const unusedContexts = new Set(items);
        const jsonToContextMap = new Map<unknown, ComponentContext>();

        if (prevContext === componentContext) {
            items.forEach(context => {
                jsonToContextMap.set(context.json, context);
            });
        }

        items = newItems.map((item, index) => {
            const found = jsonToContextMap.get(item.div);
            if (found) {
                unusedContexts.delete(found);
                return found;
            }

            return componentContext.produceChildContext(item.div, {
                path: index,
                variables: item.vars,
                id: item.id
            });
        });

        for (const ctx of unusedContexts) {
            ctx.destroy();
        }
        prevContext = componentContext;
    }

    $: {
        let children: Readable<ChildInfo>[] = [];

        items.forEach(item => {
            children.push(
                componentContext.getDerivedFromVars({
                    width: item.json.width,
                    height: item.json.height
                })
            );
        });

        // Create a new array every time so that it is not equal to the previous one
        childStore = derived(children, val => [...val]);
    }

    $: {
        if (!$jsonLayoutMode) {
            hasLayoutModeError = true;
            componentContext.logError(wrapError(new Error('Empty "layout_mode" prop for div "pager"')));
        } else if ($jsonLayoutMode.type !== 'percentage' && $jsonLayoutMode.type !== 'fixed' && $jsonLayoutMode.type !== 'wrap_content') {
            hasLayoutModeError = true;
            componentContext.logError(wrapError(new Error('Incorrect value of "layout_mode.type" for div "pager"')));
        } else {
            hasLayoutModeError = false;
        }
    }

    $: {
        orientation = correctGeneralOrientation($jsonOrientation, orientation);
    }

    $: {
        const val = $jsonItemSpacing?.value;
        if (val && isNonNegativeNumber(val)) {
            itemSpacing = pxToEmWithUnits(val || 0);
        }
    }

    $: {
        paddingObj = correctEdgeInsertsObject($jsonPaddings, paddingObj);
        padding = edgeInsertsToCss(paddingObj, $direction);
        scrollPaddings = {
            top: paddingObj.top,
            right: ($direction === 'ltr' ? paddingObj.start : paddingObj.end) ?? paddingObj.left ?? 0,
            bottom: paddingObj.bottom,
            left: ($direction === 'ltr' ? paddingObj.end : paddingObj.start) ?? paddingObj.right ?? 0
        };
    }

    $: gridSizeProp = orientation === 'horizontal' ? 'grid-auto-columns' : 'grid-auto-rows';

    $: if ($jsonScrollAxisAlignment === 'start' || $jsonScrollAxisAlignment === 'center' || $jsonScrollAxisAlignment === 'end') {
        scrollAxisAlignment = $jsonScrollAxisAlignment;
    }

    $: {
        if ($jsonLayoutMode?.type === 'fixed') {
            const paddings = componentContext.json.paddings;
            const paddingStart = orientation === 'horizontal' ?
                pxToEmWithUnits(
                    paddings?.start ||
                    ($direction === 'ltr' ? paddings?.left : paddings?.right) ||
                    0
                ) :
                pxToEmWithUnits(
                    paddings?.top || 0
                );
            const paddingEnd = orientation === 'horizontal' ?
                pxToEmWithUnits(
                    paddings?.end ||
                    ($direction === 'ltr' ? paddings?.right : paddings?.left) ||
                    0
                ) :
                pxToEmWithUnits(
                    paddings?.bottom || 0
                );
            const sumPadding = paddingStart + paddingEnd;
            const neighbourPageWidth = $jsonLayoutMode.neighbour_page_width?.value || 0;

            if (scrollAxisAlignment === 'center') {
                sizeVal = `calc(100% + ${paddingStart} + ${paddingEnd} - 2 * ${pxToEmWithUnits(neighbourPageWidth)} - 2 * ${itemSpacing})`;
            } else if (scrollAxisAlignment === 'start') {
                sizeVal = `calc(100% + ${paddingEnd} - ${pxToEmWithUnits(neighbourPageWidth)} - ${itemSpacing})`;
            } else {
                sizeVal = `calc(100% + ${paddingStart} - ${pxToEmWithUnits(neighbourPageWidth)} - ${itemSpacing})`;
            }
        } else if ($jsonLayoutMode?.type === 'percentage') {
            const pageWidth = $jsonLayoutMode.page_width?.value;
            sizeVal = `${Number(pageWidth)}%`;
        } else if ($jsonLayoutMode?.type === 'wrap_content') {
            sizeVal = 'minmax(max-content, auto)';
        }
    }

    $: if ($jsonCrossAxisAlignment === 'start' || $jsonCrossAxisAlignment === 'center' || $jsonCrossAxisAlignment === 'end') {
        crossAxisAlignment = $jsonCrossAxisAlignment;

        childLayoutParams = {
            [orientation === 'horizontal' ? 'parentVAlign' : 'parentHAlign']: crossAxisAlignment
        };
    }

    $: style = {
        'grid-gap': itemSpacing,
        padding,
        [gridSizeProp]: sizeVal,
        'scroll-padding-top': scrollPaddings.top ? pxToEm(scrollPaddings.top) : undefined,
        'scroll-padding-right': scrollPaddings.right ? pxToEm(scrollPaddings.right) : undefined,
        'scroll-padding-bottom': scrollPaddings.bottom ? pxToEm(scrollPaddings.bottom) : undefined,
        'scroll-padding-left': scrollPaddings.left ? pxToEm(scrollPaddings.left) : undefined,
    };

    $: mods = {
        clip: rootCtx.pagerChildrenClipEnabled,
        orientation,
        'cross-align': crossAxisAlignment,
        'scroll-align': scrollAxisAlignment
    };

    $: hasError = hasLayoutModeError;

    $: shouldCheckArrows = $isDesktop && mounted && !hasError;

    $: hasScrollLeft = $direction === 'ltr' ? currentItem > 0 : currentItem + 1 < items.length;

    $: hasScrollRight = $direction === 'ltr' ? currentItem + 1 < items.length : currentItem > 0;

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

    function pagerDataUpdate(size: number, currentItem: number): void {
        if (registerData) {
            registerData.update({
                instId,
                currentItem,
                size,
                scrollToPagerItem
            });
        }
    }

    function runSelectedActions(currentItem: number): void {
        // prevent initial actions execution
        if (currentItem === prevSelectedItem) {
            return;
        }
        prevSelectedItem = currentItem;

        const actions = componentContext.getJsonWithVars(items[currentItem].json?.selected_actions);
        if (!actions?.length) {
            return;
        }
        componentContext.execAnyActions(actions);
    }

    $: pagerDataUpdate(items.length, currentItem);

    $: runSelectedActions(currentItem);

    function scrollToPagerItem(index: number, behavior: ScrollBehavior = 'smooth'): void {
        if (!pagerItemsWrapper) {
            return;
        }

        const isHorizontal = orientation === 'horizontal';
        const nextPagerItem = pagerItemsWrapper.children[index] as HTMLElement;
        const elementOffset: keyof HTMLElement = isHorizontal ? 'offsetLeft' : 'offsetTop';
        const elementSize: keyof HTMLElement = isHorizontal ? 'offsetWidth' : 'offsetHeight';
        const scrollDirection: keyof ScrollToOptions = isHorizontal ? 'left' : 'top';
        const scrollSize: keyof HTMLElement = isHorizontal ? 'scrollWidth' : 'scrollHeight';
        let position;
        if (index === 0) {
            position = 0;
        } else if (index === items.length - 1) {
            position = pagerItemsWrapper[scrollSize];
        } else {
            position = nextPagerItem[elementOffset] + nextPagerItem[elementSize] / 2 -
                pagerItemsWrapper[elementSize] / 2;
        }

        pagerItemsWrapper.scroll({
            [scrollDirection]: position,
            behavior
        });
        currentItem = index;
    }

    function clampIndex(index: number, overflow: Overflow): number {
        if (index > items.length - 1) {
            return overflow === 'ring' ? nonNegativeModulo(index, items.length) : items.length - 1;
        }
        if (index < 0) {
            return overflow === 'ring' ? nonNegativeModulo(index, items.length) : 0;
        }

        return index;
    }

    function setPreviousItem(step: number, overflow: Overflow, animated: boolean) {
        let previousItem = clampIndex(currentItem - step, overflow);

        scrollToPagerItem(previousItem, animated ? 'smooth' : 'instant');
    }

    function setNextItem(step: number, overflow: Overflow, animated: boolean) {
        let nextItem = clampIndex(currentItem + step, overflow);

        scrollToPagerItem(nextItem, animated ? 'smooth' : 'instant');
    }

    function init(): void {
        registerData?.destroy();
        registerData = undefined;

        if (prevId) {
            rootCtx.unregisterInstance(prevId);
            prevId = undefined;
        }

        if (!componentContext.fakeElement) {
            registerData = componentContext.registerPager(componentContext.id || undefined);
        }

        if (componentContext.id && !componentContext.fakeElement) {
            prevId = componentContext.id;
            rootCtx.registerInstance<SwitchElements>(prevId, {
                setCurrentItem(item: number, animated: boolean) {
                    if (item < 0 || item > items.length - 1) {
                        throw new Error('Item is out of range in "set-current-item" action');
                    }

                    scrollToPagerItem(item, animated ? 'smooth' : 'instant');
                },
                setPreviousItem,
                setNextItem,
                scrollToStart(animated) {
                    scrollToPagerItem(0, animated ? 'smooth' : 'instant');
                },
                scrollToEnd(animated) {
                    scrollToPagerItem(items.length - 1, animated ? 'smooth' : 'instant');
                },
                scrollCombined({
                    step,
                    overflow,
                    animated
                }) {
                    if (step) {
                        scrollToPagerItem(clampIndex(currentItem + step, overflow || 'clamp'), animated ? 'smooth' : 'instant');
                    }
                },
            }, 'warn');
        }
    }

    $: if (componentContext.json) {
        const defaultItem = componentContext.getJsonWithVars(componentContext.json.default_item);
        if (typeof defaultItem === 'number' && defaultItem >= 0 && defaultItem < items.length) {
            currentItem = prevSelectedItem = defaultItem;
            pagerDataUpdate(items.length, defaultItem);
        }

        init();
    }

    onMount(() => {
        mounted = true;

        if (pagerItemsWrapper) {
            scrollToPagerItem(currentItem, 'instant');
        }
    });

    onDestroy(() => {
        mounted = false;

        items.forEach(context => {
            context.destroy();
        });

        if (prevId) {
            rootCtx.unregisterInstance(prevId);
            prevId = undefined;
        }

        registerData?.destroy();
        registerData = undefined;
    });
</script>

{#if !hasError}
    <Outer
        cls={genClassName('pager', css, mods)}
        {componentContext}
        {layoutParams}
        customPaddings={true}
        parentOf={items}
        {replaceItems}
    >
        <div
            class="{css.pager__items} {$jsonRestrictParentScroll ? rootCss['root_restrict-scroll'] : ''}"
            style={makeStyle(style)}
            bind:this={pagerItemsWrapper}
            on:scroll={onScrollDebounced}
        >
            {#each items as item, index}
                <div
                    class={genClassName('pager__item', css, getItemMods(orientation, $childStore[index]))}
                    role="tabpanel"
                    id="{instId}-panel-{index}"
                    aria-labelledby="{instId}-tab-{index}"
                >
                    <Unknown
                        componentContext={item}
                        layoutParams={childLayoutParams}
                    />
                </div>
            {/each}
        </div>

        {#if hasScrollLeft && shouldCheckArrows}
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-static-element-interactions -->
            <div class="{leftClass || `${css.pager__arrow} ${arrowsCss.arrow} ${arrowsCss.arrow_left}`}" on:click={() => ($direction === 'ltr' ? setPreviousItem : setNextItem)(1, 'clamp', true)}>
                {#if !leftClass}
                    <svg class={arrowsCss.arrow__icon} xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32" fill="none">
                        <path class={css['pager__arrow-icon-path']} d="m10 16 8.3 8 1.03-1-4-6-.7-1 .7-1 4-6-1.03-1z"/>
                    </svg>
                {/if}
            </div>
        {/if}
        {#if hasScrollRight && shouldCheckArrows}
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-static-element-interactions -->
            <div class="{rightClass || `${css.pager__arrow} ${arrowsCss.arrow} ${arrowsCss.arrow_right}`}" on:click={() => ($direction === 'ltr' ? setNextItem : setPreviousItem)(1, 'clamp', true)}>
                {#if !rightClass}
                    <svg class={arrowsCss.arrow__icon} xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32" fill="none">
                        <path class={css['pager__arrow-icon-path']} d="M22 16l-8.3 8-1.03-1 4-6 .7-1-.7-1-4-6 1.03-1 8.3 8z"/>
                    </svg>
                {/if}
            </div>
        {/if}
    </Outer>
{:else if process.env.DEVTOOL}
    <DevtoolHolder
        {componentContext}
    />
{/if}
