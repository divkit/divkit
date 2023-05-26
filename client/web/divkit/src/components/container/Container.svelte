<script lang="ts">
    import { getContext } from 'svelte';

    import css from './Container.module.css';

    import type { ContainerOrientation, DivContainerData } from '../../types/container';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivBaseData } from '../../types/base';
    import type {
        ContentAlignmentHorizontal,
        ContentAlignmentVertical
    } from '../../types/alignment';
    import type { SeparatorStyle } from '../../utils/container';
    import { prepareMargins } from '../../utils/container';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';
    import { genClassName } from '../../utils/genClassName';
    import { correctContainerOrientation } from '../../utils/correctContainerOrientation';
    import { assignIfDifferent } from '../../utils/assignIfDifferent';
    import { correctDrawableStyle, DrawableStyle } from '../../utils/correctDrawableStyles';
    import { calcAdditionalPaddings, calcItemsGap } from '../../utils/container';
    import { hasGapSupport } from '../../utils/hasGapSupport';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import ContainerSeparators from './ContainerSeparators.svelte';
    import Unknown from '../utilities/Unknown.svelte';
    import Outer from '../utilities/Outer.svelte';
    import { correctContentAlignmentVertical } from '../../utils/correctContentAlignmentVertical';
    import { correctContentAlignmentHorizontal } from '../../utils/correctContentAlignmentHorizontal';

    export let json: Partial<DivContainerData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const HALIGN_MAP = {
        left: 'start',
        center: 'center',
        right: 'end',
        // 'space-*' values doesn't supported for cross-axis in wrap-container
        'space-between': 'start',
        'space-around': 'start',
        'space-evenly': 'start'
    } as const;

    const VALIGN_MAP = {
        top: 'start',
        center: 'center',
        bottom: 'end',
        baseline: 'baseline',
        // 'space-*' doesn't supported for cross-axis in wrap-container
        'space-between': 'start',
        'space-around': 'start',
        'space-evenly': 'start'
    } as const;

    const AVAIL_SEPARATOR_SHAPES = ['rounded_rectangle', 'circle'];

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

    let contentVAlign: ContentAlignmentVertical = 'top';
    $: jsonContentVAlign = rootCtx.getDerivedFromVars(json.content_alignment_vertical);
    $: {
        contentVAlign = correctContentAlignmentVertical($jsonContentVAlign, contentVAlign);
    }

    let contentHAlign: ContentAlignmentHorizontal = 'left';
    $: jsonContentHAlign = rootCtx.getDerivedFromVars(json.content_alignment_horizontal);
    $: {
        contentHAlign = correctContentAlignmentHorizontal($jsonContentHAlign, contentHAlign);
    }

    $: jsonLayoutMode = rootCtx.getDerivedFromVars(json.layout_mode);
    $: wrap = $jsonLayoutMode === 'wrap';

    $: jsonSeparator = rootCtx.getDerivedFromVars(json.separator);
    let separator: SeparatorStyle | null = null;
    $: {
        if ($jsonSeparator?.style && orientation !== 'overlap' && hasGapSupport()) {
            const style = correctDrawableStyle<DrawableStyle | null>(
                $jsonSeparator.style,
                AVAIL_SEPARATOR_SHAPES,
                separator?.style || null
            );

            if (style) {
                separator = {
                    show_at_start: Boolean($jsonSeparator.show_at_start ?? false),
                    show_at_end: Boolean($jsonSeparator.show_at_end ?? false),
                    show_between: Boolean($jsonSeparator.show_between ?? true),
                    style,
                    margins: prepareMargins($jsonSeparator.margins)
                };
            } else {
                separator = null;
            }
        } else {
            separator = null;
        }
    }

    $: jsonLineSeparator = rootCtx.getDerivedFromVars(json.line_separator);
    let lineSeparator: SeparatorStyle | null = null;
    $: {
        if ($jsonLineSeparator?.style && orientation !== 'overlap' && hasGapSupport()) {
            const style = correctDrawableStyle<DrawableStyle | null>(
                $jsonLineSeparator.style,
                AVAIL_SEPARATOR_SHAPES,
                lineSeparator?.style || null
            );

            if (style) {
                lineSeparator = {
                    show_at_start: Boolean($jsonLineSeparator.show_at_start ?? false),
                    show_at_end: Boolean($jsonLineSeparator.show_at_end ?? false),
                    show_between: Boolean($jsonLineSeparator.show_between ?? true),
                    style,
                    margins: prepareMargins($jsonLineSeparator.margins)
                };
            } else {
                lineSeparator = null;
            }
        } else {
            lineSeparator = null;
        }
    }

    $: additionalPaddings = (separator || lineSeparator) ?
        calcAdditionalPaddings(orientation, separator, lineSeparator) :
        null;

    $: jsonAspect = rootCtx.getDerivedFromVars(json.aspect);
    let aspect: number | undefined = undefined;
    $: {
        const newRatio = $jsonAspect?.ratio;
        if (newRatio && isPositiveNumber(newRatio)) {
            aspect = newRatio;
        } else {
            aspect = undefined;
        }
    }

    $: jsonWidth = rootCtx.getDerivedFromVars(json.width);
    $: jsonHeight = rootCtx.getDerivedFromVars(json.height);

    let childLayoutParams: LayoutParams = {};
    $: {
        let newChildLayoutParams: LayoutParams = {};

        if (layoutParams?.fakeElement) {
            newChildLayoutParams.fakeElement = true;
        }
        if (orientation === 'overlap') {
            newChildLayoutParams.overlapParent = true;
        }
        if (orientation !== 'horizontal') {
            newChildLayoutParams.parentHAlign = HALIGN_MAP[contentHAlign];
            if (
                !aspect && (
                    !$jsonHeight ||
                    $jsonHeight.type === 'wrap_content' ||
                    $jsonHeight.type === 'match_parent' && layoutParams?.parentVerticalWrapContent
                )
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
        if (wrap) {
            newChildLayoutParams.parentContainerWrap = true;
        }

        childLayoutParams = assignIfDifferent(newChildLayoutParams, childLayoutParams);
    }

    $: mods = {
        orientation,
        valign: contentVAlign,
        halign: contentHAlign,
        wrap
    };

    $: style = {
        gap: (separator || lineSeparator) ?
            calcItemsGap(orientation, separator, lineSeparator) :
            undefined,
        'aspect-ratio': aspect
    };
</script>

{#if !hasItemsError}
    <Outer
        cls={genClassName('container', css, mods)}
        {style}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
        {additionalPaddings}
        heightByAspect={Boolean(aspect)}
    >
        {#each items as item}
            <Unknown layoutParams={childLayoutParams} div={item.json} templateContext={item.templateContext} origJson={item.origJson} />
        {/each}

        {#if separator || lineSeparator}
            <ContainerSeparators
                {separator}
                {lineSeparator}
                {orientation}
            />
        {/if}
    </Outer>
{/if}
