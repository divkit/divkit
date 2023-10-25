<script lang="ts">
    import { getContext, onMount } from 'svelte';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivCustomData } from '../../types/custom';
    import type { DivBaseData } from '../../types/base';
    import type { CustomComponentDescription } from '../../../typings/custom';
    import Unknown from '../utilities/Unknown.svelte';
    import Outer from '../utilities/Outer.svelte';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';

    export let json: Partial<DivCustomData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let customElem: HTMLElement;
    let desc: CustomComponentDescription | null = null;
    let templateContent = '';
    // shadowrootmode is an unknown attribute in TS :(
    let templateAttrs: any = {};
    $: if (typeof json.custom_type === 'string' && json.custom_type && rootCtx.customComponents?.has(json.custom_type)) {
        desc = rootCtx.customComponents.get(json.custom_type)!;
        if (typeof desc.template === 'function') {
            const ctx = rootCtx.getExtensionContext();
            const variables: Map<string, string | number | boolean | unknown[] | object> = new Map();
            for (const [key, varaible] of ctx.variables) {
                variables.set(key, varaible.getValue());
            }

            templateContent = desc.template({
                props: json.custom_props,
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
        rootCtx.logError(wrapError(new Error('Unknown or incorrect "custom_type" prop for div "custom"')));
    }

    let hasItemsError = false;
    $: jsonItems = json.items;
    $: {
        if (jsonItems !== undefined && !Array.isArray(jsonItems)) {
            hasItemsError = true;
            rootCtx.logError(wrapError(new Error('Incorrect "items" prop for div "custom"')));
        } else {
            hasItemsError = false;
        }
    }

    $: items = (!hasItemsError && jsonItems || []).map(item => {
        let childJson: DivBaseData = item as DivBaseData;
        let childContext: TemplateContext = templateContext;

        ({
            templateContext: childContext,
            json: childJson
        } = rootCtx.processTemplate(childJson, childContext));

        return {
            json: childJson,
            templateContext: childContext,
            origJson: item
        };
    });

    $: childLayoutParams = {
        fakeElement: layoutParams?.fakeElement
    };

    onMount(() => {
        if (customElem && 'divKitApiCallback' in customElem && typeof customElem.divKitApiCallback === 'function') {
            const ctx = rootCtx.getExtensionContext();
            customElem.divKitApiCallback(ctx);
        }
    });
</script>

{#if desc}
    <Outer
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
    >
        <svelte:element
            bind:this={customElem}
            this={desc.element}
            {...(json.custom_props || {})}
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
                            layoutParams={childLayoutParams}
                            div={item.json}
                            templateContext={item.templateContext}
                            origJson={item.origJson}
                        />
                    {/each}
                {/key}
            {/if}
        </svelte:element>
    </Outer>
{/if}
