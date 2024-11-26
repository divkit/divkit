<script lang="ts" context="module">
    const HALIGN_MAP = {
        start: 'start',
        center: 'center',
        end: 'end',
        // 'space-*' values doesn't supported for cross-axis in wrap-container
        'space-between': 'start',
        'space-around': 'start',
        'space-evenly': 'start'
    } as const;

    const VALIGN_MAP = {
        start: 'start',
        center: 'center',
        end: 'end',
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
    import { getContext, onDestroy } from 'svelte';
    import { type Readable, derived } from 'svelte/store';

    import css from './Container.module.css';

    import type { ContainerOrientation, DivContainerData } from '../../types/container';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBaseData } from '../../types/base';
    import type { ContainerChildInfo, SeparatorStyle } from '../../utils/container';
    import type { Variable } from '../../expressions/variable';
    import type { ComponentContext } from '../../types/componentContext';
    import type { MaybeMissing } from '../../expressions/json';
    import { prepareMargins } from '../../utils/container';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { genClassName } from '../../utils/genClassName';
    import { correctContainerOrientation } from '../../utils/correctContainerOrientation';
    import { correctDrawableStyle, type DrawableStyle } from '../../utils/correctDrawableStyles';
    import { calcAdditionalPaddings, calcItemsGap, isHeightMatchParent, isWidthMatchParent } from '../../utils/container';
    import { hasGapSupport } from '../../utils/hasGapSupport';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { type ContentAlignmentVerticalMapped, correctContentAlignmentVertical } from '../../utils/correctContentAlignmentVertical';
    import { type ContentAlignmentHorizontalMapped, correctContentAlignmentHorizontal } from '../../utils/correctContentAlignmentHorizontal';
    import { Truthy } from '../../utils/truthy';
    import { assignIfDifferent } from '../../utils/assignIfDifferent';
    import { constStore } from '../../utils/constStore';
    import { getItemsFromItemBuilder } from '../../utils/itemBuilder';
    import ContainerSeparators from './ContainerSeparators.svelte';
    import Unknown from '../utilities/Unknown.svelte';
    import Outer from '../utilities/Outer.svelte';

    export let componentContext: ComponentContext<DivContainerData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const direction = rootCtx.direction;

    let childStore: Readable<ContainerChildInfo[]>;
    let orientation: ContainerOrientation = 'vertical';
    let contentVAlign: ContentAlignmentVerticalMapped = 'start';
    let contentHAlign: ContentAlignmentHorizontalMapped = 'start';
    let separator: SeparatorStyle | null = null;
    let lineSeparator: SeparatorStyle | null = null;
    let aspect: number | undefined = undefined;
    let childLayoutParams: LayoutParams = {};

    $: origJson = componentContext.origJson;

    function rebind(): void {
        orientation = 'vertical';
        contentVAlign = 'start';
        contentHAlign = 'start';
        aspect = undefined;
    }

    $: if (origJson) {
        rebind();
    }

    $: jsonItems = componentContext.json.items;
    // eslint-disable-next-line no-nested-ternary
    $: jsonItemBuilderData = typeof componentContext.json.item_builder?.data === 'string' ? componentContext.getDerivedFromVars(
        componentContext.json.item_builder?.data, undefined, true
    ) : (componentContext.json.item_builder?.data ? constStore(componentContext.json.item_builder.data) : undefined);

    $: jsonOrientation = componentContext.getDerivedFromVars(componentContext.json.orientation);
    $: jsonLayoutMode = componentContext.getDerivedFromVars(componentContext.json.layout_mode);
    $: jsonContentVAlign = componentContext.getDerivedFromVars(componentContext.json.content_alignment_vertical);
    $: jsonContentHAlign = componentContext.getDerivedFromVars(componentContext.json.content_alignment_horizontal);
    $: jsonSeparator = componentContext.getDerivedFromVars(componentContext.json.separator);
    $: jsonLineSeparator = componentContext.getDerivedFromVars(componentContext.json.line_separator);
    $: jsonAspect = componentContext.getDerivedFromVars(componentContext.json.aspect);
    $: jsonWidth = componentContext.getDerivedFromVars(componentContext.json.width);
    $: jsonHeight = componentContext.getDerivedFromVars(componentContext.json.height);
    $: jsonClipToBounds = componentContext.getDerivedFromVars(componentContext.json.clip_to_bounds);

    function replaceItems(items: (MaybeMissing<DivBaseData> | undefined)[]): void {
        componentContext = {
            ...componentContext,
            json: {
                ...componentContext.json,
                items: items.filter(Truthy)
            }
        };
    }

    let items: ComponentContext[] = [];

    $: {
        let newItems: {
            div: MaybeMissing<DivBaseData>;
            id?: string | undefined;
            vars?: Map<string, Variable> | undefined;
        }[] = [];
        if (
            componentContext.json.item_builder &&
            Array.isArray($jsonItemBuilderData) &&
            Array.isArray(componentContext.json.item_builder.prototypes)
        ) {
            const builder = componentContext.json.item_builder;
            newItems = getItemsFromItemBuilder($jsonItemBuilderData, rootCtx, componentContext, builder);
        } else {
            newItems = (Array.isArray(jsonItems) && jsonItems || []).map(it => {
                return {
                    div: it
                };
            });
        }

        items.forEach(context => {
            context.destroy();
        });

        items = newItems.map((item, index) => {
            return componentContext.produceChildContext(item.div, {
                path: index,
                variables: item.vars,
                id: item.id
            });
        });
    }

    $: {
        let children: Readable<ContainerChildInfo>[] = [];

        items.forEach(item => {
            children.push(
                componentContext.getDerivedFromVars({
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

    $: supportWidthWrapContent = orientation !== 'horizontal' && !wrap;
    $: supportHeightWrapContent = orientation !== 'vertical' && !wrap;

    $: stretchWidth = orientation === 'overlap' && !$childStore.every(isWidthMatchParent);
    $: stretchHeight = orientation === 'overlap' && !$childStore.every(isHeightMatchParent);

    $: {
        contentVAlign = correctContentAlignmentVertical($jsonContentVAlign, contentVAlign);
    }

    $: {
        contentHAlign = correctContentAlignmentHorizontal($jsonContentHAlign, $direction, contentHAlign);
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

        if (orientation === 'overlap') {
            newChildLayoutParams.overlapParent = true;
        }
        if (orientation !== 'horizontal') {
            newChildLayoutParams.parentHAlign = wrap ? 'start' : HALIGN_MAP[contentHAlign];
        }
        if (orientation !== 'vertical') {
            newChildLayoutParams.parentVAlign = wrap ? 'start' : VALIGN_MAP[contentVAlign];
        }
        const isWidthWrapContent = (
            $jsonWidth?.type === 'wrap_content' ||
            $jsonWidth?.type === 'match_parent' && layoutParams?.parentHorizontalWrapContent
        );
        const isHeightWrapContent = (
            !$jsonHeight ||
            $jsonHeight.type === 'wrap_content' ||
            $jsonHeight.type === 'match_parent' && layoutParams?.parentVerticalWrapContent
        );
        if (!supportWidthWrapContent && isWidthWrapContent) {
            newChildLayoutParams.parentHorizontalWrapContent = true;
        }
        if (!aspect && !supportHeightWrapContent && isHeightWrapContent) {
            newChildLayoutParams.parentVerticalWrapContent = true;
        }
        newChildLayoutParams.stretchWidth = stretchWidth;
        newChildLayoutParams.stretchHeight = stretchHeight;
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
        wrap,
        overflow: ($jsonClipToBounds === false || $jsonClipToBounds === 0) ? 'visible' : undefined
    };

    $: style = {
        gap: (separator || lineSeparator) ?
            calcItemsGap(orientation, separator, lineSeparator) :
            undefined,
        'aspect-ratio': aspect
    };

    onDestroy(() => {
        items.forEach(context => {
            context.destroy();
        });
    });
</script>

<Outer
    cls={genClassName('container', css, mods)}
    {componentContext}
    {layoutParams}
    {style}
    {additionalPaddings}
    heightByAspect={Boolean(aspect)}
    parentOf={items}
    {replaceItems}
>
    {#each items as item}
        <Unknown
            componentContext={item}
            layoutParams={childLayoutParams}
        />
    {/each}

    {#if separator || lineSeparator}
        <ContainerSeparators
            direction={$direction}
            {separator}
            {lineSeparator}
            {orientation}
            {contentHAlign}
            {contentVAlign}
        />
    {/if}
</Outer>
