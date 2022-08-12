<script lang="ts">
    import { getContext } from 'svelte';
    import { derived, Readable } from 'svelte/store';

    import css from './container.module.css';

    import type { Style } from '../types/general';
    import type { ContainerOrientation, DivContainerData } from '../types/container';
    import type { LayoutParams } from '../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../typings/common';
    import type { Size } from '../types/sizes';
    import type { DivBaseData } from '../types/base';
    import type { AlignmentHorizontal, AlignmentVertical } from '../types/alignment';
    import type { MaybeMissing } from '../expressions/json';
    import { ROOT_CTX, RootCtxValue } from '../context/root';
    import Outer from './outer.svelte';
    import Unknown from './unknown.svelte';
    import { wrapError } from '../utils/wrapError';
    import { genClassName } from '../utils/genClassName';
    import { correctContainerOrientation } from '../utils/correctContainerOrientation';
    import { correctAlignmentVertical } from '../utils/correctAlignmentVertical';
    import { correctAlignmentHorizontal } from '../utils/correctAlignmentHorizontal';
    import { assignIfDifferent } from '../utils/assignIfDifferent';

    export let json: Partial<DivContainerData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const HALIGN_MAP = {
        left: 'start',
        center: 'center',
        right: 'end'
    } as const;

    const VALIGN_MAP = {
        top: 'start',
        center: 'center',
        bottom: 'end'
    } as const;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let hasItemsError = false;
    $: jsonItems = json.items;
    $: {
        if (!jsonItems?.length) {
            hasItemsError = true;
            rootCtx.logError(wrapError(new Error('Empty "items" prop for div "container"')));
        } else {
            hasItemsError = false;
        }
    }

    $: items = (jsonItems || []).map(item => {
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

    let orientation: ContainerOrientation = 'vertical';
    $: jsonOrientation = rootCtx.getDerivedFromVars(json.orientation);
    $: {
        orientation = correctContainerOrientation($jsonOrientation, orientation);
    }

    let contentVAlign: AlignmentVertical = 'top';
    $: jsonContentVAlign = rootCtx.getDerivedFromVars(json.content_alignment_vertical);
    $: {
        contentVAlign = correctAlignmentVertical($jsonContentVAlign, contentVAlign);
    }

    let contentHAlign: AlignmentHorizontal = 'left';
    $: jsonContentHAlign = rootCtx.getDerivedFromVars(json.content_alignment_horizontal);
    $: {
        contentHAlign = correctAlignmentHorizontal($jsonContentHAlign, contentHAlign);
    }

    let childLayoutParams: LayoutParams = {};
    $: {
        let newChildLayoutParams: LayoutParams = {};

        if (orientation === 'overlap') {
            newChildLayoutParams.overlapParent = true;
        }
        if (orientation !== 'horizontal') {
            newChildLayoutParams.parentHAlign = HALIGN_MAP[contentHAlign];
        }
        if (orientation !== 'vertical') {
            newChildLayoutParams.parentVAlign = VALIGN_MAP[contentVAlign];
        }
        if (orientation === 'horizontal') {
            newChildLayoutParams.parentLayoutOrientation = 'horizontal';
        }
        if (orientation === 'vertical') {
            newChildLayoutParams.parentLayoutOrientation = 'vertical';
        }

        childLayoutParams = assignIfDifferent(newChildLayoutParams, childLayoutParams);
    }

    let sizesStore: Readable<(MaybeMissing<Size> | undefined)[]>;
    $: {
        let sizes: Readable<(MaybeMissing<Size> | undefined)>[] = [];

        if (orientation !== 'overlap') {
            const sizeProp = orientation === 'vertical' ? 'height' : 'width';

            items.forEach(item => {
                const size = rootCtx.getDerivedFromVars(item.json[sizeProp] as Size);
                sizes.push(size);
            });
        }

        sizesStore = derived(sizes, vals => vals);
    }

    let style: Style = {};
    let hasConstrainedError = false;
    $: {
        let newStyle: Style = {};
        let hasConstrainedError = false;

        if (orientation !== 'overlap') {
            const sizeProp = orientation === 'vertical' ? 'height' : 'width';
            let sizes = $sizesStore;
            let constrained = 0;

            let sizeTemplate = sizes
                .map(size => {
                    if (size) {
                        if (size.type === 'match_parent') {
                            return (Number(size.weight) || 1) + 'fr';
                        }
                    } else if (sizeProp === 'width') {
                        return '1fr';
                    }

                    if (
                        size &&
                        size.type === 'wrap_content' &&
                        size.constrained
                    ) {
                        ++constrained;
                        return 'minmax(0, auto)';
                    }

                    return 'auto';
                })
                .join(' ');

            if (constrained > 1) {
                hasConstrainedError = true;
                rootCtx.logError(wrapError(new Error('Too many constrained items for div "container"')));
            }

            const templateRule = orientation === 'vertical' ? 'grid-template-rows' : 'grid-template-columns';
            newStyle[templateRule] = sizeTemplate;
        }

        style = assignIfDifferent(newStyle, style);
    }

    $: mods = {
        orientation,
        valign: contentVAlign,
        halign: contentHAlign
    };

    $: hasError = hasItemsError || hasConstrainedError;
</script>

{#if !hasError}
    <Outer
        cls={genClassName('container', css, mods)}
        {json}
        {origJson}
        {templateContext}
        {style}
        {layoutParams}
    >
        {#each items as item}
            <Unknown layoutParams={childLayoutParams} div={item.json} templateContext={item.templateContext} origJson={item.origJson} />
        {/each}
    </Outer>
{/if}
