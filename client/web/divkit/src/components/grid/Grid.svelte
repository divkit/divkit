<script lang="ts">
    import { getContext } from 'svelte';
    import { derived, Readable } from 'svelte/store';

    import css from './Grid.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivGridData } from '../../types/grid';
    import type { DivBaseData } from '../../types/base';
    import type { Size } from '../../types/sizes';
    import type { AlignmentHorizontal } from '../../types/alignment';
    import type { MaybeMissing } from '../../expressions/json';
    import type { ComponentContext } from '../../types/componentContext';
    import Outer from '../utilities/Outer.svelte';
    import Unknown from '../utilities/Unknown.svelte';
    import { genClassName } from '../../utils/genClassName';
    import { gridCalcTemplates } from '../../utils/gridCalcTemplates';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { type AlignmentVerticalMapped, correctAlignmentVertical } from '../../utils/correctAlignmentVertical';
    import { correctAlignmentHorizontal } from '../../utils/correctAlignmentHorizontal';
    import { Truthy } from '../../utils/truthy';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';

    export let componentContext: ComponentContext<DivGridData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const direction = rootCtx.direction;

    let columnCount = 1;
    let childStore: Readable<ChildInfo[]>;
    let resultItems: {
        componentContext: ComponentContext;
        layoutParams: LayoutParams;
    }[];
    let columnsWeight: number[] = [];
    let rowsWeight: number[] = [];
    let columnsMinWidth: number[] = [];
    let rowsMinHeight: number[] = [];
    let rowCount = 0;
    let contentVAlign: AlignmentVerticalMapped = 'start';
    let contentHAlign: AlignmentHorizontal = 'start';

    $: origJson = componentContext.origJson;

    function rebind(): void {
        columnCount = 1;
        contentVAlign = 'start';
        contentHAlign = 'start';
    }

    $: if (origJson) {
        rebind();
    }

    $: jsonItems = Array.isArray(componentContext.json.items) && componentContext.json.items || [];

    $: jsonColumnCount = componentContext.getDerivedFromVars(componentContext.json.column_count);
    $: jsonContentVAlign = componentContext.getDerivedFromVars(componentContext.json.content_alignment_vertical);
    $: jsonContentHAlign = componentContext.getDerivedFromVars(componentContext.json.content_alignment_horizontal);

    $: {
        columnCount = correctPositiveNumber($jsonColumnCount, columnCount);
    }

    $: items = jsonItems.map((item, index) => {
        return componentContext.produceChildContext(item, {
            path: index
        });
    });

    function replaceItems(items: (MaybeMissing<DivBaseData> | undefined)[]): void {
        componentContext = {
            ...componentContext,
            json: {
                ...componentContext.json,
                items: items.filter(Truthy)
            }
        };
    }

    interface ChildInfo {
        rowSpan: number | undefined;
        columnSpan: number | undefined;
        width: MaybeMissing<Size> | undefined;
        height: MaybeMissing<Size> | undefined;
    }
    $: {
        let children: Readable<ChildInfo>[] = [];

        items.forEach(item => {
            children.push(
                componentContext.getDerivedFromVars({
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

    $: {
        const used: Record<string, boolean> = {};
        let x = 0;
        let y = 0;
        columnsWeight = [];
        rowsWeight = [];
        columnsMinWidth = [];
        rowsMinHeight = [];

        resultItems = items.map((item, index) => {
            const childInfo = $childStore[index];
            const colSpan = Math.min(columnCount, Number(childInfo.columnSpan) || 1);
            const rowSpan = Number(childInfo.rowSpan) || 1;

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

            // eslint-disable-next-line no-constant-condition
            while (true) {
                let isFree = true;
                OUTER: for (let i = x; i < x + colSpan; ++i) {
                    for (let j = y; j < y + rowSpan; ++j) {
                        if (used[i + '_' + j]) {
                            isFree = false;
                            break OUTER;
                        }
                    }
                }

                if (isFree) {
                    break;
                }

                ++x;

                if (x > columnCount - colSpan) {
                    x = 0;
                    ++y;
                }
            }

            const gridArea = {
                x,
                y,
                colSpan,
                rowSpan
            };

            for (let i = x; i < x + colSpan; ++i) {
                for (let j = y; j < y + rowSpan; ++j) {
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

            return {
                componentContext: item,
                layoutParams: {
                    gridArea
                }
            };
        });

        rowCount = y + 1;
    }

    $: {
        contentVAlign = correctAlignmentVertical($jsonContentVAlign, contentVAlign);
    }

    $: {
        contentHAlign = correctAlignmentHorizontal($jsonContentHAlign, $direction, contentHAlign);
    }

    $: mods = {
        valign: contentVAlign,
        halign: contentHAlign
    };

    $: style = {
        'grid-template-columns': gridCalcTemplates(columnsWeight, columnsMinWidth, columnCount),
        'grid-template-rows': gridCalcTemplates(rowsWeight, rowsMinHeight, rowCount)
    };
</script>

<Outer
    cls={genClassName('grid', css, mods)}
    {componentContext}
    {style}
    {layoutParams}
    parentOf={items}
    {replaceItems}
>
    {#each resultItems as item}
        <Unknown
            componentContext={item.componentContext}
            layoutParams={item.layoutParams}
        />
    {/each}
</Outer>
