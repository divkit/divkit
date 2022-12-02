<script lang="ts">
    import { onDestroy, onMount } from 'svelte';

    import css from './ContainerSeparator.module.css';
    import outerCss from '../utilities/Outer.module.css';

    import type { ContainerOrientation } from '../../types/container';
    import type { SeparatorStyle } from '../../utils/container';
    import type { DrawableStyle } from '../../utils/correctDrawableStyles';
    import { simpleThrottle } from '../../utils/simpleThrottle';
    import { Box, getMarginBox } from '../../utils/getMarginBox';

    export let orientation: ContainerOrientation;
    export let separator: SeparatorStyle | null;
    export let lineSeparator: SeparatorStyle | null;

    const THROTTLE_TIMEOUT = 10;
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
        separatorStyle: DrawableStyle,
        box0: Box,
        box1: Box,
        containingBox: Box,
        crossAxis: boolean
    ) {
        if (crossAxis) {
            separators.push({
                top: box0.bottom,
                left: containingBox.left,
                width: containingBox.right - containingBox.left,
                height: box1.top - box0.bottom,
                style: separatorStyle
            });
        } else {
            separators.push({
                top: containingBox.top,
                left: box0.right,
                width: box1.left - box0.right,
                height: containingBox.bottom - containingBox.top,
                style: separatorStyle
            });
        }
    }

    function appendSeparators(
        separators: SeparatorItem[],
        separator: SeparatorStyle,
        boxes: Box[],
        crossAxis: boolean
    ): void {
        const containingBox = {
            top: Math.min(...boxes.map(it => it.top)),
            right: Math.max(...boxes.map(it => it.right)),
            bottom: Math.max(...boxes.map(it => it.bottom)),
            left: Math.min(...boxes.map(it => it.left))
        };

        if (separator?.show_at_start) {
            appendSeparator(
                separators,
                separator.style,
                // only right and bottom is used
                {
                    top: 0,
                    right: boxes[0].left - separator.style.width,
                    bottom: boxes[0].top - separator.style.height,
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
                    separator.style,
                    boxes[i],
                    boxes[i + 1],
                    containingBox,
                    crossAxis
                );
            }
        }
        if (separator?.show_at_end) {
            const lastBox = boxes[boxes.length - 1];

            appendSeparator(
                separators,
                separator.style,
                lastBox,
                // only top and left is used
                {
                    top: lastBox.bottom + separator.style.height,
                    right: 0,
                    bottom: 0,
                    left: lastBox.right + separator.style.width
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

        separators = [];

        let children = [...parentElement.children]
            .filter(it => it !== node && !it.classList.contains(outerCss.outer__border)) as HTMLElement[];
        let rows: HTMLElement[][] = [];

        while (children.length) {
            const row: HTMLElement[] = [];
            const firstChild = children.shift() as HTMLElement;
            row.push(firstChild);

            let bbox = firstChild.getBoundingClientRect();
            let right = bbox.right;
            let bottom = bbox.bottom;

            while (children.length) {
                let first = children[0];
                let bbox = first.getBoundingClientRect();

                if (orientation === 'vertical') {
                    if (bbox.top < bottom) {
                        break;
                    }
                } else if (bbox.left < right) {
                    break;
                }

                right = Math.max(right, bbox.right);
                bottom = Math.max(bottom, bbox.bottom);
                row.push(first);
                children.shift();
            }

            rows.push(row);
        }

        const rowBoxes: Box[] = [];
        rows.forEach(row => {
            const boxes = row.map(it => getMarginBox(it));

            if (separator) {
                appendSeparators(separators, separator as SeparatorStyle, boxes, orientation === 'vertical');
            }

            const rowBox = {
                top: Math.min(...boxes.map(it => it.top)),
                right: Math.max(...boxes.map(it => it.right)),
                bottom: Math.max(...boxes.map(it => it.bottom)),
                left: Math.min(...boxes.map(it => it.left))
            };
            rowBoxes.push(rowBox);
        });
        if (lineSeparator) {
            appendSeparators(separators, lineSeparator, rowBoxes, orientation === 'horizontal');
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
