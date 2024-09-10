<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { IntegerProperty, NumberProperty, PercentProperty, RotationProperty } from '../../data/componentProps';
    import { parseConstraint } from '../../utils/parseConstraint';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let id: string = '';
    export let value: string | number;
    export let item: NumberProperty | IntegerProperty | RotationProperty | PercentProperty;
    export let subtype: 'integer' | 'number' | 'percent' | 'angle' = 'number';
    export let min: number | undefined = undefined;
    export let max: number | undefined = undefined;
    export let constraint: string = '';
    export let mix = '';
    export let inlineLabel = '';

    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    let internalValue: number | undefined = 0;

    function updateData(value: string | number): void {
        let newVal;
        if (subtype === 'percent') {
            newVal = Math.round(Number(value) * 100);
        } else {
            newVal = Number(value);
        }
        if (Number.isNaN(newVal)) {
            internalValue = undefined;
        } else {
            internalValue = newVal;
        }
    }

    $: updateData(value);

    $: parsedConstraint = parseConstraint(subtype, constraint);

    $: minValue = min ?? parsedConstraint.min;
    $: maxValue = max ?? parsedConstraint.max;

    $: step = (subtype === 'integer' || subtype === 'percent' || subtype === 'angle') ? 1 : .01;

    $: pattern = (subtype === 'integer' || subtype === 'percent' || subtype === 'angle') ?
        '\\d+' :
        null;

    $: disabled = $readOnly || item.enabled === false;

    function onChange() {
        let val = Number(internalValue);
        if (subtype === 'percent') {
            val /= 100;
        }

        dispatch('change', {
            value: val,
            item
        });
    }
</script>

<div
    class="number-prop {mix}"
    class:number-prop_inline-label={Boolean(inlineLabel)}
    class:number-prop_disabled={disabled}
>
    <input
        type="number"
        min={minValue}
        max={maxValue}
        {step}
        {pattern}
        class="number-prop__input"
        class:number-prop__input_percent={subtype === 'percent'}
        class:number-prop__input_angle={subtype === 'angle'}
        autocomplete="off"
        autocorrect="off"
        autocapitalize="off"
        spellcheck="false"
        {id}
        readonly={disabled}
        bind:value={internalValue}
        on:input={onChange}
    >

    {#if subtype === 'percent' || subtype === 'angle'}
        <div class="number-prop__symbol" aria-hidden="true">
            {#if subtype === 'percent'}
                %
            {:else if subtype === 'angle'}
                °
            {/if}
        </div>
    {/if}

    {#if inlineLabel}
        <div class="number-prop__inline-label">{inlineLabel}</div>
    {/if}
</div>

<style>
    .number-prop {
        position: relative;
    }

    .number-prop__input {
        box-sizing: border-box;
        width: 100%;
        margin: 0;
        padding: 9px 14px;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        color: inherit;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background: var(--fill-transparent-minus-1);
        appearance: none;
        /* -moz after non-prefix on purpose */
        -moz-appearance: textfield;
        transition: .15s ease-in-out;
        transition-property: border-color, background-color;
    }

    .number-prop_disabled .number-prop__input {
        border-color: transparent;
        background: var(--fill-transparent-1);
        cursor: default;
    }

    .number-prop__input::-webkit-inner-spin-button,
    .number-prop__input::-webkit-outer-spin-button {
        display: none;
    }

    .number-prop__input:invalid {
        background: indianred;
    }

    .number-prop:not(.number-prop_disabled) .number-prop__input:not(:focus-visible):hover {
        border-color: var(--fill-transparent-4);
    }

    .number-prop__input:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }

    .number-prop__input:focus-visible:hover {
        border-color: var(--accent-purple-hover);
    }

    .number-prop_inline-label .number-prop__input {
        padding-left: 42px;
    }

    .number-prop__symbol {
        position: absolute;
        top: 0;
        right: 14px;
        bottom: 0;
        display: flex;
        align-items: center;
        pointer-events: none;
    }

    .number-prop__inline-label {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 16px;
        display: flex;
        align-items: center;
        color: var(--text-tertiary);
        pointer-events: none;
    }
</style>
