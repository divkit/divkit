<script lang="ts" context="module">
    const AVAIL_SHAPES = [
        'rounded_rectangle',
        'circle'
    ];
</script>

<script lang="ts">
    import { getContext, onDestroy, onMount, tick } from 'svelte';

    import rootCss from '../Root.module.css';
    import css from './Indicator.module.css';

    import type { DivIndicatorData } from '../../types/indicator';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { MaybeMissing } from '../../expressions/json';
    import type { DivIndicatorDefaultItemPlacement, DivIndicatorStretchItemPlacement } from '../../types/indicator';
    import type { ComponentContext, PagerData } from '../../types/componentContext';

    import Outer from '../utilities/Outer.svelte';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { genClassName } from '../../utils/genClassName';
    import { correctNonNegativeNumber } from '../../utils/correctNonNegativeNumber';
    import { pxToEm } from '../../utils/pxToEm';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { ARROW_LEFT, ARROW_RIGHT, END, HOME } from '../../utils/keyboard/codes';
    import { correctDrawableStyle, type DrawableStyle } from '../../utils/correctDrawableStyles';
    import { correctColor } from '../../utils/correctColor';

    export let componentContext: ComponentContext<DivIndicatorData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const direction = rootCtx.direction;

    let activeStyle: DrawableStyle = {
        width: 13,
        height: 13,
        borderRadius: 6.5,
        background: '#ffdc60'
    };
    let inactiveStyle: DrawableStyle = {
        width: 10,
        height: 10,
        borderRadius: 5,
        background: '#33919cb5'
    };

    let placement: 'default' | 'stretch' = 'default';
    let spaceBetweenCenters = 15;
    let maxVisibleItems = 10;
    let itemSpacing = 5;

    let scroller: HTMLElement;
    let indicatorItemsWrapper: HTMLElement;
    let pagerData: PagerData;
    let pagerDataUnsubscribe: (() => void) | undefined;
    let mounted = false;

    $: origJson = componentContext.origJson;

    function rebind(): void {
        placement = 'default';
        spaceBetweenCenters = 15;
        maxVisibleItems = 10;
        itemSpacing = 5;
        activeStyle = {
            width: 13,
            height: 13,
            borderRadius: 6.5,
            background: '#ffdc60'
        };
        inactiveStyle = {
            width: 10,
            height: 10,
            borderRadius: 5,
            background: '#33919cb5'
        };
    }

    $: if (origJson) {
        rebind();
    }

    $: if (origJson && mounted) {
        init();
    }

    $: jsonShape = componentContext.getDerivedFromVars(componentContext.json.shape);
    $: jsonActiveItemColor = componentContext.getDerivedFromVars(componentContext.json.active_item_color);
    $: jsonInactiveItemColor = componentContext.getDerivedFromVars(componentContext.json.inactive_item_color);
    $: jsonActiveItemSize = componentContext.getDerivedFromVars(componentContext.json.active_item_size);
    $: jsonActiveShape = componentContext.getDerivedFromVars(componentContext.json.active_shape);
    $: jsonInactiveShape = componentContext.getDerivedFromVars(componentContext.json.inactive_shape);
    $: jsonSpaceBetweenCenters = componentContext.getDerivedFromVars(componentContext.json.space_between_centers);
    $: jsonItemsPlacement = componentContext.getDerivedFromVars(componentContext.json.items_placement);

    $: {
        if ($jsonActiveShape) {
            activeStyle = correctDrawableStyle<DrawableStyle>({
                type: 'shape_drawable',
                shape: $jsonActiveShape
            }, AVAIL_SHAPES, activeStyle);
        }
        if ($jsonInactiveShape) {
            inactiveStyle = correctDrawableStyle<DrawableStyle>({
                type: 'shape_drawable',
                shape: $jsonInactiveShape
            }, AVAIL_SHAPES, inactiveStyle);
        }
        if (!$jsonActiveShape && !$jsonInactiveShape && $jsonShape) {
            const activeSize = correctPositiveNumber($jsonActiveItemSize, 1.3);
            inactiveStyle = correctDrawableStyle<DrawableStyle>({
                type: 'shape_drawable',
                shape: $jsonShape,
                color: inactiveStyle.background
            }, AVAIL_SHAPES, inactiveStyle);
            inactiveStyle.background = correctColor($jsonInactiveItemColor, 1, inactiveStyle.background);
            activeStyle = {
                ...inactiveStyle,
                width: inactiveStyle.width * activeSize,
                height: inactiveStyle.height * activeSize,
                borderRadius: inactiveStyle.borderRadius * activeSize,
                background: activeStyle.background
            };
            activeStyle.background = correctColor($jsonActiveItemColor, 1, activeStyle.background);
        }
    }

    $: if ($jsonItemsPlacement && ($jsonItemsPlacement.type === 'default' || $jsonItemsPlacement.type === 'stretch')) {
        placement = $jsonItemsPlacement.type;
        if (placement === 'default') {
            spaceBetweenCenters = correctNonNegativeNumber(
                ($jsonItemsPlacement as MaybeMissing<DivIndicatorDefaultItemPlacement>).space_between_centers?.value,
                spaceBetweenCenters
            );
        } else if (placement === 'stretch') {
            const placement = $jsonItemsPlacement as MaybeMissing<DivIndicatorStretchItemPlacement>;
            maxVisibleItems = correctPositiveNumber(placement.max_visible_items, maxVisibleItems);
            itemSpacing = correctNonNegativeNumber(placement.item_spacing?.value, itemSpacing);
        }
    } else {
        placement = 'default';
        if ($jsonSpaceBetweenCenters) {
            spaceBetweenCenters = correctNonNegativeNumber($jsonSpaceBetweenCenters.value, spaceBetweenCenters);
        }
    }

    async function onPagerDataUpdate(data: PagerData): Promise<void> {
        pagerData = data;

        await tick();

        if (indicatorItemsWrapper) {
            // if not destroyed yet

            const elem = indicatorItemsWrapper.children[pagerData.currentItem] as HTMLElement;

            if (elem) {
                const currentItemOffsetLeft = elem.offsetLeft;

                scroller.scroll({
                    left: currentItemOffsetLeft - scroller.clientWidth / 2,
                    behavior: 'smooth'
                });
            }
        }
    }

    function onIndicatorItemClick(index: number) {
        if (index !== pagerData.currentItem) {
            pagerData.scrollToPagerItem(index);
        }
    }

    function onIndicatorItemKeydown(event: KeyboardEvent): void {
        if (event.ctrlKey || event.shiftKey || event.altKey || event.metaKey) {
            return;
        }

        const { size, currentItem } = pagerData;
        if (event.which === ARROW_LEFT) {
            const prevItem = currentItem - 1 < 0 ? currentItem : currentItem - 1;
            moveFocus(prevItem);
        } else if (event.which === ARROW_RIGHT) {
            const nextItem = currentItem + 1 >= size ? currentItem : currentItem + 1;
            moveFocus(nextItem);
        } else if (event.which === HOME) {
            moveFocus(0);
        } else if (event.which === END) {
            moveFocus(size - 1);
        } else {
            return;
        }

        event.preventDefault();
    }

    async function moveFocus(index: number) {
        pagerData.scrollToPagerItem(index);

        await tick();

        const activeItem = indicatorItemsWrapper.querySelector(`.${css.indicator__item_active}`) as HTMLElement | null;
        if (activeItem) {
            activeItem.focus();
        }
    }

    $: mods = {
        placement,
        direction: $direction,
        visible: pagerData?.size > 1
    };

    function init() {
        pagerDataUnsubscribe?.();
        pagerDataUnsubscribe = undefined;

        const pagerId = componentContext.json.pager_id;
        pagerDataUnsubscribe = componentContext.listenPager(pagerId, onPagerDataUpdate);
    }

    onMount(() => {
        mounted = true;
    });

    onDestroy(() => {
        mounted = false;
        pagerDataUnsubscribe?.();
        pagerDataUnsubscribe = undefined;
    });
