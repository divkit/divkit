<script lang="ts">
    import { getContext } from 'svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let evalValue;

    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;
</script>

<button
    class="expression-value"
    class:expression-value_disabled={$readOnly}
    disabled={$readOnly}
    on:click
>
    {evalValue}
</button>

<style>
    .expression-value {
        box-sizing: border-box;
        width: 100%;
        margin: 0;
        padding: 10px 15px;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        color: inherit;
        text-align: left;
        border: none;
        border-radius: 8px;
        background: var(--fill-transparent-1);
        appearance: none;
        transition: .15s ease-in-out;
        transition-property: background-color;
    }

    .expression-value_disabled {
        cursor: default;
    }

    .expression-value:not(.expression-value_disabled) {
        cursor: pointer;
    }

    .expression-value:not(.expression-value_disabled):hover {
        background: var(--fill-accent-2);
    }

    .expression-value:focus-visible {
        outline: 1px solid var(--accent-purple);
    }
</style>
