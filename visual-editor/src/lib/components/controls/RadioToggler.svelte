<script lang="ts">
    import { createEventDispatcher } from 'svelte';

    export let options: {
        text: string;
        value: string;
        tooltipAlign?: 'left' | 'center';
    }[];
    export let value: string | undefined;
    export let name: string;
    export let mix = '';
    export let theme: 'normal' | 'secondary' | 'tertiary';
    export let defaultValue = '';
    export let disabled = false;

    const dispatch = createEventDispatcher();

    $: internalValue = value || defaultValue;

    function onChange(): void {
        value = internalValue;
        dispatch('change');
    }
</script>

<div
    class="radio-toggler radio-toggler_theme_{theme} {mix}"
    class:radio-toggler_disabled={disabled}
>
    {#each options as option}
        <div class="radio-toggler__item">
            <input
                class="radio-toggler__radio"
                type="radio"
                {name}
                value={option.value}
                bind:group={internalValue}
                {disabled}
                on:change={onChange}
            >
            <div class="radio-toggler__item-bg"></div>

            <div class="radio-toggler__item-text">
                {option.text}
            </div>
        </div>
    {/each}
</div>

<style>
    .radio-toggler {
        box-sizing: border-box;
        display: flex;
        gap: 4px;
        padding: 4px;
        border-radius: 9px;
        background-color: var(--fill-transparent-2);
    }

    .radio-toggler_theme_tertiary {
        border: 1px solid var(--fill-transparent-3);
        background: var(--fill-transparent-minus-1);
    }

    .radio-toggler_theme_tertiary.radio-toggler_disabled {
        border: none;
        background: var(--fill-transparent-1);
    }

    .radio-toggler__item {
        position: relative;
        z-index: 1;
        flex: 1 0 0;
        height: 32px;
    }

    .radio-toggler__radio {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        margin: 0;
        padding: 0;
        border-radius: 6px;
        cursor: pointer;
        appearance: none;
    }

    .radio-toggler__radio:checked,
    .radio-toggler_disabled .radio-toggler__radio {
        cursor: default;
    }

    .radio-toggler__radio:focus-visible {
        outline: 2px solid var(--accent-purple);
        outline-offset: 2px;
    }

    .radio-toggler__item-bg {
        z-index: -1;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border-radius: 6px;
        transition: background-color .15s ease-in-out;
    }

    .radio-toggler:not(.radio-toggler_disabled)
    .radio-toggler__radio:not(:checked):hover ~
    .radio-toggler__item-bg {
        background-color: var(--fill-transparent-3);
    }

    .radio-toggler:not(.radio-toggler_disabled)
    .radio-toggler__radio:not(:checked):active ~
    .radio-toggler__item-bg {
        background-color: var(--fill-transparent-4);
    }

    .radio-toggler_theme_normal .radio-toggler__radio:checked ~ .radio-toggler__item-bg {
        background-color: var(--fill-accent-2);
    }

    .radio-toggler_theme_secondary .radio-toggler__radio:checked ~ .radio-toggler__item-bg {
        background-color: var(--background-primary);
    }

    .radio-toggler_theme_tertiary .radio-toggler__radio:checked ~ .radio-toggler__item-bg {
        background-color: var(--fill-accent-3);
    }

    .radio-toggler__item-text {
        box-sizing: border-box;
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100%;
        padding: 8px;
        text-align: center;
    }
</style>
