<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let list: {
        text: string;
        name: string;
        value: boolean;
        icon?: string;
        iconLight?: string;
        iconDark?: string;
    }[];
    export let disabled = false;

    const { state } = getContext<AppContext>(APP_CTX);
    const { themeStore } = state;

    const dispatch = createEventDispatcher();

    function onChange(name: string, event: Event): void {
        dispatch('change', {
            name,
            value: (event.target as HTMLInputElement).checked
        });
    }
</script>

<div
    class="checkbox-group"
    class:checkbox-group_disabled={disabled}
>
    {#each list as item}
        <div class="checkbox-group__item">
            <input
                class="checkbox-group__input"
                type="checkbox"
                checked={item.value}
                data-custom-tooltip={item.text}
                {disabled}
                on:change={event => onChange(item.name, event)}
            >
            <div class="checkbox-group__item-bg"></div>
            <div
                class="checkbox-group__item-icon"
                class:checkbox-group__item-icon_both-themes={Boolean(item.icon)}
                style:background-image="url({item.icon || ($themeStore === 'light' ? item.iconLight : item.iconDark)})"
            ></div>
        </div>
    {/each}
</div>

<style>
    .checkbox-group {
        display: flex;
        gap: 4px;
        width: fit-content;
        padding: 4px;
        border-radius: 9px;
        border: 1px solid var(--fill-transparent-3);
        background: var(--fill-transparent-minus-1);
    }

    .checkbox-group_disabled {
        border: none;
        background: var(--fill-transparent-1);
    }

    .checkbox-group__item {
        position: relative;
        z-index: 1;
        width: 32px;
        height: 32px;
    }

    .checkbox-group__input {
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

    .checkbox-group_disabled .checkbox-group__input {
        cursor: default;
    }

    .checkbox-group__item-bg {
        z-index: -1;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border-radius: 6px;
        transition: background-color .15s ease-in-out;
    }

    .checkbox-group:not(.checkbox-group_disabled)
    .checkbox-group__input:not(:checked):hover ~
    .checkbox-group__item-bg {
        background-color: var(--fill-transparent-3);
    }

    .checkbox-group__input:checked ~ .checkbox-group__item-bg {
        background-color: var(--fill-accent-2);
    }

    .checkbox-group:not(.checkbox-group_disabled) .checkbox-group__input:checked:hover ~ .checkbox-group__item-bg {
        background-color: var(--fill-accent-3);
    }

    .checkbox-group__item-icon {
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

    .checkbox-group__item-icon_both-themes {
        filter: var(--icon-filter);
    }
</style>
