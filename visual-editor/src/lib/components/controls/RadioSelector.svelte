<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import { encodeBackground } from '../../utils/encodeBackground';

    export let options: {
        text: string;
        value: string;
        icon?: string;
        iconLight?: string;
        iconDark?: string;
        tooltipAlign?: 'left' | 'center';
    }[];
    export let value: string | undefined;
    export let name: string;
    export let mix = '';
    export let theme: 'normal' | 'secondary' | 'tertiary';
    export let defaultValue = '';
    export let disabled = false;
    export let customTooltips = true;
    export let iconSize: 'contain' | '20' = 'contain';

    const { state } = getContext<AppContext>(APP_CTX);
    const { themeStore } = state;

    const dispatch = createEventDispatcher();

    $: internalValue = value || defaultValue;

    function onChange(): void {
        value = internalValue;
        dispatch('change');
    }
</script>

<div
    class="radio-selector radio-selector_theme_{theme} radio-selector_icon-size_{iconSize} {mix}"
    class:radio-selector_disabled={disabled}
>
    {#each options as option}
        <div class="radio-selector__item">
            <input
                class="radio-selector__radio"
                type="radio"
                {name}
                value={option.value}
                bind:group={internalValue}
                aria-label={option.text}
                title={customTooltips ? undefined : option.text}
                data-custom-tooltip={customTooltips ? option.text : undefined}
                data-custom-tooltop-align={option.tooltipAlign || 'center'}
                {disabled}
                on:change={onChange}
            >
            <div class="radio-selector__item-bg"></div>
            <div
                class="radio-selector__item-icon"
                class:radio-selector__item-icon_both-themes={Boolean(option.icon)}
                style:background-image="url({encodeBackground(option.icon || ($themeStore === 'light' ? option.iconLight : option.iconDark) || '')})"
            ></div>
        </div>
    {/each}
</div>

<style>
    .radio-selector {
        display: flex;
        gap: 4px;
        width: fit-content;
        padding: 4px;
        border-radius: 9px;
        background-color: var(--fill-transparent-2);
    }

    .radio-selector_theme_tertiary {
        border: 1px solid var(--fill-transparent-3);
        background: var(--fill-transparent-minus-1);
    }

    .radio-selector_theme_tertiary.radio-selector_disabled {
        border: none;
        background: var(--fill-transparent-1);
    }

    .radio-selector__item {
        position: relative;
        z-index: 1;
        width: 32px;
        height: 32px;
    }

    .radio-selector__radio {
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

    .radio-selector__radio:checked,
    .radio-selector_disabled .radio-selector__radio {
        cursor: default;
    }

    .radio-selector__radio:focus-visible {
        outline: 2px solid var(--accent-purple);
        outline-offset: 2px;
    }

    .radio-selector__item-bg {
        z-index: -1;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border-radius: 6px;
        transition: background-color .15s ease-in-out;
    }

    .radio-selector:not(.radio-selector_disabled)
    .radio-selector__radio:not(:checked):hover ~
    .radio-selector__item-bg {
        background-color: var(--fill-transparent-3);
    }

    .radio-selector:not(.radio-selector_disabled)
    .radio-selector__radio:not(:checked):active ~
    .radio-selector__item-bg {
        background-color: var(--fill-transparent-4);
    }

    .radio-selector_theme_normal .radio-selector__radio:checked ~ .radio-selector__item-bg {
        background-color: var(--accent-purple);
    }

    .radio-selector_theme_secondary .radio-selector__radio:checked ~ .radio-selector__item-bg {
        background-color: var(--background-primary);
    }

    .radio-selector_theme_tertiary .radio-selector__radio:checked ~ .radio-selector__item-bg {
        background-color: var(--fill-accent-3);
    }

    .radio-selector__item-icon {
        z-index: -1;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50%;
        background-size: contain;
        transition: filter .15s ease-in-out;
    }

    .radio-selector_icon-size_20 .radio-selector__item-icon {
        background-size: 20px;
    }

    .radio-selector__item-icon_both-themes {
        filter: var(--icon-filter);
    }

    .radio-selector_theme_normal .radio-selector__radio:checked ~ .radio-selector__item-icon_both-themes,
    .radio-selector_theme_secondary .radio-selector__radio:checked ~ .radio-selector__item-icon_both-themes {
        filter: none;
    }
</style>