</script>

<Outer
    cls={genClassName('indicator', css, mods)}
    {componentContext}
    {layoutParams}
>
    <div
        class={css.indicator__scroller}
        bind:this={scroller}
    >
        <div
            class={css.indicator__items}
            role="tablist"
            bind:this={indicatorItemsWrapper}
            style:margin={placement === 'default' ? `0 ${pxToEm(Math.max(0, activeStyle.width - inactiveStyle.width) / 2)}` : ''}
            style:--divkit-indicator-inactive-width={pxToEm(inactiveStyle.width)}
            style:--divkit-indicator-inactive-height={pxToEm(inactiveStyle.height)}
            style:--divkit-indicator-inactive-border-radius={pxToEm(inactiveStyle.borderRadius)}
            style:--divkit-indicator-inactive-background={inactiveStyle.background || ''}
            style:--divkit-indicator-inactive-box-shadow={inactiveStyle.boxShadow || ''}
            style:--divkit-indicator-active-width={pxToEm(activeStyle.width)}
            style:--divkit-indicator-active-height={pxToEm(activeStyle.height)}
            style:--divkit-indicator-active-border-radius={pxToEm(activeStyle.borderRadius)}
            style:--divkit-indicator-active-background={activeStyle.background || ''}
            style:--divkit-indicator-active-box-shadow={activeStyle.boxShadow || ''}
            style:--divkit-indicator-active-scale={activeStyle.width / inactiveStyle.width}
            style:--divkit-indicator-default-margin={placement === 'default' ? `0 ${pxToEm((spaceBetweenCenters - inactiveStyle.width) / 2)}` : ''}
            style:--divkit-indicator-stretch-margin={placement === 'stretch' ? pxToEm(itemSpacing) : ''}
            style:--divkit-indicator-stretch-max-count={placement === 'stretch' ? maxVisibleItems : ''}
            style:--divkit-indicator-stretch-max-spacer={placement === 'stretch' ? pxToEm((maxVisibleItems - 1) * itemSpacing) : ''}
        >
            {#if pagerData}
                {#each Array(pagerData.size) as _, index}
                    {@const isActiveItem = index === pagerData.currentItem}
                    <div
                        class="{genClassName('indicator__item', css, { active: isActiveItem })} {rootCss.root__clickable}"
                        role="tab"
                        id="{pagerData.instId}-tab-{index}"
                        aria-controls="{pagerData.instId}-panel-{index}"
                        aria-selected={isActiveItem ? 'true' : 'false'}
                        tabindex={isActiveItem ? 0 : -1}
                        on:click={() => onIndicatorItemClick(index)}
                        on:keydown={onIndicatorItemKeydown}
                    ></div>
                {/each}
            {/if}
        </div>
    </div>
</Outer>
