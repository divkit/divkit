<script lang="ts">
    import { afterUpdate } from 'svelte';
    import { fly } from 'svelte/transition';

    export let owner: HTMLElement | null;

    let prevOwner: HTMLElement | null = null;
    let text = '';
    let counter = 0;
    let key: string;
    let prevOwners = new Set<HTMLElement>();
    let popover: HTMLElement | null = null;
    $: {
        text = owner?.getAttribute('data-custom-tooltip') || '';
    }

    $: {
        if (prevOwner) {
            prevOwner.removeEventListener('click', onClick);
            prevOwner.removeEventListener('pointerdown', onClick);
            prevOwners.add(prevOwner);
        }
        if (owner) {
            ++counter;
            prevOwners.delete(owner);
            owner.addEventListener('click', onClick);
            owner.addEventListener('pointerdown', onClick);
            key = `custom-tooltip-${counter}`;
            owner.style.anchorName = `--${key}`;
            prevOwner = owner;
        }
    }

    function onClick(): void {
        text = '';
    }

    function onOutroEnd(): void {
        for (const node of prevOwners) {
            node.style.anchorName = '';
        }
        prevOwners.clear();
    }

    afterUpdate(() => {
        if (popover) {
            popover.showPopover();
        }
    });
</script>

{#key key}
    {#if text}
        <div
            bind:this={popover}
            id={key}
            popover="auto"
            class="custom-tooltip"
            transition:fly|global={{ y: 10, duration: 200 }}
            on:outroend={onOutroEnd}
            style:position-anchor="--{key}"
        >
            {text}
        </div>
    {/if}
{/key}

<style>
    .custom-tooltip {
        position: fixed;
        z-index: 10;
        top: anchor(bottom, -100%);
        justify-self: anchor-center;
        margin: 12px 0 0;
        padding: 8px 12px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-primary);
        border-radius: 8px;
        border: none;
        background: var(--background-tertiary);
        filter: drop-shadow(0px 1px 8px rgba(0, 0, 0, 0.14));
        overflow: visible;
    }

    .custom-tooltip::before {
        position: absolute;
        z-index: -1;
        top: -3px;
        left: calc(50% - 8px);
        width: 16px;
        height: 16px;
        background: var(--background-tertiary);
        transform: rotate(45deg);
        content: '';
    }
</style>
