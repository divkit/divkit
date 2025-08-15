<script lang="ts" context="module">
    import type { Mods } from '../../types/general';
    import { correctBooleanInt } from '../../utils/correctBooleanInt';

    interface ChildInfo {
        width?: MaybeMissing<Size>;
        height?: MaybeMissing<Size>;
        visibility?: Visibility;
    }

    const SIZE_MAP: Record<Size['type'], string> = {
        wrap_content: 'content',
        fixed: 'fixed',
        match_parent: 'parent'
    };

    const DUPLICATES_IN_INFINITE = 2;
    const WHEEL_THROTTLE = 400;
    const MIN_SWIPE_DISTANCE = 8;

    function getItemMods(orientation: Orientation, childInfo: {
        width?: MaybeMissing<Size>;
        height?: MaybeMissing<Size>;
    }): Mods {
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
    import { getContext, onDestroy, onMount, tick } from 'svelte';
    import { derived, type Readable } from 'svelte/store';

    import css from './Pager.module.css';
    import rootCss from '../Root.module.css';
    import arrowsCss from '../utilities/Arrows.module.css';

    import type { DivBaseData, Visibility } from '../../types/base';
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
    import { pxToEmWithUnits } from '../../utils/pxToEm';
    import { makeStyle } from '../../utils/makeStyle';
    import { correctGeneralOrientation } from '../../utils/correctGeneralOrientation';
    import { isNonNegativeNumber } from '../../utils/isNonNegativeNumber';
    import { Truthy } from '../../utils/truthy';
    import { nonNegativeModulo } from '../../utils/nonNegativeModulo';
    import { getItemsFromItemBuilder } from '../../utils/itemBuilder';
    import { constStore } from '../../utils/constStore';
    import { correctEdgeInsertsObject } from '../../utils/correctEdgeInsertsObject';
    import { edgeInsertsToCss } from '../../utils/edgeInsertsToCss';
    import { componentFakePagerDuplicate } from '../../utils/componentContext';
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

    let items: ComponentContext[] = [];
    let visibleItemsWithOutDuplicates = 0;
    let visibleItems: {
        width?: MaybeMissing<Size>;
        height?: MaybeMissing<Size>;
        index: number;
        componentContext: ComponentContext;
        duplicate?: boolean;
    }[] = [];
    let visibleToAllMap: Record<number, number> = {};
    let allToVisibleMap: Record<number, number> = {};
    let prevContext: ComponentContext<DivPagerData> | undefined;

    let registerData: PagerRegisterData | undefined;

    let wheelFired = 0;
    let infinite = false;
    let hasDuplicates = false;
    let animated = false;
    let shouldClampDuplicates = false;
    let transform = 0;
    let transformStr = '';
    let swipeTs = 0;
    let cancelPointer: (() => void) | undefined;

    $: origJson = componentContext.origJson;

    function rebind(): void {
        paddingObj = {};
        childLayoutParams = {};
        crossAxisAlignment = 'start';
        scrollAxisAlignment = 'center';
        infinite = false;
        hasDuplicates = false;
        shouldClampDuplicates = false;
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
    $: jsonInfiniteScroll = componentContext.getDerivedFromVars(componentContext.json.infinite_scroll);

    $: {
        infinite = correctBooleanInt($jsonInfiniteScroll, infinite);
    }

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
                    height: item.json.height,
                    visibility: item.json.visibility
                })
            );
        });

        // Create a new array every time so that it is not equal to the previous one
        childStore = derived(children, val => [...val]);
    }

    $: {
        allToVisibleMap = {};
        visibleToAllMap = {};
        visibleItems = $childStore.map((it, index) => {
            return {
                width: it.width,
                height: it.height,
                index,
                componentContext: items[index]
            };
        }).filter((_it, index) => $childStore[index].visibility !== 'gone');

        visibleItems.forEach((it, index) => {
            visibleToAllMap[index] = it.index;
            allToVisibleMap[it.index] = index;
        });

        visibleItemsWithOutDuplicates = visibleItems.length;

        if (infinite && visibleItems.length >= DUPLICATES_IN_INFINITE) {
            const firstDuplicates = visibleItems.slice(0, DUPLICATES_IN_INFINITE).map(it => {
                return {
                    ...it,
                    componentContext: it.componentContext.dup(componentFakePagerDuplicate),
                    duplicate: true
                };
            });
            const lastDuplicates = visibleItems.slice(visibleItems.length - DUPLICATES_IN_INFINITE).map(it => {
                return {
                    ...it,
                    componentContext: it.componentContext.dup(componentFakePagerDuplicate),
                    duplicate: true
                };
            });

            firstDuplicates.forEach((_it, index) => {
                visibleToAllMap[visibleItems.length + index] = index;
            });
            lastDuplicates.forEach((_it, index) => {
                visibleToAllMap[index - DUPLICATES_IN_INFINITE] = visibleItems.length - DUPLICATES_IN_INFINITE + index;
            });

            visibleItems = ([] as typeof visibleItems).concat(lastDuplicates, visibleItems, firstDuplicates);
            hasDuplicates = true;
        } else {
            hasDuplicates = false;
        }

        resnap();
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
    }

    $: gridSizeProp = orientation === 'horizontal' ? 'grid-auto-columns' : 'grid-auto-rows';

    $: if ($jsonScrollAxisAlignment === 'start' || $jsonScrollAxisAlignment === 'center' || $jsonScrollAxisAlignment === 'end') {
        scrollAxisAlignment = $jsonScrollAxisAlignment;

        resnap();
    }

    $: {
        const paddingStart = orientation === 'horizontal' ?
            pxToEmWithUnits(
                paddingObj?.start ||
                ($direction === 'ltr' ? paddingObj?.left : paddingObj?.right) ||
                0
            ) :
            pxToEmWithUnits(
                paddingObj?.top || 0
            );
        const paddingEnd = orientation === 'horizontal' ?
            pxToEmWithUnits(
                paddingObj?.end ||
                ($direction === 'ltr' ? paddingObj?.right : paddingObj?.left) ||
                0
            ) :
            pxToEmWithUnits(
                paddingObj?.bottom || 0
            );
        if ($jsonLayoutMode?.type === 'fixed') {
            const neighbourPageWidth = $jsonLayoutMode.neighbour_page_width?.value || 0;

            if (scrollAxisAlignment === 'center') {
                sizeVal = `calc(100% + ${paddingStart} + ${paddingEnd} - 2 * ${pxToEmWithUnits(neighbourPageWidth)} - 2 * ${itemSpacing})`;
            } else if (scrollAxisAlignment === 'start') {
                sizeVal = `calc(100% + ${paddingEnd} - ${pxToEmWithUnits(neighbourPageWidth)} - ${itemSpacing})`;
            } else {
                sizeVal = `calc(100% + ${paddingStart} - ${pxToEmWithUnits(neighbourPageWidth)} - ${itemSpacing})`;
            }
        } else if ($jsonLayoutMode?.type === 'percentage') {
            let pageWidth = $jsonLayoutMode.page_width?.value;
            if (typeof pageWidth !== 'number' || pageWidth < 0) {
                pageWidth = 100;
            }
            sizeVal = `calc(${(pageWidth / 100).toFixed(2)} * (100% + ${paddingStart} + ${paddingEnd}))`;
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
        transform: transformStr,
    };

    $: mods = {
        animated,
        clip: rootCtx.pagerChildrenClipEnabled,
        orientation,
        'cross-align': crossAxisAlignment,
        'scroll-align': scrollAxisAlignment
    };

    $: hasError = hasLayoutModeError;

    $: shouldCheckArrows = $isDesktop && mounted && !hasError;

    $: hasScrollLeft = hasDuplicates || (
        $direction === 'ltr' ?
            allToVisibleMap[currentItem] > 0 :
            allToVisibleMap[currentItem] + 1 < visibleItems.length
    );

    $: hasScrollRight = hasDuplicates || (
        $direction === 'ltr' ?
            allToVisibleMap[currentItem] + 1 < visibleItems.length :
            allToVisibleMap[currentItem] > 0
    );

    function pagerDataUpdate(size: number, currentItem: number): void {
        if (registerData) {
            registerData.update({
                instId,
                currentItem: allToVisibleMap[currentItem],
                size,
                scrollToPagerItem(index: number) {
                    scrollToPagerItem(visibleToAllMap[index]);
                }
            });
        }
    }

    function runSelectedActions(currentItem: number): void {
        // prevent initial actions execution
        if (currentItem === prevSelectedItem) {
            return;
        }
        prevSelectedItem = currentItem;

        if (!items[currentItem]) {
            return;
        }

        const actions = items[currentItem].json?.selected_actions;
        if (!actions?.length) {
            return;
        }
        componentContext.execAnyActions(actions);
    }

    $: pagerDataUpdate(visibleItemsWithOutDuplicates, currentItem);

    $: runSelectedActions(currentItem);

    function getTransformPosition(visibleIndex: number): number {
        const atStart = hasDuplicates ? false : (visibleIndex === 0);
        const atEnd = hasDuplicates ? false : (visibleIndex === visibleItems.length - 1);

        const isHorizontal = orientation === 'horizontal';
        const pagerItem = pagerItemsWrapper.children[
            visibleIndex + (hasDuplicates ? DUPLICATES_IN_INFINITE : 0)
        ] as HTMLElement;
        if (!pagerItem) {
            return 0;
        }
        const elementOffset: keyof HTMLElement = isHorizontal ? 'offsetLeft' : 'offsetTop';
        const elementSize: keyof HTMLElement = isHorizontal ? 'offsetWidth' : 'offsetHeight';
        const containerSize = getContainerSize();
        const startPadding = getStartPadding();
        const endPadding = getEndPadding();
        const scrollSize = getScrollSize();

        if (containerSize >= scrollSize + startPadding + endPadding) {
            return 0;
        }

        if (atStart) {
            return 0;
        } else if (atEnd) {
            return (containerSize - startPadding - endPadding - scrollSize) * ($direction === 'rtl' ? -1 : 1);
        }

        if (
            scrollAxisAlignment === 'start' && $direction === 'ltr' ||
            scrollAxisAlignment === 'end' && $direction === 'rtl'
        ) {
            return -(pagerItem[elementOffset] - startPadding);
        } else if (
            scrollAxisAlignment === 'end' && $direction === 'ltr' ||
            scrollAxisAlignment === 'start' && $direction === 'rtl'
        ) {
            return -(pagerItem[elementOffset] + pagerItem[elementSize] - containerSize + endPadding);
        }
        return pagerItemsWrapper[elementSize] / 2 - (pagerItem[elementOffset] + pagerItem[elementSize] / 2);
    }

    function scrollToVisiblePagerItem(visibleIndex: number, isAnimated: boolean): void {
        if (!pagerItemsWrapper) {
            return;
        }

        const position = getTransformPosition(visibleIndex);

        animated = isAnimated;
        tick().then(() => {
            transform = position;
            transformStr = transformToStr(transform);
            currentItem = visibleToAllMap[visibleIndex] ?? 0;

            shouldClampDuplicates = hasDuplicates &&
                (visibleIndex < 0 || visibleIndex >= visibleItemsWithOutDuplicates);
        });
    }

    function scrollToPagerItem(index: number, isAnimated = true): void {
        scrollToVisiblePagerItem(allToVisibleMap[index] ?? 0, isAnimated);
    }

    function transformToStr(transform: number): string {
        const isHorizontal = orientation === 'horizontal';
        const transformProp = isHorizontal ? 'translateX' : 'translateY';

        return `${transformProp}(${pxToEmWithUnits(transform)})`;
    }

    function clampIndex(visibleIndex: number, overflow: Overflow): number {
        if (
            hasDuplicates &&
            visibleIndex >= -DUPLICATES_IN_INFINITE &&
            visibleIndex < visibleItemsWithOutDuplicates + DUPLICATES_IN_INFINITE
        ) {
            return visibleIndex;
        }

        if (visibleIndex > visibleItems.length - 1) {
            return overflow === 'ring' ? nonNegativeModulo(visibleIndex, visibleItems.length) : visibleItems.length - 1;
        }
        if (visibleIndex < 0) {
            return overflow === 'ring' ? nonNegativeModulo(visibleIndex, visibleItems.length) : 0;
        }

        return visibleIndex;
    }

    function setPreviousItem(step: number, overflow: Overflow, animated: boolean) {
        const previousItem = clampIndex(allToVisibleMap[currentItem] - step, overflow);

        scrollToVisiblePagerItem(previousItem, animated);
    }

    function setNextItem(step: number, overflow: Overflow, animated: boolean) {
        const nextItem = clampIndex(allToVisibleMap[currentItem] + step, overflow);

        scrollToVisiblePagerItem(nextItem, animated);
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

                    scrollToPagerItem(item, animated);
                },
                setPreviousItem,
                setNextItem,
                scrollToStart(animated) {
                    scrollToPagerItem(visibleItems[hasDuplicates ? DUPLICATES_IN_INFINITE : 0].index, animated);
                },
                scrollToEnd(animated) {
                    scrollToPagerItem(visibleItems[
                        visibleItems.length - 1 - (hasDuplicates ? DUPLICATES_IN_INFINITE : 0)
                    ].index, animated);
                },
                scrollCombined({
                    step,
                    overflow,
                    animated
                }) {
                    if (step) {
                        scrollToPagerItem(clampIndex(allToVisibleMap[currentItem] + step, overflow || 'clamp'), animated);
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

    function getStartPadding(): number {
        const isHorizontal = orientation === 'horizontal';

        if (isHorizontal) {
            return paddingObj.start ?? ($direction === 'ltr' ? paddingObj.left : paddingObj.right) ?? 0;
        }
        return paddingObj.top ?? 0;
    }

    function getEndPadding(): number {
        const isHorizontal = orientation === 'horizontal';

        if (isHorizontal) {
            return paddingObj.end ?? ($direction === 'ltr' ? paddingObj.right : paddingObj.left) ?? 0;
        }
        return paddingObj.bottom ?? 0;
    }

    function getContainerSize(): number {
        if (!pagerItemsWrapper) {
            return 0;
        }

        const isHorizontal = orientation === 'horizontal';

        if (isHorizontal) {
            return pagerItemsWrapper.parentElement?.offsetWidth || 0;
        }
        return pagerItemsWrapper.parentElement?.offsetHeight || 0;
    }

    function getScrollSize(): number {
        const isHorizontal = orientation === 'horizontal';
        const children = Array.from(pagerItemsWrapper.children) as HTMLElement[];
        const first = children[0].getBoundingClientRect();
        const last = children[children.length - 1].getBoundingClientRect();

        if (isHorizontal) {
            if ($direction === 'rtl') {
                return first.right - last.left;
            }
            return last.right - first.left;
        }
        return last.bottom - first.top;
    }

    function onFocus(event: Event): void {
        const target = event.target;
        if (!(target instanceof Element) || !pagerItemsWrapper) {
            return;
        }

        let node = target;

        while (node.parentElement && node.parentElement !== pagerItemsWrapper) {
            node = node.parentElement;
        }
        if (!node) {
            return;
        }
        const index = Array.from(pagerItemsWrapper.children).indexOf(node);
        if (index < 0) {
            return;
        }
        const visibleIndex = index - (hasDuplicates ? DUPLICATES_IN_INFINITE : 0);

        scrollToVisiblePagerItem(visibleIndex, true);
    }

    function onItemsClick(event: MouseEvent): void {
        if (Date.now() - swipeTs < 300) {
            event.preventDefault();
            event.stopImmediatePropagation();
        }
    }

    function onPointerDown(event: PointerEvent): void {
        if (!rootCtx.pagerMouseDragEnabled && event.pointerType === 'mouse') {
            return;
        }

        const isHorizontal = orientation === 'horizontal';
        const start = isHorizontal ? event.pageX : event.pageY;

        const startTransform = transform;
        const containerSize = getContainerSize() - getStartPadding() - getEndPadding();
        const scrollSize = getScrollSize();
        const swipeStartTime = Date.now();

        const onPointerMove = (event: PointerEvent) => {
            const current = isHorizontal ? event.pageX : event.pageY;
            let newTransform = startTransform + current - start;

            if (!hasDuplicates) {
                if ($direction === 'rtl') {
                    if (newTransform < 0) {
                        newTransform = newTransform * containerSize / (newTransform + containerSize * 3);
                    } else if (newTransform + containerSize > scrollSize) {
                        let space = newTransform + containerSize - scrollSize;
                        space = space * containerSize / (space + containerSize * 3);
                        newTransform = -containerSize + scrollSize + space;
                    }
                } else if ($direction === 'ltr') {
                    if (newTransform > 0) {
                        newTransform = newTransform * containerSize / (newTransform + containerSize * 3);
                    } else if (-newTransform + containerSize > scrollSize) {
                        let space = -newTransform + containerSize - scrollSize;
                        space = space * containerSize / (space + containerSize * 3);
                        newTransform = containerSize - scrollSize - space;
                    }
                }
            }

            transform = newTransform;
            transformStr = transformToStr(transform);

            event.preventDefault();
        };
        const onPointerUp = (event: PointerEvent) => {
            cancelPointer?.();
            cancelPointer = undefined;

            // 512px limit for big screens
            const panelsWrapperWidth = Math.min(512, containerSize);
            const swipeDist = Math.abs(startTransform - transform);
            if (swipeDist < MIN_SWIPE_DISTANCE) {
                scrollToVisiblePagerItem(allToVisibleMap[currentItem], true);

                return;
            }

            event.preventDefault();
            swipeTs = Date.now();

            const swipeCoefficient = Math.min(1, (Date.now() - swipeStartTime) / 750);
            let newCurrent = allToVisibleMap[currentItem];
            if (swipeDist > (panelsWrapperWidth / 4) * swipeCoefficient) {
                newCurrent += (startTransform > transform ? 1 : -1) * ($direction === 'rtl' ? -1 : 1);
            }
            if (!hasDuplicates) {
                if (newCurrent >= visibleItems.length) {
                    newCurrent = visibleItems.length - 1;
                } else if (newCurrent < 0) {
                    newCurrent = 0;
                }
            }

            scrollToVisiblePagerItem(newCurrent, true);
        };

        window.addEventListener('pointermove', onPointerMove);
        window.addEventListener('pointerup', onPointerUp);
        window.addEventListener('pointercancel', onPointerUp);

        cancelPointer?.();
        cancelPointer = () => {
            window.removeEventListener('pointermove', onPointerMove);
            window.removeEventListener('pointerup', onPointerUp);
            window.removeEventListener('pointercancel', onPointerUp);
        };
    }

    function onWheel(event: WheelEvent): void {
        if (!event.deltaX || Math.abs(event.deltaX) < Math.abs(event.deltaY)) {
            return;
        }

        const now = Date.now();
        if (now - wheelFired < WHEEL_THROTTLE) {
            return;
        }
        wheelFired = now;

        const dir = ($direction === 'rtl' ? -1 : 1) * event.deltaX;
        if (dir > 0) {
            setNextItem(1, 'clamp', true);
        } else {
            setPreviousItem(1, 'clamp', true);
        }
    }

    function onTransitionEnd(): void {
        animated = false;

        if (shouldClampDuplicates) {
            tick().then(() => {
                scrollToPagerItem(currentItem, false);
            });
        }
    }

    function resnap(): void {
        scrollToPagerItem(currentItem, false);
    }

    onMount(() => {
        mounted = true;

        if (pagerItemsWrapper) {
            scrollToPagerItem(currentItem, false);
        }
    });

    onDestroy(() => {
        mounted = false;

        cancelPointer?.();

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

<svelte:window
    on:resize={resnap}
/>

{#if !hasError}
    <Outer
        cls={genClassName('pager', css, mods)}
        {componentContext}
        {layoutParams}
        customPaddings={true}
        parentOf={items}
        {replaceItems}
        on:pointerdown={onPointerDown}
        on:wheel={onWheel}
    >
        <!-- svelte-ignore a11y-click-events-have-key-events -->
        <!-- svelte-ignore a11y-no-static-element-interactions -->
        <div
            class="{css.pager__items} {$jsonRestrictParentScroll ? rootCss['root_restrict-scroll'] : ''}"
            style={makeStyle(style)}
            bind:this={pagerItemsWrapper}
            on:transitionend={onTransitionEnd}
            on:focus|capture={onFocus}
            on:click|capture={onItemsClick}
        >
            {#each visibleItems as item}
                <div
                    class={genClassName('pager__item', css, getItemMods(orientation, item))}
                    role="tabpanel"
                    id="{instId}-panel-{item.index}"
                    aria-labelledby="{instId}-tab-{item.index}"
                >
                    <Unknown
                        componentContext={item.componentContext}
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
