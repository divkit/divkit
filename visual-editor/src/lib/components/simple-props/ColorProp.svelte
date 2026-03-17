<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { ColorProperty } from '../../data/componentProps';
    import ColorInput from '../controls/ColorInput.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let value: string;
    export let item: ColorProperty;

    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    function onChange() {
        dispatch('change', {
            value,
            item
        });
    }
</script>

<ColorInput
    bind:value={value}
    on:change={onChange}
    hasDialog={true}
    readOnly={$readOnly}
    showAlpha={item.showAlpha}
/>
