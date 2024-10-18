<script lang="ts">
    import { setContext } from 'svelte';
    import { writable } from 'svelte/store';
    import type { ComponentContext } from '../../types/componentContext';
    import type { LayoutParams } from '../../types/layoutParams';
    import { ENABLED_CTX, type EnabledCtxValue } from '../../context/enabled';
    import Unknown from './Unknown.svelte';

    export let componentContext: ComponentContext;
    export let layoutParams: LayoutParams | undefined = undefined;
    export let enabled: boolean;

    const enabledStore = writable(enabled);

    setContext<EnabledCtxValue>(ENABLED_CTX, {
        isEnabled: enabledStore
    });

    $: {
        enabledStore.set(enabled);
    }
</script>

<Unknown
    {componentContext}
    {layoutParams}
/>
