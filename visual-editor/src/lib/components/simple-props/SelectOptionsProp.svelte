<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { SelectOption } from '../../utils/select';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import type { ComponentProperty } from '../../data/componentProps';
    import MoveList2 from '../controls/MoveList2.svelte';
    import SelectOptionsItem from './SelectOptionsItem.svelte';
    import AddButton from '../controls/AddButton.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let value: SelectOption[];
    export let item: ComponentProperty;

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
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
                    value: ''
                }]
            });
        }
    }
</script>

<svelte:options immutable={true} />

<div class="select-options">
    <div class="select-options__list">
        <MoveList2
            bind:values={value}
            itemView={SelectOptionsItem}
            readOnly={$readOnly}
            on:change={onChange}
            on:reorder={onChange}
        />
    </div>

    <AddButton
        cls="select-options__add"
        disabled={$readOnly}
        on:click={onAdd}
    >
        {$l10n('add_select_option')}
    </AddButton>
</div>

<style>
    .select-options__list {
        margin-left: -20px;
    }
</style>
