<script lang="ts">
    import { createEventDispatcher, getContext, onDestroy } from 'svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import type { Item, ItemStates, ItemTabs } from '../../utils/items';

    export let value: Item;
    export let subtype: 'state' | 'tabs';

    const { state, itemsListDialog } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    let elem: HTMLElement;
    let text = '';

    function updateVal(value: Item): void {
        text = (subtype === 'state' ?
            (value as ItemStates).state_id :
            (value as ItemTabs).title) || '';
    }

    $: updateVal(value);

    function onClick(): void {
        itemsListDialog().show({
            value,
            target: elem,
            readOnly: $readOnly,
            subtype,
            callback(newValue) {
                value = newValue;
                dispatch('change');
            },
        });
    }

    onDestroy(() => {
        itemsListDialog().hide();
    });
</script>

<button
    class="items-list-item"
    class:items-list-item_readonly={$readOnly}
    bind:this={elem}
    on:click={onClick}
>
    <div
        class="items-list-item__id"
    >
        {text}
    </div>
</button>

<style>
    .items-list-item {
        position: relative;
        box-sizing: border-box;
        width: 100%;
        padding: 9px 15px;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background-color: var(--background-primary);
        transition: border-color .15s ease-in-out;
        cursor: pointer;
        text-align: left;
    }

    .items-list-item_readonly {
        border: none;
        background: var(--fill-transparent-1);
    }

    .items-list-item:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }

    .items-list-item:not(.items-list-item_readonly):hover {
        border-color: var(--fill-transparent-4);
    }

    .items-list-item:not(.items-list-item_readonly):focus-within {
        border-color: var(--accent-purple);
    }

    .items-list-item__id {
        min-height: 20px;
        padding-right: var(--inline-size-width);
        font-size: 14px;
        line-height: 20px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .items-list-item__id:focus {
        outline: none;
    }
</style>
