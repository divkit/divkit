<script lang="ts">
    import { getContext } from 'svelte';

    import css from './Container.module.css';

    import type { ContainerOrientation, DivContainerData } from '../../types/container';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivBaseData } from '../../types/base';
    import type { AlignmentHorizontal, AlignmentVertical } from '../../types/alignment';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import Outer from '../utilities/Outer.svelte';
    import Unknown from '../utilities/Unknown.svelte';
    import { wrapError } from '../../utils/wrapError';
    import { genClassName } from '../../utils/genClassName';
    import { correctContainerOrientation } from '../../utils/correctContainerOrientation';
    import { correctAlignmentVertical } from '../../utils/correctAlignmentVertical';
    import { correctAlignmentHorizontal } from '../../utils/correctAlignmentHorizontal';
    import { assignIfDifferent } from '../../utils/assignIfDifferent';

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
        if (!jsonItems?.length || !Array.isArray(jsonItems)) {
            hasItemsError = true;
            rootCtx.logError(wrapError(new Error('Incorrect or empty "items" prop for div "container"')));
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

    $: jsonWidth = rootCtx.getDerivedFromVars(json.width);
    $: jsonHeight = rootCtx.getDerivedFromVars(json.height);

    let childLayoutParams: LayoutParams = {};
    $: {
        let newChildLayoutParams: LayoutParams = {};

        if (orientation === 'overlap') {
            newChildLayoutParams.overlapParent = true;
        }
        if (orientation !== 'horizontal') {
            newChildLayoutParams.parentHAlign = HALIGN_MAP[contentHAlign];
            if (
                !$jsonHeight ||
                $jsonHeight.type === 'wrap_content' ||
                $jsonHeight.type === 'match_parent' && layoutParams?.parentVerticalWrapContent
            ) {
                newChildLayoutParams.parentVerticalWrapContent = true;
            }
        }
        if (orientation !== 'vertical') {
            newChildLayoutParams.parentVAlign = VALIGN_MAP[contentVAlign];
            if (
                $jsonWidth?.type === 'wrap_content' ||
                $jsonWidth?.type === 'match_parent' && layoutParams?.parentHorizontalWrapContent
            ) {
                newChildLayoutParams.parentHorizontalWrapContent = true;
            }
        }
        if (orientation === 'horizontal') {
            newChildLayoutParams.parentContainerOrientation = 'horizontal';
        }
        if (orientation === 'vertical') {
            newChildLayoutParams.parentContainerOrientation = 'vertical';
        }

        childLayoutParams = assignIfDifferent(newChildLayoutParams, childLayoutParams);
    }

    $: mods = {
        orientation,
        valign: contentVAlign,
        halign: contentHAlign
    };
</script>

{#if !hasItemsError}
    <Outer
        cls={genClassName('container', css, mods)}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
    >
        {#each items as item}
            <Unknown layoutParams={childLayoutParams} div={item.json} templateContext={item.templateContext} origJson={item.origJson} />
        {/each}
    </Outer>
{/if}
