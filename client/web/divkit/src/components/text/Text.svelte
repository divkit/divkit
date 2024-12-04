<script lang="ts">
    import { getContext, onDestroy } from 'svelte';

    import css from './Text.module.css';
    import rootCss from '../Root.module.css';

    import type { DivTextData, TextImage, TextRange, TextStyles, TextVerticalAlignment } from '../../types/text';
    import type { Style } from '../../types/general';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { AlignmentHorizontal } from '../../types/alignment';
    import type { Action } from '../../../typings/common';
    import type { TintMode } from '../../types/image';
    import type { MaybeMissing } from '../../expressions/json';
    import type { ComponentContext } from '../../types/componentContext';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import Outer from '../utilities/Outer.svelte';
    import TextRangeView from './TextRange.svelte';
    import { makeStyle } from '../../utils/makeStyle';
    import { pxToEm } from '../../utils/pxToEm';
    import { genClassName } from '../../utils/genClassName';
    import { getBackground } from '../../utils/background';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { correctAlignmentHorizontal } from '../../utils/correctAlignmentHorizontal';
    import { type AlignmentVerticalMapped, correctAlignmentVertical } from '../../utils/correctAlignmentVertical';
    import { correctColor } from '../../utils/correctColor';
    import { correctBooleanInt } from '../../utils/correctBooleanInt';
    import { propToString } from '../../utils/propToString';
    import { correctTintMode } from '../../utils/correctTintMode';
    import { filterEnabledActions } from '../../utils/filterEnabledActions';
    import { autoEllipsize } from '../../use/autoEllipsize';
    import { edgeInsertsToCss } from '../../utils/edgeInsertsToCss';
    import { correctEdgeInsertsObject } from '../../utils/correctEdgeInsertsObject';
    import { edgeInsertsMultiply } from '../../utils/edgeInsetsMultiply';
    import { wrapError } from '../../utils/wrapError';

    export let componentContext: ComponentContext<DivTextData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const direction = rootCtx.direction;

    let text = '';
    let fontSize = 12;
    let lineHeight = 1.25;
    let customLineHeight: number | null = null;
    let maxHeight = '';
    let maxLines: number | undefined;
    let lineClamp: string | number = '';
    let multiline = false;
    let halign: AlignmentHorizontal = 'start';
    let valign: AlignmentVerticalMapped = 'start';
    let rootTextColor = '';
    let focusTextColor = '';
    let gradient = '';
    let selectable = false;

    let renderList: ({
        text: string;
        textStyles: TextRange;
        actions?: MaybeMissing<Action[]>;
    } | {
        image: {
            url: string;
            width: string;
            height: string;
            wrapperStyle: Style;
            svgFilterId: string;
            preloadRequired: boolean;
            verticalAlign: TextVerticalAlignment | undefined;
        };
    })[] = [];
    let hasCloudBg = false;
    let usedTintColors: [string, TintMode][] = [];

    $: if (componentContext.json) {
        fontSize = 12;
        lineHeight = 1.25;
        customLineHeight = null;
        maxHeight = '';
        maxLines = undefined;
        lineClamp = '';
        multiline = false;
        halign = 'start';
        valign = 'start';
        rootTextColor = '';
        gradient = '';
        selectable = false;
    }

    $: jsonText = componentContext.getDerivedFromVars(componentContext.json.text);
    $: jsonRanges = componentContext.getDerivedFromVars(componentContext.json.ranges);
    $: jsonImages = componentContext.getDerivedFromVars(componentContext.json.images);
    $: jsonRootTextStyles = componentContext.getDerivedFromVars({
        font_size: componentContext.json.font_size,
        letter_spacing: componentContext.json.letter_spacing,
        font_weight: componentContext.json.font_weight,
        font_weight_value: componentContext.json.font_weight_value,
        font_family: componentContext.json.font_family,
        text_color: componentContext.json.text_color,
        underline: componentContext.json.underline,
        strike: componentContext.json.strike,
        line_height: componentContext.json.line_height,
        text_shadow: componentContext.json.text_shadow,
        font_feature_settings: componentContext.json.font_feature_settings
    });
    $: jsonTextSize = componentContext.getDerivedFromVars(componentContext.json.font_size);
    $: jsonLineHeight = componentContext.getDerivedFromVars(componentContext.json.line_height);
    $: jsonMaxLines = componentContext.getDerivedFromVars(componentContext.json.max_lines);
    $: jsonHAlign = componentContext.getDerivedFromVars(componentContext.json.text_alignment_horizontal);
    $: jsonVAlign = componentContext.getDerivedFromVars(componentContext.json.text_alignment_vertical);
    $: jsonTextColor = componentContext.getDerivedFromVars(componentContext.json.text_color);
    $: jsonFocusTextColor = componentContext.getDerivedFromVars(componentContext.json.focused_text_color);
    $: jsonTruncate = componentContext.getDerivedFromVars(componentContext.json.truncate);
    $: jsonTextGradient = componentContext.getDerivedFromVars(componentContext.json.text_gradient);
    $: jsonSelectable = componentContext.getDerivedFromVars(componentContext.json.selectable);
    $: jsonAutoEllipsize = componentContext.getDerivedFromVars(componentContext.json.auto_ellipsize);
    $: jsonPaddings = componentContext.getDerivedFromVars(componentContext.json.paddings);

    $: {
        if (typeof componentContext.json.text === 'string') {
            text = propToString($jsonText);
        } else {
            text = '';
            componentContext.logError(wrapError(new Error('Incorrect text value type')));
        }
    }

    $: {
        fontSize = correctPositiveNumber($jsonTextSize, fontSize);
    }

    $: {
        const newLineHeight = $jsonLineHeight;
        if (isPositiveNumber(newLineHeight)) {
            lineHeight = Number(newLineHeight) / fontSize;
            customLineHeight = lineHeight;
        } else {
            customLineHeight = null;
        }
    }

    $: singleline = $jsonMaxLines === 1;
    $: {
        let newMaxHeight = '';
        let newMaxLines: number | undefined;
        let newLineClamp: string | number = '';
        let newMultiline = false;

        if ($jsonMaxLines && $jsonMaxLines > 1) {
            const lines = Number($jsonMaxLines);

            newMaxHeight = lines * lineHeight + 'em';
            newMaxLines = lines;
            newLineClamp = lines;
            newMultiline = true;
        } else if ($jsonAutoEllipsize && $jsonMaxLines !== 1) {
            newMultiline = true;
        }

        maxHeight = newMaxHeight;
        maxLines = newMaxLines;
        lineClamp = newLineClamp;
        multiline = newMultiline;
    }

    $: {
        halign = correctAlignmentHorizontal($jsonHAlign, $direction, halign);
    }

    $: {
        valign = correctAlignmentVertical($jsonVAlign, valign);
    }

    $: isAllTextSameColor =
        !$jsonRanges ||
        (
            text && $jsonRanges.length === 1 && $jsonRanges[0] &&
            (
                !$jsonRanges[0].start || $jsonRanges[0].start === 0
            ) &&
            (
                !$jsonRanges[0].end ||
                typeof $jsonRanges[0].end === 'number' && $jsonRanges[0].end >= text.length
            )
        );


    $: isOnlyOneColorDefined = Boolean($jsonTextColor) !==
        Boolean($jsonRanges && $jsonRanges[0] && $jsonRanges[0].text_color);

    $: {
        let newRootTextColor = '';

        if ($jsonMaxLines && isAllTextSameColor && isOnlyOneColorDefined) {
            // Recolor the ellipsis only if the entire text have the same color
            newRootTextColor = correctColor(
                ($jsonTextColor || ($jsonRanges && $jsonRanges[0] && $jsonRanges[0].text_color)) as string,
                1,
                rootTextColor
            );
        }

        rootTextColor = newRootTextColor;
    }

    $: {
        focusTextColor = correctColor($jsonFocusTextColor, 1, focusTextColor);
    }

    $: truncate = $jsonTruncate === 'none' ? 'none' : '';

    $: {
        let newGradient = '';

        if ($jsonTextGradient) {
            const bg = getBackground([$jsonTextGradient]);
            if (bg.image) {
                newGradient = bg.image;
            }
        }

        gradient = newGradient;
    }

    $: {
        selectable = correctBooleanInt($jsonSelectable, selectable);
    }

    function updateRenderList(
        text: string,
        textRanges: MaybeMissing<TextRange[]> | undefined,
        textImages: MaybeMissing<TextImage[]> | undefined,
        rootTextStyles: typeof $jsonRootTextStyles
    ) {
        let newRenderList: typeof renderList = [];

        usedTintColors.forEach(([color, mode]) => {
            rootCtx.removeSvgFilter(color, mode);
        });
        usedTintColors = [];

        if (!(
            Array.isArray(textRanges) && textRanges.length ||
            Array.isArray(textImages) && textImages.length && text
        )) {
            renderList = [];
            return;
        }

        const content = text;
        let ranges = textRanges || [
            {
                start: 0,
                end: content.length
            }
        ];
        let images = textImages || [];
        let prevIndex = 0;
        let activeRanges: MaybeMissing<TextRange>[] = [];
        let list: ({
            index: number;
            range: MaybeMissing<TextRange> & {
                start: number;
                end: number;
            };
            type: 'rangeStart' | 'rangeEnd';
            isStart?: boolean;
        } | {
            index: number;
            type: 'image';
            arrayIndex: number;
            image: MaybeMissing<TextImage> & {
                start: number;
                url: string;
            };
        })[] = [];

        ranges.forEach(range => {
            const rangeStart = range.start || 0;
            const rangeEnd = range.end || text.length;
            const rangeWithExplicitProps = {
                top_offset: 0,
                ...range,
                start: rangeStart,
                end: rangeEnd
            };
            list.push({
                index: rangeStart,
                range: rangeWithExplicitProps as typeof range & {
                    start: number;
                    end: number;
                },
                type: 'rangeStart',
                isStart: true
            });
            list.push({
                index: rangeEnd,
                range: rangeWithExplicitProps as typeof range & {
                    start: number;
                    end: number;
                },
                type: 'rangeEnd'
            });
        });
        images.forEach((image, index) => {
            if (image.start !== undefined && image.url && image.start <= content.length) {
                list.push({
                    index: image.indexing_direction === 'reversed' ? text.length - image.start : image.start,
                    image: image as typeof image & {
                        start: number;
                        url: string;
                    },
                    type: 'image',
                    arrayIndex: index
                });
            }
        });
        list.sort((a, b) => {
            if (a.index === b.index) {
                if (a.type !== b.type) {
                    if (a.type === 'image') {
                        return -1;
                    } else if (b.type === 'image') {
                        return 1;
                    }

                    return a.type < b.type ? -1 : 1;
                } else if (a.type === 'image' && b.type === 'image') {
                    return b.arrayIndex - a.arrayIndex;
                } else if (a.type === 'rangeStart' && b.type === 'rangeStart') {
                    return a.range.end - b.range.end;
                } else if (a.type === 'rangeStart') {
                    return 1;
                } else if (b.type === 'rangeStart') {
                    return -1;
                }
                if (a.type !== 'image' && b.type !== 'image') {
                    return a.range.start - b.range.start;
                }
                return 0;
            }

            return a.index - b.index;
        });
        list.forEach(item => {
            let range = item.type === 'image' ? null : item.range;
            let index = item.index;

            if (index > prevIndex) {
                let textStyles = Object.assign({ ...rootTextStyles }, ...activeRanges as any[]) as TextRange;
                if (activeRanges.length && activeRanges[activeRanges.length - 1].start !== prevIndex) {
                    textStyles.top_offset = 0;
                }
                newRenderList.push({
                    text: content.substring(prevIndex, index),
                    textStyles,
                    actions: item.type === 'rangeEnd' && item.range?.actions?.filter(filterEnabledActions) || undefined
                });
            }

            if (item.type === 'rangeStart' && range) {
                activeRanges.push(range);
            } else if (item.type === 'rangeEnd') {
                activeRanges = activeRanges.filter(range => range !== item.range);
            } else if (item.type === 'image') {
                let textStyles2 = Object.assign({ ...rootTextStyles }, ...activeRanges as any[]) as TextStyles;
                let imageWidth = pxToEm(
                    (((item.image.width && item.image.width.value) || 20) * 10) / (textStyles2.font_size || 12)
                );
                let imageHeight = pxToEm(
                    (((item.image.height && item.image.height.value) || 20) * 10) / (textStyles2.font_size || 12)
                );
                const wrapperStyle: Style = {
                    'font-size': pxToEm(((Number(textStyles2.font_size) || 12) * 10) / fontSize)
                };

                let svgFilterId = '';
                const tintColor = item.image.tint_color;
                const tintMode = correctTintMode(item.image.tint_mode, 'source_in');
                if (tintColor) {
                    const color = correctColor(item.image.tint_color);
                    svgFilterId = rootCtx.addSvgFilter(color, tintMode);
                    usedTintColors.push([color, tintMode]);
                }

                newRenderList.push({
                    image: {
                        url: item.image.url,
                        width: imageWidth,
                        height: imageHeight,
                        wrapperStyle,
                        svgFilterId,
                        preloadRequired: Boolean(item.image.preload_required),
                        verticalAlign: item.image.alignment_vertical
                    }
                });
            }

            prevIndex = index;
        });

        if (prevIndex < content.length) {
            newRenderList.push({
                text: content.substring(prevIndex),
                textStyles: { ...rootTextStyles } as TextStyles
            });
        }

        renderList = newRenderList;
        hasCloudBg = newRenderList.some(it => 'text' in it && it.textStyles.background?.type === 'cloud');
    }

    $: updateRenderList(text, $jsonRanges, $jsonImages, $jsonRootTextStyles);

    $: mods = {
        singleline,
        multiline,
        halign,
        valign,
        truncate,
        'has-focus-color': Boolean(focusTextColor)
    };

    $: innerMods = {
        gradient: Boolean(gradient),
        'has-cloud-bg': hasCloudBg
    };

    $: style = {
        'font-size': pxToEm(fontSize),
        'line-height': lineHeight,
        'max-height': maxHeight,
        '-webkit-line-clamp': lineClamp,
        color: rootTextColor,
        'background-image': gradient,
        '--divkit-text-focus-color': focusTextColor
    };

    $: cloudPadding = edgeInsertsToCss(
        edgeInsertsMultiply(correctEdgeInsertsObject($jsonPaddings, {}) || {}, 10 / fontSize),
        $direction
    );

    function onImgError(event: Event): void {
        if (event.target && 'classList' in event.target) {
            (event.target as HTMLElement).classList.add(css.text__image_hidden);
        }
    }

    onDestroy(() => {
        usedTintColors.forEach(([color, mode]) => {
            rootCtx.removeSvgFilter(color, mode);
        });
    });
