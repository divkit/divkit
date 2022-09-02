<script lang="ts">
    import { getContext, tick } from 'svelte';

    import rootCss from '../Root.module.css';
    import css from './Indicator.module.css';

    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivIndicatorData } from '../../types/indicator';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { PagerData } from '../../stores/pagers';

    import Outer from '../utilities/Outer.svelte';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { genClassName } from '../../utils/genClassName';
    import { correctNonNegativeNumber } from '../../utils/correctNonNegativeNumber';
    import { pxToEm } from '../../utils/pxToEm';
    import { correctColor } from '../../utils/correctColor';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { ARROW_LEFT, ARROW_RIGHT, END, HOME } from '../../utils/keyboard/codes';

    export let json: Partial<DivIndicatorData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    $: jsonShape = rootCtx.getDerivedFromVars(json.shape);
    let shapeWidth = 10;
    let shapeHeight = 10;
    let shapeCornerRadius = 5;
    $: {
        // TODO: think about what if $jsonShape was deleted
        if ($jsonShape) {
            if ($jsonShape.item_width) {
                shapeWidth = correctNonNegativeNumber($jsonShape.item_width.value, shapeWidth);
            }

            if ($jsonShape.item_height) {
                shapeHeight = correctNonNegativeNumber($jsonShape.item_height.value, shapeHeight);
            }

            if ($jsonShape.corner_radius) {
                shapeCornerRadius = correctNonNegativeNumber($jsonShape.corner_radius.value, shapeCornerRadius);
            }
        }
    }

    $: jsonActiveItemColor = rootCtx.getDerivedFromVars(json.active_item_color);
    let activeItemColor = '#ffdc60';
    $: {
        activeItemColor = correctColor($jsonActiveItemColor, 1, activeItemColor);
    }

    $: jsonInactiveItemColor = rootCtx.getDerivedFromVars(json.inactive_item_color);
    let inactiveItemColor = correctColor('#33919cb5');
    $: {
        inactiveItemColor = correctColor($jsonInactiveItemColor, 1, inactiveItemColor);
    }

    $: jsonSpaceBetweenCenters = rootCtx.getDerivedFromVars(json.space_between_centers);
    let spaceBetweenCenters = 15;
    $: {
        if ($jsonSpaceBetweenCenters) {
            spaceBetweenCenters = correctNonNegativeNumber($jsonSpaceBetweenCenters.value, spaceBetweenCenters);
        }
    }

    $: jsonActiveItemSize = rootCtx.getDerivedFromVars(json.active_item_size);
    let activeItemSize = 1.3;
    $: {
        activeItemSize = correctPositiveNumber($jsonActiveItemSize, activeItemSize);
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

            const currentItemOffsetLeft =
                (indicatorItemsWrapper.children[pagerData.currentItem] as HTMLElement).offsetLeft;

            scroller.scroll({
                left: currentItemOffsetLeft - scroller.clientWidth / 2,
                behavior: 'smooth'
            });
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
</script>

<Outer
    cls={genClassName('indicator', css, {})}
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
            style:margin="0 {pxToEm((activeItemSize - 1) * shapeWidth / 2)}"
            style:--divkit-indicator-width={pxToEm(shapeWidth)}
            style:--divkit-indicator-height={pxToEm(shapeHeight)}
            style:--divkit-indicator-border-radius={pxToEm(shapeCornerRadius)}
            style:--divkit-indicator-active-color={activeItemColor}
            style:--divkit-indicator-inactive-color={inactiveItemColor}
            style:--divkit-indicator-active-scale={activeItemSize}
            style:--divkit-indicator-margin="0 {pxToEm((spaceBetweenCenters - shapeWidth) / 2)}"
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
