<script lang="ts">
    import type { Component } from 'svelte';

    export let components: {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        component: Component<any>;
        weight: number;
        minSize?: number;
        key?: string;
        props?: Record<string, unknown>;
    }[];

    export let orientation: 'horizontal' | 'vertical' = 'horizontal';

    const MIN_SIZE = 300;

    let parts: HTMLElement[] = [];

    function onPointerdown(event: PointerEvent, movedIndex: number): void {
        if (event.button !== 0) {
            return;
        }

        const eventProp = orientation === 'horizontal' ? 'pageX' : 'pageY';
        const sizeProp = orientation === 'horizontal' ? 'offsetWidth' : 'offsetHeight';
        const startX = event[eventProp];
        const startSizes = parts.map(it => it[sizeProp]);

        const currentMinSize = components[movedIndex].minSize ?? MIN_SIZE;
        const nextMinSize = components[movedIndex + 1].minSize ?? MIN_SIZE;

        const pointermove = (event: PointerEvent) => {
            event.preventDefault();

            let change = event[eventProp] - startX;
            if (startSizes[movedIndex] + change < currentMinSize) {
                change = currentMinSize - startSizes[movedIndex];
            }
            if (startSizes[movedIndex + 1] - change < nextMinSize) {
                change = startSizes[movedIndex + 1] - nextMinSize;
            }
            parts.forEach((part, index) => {
                let newSize;
                if (index === movedIndex) {
                    newSize = startSizes[index] + change;
                } else if (index === movedIndex + 1) {
                    newSize = startSizes[index] - change;
                }
                if (newSize) {
                    part.style.flexBasis = `${newSize}px`;
                } else {
                    part.style.flexBasis = `${startSizes[index]}px`;
                }
            });
        };

        const pointerup = () => {
            window.removeEventListener('pointermove', pointermove);
            window.removeEventListener('pointerup', pointerup);
            window.removeEventListener('pointercancel', pointerup);
        };

        window.addEventListener('pointermove', pointermove);
        window.addEventListener('pointerup', pointerup);
        window.addEventListener('pointercancel', pointerup);
    }

    function onDoubleClick(): void {
        parts.forEach(part => {
            part.style.flexBasis = '';
        });
    }
</script>

<div class="split-view" class:split-view_vertical={orientation === 'vertical'}>
    {#each components as item, index (item.key || index)}
        {#if index > 0}
            <!-- svelte-ignore a11y_no_static_element_interactions -->
            <div
                class="split-view__splitter"
                on:pointerdown|preventDefault={event => onPointerdown(event, index - 1)}
                on:dblclick|preventDefault={onDoubleClick}
            ></div>
        {/if}
        <div
            class="split-view__part"
            style:--grow={item.weight}
            style:min-width={orientation === 'horizontal' ? `${item.minSize || MIN_SIZE}px` : undefined}
            style:min-height={orientation === 'vertical' ? `${item.minSize || MIN_SIZE}px` : undefined}
            bind:this={parts[index]}
        >
            <svelte:component this={item.component} {...item.props} />
        </div>
    {/each}
</div>

<style>
    .split-view {
        display: flex;
        flex-direction: row;
        flex: 1 1 auto;
        width: 100%;
        height: 100%;
        min-height: 0;
        min-width: 0;
        background: var(--background-primary);
    }

    .split-view_vertical {
        flex-direction: column;
    }

    .split-view__part {
        display: flex;
        flex-direction: column;
        flex: var(--grow, 1) 1 0;
        overflow: auto;
    }

    .split-view__part:focus-visible {
        outline: 3px solid var(--accent-purple);
    }

    .split-view__splitter {
        position: relative;
        z-index: 2;
        box-sizing: border-box;
        width: 1px;
        flex: 0 0 auto;
        background: var(--applied-splitter);
    }

    .split-view__splitter::before {
        position: absolute;
        top: 0;
        right: -3px;
        bottom: 0;
        left: -3px;
        content: '';
        transition: .15s ease-in-out;
        cursor: col-resize;
    }

    .split-view__splitter:hover::before {
        background: var(--accent-purple);
    }

    .split-view__splitter:active::before {
        background: var(--accent-purple-sub);
    }

    .split-view_vertical .split-view__splitter {
        width: auto;
        height: 1px;
    }

    .split-view_vertical .split-view__splitter::before {
        top: -3px;
        right: 0;
        bottom: -3px;
        left: 0;
        cursor: row-resize;
    }
</style>
