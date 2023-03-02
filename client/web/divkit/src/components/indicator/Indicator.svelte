<script lang="ts">
    import { getContext, tick } from 'svelte';

    import rootCss from '../Root.module.css';
    import css from './Indicator.module.css';

    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivIndicatorData } from '../../types/indicator';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { PagerData } from '../../stores/pagers';
    import type { MaybeMissing } from '../../expressions/json';
    import type { DivIndicatorDefaultItemPlacement, DivIndicatorStretchItemPlacement } from '../../types/indicator';

    import Outer from '../utilities/Outer.svelte';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { genClassName } from '../../utils/genClassName';
    import { correctNonNegativeNumber } from '../../utils/correctNonNegativeNumber';
    import { pxToEm } from '../../utils/pxToEm';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { ARROW_LEFT, ARROW_RIGHT, END, HOME } from '../../utils/keyboard/codes';
    import { correctDrawableStyle, DrawableStyle } from '../../utils/correctDrawableStyles';
    import { correctColor } from '../../utils/correctColor';

    export let json: Partial<DivIndicatorData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const AVAIL_SHAPES = ['rounded_rectangle', 'circle'];

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    $: jsonShape = rootCtx.getDerivedFromVars(json.shape);
    $: jsonActiveItemColor = rootCtx.getDerivedFromVars(json.active_item_color);
    $: jsonInactiveItemColor = rootCtx.getDerivedFromVars(json.inactive_item_color);
    $: jsonActiveItemSize = rootCtx.getDerivedFromVars(json.active_item_size);
    $: jsonActiveShape = rootCtx.getDerivedFromVars(json.active_shape);
    $: jsonInactiveShape = rootCtx.getDerivedFromVars(json.inactive_shape);
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

    $: jsonSpaceBetweenCenters = rootCtx.getDerivedFromVars(json.space_between_centers);
    $: jsonItemsPlacement = rootCtx.getDerivedFromVars(json.items_placement);
    let placement: 'default' | 'stretch' = 'default';
    let spaceBetweenCenters = 15;
    let maxVisibleItems = 10;
    let itemSpacing = 5;
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

    let scroller: HTMLElement;
    let indicatorItemsWrapper: HTMLElement;
    let pagerData: PagerData;

    $: pagers = rootCtx.getStore<Map<string, PagerData>>('pagers');
    $: {
        onPagerDataUpdate($pagers);
    }

    async function onPagerDataUpdate(pagersMap: Map<string, PagerData>): Promise<void> {
        if (json.pager_id && pagersMap.has(json.pager_id)) {
            pagerData = pagersMap.get(json.pager_id) as PagerData;

            await tick();

            if (indicatorItemsWrapper) {
                // if not destroyed yet

                const currentItemOffsetLeft =
                    (indicatorItemsWrapper.children[pagerData.currentItem] as HTMLElement).offsetLeft;

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
        placement
    };
</script>

<Outer
    cls={genClassName('indicator', css, mods)}
    {json}
    {origJson}
    {templateContext}
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
