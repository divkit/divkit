<script lang="ts">
    import { createEventDispatcher } from 'svelte';

    export let id: string = '';
    export let value: boolean;
    export let label = '';
    export let disabled = false;

    const dispatch = createEventDispatcher();

    function onChange() {
        dispatch('change', value);
    }
</script>

<div
    class="checkbox"
    class:checkbox_disabled={disabled}
>
    {#if label}
        <label class="checkbox__label">
            <div class="checkbox__box">
                <input
                    class="checkbox__input"
                    type="checkbox"
                    autocomplete="off"
                    {id}
                    disabled={disabled}
                    bind:checked={value}
                    on:input={onChange}
                >
                <div class="checkbox__bg"></div>
            </div>

            {label}
        </label>
    {:else}
        <div class="checkbox__box">
            <input
                class="checkbox__input"
                type="checkbox"
                autocomplete="off"
                {id}
                disabled={disabled}
                bind:checked={value}
                on:change={onChange}
            >
            <div class="checkbox__bg"></div>
        </div>
    {/if}
</div>

<style>
    .checkbox {
        box-sizing: border-box;
        width: 100%;
        margin: 0;
        padding: 0 0;
        font: inherit;
        font-size: 14px;
        color: inherit;
        border-radius: 8px;
        appearance: none;
        transition: border-color .15s ease-in-out;
    }

    /* .checkbox:focus-within {
        outline: 1px solid var(--accent-purple);
    } */

    .checkbox__box {
        position: relative;
        width: 20px;
        height: 20px;
        margin: 0 8px 0 0;
    }

    .checkbox__input {
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

    .checkbox__bg {
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

    .checkbox__input:hover ~ .checkbox__bg {
        border-color: var(--fill-transparent-3);
        background-color: var(--fill-transparent-2);
    }

    .checkbox__input:active ~ .checkbox__bg {
        border-color: var(--fill-transparent-3);
        background-color: var(--fill-transparent-3);
    }

    .checkbox__bg::before {
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

    .checkbox__input:checked ~ .checkbox__bg {
        border-color: transparent;
        background: var(--accent-purple);
    }

    .checkbox__input:checked ~ .checkbox__bg::before {
        opacity: 1;
    }

    .checkbox__input:focus-visible ~ .checkbox__bg {
        outline: 1px solid var(--accent-purple);
    }

    .checkbox__label {
        display: flex;
        align-items: center;
    }
</style>
