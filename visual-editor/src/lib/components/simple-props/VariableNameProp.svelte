<script lang="ts">
    import { getContext } from 'svelte';
    import type { ComponentProperty } from '../../data/componentProps';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import SelectProp from './SelectProp.svelte';

    export let value: string;
    export let item: ComponentProperty;
    export let processedJson: unknown | undefined = undefined;
    export let parentProcessedJson: unknown | undefined = undefined;

    const { state } = getContext<AppContext>(APP_CTX);
    const { customVariables } = state;

    $: options = $customVariables.map(it => {
        return {
            rawName: it.name,
            value: it.name
        };
    });
</script>

<SelectProp
    {value}
    {item}
    {processedJson}
    {parentProcessedJson}
    {options}
    on:change
/>