</script>

<Outer
    cls="{genClassName('text', css, mods)} {selectable ? '' : rootCss.root__unselectable}"
    {componentContext}
    {layoutParams}
>
    {#if hasCloudBg}
        <span
            class={genClassName('text__inner', css, {
                ...innerMods,
                'cloud-bg': true
            })}
            style={makeStyle({
                ...style,
                padding: cloudPadding
            })}
        >
            {#each renderList as item}
                {#if 'text' in item}
                    {#if item.text}
                        <TextRangeView
                            {componentContext}
                            text={item.text}
                            rootFontSize={fontSize}
                            textStyles={item.textStyles}
                            {singleline}
                            cloudBg
                        />
                    {/if}
                {:else if item.image}
                    <span style={makeStyle(item.image.wrapperStyle)}><span class={genClassName('text__image-wrapper', css, {
                        align: item.image.verticalAlign,
                        crop: customLineHeight !== null
                    })} style={makeStyle({
                        width: item.image.width,
                        height: (customLineHeight && item.image.verticalAlign !== 'baseline') ? customLineHeight + 'em' : undefined
                    })}></span></span>
                {/if}
            {/each}
        </span>
    {/if}
    <span
        class={genClassName('text__inner', css, innerMods)}
        style={makeStyle(style)}
        use:autoEllipsize={{
            enabled: $jsonAutoEllipsize,
            lineClamp: typeof lineClamp === 'number' ? lineClamp : undefined,
            maxLines
        }}
    >
        {#if renderList.length}
            {#each renderList as item}
                {#if 'text' in item}
                    {#if item.text}
                        <TextRangeView
                            {componentContext}
                            text={item.text}
                            rootFontSize={fontSize}
                            textStyles={item.textStyles}
                            {singleline}
                            actions={item.actions}
                        />
                    {/if}
                {:else if item.image}
                    <span style={makeStyle(item.image.wrapperStyle)}><span class={genClassName('text__image-wrapper', css, {
                        align: item.image.verticalAlign,
                        crop: customLineHeight !== null
                    })} style={makeStyle({
                        width: item.image.width,
                        height: (customLineHeight && item.image.verticalAlign !== 'baseline') ? customLineHeight + 'em' : undefined
                    })}><img
                        class={css.text__image}
                        src={item.image.url}
                        loading={item.image.preloadRequired ? 'eager' : 'lazy'}
                        decoding="async"
                        aria-hidden="true"
                        alt=""
                        style={makeStyle({
                            height: item.image.height,
                            filter: item.image.svgFilterId ? `url(#${item.image.svgFilterId})` : undefined
                        })}
                        on:error={onImgError}
                    ></span></span>
                {/if}
            {/each}
        {:else}
            <TextRangeView
                {componentContext}
                {text}
                rootFontSize={fontSize}
                textStyles={$jsonRootTextStyles}
                {singleline}
            />
        {/if}
    </span>
</Outer>
