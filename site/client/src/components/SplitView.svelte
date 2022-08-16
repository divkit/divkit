<script lang="ts">
    import type { SvelteComponent } from 'svelte';

    export let components: {
        component: typeof SvelteComponent;
        weight: number;
        minWidth?: number;
    }[];

    const MIN_WIDTH = 300;

    let parts: HTMLElement[] = [];

    function onPointerdown(event: PointerEvent, movedIndex: number): void {
        const startX = event.pageX;
        const startWidths = parts.map(it => it.offsetWidth);

        const currentMinWidth = components[movedIndex].minWidth ?? MIN_WIDTH;
        const nextMinWidth = components[movedIndex + 1].minWidth ?? MIN_WIDTH;

        const pointermove = (event: PointerEvent) => {
            event.preventDefault();

            let change = event.pageX - startX;
            if (startWidths[movedIndex] + change < currentMinWidth) {
                change = currentMinWidth - startWidths[movedIndex];
            }
            if (startWidths[movedIndex + 1] - change < nextMinWidth) {
                change = startWidths[movedIndex + 1] - nextMinWidth;
            }
            parts.forEach((part, index) => {
                let newWidth;
                if (index === movedIndex) {
                    newWidth = startWidths[index] + change;
                } else if (index === movedIndex + 1) {
                    newWidth = startWidths[index] - change;
                }
                if (newWidth) {
                    part.style.flexBasis = `${newWidth}px`;
                } else {
                    part.style.flexBasis = `${startWidths[index]}px`;
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
</script>

<div class="split-view">
    {#each components as item, index (item.component)}
        {#if index > 0}
            <div class="split-view__splitter" on:pointerdown|preventDefault={event => onPointerdown(event, index - 1)}></div>
        {/if}
        <div
            class="split-view__part"
            style="--grow: {item.weight};min-width:{item.minWidth || MIN_WIDTH}px"
            bind:this={parts[index]}
        >
            <svelte:component this={item.component} />
        </div>
    {/each}
</div>

<style>
    .split-view {
        display: flex;
        flex-direction: row;
        flex: 1 1 auto;
        height: 100%;
        min-width: 0;
    }

    .split-view__part {
        display: flex;
        flex-direction: column;
        flex: var(--grow, 1) 1 0;
        overflow: auto;
    }

    .split-view__splitter {
        width: 4px;
        flex: 0 0 auto;
        border: solid var(--bg-primary);
        border-width: 0 2px;
        background: var(--separator);
        cursor: col-resize;
        transition: background-color .15s ease-in-out;
    }

    .split-view__splitter:hover {
        background: #6cd7ff;
    }
</style>
