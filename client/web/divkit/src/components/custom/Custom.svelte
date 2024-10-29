<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivCustomData } from '../../types/custom';
    import type { CustomComponentDescription } from '../../../typings/custom';
    import type { ComponentContext } from '../../types/componentContext';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';
    import Unknown from '../utilities/Unknown.svelte';
    import Outer from '../utilities/Outer.svelte';
    import DevtoolHolder from '../utilities/DevtoolHolder.svelte';

    export let componentContext: ComponentContext<DivCustomData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let customElem: HTMLElement;
    let desc: CustomComponentDescription | null = null;
    let templateContent = '';
    // shadowrootmode is an unknown attribute in TS :(
    let templateAttrs: any = {};
    let items: ComponentContext[] = [];

    $: if (
        typeof componentContext.json.custom_type === 'string' &&
        componentContext.json.custom_type &&
        rootCtx.customComponents?.has(componentContext.json.custom_type)
    ) {
        desc = rootCtx.customComponents.get(componentContext.json.custom_type)!;
        if (typeof desc.template === 'function') {
            const ctx = rootCtx.getExtensionContext(componentContext);
            const variables: Map<string, string | number | boolean | unknown[] | object> = new Map();
            for (const [key, varaible] of ctx.variables) {
                variables.set(key, varaible.getValue());
            }

            templateContent = desc.template({
                props: componentContext.json.custom_props,
                variables
            });
        } else if (desc.template && typeof desc.template === 'string') {
            templateContent = desc.template;
        } else {
            templateContent = '';
        }
        templateAttrs = {
            shadowrootmode: desc.shadowRootMode || 'open'
        };
    } else {
        desc = null;
        templateContent = ';';
        componentContext.logError(wrapError(new Error('Unknown or incorrect "custom_type" prop for div "custom"')));
    }

    let hasItemsError = false;
    $: jsonItems = componentContext.json.items;
    $: {
        if (jsonItems !== undefined && !Array.isArray(jsonItems)) {
            hasItemsError = true;
            componentContext.logError(wrapError(new Error('Incorrect "items" prop for div "custom"')));
        } else {
            hasItemsError = false;
        }
    }

    $: {
        items.forEach(context => {
            context.destroy();
        });

        items = (!hasItemsError && jsonItems || []).map((item, index) => {
            return componentContext.produceChildContext(item, {
                path: index
            });
        });
    }

    onMount(() => {
        if (customElem && 'divKitApiCallback' in customElem && typeof customElem.divKitApiCallback === 'function') {
            const ctx = rootCtx.getExtensionContext(componentContext);
            customElem.divKitApiCallback(ctx);
        }
    });

    onDestroy(() => {
        items.forEach(context => {
            context.destroy();
        });
    });
</script>

{#if desc}
    <Outer
        {componentContext}
        {layoutParams}
    >
        <svelte:element
            bind:this={customElem}
            this={desc.element}
            {...(componentContext.json.custom_props || {})}
        >
            {#if templateContent}
                <template {...templateAttrs}>
                    <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                    {@html templateContent}
                </template>
            {/if}

            {#if !hasItemsError && jsonItems}
                {#key jsonItems}
                    {#each items as item}
                        <Unknown
                            componentContext={item}
                        />
                    {/each}
                {/key}
            {/if}
        </svelte:element>
    </Outer>
{:else if process.env.DEVTOOL}
    <DevtoolHolder
        {componentContext}
    />
{/if}
