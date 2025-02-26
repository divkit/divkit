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
    import type { PagerData } from '../../stores/pagers';
    import type { Overflow, SwitchElements } from '../../types/switch-elements';
    import type { ComponentContext } from '../../types/componentContext';
    import type { MaybeMissing } from '../../expressions/json';
    import type { Size } from '../../types/sizes';
    import type { Variable } from '../../expressions/variable';

    import Outer from '../utilities/Outer.svelte';
    import Unknown from '../utilities/Unknown.svelte';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';
    import { genClassName } from '../../utils/genClassName';
    import { pxToEmWithUnits } from '../../utils/pxToEm';
    import { makeStyle } from '../../utils/makeStyle';
    import { correctGeneralOrientation } from '../../utils/correctGeneralOrientation';
    import { correctEdgeInserts } from '../../utils/correctEdgeInserts';
    import { isNonNegativeNumber } from '../../utils/isNonNegativeNumber';
    import { debounce } from '../../utils/debounce';
    import { Truthy } from '../../utils/truthy';
    import { nonNegativeModulo } from '../../utils/nonNegativeModulo';
    import { getItemsFromItemBuilder } from '../../utils/itemBuilder';
    import { constStore } from '../../utils/constStore';
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
    let padding = '';
    let sizeVal = '';

    let childLayoutParams: LayoutParams = {};
    let crossAxisAlignment: 'start' | 'center' | 'end' = 'start';

    let items: ComponentContext[] = [];
    let prevContext: ComponentContext<DivPagerData> | undefined;

    $: origJson = componentContext.origJson;

    function rebind(): void {
        padding = '';
        childLayoutParams = {};
        crossAxisAlignment = 'start';
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
        padding = correctEdgeInserts($jsonPaddings, $direction, padding);
    }

    $: gridAuto = orientation === 'horizontal' ? 'grid-auto-columns' : 'grid-auto-rows';

    $: {
        if ($jsonLayoutMode?.type === 'fixed') {
            const paddings = orientation === 'horizontal' ?
                pxToEmWithUnits(
                    (($direction === 'ltr' ?
                        componentContext.json.paddings?.start :
                        componentContext.json.paddings?.end
                    ) || componentContext.json.paddings?.left || 0) +
                    (($direction === 'ltr' ?
                        componentContext.json.paddings?.end :
                        componentContext.json.paddings?.start
                    ) || componentContext.json.paddings?.right || 0)
                ) :
                pxToEmWithUnits(
                    (componentContext.json.paddings?.top || 0) +
                    (componentContext.json.paddings?.bottom || 0)
                );
            const neighbourPageWidth = $jsonLayoutMode.neighbour_page_width?.value;
            sizeVal = neighbourPageWidth ?
                `calc(100% + ${paddings} - 2 * ${pxToEmWithUnits(neighbourPageWidth)} - 2 * ${itemSpacing})` :
                '100%';
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
        [gridAuto]: sizeVal
    };

    $: mods = {
        clip: rootCtx.pagerChildrenClipEnabled,
        orientation,
        'cross-align': crossAxisAlignment
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

    $: pagers = rootCtx.getStore<Map<string, PagerData>>('pagers');

    function pagerDataUpdate(size: number, currentItem: number): void {
        const pagerId = componentContext.id;
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
        const position = nextPagerItem[elementOffset] + nextPagerItem[elementSize] / 2 -
            pagerItemsWrapper[elementSize] / 2;

        pagerItemsWrapper.scroll({
            [scrollDirection]: position,
            behavior
        });
        currentItem = index;
    }

    function setPreviousItem(step: number, overflow: Overflow) {
        let previousItem = currentItem - step;

        if (previousItem < 0) {
            previousItem = overflow === 'ring' ? nonNegativeModulo(previousItem, items.length) : 0;
        }

        scrollToPagerItem(previousItem);
    }

    function setNextItem(step: number, overflow: Overflow) {
        let nextItem = currentItem + step;

        if (nextItem > items.length - 1) {
            nextItem = overflow === 'ring' ? nonNegativeModulo(nextItem, items.length) : items.length - 1;
        }

        scrollToPagerItem(nextItem);
    }

    $: if (componentContext.json) {
        const defaultItem = componentContext.getJsonWithVars(componentContext.json.default_item);
        if (typeof defaultItem === 'number' && defaultItem >= 0 && defaultItem < items.length) {
            currentItem = prevSelectedItem = defaultItem;
        }

        if (prevId) {
            rootCtx.unregisterInstance(prevId);
            prevId = undefined;
        }

        if (componentContext.id && !componentContext.fakeElement) {
            prevId = componentContext.id;
            rootCtx.registerInstance<SwitchElements>(prevId, {
                setCurrentItem(item: number) {
                    if (item < 0 || item > items.length - 1) {
                        throw new Error('Item is out of range in "set-current-item" action');
                    }

                    scrollToPagerItem(item);
                },
                setPreviousItem,
                setNextItem,
                scrollToStart() {
                    scrollToPagerItem(0);
                },
                scrollToEnd() {
                    scrollToPagerItem(items.length - 1);
                }
            });
        }
    }

    onMount(() => {
        mounted = true;

        if (pagerItemsWrapper) {
            const isIndicatorExist = Boolean(document.getElementById(`${instId}-tab-0`));

            if (isIndicatorExist) {
                const pagerItems = [...pagerItemsWrapper.children] as HTMLElement[];

                for (const [index, item] of pagerItems.entries()) {
                    item.setAttribute('role', 'tabpanel');
                    item.setAttribute('id', `${instId}-panel-${index}`);
                    item.setAttribute('aria-labelledby', `${instId}-tab-${index}`);
                }
            }

            if (currentItem > 0) {
                scrollToPagerItem(currentItem, 'instant');
            }
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
                <div class={genClassName('pager__item', css, getItemMods(orientation, $childStore[index]))}>
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
            <div class="{leftClass || `${css.pager__arrow} ${arrowsCss.arrow} ${arrowsCss.arrow_left}`}" on:click={() => ($direction === 'ltr' ? setPreviousItem : setNextItem)(1, 'clamp')}>
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
            <div class="{rightClass || `${css.pager__arrow} ${arrowsCss.arrow} ${arrowsCss.arrow_right}`}" on:click={() => ($direction === 'ltr' ? setNextItem : setPreviousItem)(1, 'clamp')}>
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
