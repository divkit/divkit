<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { Action } from '@divkitframework/divkit/typings/common';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import type { Actions2Property } from '../../data/componentProps';
    import MoveList2 from '../controls/MoveList2.svelte';
    import Actions2Item from './Actions2Item.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import AddButton from '../controls/AddButton.svelte';
    import { getProp } from '../../data/props';

    export let value: Action[] | undefined;
    export let item: Actions2Property;
    export let processedJson: object | undefined = undefined;

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { actionLogUrlVariable, state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    let list = value || [];

    $: aloneValue = processedJson && item.aloneProp ? getProp(processedJson, item.aloneProp) : undefined;

    $: {
        list = value || aloneValue && [aloneValue] || [];
    }

    function onChange(): void {
        if (!$readOnly) {
            value = list;

            const values: {
                prop: string;
                value: unknown;
            }[] = [{
                prop: item.prop || 'actions',
                value
            }];

            if (item.aloneProp) {
                values.push({
                    prop: item.aloneProp,
                    value: undefined
                });
            }

            dispatch('change', {
                item,
                values
            });
        }
    }

    function onAdd(): void {
        if (!$readOnly) {
            const values: {
                prop: string;
                value: unknown;
            }[] = [{
                prop: item.prop || 'actions',
                value: [...(list || []), {
                    log_id: 'action_id',
                    url: 'https://',
                    log_url: actionLogUrlVariable ? `@{${actionLogUrlVariable}}` : undefined
                }]
            }];

            if (item.aloneProp) {
                values.push({
                    prop: item.aloneProp,
                    value: undefined
                });
            }

            dispatch('change', {
                item,
                values
            });
        }
    }
</script>

<svelte:options immutable={true} />

<div class="actions2">
    <div class="actions2__list">
        <MoveList2
            bind:values={list}
            itemView={Actions2Item}
            readOnly={$readOnly}
            on:change={onChange}
            on:reorder={onChange}
        />
    </div>

    {#if !$readOnly}
        <AddButton
            cls="actions2__add"
            title={$l10nString('add_background')}
            disabled={$readOnly}
            on:click={onAdd}
        >
            {$l10nString('add_action')}
        </AddButton>
    {/if}
</div>

<style>
    .actions2__list {
        margin-left: -20px;
    }
</style>
