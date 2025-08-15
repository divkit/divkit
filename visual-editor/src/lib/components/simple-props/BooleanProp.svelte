<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { BooleanProperty } from '../../data/componentProps';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let id: string = '';
    export let value: string;
    export let item: BooleanProperty;

    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    let val = false;

    $: if (value !== undefined) {
        val = Boolean(value);
    }

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);

    const dispatch = createEventDispatcher();

    function onChange() {
        dispatch('change', {
            value: val,
            item
        });
    }
</script>

<div
    class="boolean"
    class:boolean_sibling={item.theme === 'sibling'}
    class:boolean_disabled={$readOnly}
>
    {#if item.label}
        <label class="boolean__label">
            <div class="boolean__box">
                <input
                    class="boolean__input"
                    type="checkbox"
                    autocomplete="off"
                    {id}
                    disabled={$readOnly}
                    bind:checked={val}
                    on:change={onChange}
                >
                <div class="boolean__bg"></div>
            </div>

            {$l10n(item.label)}
        </label>
    {:else}
        <div class="boolean__box">
            <input
                class="boolean__input"
                type="checkbox"
                autocomplete="off"
                {id}
                disabled={$readOnly}
                bind:checked={val}
                on:change={onChange}
            >
            <div class="boolean__bg"></div>
        </div>
    {/if}
</div>

<style>
    .boolean {
        box-sizing: border-box;
        width: 100%;
        margin: 0;
        padding: 0;
        font: inherit;
        font-size: 14px;
        color: inherit;
        border-radius: 8px;
        appearance: none;
        transition: border-color .15s ease-in-out;
    }

    .boolean_sibling {
        padding: 10px 6px;
    }

    .boolean__box {
        position: relative;
        width: 20px;
        height: 20px;
        margin: 0 8px 0 0;
    }

    .boolean__input {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        margin: 0;
        padding: 0;
        opacity: 0;
        outline: none;
    }

    .boolean__bg {
        position: absolute;
        top: 0;
        left: 0;
        box-sizing: border-box;
        width: 100%;
        height: 100%;
        border: 1px solid var(--fill-transparent-3);
        background: var(--background-primary);
        border-radius: 6px;
        pointer-events: none;
        transition: .15s ease-in-out;
        transition-property: border-color, background-color;
    }

    .boolean__input:hover ~ .boolean__bg {
        border-color: var(--fill-transparent-3);
        background-color: var(--fill-transparent-2);
    }

    .boolean__input:active ~ .boolean__bg {
        border-color: var(--fill-transparent-3);
        background-color: var(--fill-transparent-3);
    }

    .boolean__bg::before {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50% url(../../../assets/check.svg);
        opacity: 0;
        transition: .15s ease-in-out;
        content: '';
    }

    .boolean__input:checked ~ .boolean__bg {
        border-color: transparent;
        background: var(--accent-purple);
    }

    .boolean__input:checked ~ .boolean__bg::before {
        opacity: 1;
    }

    .boolean__input:focus-visible ~ .boolean__bg {
        outline: 1px solid var(--accent-purple);
    }

    .boolean__label {
        display: flex;
        align-items: center;
    }
</style>
