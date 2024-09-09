<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let id: string;
    export let value: string | number | boolean;
    export let prop: string;

    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    let val = false;

    $: if (value !== undefined) {
        val = Boolean(value);
    }

    const dispatch = createEventDispatcher();

    function onChange() {
        dispatch('change', {
            value: value,
            prop
        });
    }
</script>

<input
    type="checkbox"
    class="checkbox-prop"
    autocomplete="off"
    autocorrect="off"
    autocapitalize="off"
    spellcheck="false"
    {id}
    disabled={$readOnly}
    bind:checked={val}
    on:change={onChange}
/>

<style>
    .checkbox-prop {
        box-sizing: border-box;
        display: block;
        width: 100%;
        height: 100%;
        margin: 0;
        padding: 6px 12px;
        accent-color: var(--accent-purple);
    }
</style>
