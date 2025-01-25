<script lang="ts">
    import { fly } from 'svelte/transition';

    export let owner: HTMLElement | null;

    let prevOwner: HTMLElement | null = null;
    let text = '';
    let align: 'left' | 'center' = 'center';
    let bbox: DOMRect | undefined;
    let appBbox: DOMRect | undefined;
    let wrapper: HTMLElement;
    $: {
        let defaultAlign = 'center';
        bbox = owner?.getBoundingClientRect();
        if (bbox && bbox.left < 50) {
            defaultAlign = 'left';
        }
        text = owner?.getAttribute('data-custom-tooltip') || '';
        align = (owner?.getAttribute('data-custom-tooltop-align') || defaultAlign) as 'left' | 'center';
        appBbox = wrapper?.parentElement?.getBoundingClientRect();
    }
    let scrollX = 0;
    let scrollY = 0;

    $: {
        if (prevOwner) {
            prevOwner.removeEventListener('click', onClick);
            prevOwner.removeEventListener('pointerdown', onClick);
        }
        if (owner) {
            owner.addEventListener('click', onClick);
            owner.addEventListener('pointerdown', onClick);
            prevOwner = owner;
        }
    }

    function onClick(): void {
        text = '';
    }
</script>

<svelte:window bind:scrollX={scrollX} bind:scrollY={scrollY} />

<div bind:this={wrapper}>
    {#key owner}
        {#if text && bbox}
            <div
                class="custom-tooltip custom-tooltip_align_{align}"
                style:top="{bbox.bottom + scrollY - (appBbox?.top || 0)}px"
                style:left="{bbox.left + scrollX + (align === 'center' ? bbox.width / 2 : 0) - (appBbox?.left || 0)}px"
                transition:fly|global={{ y: 10, duration: 200 }}
            >
                {text}
            </div>
        {/if}
    {/key}
</div>

<style>
    .custom-tooltip {
        position: absolute;
        z-index: 10;
        margin-top: 12px;
        padding: 8px 12px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-primary);
        border-radius: 8px;
        background: var(--background-tertiary);
        filter: drop-shadow(0px 1px 8px rgba(0, 0, 0, 0.14));
        transform: translateX(-50%);
    }

    .custom-tooltip_align_left {
        transform: none;
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

    .custom-tooltip_align_left::before {
        left: 8px;
    }
</style>
