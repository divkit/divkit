<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { evalCondition, type ConditionObject } from '../../data/props';
    import type { RadioProperty } from '../../data/componentProps';

    export let value: string | number;
    export let item: RadioProperty;
    export let processedJson;
    export let parentProcessedJson;
    export let options: {
        name: string;
        value: string | number;
        show?: ConditionObject;
    }[];

    $: filteredOptions = options.filter(
        it => it.show === undefined ||
            evalCondition(processedJson, parentProcessedJson, it.show)
    );

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);

    const dispatch = createEventDispatcher();

    function onChange() {
        dispatch('change', {
            value,
            item
        });
    }
</script>

<div class="radio-prop">
    {#each filteredOptions as option}
        <label class="radio-prop__item" title={$l10nString(option.name)}>
            <input
                type="radio"
                name={item.prop}
                class="radio-prop__input"
                value={option.value}
                bind:group={value}
                on:change={onChange}
            >
            <span class="radio-prop__item-inner">
                {$l10nString(option.name)}
            </span>
        </label>
    {/each}
</div>

<style>
    .radio-prop {
        box-sizing: border-box;
        display: flex;
        width: 100%;
        margin: 0;
        padding: 4px;
        font-size: 14px;
        line-height: 20px;
        color: inherit;
        border: none;
        border-radius: 8px;
        background: var(--fill-transparent-1);
        appearance: none;
        text-align: center;
    }

    .radio-prop__item {
        position: relative;
        display: flex;
        flex: 1 0 0;
        min-width: 0;
    }

    .radio-prop__input {
        position: absolute;
        top: 0;
        left: 0;
        box-sizing: border-box;
        width: 100%;
        height: 100%;
        margin: 0;
        padding: 0;
        border: 2px solid transparent;
        border-radius: 6px;
        appearance: none;
        transition: border-color .15s ease-in-out;
        cursor: pointer;
    }

    .radio-prop__input:focus-visible {
        border-color: var(--accent-purple);
        outline: none;
    }

    .radio-prop__item-inner {
        flex: 1 1 auto;
        padding: 6px 16px;
        border-radius: 6px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        transition: background-color .15s ease-in-out;
    }

    .radio-prop__item:hover .radio-prop__item-inner {
        background-color: var(--fill-transparent-2);
    }

    .radio-prop__input:checked + .radio-prop__item-inner {
        background-color: var(--fill-transparent-4);
    }
</style>
