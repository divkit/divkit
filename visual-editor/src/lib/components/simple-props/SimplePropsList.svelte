<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { slide } from 'svelte/transition';
    import type { ComponentProperty } from '../../data/componentProps';
    import { evalCondition, getProp } from '../../data/props';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import SimplePropsGroup from './SimplePropsGroup.svelte';
    import UnknownPropWithLabel from './UnknownPropWithLabel.svelte';
    import { formatSize } from '../../utils/formatSize';
    import type { SelectedElemProps } from '../../ctx/appContext';

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);

    const dispatch = createEventDispatcher();

    export let propsList: ComponentProperty[];
    export let processedJson: Record<string, unknown>;
    export let parentProcessedJson: Record<string, unknown> | null;
    export let parentEvalJson: Record<string, unknown> | null;
    export let evalJson: Record<string, unknown> | null;
    export let selectedElemProps: SelectedElemProps | null;
    export let hasMargins = true;

    function onChange(event: CustomEvent): void {
        // Avoid TypeScript type infinite loop - manual bypass events
        dispatch(event.type, event.detail);
    }
</script>

<div class="simple-props-group">
    <ul class="simple-props-group__list">
        {#each propsList as item}
            {#if item.type === 'group'}
                <li class="simple-props-group__item simple-props-group__item_group">
                    <SimplePropsGroup
                        title={(item.title ? $l10nString(item.title) : item.rawTitle) || ''}
                        propsList={item.list}
                        {processedJson}
                        {parentProcessedJson}
                        {parentEvalJson}
                        {evalJson}
                        {selectedElemProps}
                        on:change={onChange}
                    />
                </li>
            {:else if item.type === 'split'}
                <li class="simple-props-group__item simple-props-group__item_raw">
                    <div class="simple-props-group__split">
                        <div class="simple-props-group__split-item">
                            <svelte:self
                                propsList={[item.list[0]]}
                                {processedJson}
                                {parentProcessedJson}
                                {parentEvalJson}
                                {evalJson}
                                {selectedElemProps}
                                hasMargins={false}
                                on:change
                            />
                        </div>
                        <div class="simple-props-group__split-item">
                            <svelte:self
                                propsList={[item.list[1]]}
                                {processedJson}
                                {parentProcessedJson}
                                {parentEvalJson}
                                {evalJson}
                                {selectedElemProps}
                                hasMargins={false}
                                on:change
                            />
                        </div>
                    </div>
                </li>
            {:else}
                {@const value = item.prop && getProp(processedJson, item.prop, item.default)}
                {@const evalValue = item.prop && getProp(evalJson, item.prop, item.default)}
                {@const enabled = item.enabled === false ?
                    false :
                    (
                        !item.enabled ||
                        evalCondition(processedJson, parentProcessedJson, item.enabled)
                    )
                }

                <li
                    class="simple-props-group__item"
                    class:simple-props-group__item_raw={hasMargins}
                    class:simple-props-group__item_disabled={!enabled}
                    inert={!enabled}
                >
                    <UnknownPropWithLabel
                        {item}
                        {value}
                        {evalValue}
                        {processedJson}
                        {parentProcessedJson}
                        {parentEvalJson}
                        on:change
                    />

                    {#if item.siblings}
                        {#each item.siblings as sibling}
                            {#if !('show' in sibling && sibling.show) || evalCondition(processedJson, parentProcessedJson, sibling.show)}
                                {@const value = ('sizeValue' in sibling && sibling.sizeValue) ?
                                    formatSize(selectedElemProps?.[sibling.sizeValue]) :
                                    ('prop' in sibling && sibling.prop && getProp(processedJson, sibling.prop, sibling.default))
                                }
                                {@const evalValue = ('sizeValue' in sibling && sibling.sizeValue) ?
                                    formatSize(selectedElemProps?.[sibling.sizeValue]) :
                                    ('prop' in sibling && sibling.prop && getProp(evalJson, sibling.prop, sibling.default))
                                }

                                <div class="simple-props-group__sibling" transition:slide|local>
                                    <UnknownPropWithLabel
                                        item={sibling}
                                        {value}
                                        {evalValue}
                                        {processedJson}
                                        {parentProcessedJson}
                                        {parentEvalJson}
                                        on:change
                                    />
                                </div>
                            {/if}
                        {/each}
                    {/if}
                </li>
            {/if}
        {/each}
    </ul>
</div>

<style>
    .simple-props-group__list {
        display: flex;
        flex-direction: column;
        /* margin: 0 16px; */
        margin: 0;
        padding: 0;
        list-style: none;
    }

    .simple-props-group__item {
        transition: opacity .15s ease-in-out;
    }

    .simple-props-group__item_disabled {
        opacity: .4;
        pointer-events: none;
    }

    .simple-props-group__item_raw {
        margin: 0 20px;
    }

    .simple-props-group__item + .simple-props-group__item {
        margin-top: 24px;
    }

    .simple-props-group__item + .simple-props-group__item_group {
        margin-top: 0;
    }

    .simple-props-group__sibling {
        margin-top: 12px;
    }

    .simple-props-group__split {
        display: flex;
        gap: 16px;
    }

    .simple-props-group__split-item {
        flex: 1 0 0;
        min-width: 0;
    }
</style>
