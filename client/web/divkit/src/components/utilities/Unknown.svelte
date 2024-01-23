<script lang="ts">
    import { getContext } from 'svelte';
    import type { SvelteComponent } from 'svelte';
    import type { ComponentContext } from '../../types/componentContext';
    import type { LayoutParams } from '../../types/layoutParams';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';
    import { TYPE_MAP } from '../typeMap';

    export let componentContext: ComponentContext;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let component: typeof SvelteComponent | undefined;

    $: {
        const childJson = componentContext.json;

        component = childJson?.type && TYPE_MAP[childJson.type] || undefined;

        if (!component) {
            let errorText: string;
            if (childJson?.type && rootCtx.hasTemplate(childJson.type)) {
                errorText = 'Recursive template';
            } else {
                errorText = 'Unknown component';
            }

            componentContext.logError(wrapError(new Error(errorText), {
                additional: {
                    component: childJson?.type || '<missing>'
                }
            }));
        }
    }
</script>

{#if component}
    <svelte:component
        this={component}
        {componentContext}
        {layoutParams}
    />
{/if}
