<script lang="ts">
    import { getContext } from 'svelte';
    import type { SvelteComponent } from 'svelte';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivBaseData } from '../../types/base';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';
    import { TYPE_MAP } from '../typeMap';

    export let div: DivBaseData;
    export let templateContext: TemplateContext;
    export let origJson: DivBaseData | DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let childJson: DivBaseData;
    let childContext: TemplateContext;

    let component: typeof SvelteComponent | undefined;

    $: {
        childJson = div;
        childContext = templateContext;

        component = childJson && TYPE_MAP[childJson.type] || undefined;

        if (!component) {
            ({
                templateContext: childContext,
                json: childJson
            } = rootCtx.processTemplate(childJson, childContext));

            component = childJson && TYPE_MAP[childJson.type] || undefined;
            if (!component) {
                let error: string;
                if (childJson?.type && rootCtx.hasTemplate(childJson.type)) {
                    error = 'Recursive template';
                } else {
                    error = 'Unknown component';
                }

                rootCtx.logError(wrapError(new Error(error), {
                    additional: {
                        component: childJson?.type || '<missing>'
                    }
                }));
            }
        }
    }
</script>

{#if component}
    <svelte:component
        this={component}
        json={childJson}
        templateContext={childContext}
        {layoutParams}
        origJson={origJson || div}
    />
{/if}
