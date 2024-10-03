<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import type { ComponentProperty } from '../../data/componentProps';
    import MoveList2 from '../controls/MoveList2.svelte';
    import Background2Item from './Background2Item.svelte';
    import type { Background } from '../../data/background';
    import AddButton from '../controls/AddButton.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let value: Background[];
    export let item: ComponentProperty;

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
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
                    type: 'solid',
                    color: '#000'
                }]
            });
        }
    }
</script>

<svelte:options immutable={true} />

<div class="background2">
    <div class="background2__list">
        <MoveList2
            bind:values={value}
            itemView={Background2Item}
            readOnly={$readOnly}
            on:change={onChange}
            on:reorder={onChange}
        />
    </div>

    {#if !$readOnly}
        <AddButton
            cls="background2__add"
            title={$l10nString('add_background')}
            disabled={$readOnly}
            slim
            on:click={onAdd}
        >
        </AddButton>
    {/if}
</div>

<style>
    .background2__list {
        margin-left: -20px;
    }
</style>
