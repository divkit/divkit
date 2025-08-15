<script lang="ts">
    import { createEventDispatcher, getContext, onDestroy } from 'svelte';
    import type { SelectOption } from '../../utils/select';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let value: SelectOption;

    const { state, selectOptionsDialog } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    let elem: HTMLElement;
    let text = '';

    function updateVal(option: SelectOption): void {
        text = option.text || option.value || '';
    }

    $: updateVal(value);

    function onClick(): void {
        selectOptionsDialog().show({
            value,
            target: elem,
            readOnly: $readOnly,
            callback(newValue) {
                value = newValue;
                dispatch('change');
            }
        });
    }

    onDestroy(() => {
        selectOptionsDialog().hide();
    });
</script>

<button
    class="select-options-item"
    class:select-options-item_readonly={$readOnly}
    bind:this={elem}
    on:click={onClick}
>
    <div class="select-options-item__text">
        {text}
    </div>
</button>

<style>
    .select-options-item {
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

    .select-options-item_readonly {
        border: none;
        background: var(--fill-transparent-1);
    }

    .select-options-item:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }

    .select-options-item:not(.select-options-item_readonly):hover {
        border-color: var(--fill-transparent-4);
    }

    .select-options-item:not(.select-options-item_readonly):focus-within {
        border-color: var(--accent-purple);
    }

    .select-options-item__text {
        min-height: 20px;
        font-size: 14px;
        line-height: 20px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .select-options-item__text:focus {
        outline: none;
    }
</style>
