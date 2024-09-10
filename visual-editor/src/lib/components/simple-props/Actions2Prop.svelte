<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { Action } from '@divkitframework/divkit/typings/common';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import type { ComponentProperty } from '../../data/componentProps';
    import MoveList2 from '../controls/MoveList2.svelte';
    import Actions2Item from './Actions2Item.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import AddButton from '../controls/AddButton.svelte';

    export let value: Action[] | undefined;
    export let item: ComponentProperty;

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { actionLogUrlVariable, state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    function onChange(): void {
        if (!$readOnly) {
            dispatch('change', {
                item,
                value
            });
        }
    }

    function onAdd(): void {
        if (!$readOnly) {
            dispatch('change', {
                item,
                value: [...(value || []), {
                    log_id: 'action_id',
                    url: 'https://',
                    log_url: actionLogUrlVariable ? `@{${actionLogUrlVariable}}` : undefined
                }]
            });
        }
    }
</script>

<div class="actions2">
    <div class="actions2__list">
        <MoveList2
            bind:values={value}
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
