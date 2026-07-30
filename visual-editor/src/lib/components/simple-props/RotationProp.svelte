<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { ComponentProperty } from '../../data/componentProps';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import Rotation from '../controls/Rotation.svelte';

    export let id: string = '';
    export let value: number;
    export let item: ComponentProperty;
    export let mix = '';

    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    function onChange(event: CustomEvent<{
        value: number;
    }>): void {
        dispatch('change', {
            value: event.detail.value,
            item
        });
    }
</script>

<Rotation
    {id}
    {mix}
    disabled={$readOnly}
    bind:value={value}
    on:change={onChange}
/>
