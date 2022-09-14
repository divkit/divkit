<script lang="ts">
    import { getContext } from 'svelte';
    import { derived, Readable } from 'svelte/store';

    import css from './Grid.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivGridData } from '../../types/grid';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivBaseData } from '../../types/base';
    import type { Size } from '../../types/sizes';
    import type { AlignmentHorizontal, AlignmentVertical } from '../../types/alignment';
    import type { MaybeMissing } from '../../expressions/json';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import Outer from '../utilities/Outer.svelte';
    import Unknown from '../utilities/Unknown.svelte';
    import { wrapError } from '../../utils/wrapError';
    import { genClassName } from '../../utils/genClassName';
    import { gridCalcTemplates } from '../../utils/gridCalcTemplates';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { correctAlignmentVertical } from '../../utils/correctAlignmentVertical';
    import { correctAlignmentHorizontal } from '../../utils/correctAlignmentHorizontal';

    export let json: Partial<DivGridData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let hasItemsError = false;
    $: jsonItems = json.items;
    $: {
        if (!jsonItems?.length || !Array.isArray(jsonItems)) {
            hasItemsError = true;
            rootCtx.logError(wrapError(new Error('Incorrect or empty "items" prop for div "grid"')));
        } else {
            hasItemsError = false;
        }
    }


    let columnCount = 1;
    $: jsonColumnCount = rootCtx.getDerivedFromVars(json.column_count);
    $: {
        columnCount = correctPositiveNumber($jsonColumnCount, columnCount);
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

    interface ChildInfo {
        rowSpan: number | undefined;
        columnSpan: number | undefined;
        width: MaybeMissing<Size> | undefined;
        height: MaybeMissing<Size> | undefined;
    }
    let childStore: Readable<ChildInfo[]>;
    $: {
        let children: Readable<ChildInfo>[] = [];

        items.forEach(item => {
            children.push(
                rootCtx.getDerivedFromVars({
                    rowSpan: item.json.row_span,
                    columnSpan: item.json.column_span,
                    width: item.json.width,
                    height: item.json.height
                })
            );
        });

        // Create a new array every time so that it is not equal to the previous one
        childStore = derived(children, val => [...val]);
    }

    let resultItems: {
        json: DivBaseData;
        templateContext: TemplateContext;
        origJson: DivBaseData;
        layoutParams: LayoutParams;
    }[];
    let columnsWeight: number[] = [];
    let rowsWeight: number[] = [];
    let columnsMinWidth: number[] = [];
    let rowsMinHeight: number[] = [];
    let rowCount = 0;
    let hasLayoutError = false;
    $: {
        const used: Record<string, boolean> = {};
        let x = 0;
        let y = 0;
        columnsWeight = [];
        rowsWeight = [];
        columnsMinWidth = [];
        rowsMinHeight = [];
        hasLayoutError = false;

        resultItems = items.map((item, index) => {
            const childInfo = $childStore[index];
            const colSpan = Number(childInfo.columnSpan) || 1;
            const rowSpan = Number(childInfo.rowSpan) || 1;

            const gridArea = {
                x,
                y,
                colSpan,
                rowSpan
            };

            const widthWeight =
                childInfo.width?.type === 'match_parent' ?
                    Number(childInfo.width.weight || 1) / colSpan :
                    0;
            const heightWeight =
                childInfo.height?.type === 'match_parent' ?
                    Number(childInfo.height.weight || 1) / rowSpan :
                    0;
            const widthMin =
                colSpan === 1 && childInfo.width?.type === 'fixed' && childInfo.width.value ?
                    Number(childInfo.width.value) / colSpan :
                    0;
            const heightMin =
                rowSpan === 1 && childInfo.height?.type === 'fixed' && childInfo.height.value ?
                    Number(childInfo.height.value) / rowSpan :
                    0;

            for (let i = x; i < x + colSpan; ++i) {
                for (let j = y; j < y + rowSpan; ++j) {
                    if (used[i + '_' + j]) {
                        hasLayoutError = true;
                        continue;
                    }

                    used[i + '_' + j] = true;

                    if (!columnsWeight[i] || columnsWeight[i] < widthWeight) {
                        columnsWeight[i] = widthWeight;
                    }

                    if (!rowsWeight[j] || rowsWeight[j] < heightWeight) {
                        rowsWeight[j] = heightWeight;
                    }

                    if (!columnsMinWidth[i] || columnsMinWidth[i] < widthMin) {
                        columnsMinWidth[i] = widthMin;
                    }

                    if (!rowsMinHeight[j] || rowsMinHeight[j] < heightMin) {
                        rowsMinHeight[j] = heightMin;
                    }
                }
            }

            do {
                ++x;

                if (x >= columnCount) {
                    x = 0;
                    ++y;
                }
            } while (used[x + '_' + y]);

            return {
                ...item,
                layoutParams: {
                    gridArea
                }
            };
        });

        if (x !== 0) {
            hasLayoutError = true;
        }

        if (hasLayoutError) {
            rootCtx.logError(wrapError(new Error('Mismatching "grid" rows/columns')));
        }

        rowCount = y;
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

    $: mods = {
        valign: contentVAlign,
        halign: contentHAlign
    };

    $: style = {
        'grid-template-columns': gridCalcTemplates(columnsWeight, columnsMinWidth, columnCount),
        'grid-template-rows': gridCalcTemplates(rowsWeight, rowsMinHeight, rowCount)
    };

    $: hasError = hasItemsError || hasLayoutError;
</script>

{#if !hasError}
    <Outer
        cls={genClassName('grid', css, mods)}
        {json}
        {origJson}
        {templateContext}
        {style}
        {layoutParams}
    >
        {#each resultItems as item}
            <Unknown layoutParams={item.layoutParams} div={item.json} templateContext={item.templateContext} origJson={item.origJson} />
        {/each}
    </Outer>
{/if}
