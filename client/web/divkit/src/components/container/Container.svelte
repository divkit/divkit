<script lang="ts" context="module">
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

    const AVAIL_SEPARATOR_SHAPES = [
        'rounded_rectangle',
        'circle'
    ];
</script>

<script lang="ts">
    import { getContext } from 'svelte';
    import { Readable, derived } from 'svelte/store';

    import css from './Container.module.css';

    import type { ContainerOrientation, DivContainerData } from '../../types/container';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivBaseData } from '../../types/base';
    import type {
        ContentAlignmentHorizontal,
        ContentAlignmentVertical
    } from '../../types/alignment';
    import type { ContainerChildInfo, SeparatorStyle } from '../../utils/container';
    import { prepareMargins } from '../../utils/container';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { genClassName } from '../../utils/genClassName';
    import { correctContainerOrientation } from '../../utils/correctContainerOrientation';
    import { assignIfDifferent } from '../../utils/assignIfDifferent';
    import { correctDrawableStyle, DrawableStyle } from '../../utils/correctDrawableStyles';
    import { calcAdditionalPaddings, calcItemsGap, hasKnownHeightCheck, hasKnownWidthCheck } from '../../utils/container';
    import { hasGapSupport } from '../../utils/hasGapSupport';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import ContainerSeparators from './ContainerSeparators.svelte';
    import Unknown from '../utilities/Unknown.svelte';
    import Outer from '../utilities/Outer.svelte';
    import { correctContentAlignmentVertical } from '../../utils/correctContentAlignmentVertical';
    import { correctContentAlignmentHorizontal } from '../../utils/correctContentAlignmentHorizontal';
    import { Truthy } from '../../utils/truthy';

    export let json: Partial<DivContainerData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let childStore: Readable<ContainerChildInfo[]>;
    let orientation: ContainerOrientation = 'vertical';
    let contentVAlign: ContentAlignmentVertical = 'top';
    let contentHAlign: ContentAlignmentHorizontal = 'left';
    let separator: SeparatorStyle | null = null;
    let lineSeparator: SeparatorStyle | null = null;
    let aspect: number | undefined = undefined;
    let childLayoutParams: LayoutParams = {};

    $: if (json) {
        orientation = 'vertical';
        contentVAlign = 'top';
        contentHAlign = 'left';
        aspect = undefined;
    }

    $: jsonItems = json.items;

    $: jsonOrientation = rootCtx.getDerivedFromVars(json.orientation);
    $: jsonLayoutMode = rootCtx.getDerivedFromVars(json.layout_mode);
    $: jsonContentVAlign = rootCtx.getDerivedFromVars(json.content_alignment_vertical);
    $: jsonContentHAlign = rootCtx.getDerivedFromVars(json.content_alignment_horizontal);
    $: jsonSeparator = rootCtx.getDerivedFromVars(json.separator);
    $: jsonLineSeparator = rootCtx.getDerivedFromVars(json.line_separator);
    $: jsonAspect = rootCtx.getDerivedFromVars(json.aspect);
    $: jsonWidth = rootCtx.getDerivedFromVars(json.width);
    $: jsonHeight = rootCtx.getDerivedFromVars(json.height);

    function replaceItems(items: (DivBaseData | undefined)[]): void {
        json = {
            ...json,
            items: items.filter(Truthy)
        };
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

    $: {
        let children: Readable<ContainerChildInfo>[] = [];

        items.forEach(item => {
            children.push(
                rootCtx.getDerivedFromVars({
                    width: item.json.width,
                    height: item.json.height
                })
            );
        });

        // Create a new array every time so it is not equal to the previous one
        childStore = derived(children, val => [...val]);
    }

    $: {
        orientation = correctContainerOrientation($jsonOrientation, orientation);
    }

    $: wrap = $jsonLayoutMode === 'wrap';

    $: hasKnownWidth = orientation !== 'horizontal' && !wrap && $childStore.some(hasKnownWidthCheck);
    $: hasKnownHeight = orientation !== 'vertical' && !wrap && $childStore.some(hasKnownHeightCheck);

    $: {
        contentVAlign = correctContentAlignmentVertical($jsonContentVAlign, contentVAlign);
    }

    $: {
        contentHAlign = correctContentAlignmentHorizontal($jsonContentHAlign, contentHAlign);
    }

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

    $: {
        const newRatio = $jsonAspect?.ratio;
        if (newRatio && isPositiveNumber(newRatio)) {
            aspect = newRatio;
        } else {
            aspect = undefined;
        }
    }


    $: {
        let newChildLayoutParams: LayoutParams = {};

        if (layoutParams?.fakeElement) {
            newChildLayoutParams.fakeElement = true;
        }
        if (orientation === 'overlap') {
            newChildLayoutParams.overlapParent = true;
        }
        if (orientation !== 'horizontal') {
            newChildLayoutParams.parentHAlign = wrap ? 'start' : HALIGN_MAP[contentHAlign];
        }
        if (orientation !== 'vertical') {
            newChildLayoutParams.parentVAlign = wrap ? 'start' : VALIGN_MAP[contentVAlign];
        }
        if (
            !aspect && !hasKnownHeight && (
                !$jsonHeight ||
                $jsonHeight.type === 'wrap_content' ||
                $jsonHeight.type === 'match_parent' && layoutParams?.parentVerticalWrapContent
            )
        ) {
            newChildLayoutParams.parentVerticalWrapContent = true;
        }
        if (
            !hasKnownWidth && (
                $jsonWidth?.type === 'wrap_content' ||
                $jsonWidth?.type === 'match_parent' && layoutParams?.parentHorizontalWrapContent
            )
        ) {
            newChildLayoutParams.parentHorizontalWrapContent = true;
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

<Outer
    cls={genClassName('container', css, mods)}
    {style}
    {json}
    {origJson}
    {templateContext}
    {layoutParams}
    {additionalPaddings}
    heightByAspect={Boolean(aspect)}
    parentOf={jsonItems}
    {replaceItems}
>
    {#each items as item}
        <Unknown
            layoutParams={childLayoutParams}
            div={item.json}
            templateContext={item.templateContext}
            origJson={item.origJson}
        />
    {/each}

    {#if separator || lineSeparator}
        <ContainerSeparators
            {separator}
            {lineSeparator}
            {orientation}
            {contentHAlign}
            {contentVAlign}
        />
    {/if}
</Outer>
