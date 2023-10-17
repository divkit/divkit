<script lang="ts">
    import { getContext } from 'svelte';

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

    let desc: CustomComponentDescription | null = null;
    let templateContent = '';
    // shadowrootmode is an unknown attribute in TS :(
    let templateAttrs: any = {};
    $: if (typeof json.custom_type === 'string' && json.custom_type && rootCtx.customComponents?.has(json.custom_type)) {
        desc = rootCtx.customComponents.get(json.custom_type)!;
        templateContent = desc.template || '';
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
</script>

{#if desc}
    <Outer
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
    >
        <svelte:element this={desc.element} {...(json.custom_props || {})}>
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
