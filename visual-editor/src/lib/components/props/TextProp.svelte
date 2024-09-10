<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { parseConstraint } from '../../utils/parseConstraint';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let id: string;
    export let value: string;
    export let prop: string;
    export let flags: {
        subtype?: string;
        constraint?: string;
    };

    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    $: number = flags.subtype === 'number' || flags.subtype === 'integer';

    $: type = number ? 'number' : 'text';

    $: limits = parseConstraint(flags.subtype, flags.constraint);
    $: min = limits.min;
    $: max = limits.max;

    // eslint-disable-next-line no-nested-ternary
    $: step = flags.subtype === 'integer' ? 1 : (flags.subtype === 'number' ? .01 : null);

    $: pattern = flags.subtype === 'integer' ? '\\d+' : null;

    function onChange() {
        dispatch('change', {
            value,
            prop
        });
    }
</script>

{#if type === 'text'}
    <input
        type="text"
        {min}
        {max}
        {step}
        {pattern}
        class="input-prop"
        autocomplete="off"
        autocorrect="off"
        autocapitalize="off"
        spellcheck="false"
        {id}
        disabled={$readOnly}
        bind:value={value}
        on:input={onChange}
    />
{:else}
    <input
        type="number"
        {min}
        {max}
        {step}
        {pattern}
        class="input-prop"
        autocomplete="off"
        autocorrect="off"
        autocapitalize="off"
        spellcheck="false"
        {id}
        disabled={$readOnly}
        bind:value={value}
        on:input={onChange}
    />

{/if}

<style>
    .input-prop {
        box-sizing: border-box;
        width: 100%;
        margin: 0;
        padding: 6px 12px;
        font: inherit;
        color: inherit;
        border: none;
        background: none;
        appearance: none;
    }

    .input-prop:invalid {
        background: indianred;
    }
</style>
