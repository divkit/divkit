<script lang="ts" context="module">
    const THROTTLE_TIMEOUT = 10;
</script>

<script lang="ts">
    import { onDestroy, onMount } from 'svelte';

    import css from './ContainerSeparator.module.css';
    import outerCss from '../utilities/Outer.module.css';

    import type { ContainerOrientation } from '../../types/container';
    import type { SeparatorStyle } from '../../utils/container';
    import type { DrawableStyle } from '../../utils/correctDrawableStyles';
    import type { Direction } from '../../../typings/common';
    import type { ContentAlignmentHorizontalMapped } from '../../utils/correctContentAlignmentHorizontal';
    import type { ContentAlignmentVerticalMapped } from '../../utils/correctContentAlignmentVertical';
    import { simpleThrottle } from '../../utils/simpleThrottle';
    import { type Box, getMarginBox } from '../../utils/getMarginBox';

    export let orientation: ContainerOrientation;
    export let separator: SeparatorStyle | null;
    export let lineSeparator: SeparatorStyle | null;
    export let contentHAlign: ContentAlignmentHorizontalMapped;
    export let contentVAlign: ContentAlignmentVerticalMapped;
    export let direction: Direction;

    const throttledUpdated = simpleThrottle(updateSeparators, THROTTLE_TIMEOUT);

    interface SeparatorItem {
        left: number;
        top: number;
        width: number;
        height: number;
        style: DrawableStyle;
    }
    let separators: SeparatorItem[] = [];
    let node: HTMLElement;
    $: parentElement = node?.parentElement || null;
    let mounted = false;
    let mutationObserver: MutationObserver | null = null;
    let resizeObserver: ResizeObserver | null = null;

    $: if (mounted && parentElement || mutationObserver || resizeObserver) {
        if (mutationObserver) {
            mutationObserver.disconnect();
            mutationObserver = null;
        }
        if (resizeObserver) {
            resizeObserver.disconnect();
            resizeObserver = null;
        }
        if (mounted && parentElement) {
            if (typeof MutationObserver !== 'undefined') {
                mutationObserver = new MutationObserver(mutationObserverCallback);
                mutationObserver.observe(parentElement, {
                    childList: true,
                    attributes: true,
                    characterData: true,
                    subtree: true
                });
            }
            if (typeof ResizeObserver !== 'undefined') {
                resizeObserver = new ResizeObserver(throttledUpdated);
                resizeObserver.observe(parentElement);
            }
        }
    }

    $: if (mounted && parentElement) {
        throttledUpdated();
    }

    function mutationObserverCallback(records: MutationRecord[]): void {
        if (records.some(record => {
            const classList = (record.target as HTMLElement)?.classList;

            return !classList?.contains(css['container-separator__shape']) &&
                !classList?.contains(css['container-separator']);
        })) {
            throttledUpdated();
        }
    }

    // eslint-disable-next-line max-params
    function appendSeparator(
        separators: SeparatorItem[],
        separatorStyle: SeparatorStyle,
        box0: Box,
        box1: Box,
        containingBox: Box,
        crossAxis: boolean
    ) {
        const leftMargin = separatorStyle.margins.left;
        const rightMargin = separatorStyle.margins.right;
        const topMargin = separatorStyle.margins.top;
        const bottomMargin = separatorStyle.margins.bottom;

        if (crossAxis) {
            separators.push({
                top: box0.bottom + topMargin,
                left: containingBox.left + leftMargin,
                width: Math.max(0, containingBox.right - containingBox.left - leftMargin - rightMargin),
                height: box1.top - box0.bottom - topMargin - bottomMargin,
                style: separatorStyle.style
            });
        } else {
            separators.push({
                top: containingBox.top + topMargin,
                left: box0.right + leftMargin,
                width: box1.left - box0.right - leftMargin - rightMargin,
                height: Math.max(0, containingBox.bottom - containingBox.top - topMargin - bottomMargin),
                style: separatorStyle.style
            });
        }
    }

    // eslint-disable-next-line max-params
    function appendSeparators(
        separators: SeparatorItem[],
        separator: SeparatorStyle,
        boxes: Box[],
        crossAxis: boolean,
        align: ContentAlignmentHorizontalMapped | ContentAlignmentVerticalMapped,
        contentBox: {
            top: number;
            right: number;
            bottom: number;
            left: number;
        }
    ): void {
        const containingBox = {
            top: Math.min(...boxes.map(it => it.top)),
            right: Math.max(...boxes.map(it => it.right)),
            bottom: Math.max(...boxes.map(it => it.bottom)),
            left: Math.min(...boxes.map(it => it.left))
        };

        if (separator?.show_at_start) {
            let right: number;
            let bottom: number;
            if (align === 'space-around' || align === 'space-evenly') {
                right = contentBox.left - separator.style.width;
                bottom = contentBox.top - separator.style.height;
            } else {
                right = boxes[0].left - separator.style.width - separator.margins.left - separator.margins.right;
                bottom = boxes[0].top - separator.style.height - separator.margins.top - separator.margins.bottom;
            }
            appendSeparator(
                separators,
                separator,
                // only right and bottom is used
                {
                    top: 0,
                    right,
                    bottom,
                    left: 0
                },
                boxes[0],
                containingBox,
                crossAxis
            );
        }
        if (separator?.show_between) {
            for (let i = 0; i < boxes.length - 1; ++i) {
                appendSeparator(
                    separators,
                    separator,
                    boxes[i],
                    boxes[i + 1],
                    containingBox,
                    crossAxis
                );
            }
        }
        if (separator?.show_at_end) {
            const lastBox = boxes[boxes.length - 1];
            let top: number;
            let left: number;
            if (align === 'space-around' || align === 'space-evenly') {
                top = contentBox.bottom + separator.style.height;
                left = contentBox.right + separator.style.width;
            } else {
                top = lastBox.bottom + separator.style.height + separator.margins.top + separator.margins.bottom;
                left = lastBox.right + separator.style.width + separator.margins.left + separator.margins.right;
            }

            appendSeparator(
                separators,
                separator,
                lastBox,
                // only top and left is used
                {
                    top,
                    right: 0,
                    bottom: 0,
                    left
                },
                containingBox,
                crossAxis
            );
        }
    }

    function updateSeparators(): void {
        if (!parentElement) {
            return;
        }

        const parentBbox = parentElement.getBoundingClientRect();
        const computedStyle = window.getComputedStyle(parentElement);
        const contentBox = {
            top: parentBbox.top + parseFloat(computedStyle.paddingTop),
            right: parentBbox.right - parseFloat(computedStyle.paddingRight),
            bottom: parentBbox.bottom - parseFloat(computedStyle.paddingBottom),
            left: parentBbox.left + parseFloat(computedStyle.paddingLeft)
        };

        separators = [];

        let children = [...parentElement.children]
            .filter(it => it !== node && !it.classList.contains(outerCss.outer__border)) as HTMLElement[];
        let rows: HTMLElement[][] = [];

        while (children.length) {
            const row: HTMLElement[] = [];
            const firstChild = children.shift() as HTMLElement;
            row.push(firstChild);

            let bbox = firstChild.getBoundingClientRect();
            let left = bbox.left;
            let right = bbox.right;
            let bottom = bbox.bottom;

            while (children.length) {
                let first = children[0];
                let bbox = first.getBoundingClientRect();

                if (orientation === 'vertical') {
                    if (bbox.top < bottom) {
                        break;
                    }
                } else if (direction === 'ltr' ? (bbox.left < right) : (bbox.right > left)) {
                    break;
                }

                right = Math.max(right, bbox.right);
                left = Math.min(left, bbox.left);
                bottom = Math.max(bottom, bbox.bottom);
                row.push(first);
                children.shift();
            }

            rows.push(row);
        }

        const rowBoxes: Box[] = [];
        rows.forEach(row => {
            const boxes = row.map(it => getMarginBox(it));

            if (direction === 'rtl' && orientation === 'horizontal') {
                boxes.reverse();
            }

            if (separator) {
                appendSeparators(
                    separators,
                    separator as SeparatorStyle,
                    boxes,
                    orientation === 'vertical',
                    orientation === 'vertical' ? contentVAlign : contentHAlign,
                    contentBox
                );
            }

            const rowBox = {
                top: Math.min(...boxes.map(it => it.top)),
                right: Math.max(...boxes.map(it => it.right)),
                bottom: Math.max(...boxes.map(it => it.bottom)),
                left: Math.min(...boxes.map(it => it.left))
            };
            rowBoxes.push(rowBox);
        });

        if (direction === 'rtl' && orientation === 'vertical') {
            rowBoxes.reverse();
        }
        if (lineSeparator) {
            appendSeparators(
                separators,
                lineSeparator,
                rowBoxes,
                orientation === 'horizontal',
                orientation === 'vertical' ? contentHAlign : contentVAlign,
                contentBox
            );
        }

        separators.forEach(separator => {
            separator.top -= parentBbox.top;
            separator.left -= parentBbox.left;
        });
    }

    onMount(() => {
        mounted = true;
    });

    onDestroy(() => {
        mounted = false;
    });
</script>

<svelte:window on:resize={throttledUpdated} />

<div bind:this={node} class={css['container-separator']}>
    {#each separators as item}
        <div
            class={css['container-separator__item']}
            style:left="{item.left}px"
            style:top="{item.top}px"
            style:width="{item.width}px"
            style:height="{item.height}px"
        >
            <div
                class={css['container-separator__shape']}
                style:width="{item.style.width}px"
                style:height="{item.style.height}px"
                style:border-radius="{item.style.borderRadius}px"
                style:background="{item.style.background}"
                style:box-shadow="{item.style.boxShadow}"
            ></div>
        </div>
    {/each}
</div>
