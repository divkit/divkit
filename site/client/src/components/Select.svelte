<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    import { fly } from 'svelte/transition';

    export let value: string;
    export let items: {
        name: string;
        value: string;
    }[];
    export let popupOrientation: 'left' | 'right' = 'left'
    export let toggledClass = '';

    const dispatch = createEventDispatcher();

    let popupShown = false;
    let timer: number;

    $: if (value) {
        dispatch('change');
    }

    function onMouseEnterOrClick(): void {
        popupShown = true;
        clearTimeout(timer);
    }

    function onMouseLeave(): void {
        clearTimeout(timer);
        timer = window.setTimeout(() => {
            popupShown = false;
        }, 200);
    }
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<div
    class={'select' + (popupShown && toggledClass ? ' ' + toggledClass : '')}
    on:click={onMouseEnterOrClick}
    on:mouseenter={onMouseEnterOrClick}
    on:mouseleave={onMouseLeave}
>
    <slot />

    {#if popupShown}
        <div
            class="select__popup"
            class:select__popup_orientation_right={popupOrientation === 'right'}
            aria-hidden="true"
            transition:fly={{y: 20, duration: 150}}
        >
            <ul class="select__list">
                {#each items as item}
                    <li class="select__item">
                        <button
                            class="select__item-button"
                            class:select__item-button_selected={value === item.value}
                            on:click={() => {
                                value = item.value;
                            }}
                        >
                            {item.name}
                        </button>
                    </li>
                {/each}
            </ul>
        </div>
    {/if}

    <select class="select__control">
        {#each items as item}
            <option value={item.value}>{item.name}</option>
        {/each}
    </select>
</div>

<style>
    .select {
        position: relative;
        display: inline-block;
    }

    .select__control {
        position: absolute;
        left: 0;
        width: 100%;
        height: 100%;
        top: 0;
        overflow: hidden;
        opacity: 0;
        clip: rect(1px 1px 1px 1px);
    }

    .select__popup {
        position: absolute;
        z-index: 10;
        top: 100%;
        left: 0;
        padding: 10px 10px;
        border-radius: 22px;
        background: var(--bg-primary);
        box-shadow: 0 0 20px 2px rgba(0, 0, 0, .15);
    }

    .select__popup_orientation_right {
        left: auto;
        right: 0;
    }

    .select__list {
        display: flex;
        flex-direction: column;
        gap: 4px;
        margin: 0;
        padding: 0;
        list-style: none;
    }

    .select__item-button {
        box-sizing: border-box;
        width: 100%;
        margin: 0;
        padding: 4px 32px 4px 16px;
        font: inherit;
        background: none;
        border: 1px solid transparent;
        border-radius: 1024px;
        appearance: none;
        color: inherit;
        text-align: left;
        cursor: pointer;

        transition: .15s ease-in-out;
        transition-property: border-color, color;
    }

    .select__item-button_selected {
        background: var(--accent0);
        color: var(--accent0-text);
    }

    .select__item-button:not(.select__item-button_selected):hover {
        border-color: var(--accent0);
        color: var(--accent0);
    }
</style>
