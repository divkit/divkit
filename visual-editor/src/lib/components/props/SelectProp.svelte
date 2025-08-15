<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    export let id: string;
    export let value: string;
    export let prop: string;
    export let options: {
        name: string;
        value: string;
    }[];

    const dispatch = createEventDispatcher();

    function onChange() {
        dispatch('change', {
            value,
            prop
        });
    }
</script>

<select
    class="select-prop"
    {id}
    disabled={$readOnly}
    bind:value={value}
    on:change={onChange}
>
    <option class="select-prop__option" value=""></option>
    {#each options as option}
        <option class="select-prop__option" value={option.value}>{option.name}</option>
    {/each}
</select>

<style>
    .select-prop {
        box-sizing: border-box;
        width: 100%;
        height: 28px;
        margin: 0;
        padding: 4px 12px;
        font: inherit;
        color: inherit;
        border: none;
        appearance: none;
        background: none;
    }

    .select-prop__option {
        color: #000;
        background: #fff;
    }
</style>
