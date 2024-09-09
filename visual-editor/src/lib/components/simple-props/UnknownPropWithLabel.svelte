<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import UnknownProp from './UnknownProp.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import type { ComponentProperty, SiblingComponentProperty } from '../../data/componentProps';
    import Switcher from '../controls/Switcher.svelte';
    import { tankerKeyToVariableName } from '../../utils/tanker';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import sourceIcon from '../../../assets/source.svg?raw';
    import ExpressionValue from './ExpressionValue.svelte';

    export let item: ComponentProperty | SiblingComponentProperty;
    export let value: unknown;
    export let evalValue;
    export let processedJson;
    export let parentProcessedJson;
    export let parentEvalJson;

    const dispatch = createEventDispatcher();

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { cardLocales, state, expression2Dialog } = getContext<AppContext>(APP_CTX);
    const { tanker, locale, readOnly } = state;

    $: tankerToggled = typeof value === 'string' && value.startsWith('@{tanker_');
    $: isExpression = !tankerToggled && value !== evalValue && typeof value !== 'object';
    $: showSources = !tankerToggled && ('enableSources' in item && item.enableSources || isExpression);

    $: isLabel = !showSources && !(
        item.type === 'text-styles' ||
        item.type === 'text-align' ||
        item.type === 'radio' ||
        item.type === 'background2' ||
        item.type === 'actions2' ||
        item.type === 'string' && item.enableTanker ||
        item.type === 'video_sources' ||
        item.type === 'alignment' ||
        item.type === 'margins-paddings'
    );

    $: hasLabel = 'name' in item && item.name || 'rawName' in item && item.rawName;

    let sourcesButton: HTMLButtonElement;

    function onTankerChange(): void {
        if (tankerToggled) {
            // todo do not lose value on multiple toggles
            dispatch('change', {
                values: [{
                    prop: 'text',
                    value: `@{${tankerKeyToVariableName('')}}`
                }, {
                    prop: 'ranges',
                    value: undefined
                }, {
                    prop: 'images',
                    value: undefined
                }],
                item
            });
        } else {
            const key = typeof value === 'string' ? state.getTankerKey(value) : '';
            const meta = key && $tanker[key] || undefined;
            const text = meta?.[$locale] || 'text';

            dispatch('change', {
                value: text,
                item
            });
        }
    }

    function onSourceClick(): void {
        expression2Dialog().show({
            target: sourcesButton,
            value: typeof value === 'string' ? value : '',
            disabled: $readOnly,
            callback(val) {
                dispatch('change', {
                    value: val,
                    item
                });
            }
        });
    }
</script>

<svelte:element
    this={
        isLabel ?
            'label' :
            'div'
    }
    class="unknown-prop"
>
    {#if 'name' in item && item.name || 'rawName' in item && item.rawName}
        <div class="unknown-prop__name">
            <div class="unknown-prop__label">
                {#if 'rawName' in item}
                    {item.rawName}
                {:else if item.name}
                    {$l10n(item.name)}
                {/if}

                {#if showSources}
                    <button
                        bind:this={sourcesButton}
                        class="unknown-prop__source"
                        class:unknown-prop__source_toggle={isExpression}
                        on:click={onSourceClick}
                    >
                        <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                        {@html sourceIcon}
                    </button>
                {/if}
            </div>

            {#if 'enableTanker' in item && item.enableTanker && cardLocales.length}
                <svelte:element
                    this={(item.type === 'string' && item.enableTanker) ? 'label' : 'div'}
                    class="unknown-prop__tanker"
                >
                    {$l10n('tankerTitle')}

                    <Switcher
                        bind:checked={tankerToggled}
                        disabled={$readOnly}
                        on:change={onTankerChange}
                    />
                </svelte:element>
            {/if}
        </div>
    {/if}

    {#if hasLabel && isExpression}
        <ExpressionValue
            {evalValue}
            on:click={onSourceClick}
        />
    {:else}
        <UnknownProp
            {item}
            {value}
            {processedJson}
            {parentProcessedJson}
            {parentEvalJson}
            {tankerToggled}
            on:change
        />
    {/if}
</svelte:element>

<style>
    .unknown-prop__name {
        display: flex;
        justify-content: space-between;
        margin-bottom: 6px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
    }

    .unknown-prop__tanker {
        display: flex;
        gap: 8px;
        user-select: none;
    }

    .unknown-prop__label {
        display: flex;
        align-items: center;
    }

    .unknown-prop__source {
        width: 20px;
        height: 20px;
        margin: 0;
        margin-left: 4px;
        padding: 0;
        color: inherit;
        border: none;
        border-radius: 4px;
        background: none;
        cursor: pointer;
        opacity: 0;
        transition: .15s ease-in-out;
        transition-property: background-color, opacity;
    }

    .unknown-prop__source:focus,
    .unknown-prop:hover .unknown-prop__source {
        opacity: 1;
    }

    .unknown-prop__source_toggle {
        opacity: 1;
        color: var(--accent-purple);
    }

    .unknown-prop__source:hover {
        background-color: var(--fill-accent-2);
    }

    .unknown-prop__source :global(.source-icon) {
        transition: fill .15s ease-in-out;
    }

    .unknown-prop__source:focus-visible {
        outline: 1px solid var(--accent-purple);
    }
</style>
