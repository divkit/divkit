<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { FileProperty } from '../../data/componentProps';
    import Text from '../controls/Text.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let value: string;
    export let item: FileProperty;

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

<Text
    subtype="file"
    fileType={item.subtype}
    disabled={$readOnly}
    bind:value={value}
    on:change={onChange}
/>
