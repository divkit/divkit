<script lang="ts">
    import { slide } from 'svelte/transition';

    export let open = true;
    export let mix = '';
    export let theme: 'rounded' | 'straight' | 'straight-small' | 'props' = 'rounded';

    let toggled = open;
    let openAttr = open;
    let details: HTMLDetailsElement;
    let transitionRunning = false;

    function onToggle(event: Event): void {
        if (event.target instanceof HTMLElement && event.target.closest('summary')) {
            toggled = !toggled;
            if (toggled) {
                openAttr = true;
            }
        }
    }

    function afterHide(): void {
        openAttr = false;
    }

    function onTransitionStart(): void {
        transitionRunning = true;
    }

    function onTransitionEnd(): void {
        transitionRunning = false;
    }
</script>

<details
    open={openAttr}
    bind:this={details}
    class="spoiler2 spoiler2_theme_{theme} {mix}"
    class:spoiler2_toggled={toggled}
    class:spoiler2_animated={transitionRunning}
>
    <summary class="spoiler2__title" on:click|preventDefault={onToggle}>
        <div class="spoiler2__arrow-wrapper">
            <slot name="title" />
        </div>
    </summary>

    {#if toggled}
        <div
            class="spoiler2__content"
            transition:slide|local
            on:introstart={onTransitionStart}
            on:outrostart={onTransitionStart}
            on:introend={onTransitionEnd}
            on:outroend={onTransitionEnd}
            on:outroend={afterHide}
        >
            <div class="spoiler2__content-inner">
                <slot />
            </div>
        </div>
    {/if}
</details>

<style>
    .spoiler2_theme_rounded {
        border-radius: 8px;
        border: 1px solid var(--fill-transparent-3);
    }

    .spoiler2_theme_props {
        border-bottom: 1px solid var(--fill-transparent-2);
    }

    .spoiler2__title {
        box-sizing: border-box;
        border: 2px solid transparent;
        transition: .15s ease-in-out;
        transition-property: background-color, border-color;
    }

    .spoiler2__title::-webkit-details-marker {
        display: none;
    }

    .spoiler2__title:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }

    .spoiler2__title:hover {
        background-color: var(--fill-transparent-2);
    }

    .spoiler2__title:active {
        background-color: var(--fill-transparent-4);
    }

    .spoiler2_theme_props .spoiler2__title {
        margin: 8px 0;
    }

    .spoiler2__arrow-wrapper {
        position: relative;
        padding: 5px 40px 5px 11px;
        font-weight: 500;
        user-select: none;
        cursor: pointer;
        border: 2px solid transparent;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .spoiler2_theme_straight-small .spoiler2__arrow-wrapper {
        padding-top: 2px;
        padding-bottom: 2px;
    }

    .spoiler2_theme_props .spoiler2__arrow-wrapper {
        padding: 6px 50px 6px 16px;
    }

    .spoiler2__content-inner {
        padding: 2px 11px 11px;
    }

    .spoiler2_theme_props .spoiler2__content-inner {
        padding: 0 0 32px;
    }

    .spoiler2__title::marker {
        content: none;
    }

    .spoiler2 {
        position: relative;
    }

    .spoiler2_animated {
        overflow: hidden;
    }

    .spoiler2__arrow-wrapper::after {
        position: absolute;
        top: 0;
        right: 12px;
        bottom: 0;
        width: 14px;
        height: 14px;
        margin: auto 0;
        background: no-repeat 50% 50% url(../../../assets/groupToggle.svg);
        filter: var(--icon-filter);
        transform: rotate(180deg);
        content: '';
        transition: transform .15s ease-in-out;
    }

    .spoiler2_theme_props .spoiler2__arrow-wrapper::after {
        right: 28px;
    }

    .spoiler2_toggled > .spoiler2__title > .spoiler2__arrow-wrapper::after {
        transform: rotate(360deg);
    }
</style>
