<script lang="ts">
    import { slide } from 'svelte/transition';

    export let open = true;
    export let mix = '';

    let toggled = open;
    let openAttr = open;
    let details: HTMLDetailsElement;

    function onToggle(event: Event): void {
        if (event.target instanceof HTMLElement && event.target.closest('summary')) {
            toggled = !toggled;
            if (toggled) {
                openAttr = true;
            }
        }
    }

    function onAnimationEnd(): void {
        openAttr = false;
    }
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<details
    open={openAttr}
    bind:this={details}
    class="spoiler {mix}"
    class:spoiler_toggled={toggled}
>
    <summary class="spoiler__title" on:click|preventDefault={onToggle}>
        <div class="spoiler__arrow-wrapper">
            <slot name="title" />
        </div>
    </summary>

    {#if toggled}
        <div
            class="spoiler__content"
            transition:slide|local
            on:outroend={onAnimationEnd}
        >
            <div class="spoiler__content-inner">
                <slot />
            </div>
        </div>
    {/if}
</details>

<style>
    .spoiler {
        border-bottom: 1px solid var(--fill-transparent-2);
    }

    .spoiler__title {
        box-sizing: border-box;
        border: 2px solid transparent;
        border-radius: 0 12px 12px 0;
        transition: .15s ease-in-out;
        transition-property: background-color, border-color;
    }

    .spoiler__title::-webkit-details-marker {
        display: none;
    }

    .spoiler__title:focus-visible {
        outline: none;
        border-color: #fc0;
    }

    .spoiler__title:hover {
        background-color: #ddd;
    }

    .spoiler__title:active {
        background-color: #ccc;
    }

    .spoiler__arrow-wrapper {
        position: relative;
        padding: 5px 40px 5px 11px;
        font-weight: 500;
        user-select: none;
        cursor: pointer;
        border: 2px solid transparent;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .spoiler__arrow-wrapper {
        padding: 6px 50px 6px 16px;
    }

    .spoiler__content-inner {
        padding: 2px 11px 11px;
    }

    .spoiler__content-inner {
        padding: 0;
    }

    .spoiler__title::marker {
        content: none;
    }

    .spoiler {
        position: relative;
        overflow: hidden;
    }

    .spoiler__arrow-wrapper::after {
        position: absolute;
        top: 0;
        right: 12px;
        bottom: 0;
        width: 14px;
        height: 14px;
        margin: auto 0;
        background: no-repeat 50% 50% url(../assets/groupToggle.svg);
        filter: var(--icon-filter);
        transform: rotate(180deg);
        content: '';
        transition: transform .15s ease-in-out;
    }

    .spoiler_toggled > .spoiler__title > .spoiler__arrow-wrapper::after {
        transform: rotate(360deg);
    }
</style>
