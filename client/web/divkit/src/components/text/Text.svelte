<script lang="ts">
    import { getContext, onDestroy } from 'svelte';

    import css from './Text.module.css';
    import rootCss from '../Root.module.css';

    import type { DivTextData, TextImage, TextRange, TextStyles } from '../../types/text';
    import type { Style } from '../../types/general';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { Action, DivBase, TemplateContext } from '../../../typings/common';
    import type { AlignmentHorizontal, AlignmentVertical } from '../../types/alignment';
    import type { MaybeMissing } from '../../expressions/json';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import Outer from '../utilities/Outer.svelte';
    import TextRangeView from './TextRange.svelte';
    import { makeStyle } from '../../utils/makeStyle';
    import { pxToEm } from '../../utils/pxToEm';
    import { wrapError } from '../../utils/wrapError';
    import { genClassName } from '../../utils/genClassName';
    import { getBackground } from '../../utils/background';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { correctAlignmentHorizontal } from '../../utils/correctAlignmentHorizontal';
    import { correctAlignmentVertical } from '../../utils/correctAlignmentVertical';
    import { correctColor } from '../../utils/correctColor';
    import { correctBooleanInt } from '../../utils/correctBooleanInt';
    import { propToString } from '../../utils/propToString';

    export let json: Partial<DivTextData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let hasError = false;
    $: jsonText = rootCtx.getDerivedFromVars(json.text);
    let text = '';
    $: {
        text = propToString($jsonText, text);
        if (!text) {
            hasError = true;
            rootCtx.logError(wrapError(new Error('Missing "text" prop for div "text"')));
        } else {
            hasError = false;
        }
    }
    $: jsonRanges = rootCtx.getDerivedFromVars(json.ranges);
    $: jsonImages = rootCtx.getDerivedFromVars(json.images);

    $: jsonRootTextStyles = rootCtx.getDerivedFromVars({
        font_size: json.font_size,
        letter_spacing: json.letter_spacing,
        font_weight: json.font_weight,
        text_color: json.text_color,
        underline: json.underline,
        strike: json.strike,
        line_height: json.line_height
    });

    $: jsonTextSize = rootCtx.getDerivedFromVars(json.font_size);
    let fontSize = 12;
    $: {
        fontSize = correctPositiveNumber($jsonTextSize, fontSize);
    }

    $: jsonLineHeight = rootCtx.getDerivedFromVars(json.line_height);
    let lineHeight = 1.25;
    let customLineHeight = false;
    $: {
        const newLineHeight = $jsonLineHeight;
        if (isPositiveNumber(newLineHeight)) {
            lineHeight = Number(newLineHeight) / fontSize;
            customLineHeight = true;
        } else {
            customLineHeight = false;
        }
    }

    $: jsonMaxLines = rootCtx.getDerivedFromVars(json.max_lines);
    $: singleline = $jsonMaxLines === 1;
    let maxHeight = '';
    let lineClamp: string | number = '';
    let multiline = false;
    $: {
        let newMaxHeight = '';
        let newLineClamp: string | number = '';
        let newMultiline = false;

        if ($jsonMaxLines && $jsonMaxLines > 1) {
            let lines = Number($jsonMaxLines);

            newMaxHeight = lines * lineHeight + 'em';

            // multiline overflow enabled only if there is no images and ranges
            if (!$jsonRanges && !$jsonImages) {
                newLineClamp = lines;
                newMultiline = true;
            }
        }

        maxHeight = newMaxHeight;
        lineClamp = newLineClamp;
        multiline = newMultiline;
    }

    let halign: AlignmentHorizontal = 'left';
    $: jsonHAlign = rootCtx.getDerivedFromVars(json.text_alignment_horizontal);
    $: {
        halign = correctAlignmentHorizontal($jsonHAlign, halign);
    }

    let valign: AlignmentVertical = 'top';
    $: jsonVAlign = rootCtx.getDerivedFromVars(json.text_alignment_vertical);
    $: {
        valign = correctAlignmentVertical($jsonVAlign, valign);
    }

    $: isAllTextSameColor =
        !$jsonRanges ||
        (
            text && $jsonRanges.length === 1 && $jsonRanges[0] &&
            $jsonRanges[0].start === 0 && typeof $jsonRanges[0].end === 'number' && $jsonRanges[0].end >= text.length
        );

    $: jsonTextColor = rootCtx.getDerivedFromVars(json.text_color);

    $: isOnlyOneColorDefined = Boolean($jsonTextColor) !==
        Boolean($jsonRanges && $jsonRanges[0] && $jsonRanges[0].text_color);

    let rootTextColor = '';
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

    $: jsonTruncate = rootCtx.getDerivedFromVars(json.truncate);
    $: truncate = $jsonTruncate === 'none' ? 'none' : '';

    $: jsonTextGradient = rootCtx.getDerivedFromVars(json.text_gradient);
    let gradient = '';
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

    $: jsonSelectable = rootCtx.getDerivedFromVars(json.selectable);
    let selectable = false;
    $: {
        selectable = correctBooleanInt($jsonSelectable, selectable);
    }

    $: jsonWidth = rootCtx.getDerivedFromVars(json.width);
    $: jsonHeight = rootCtx.getDerivedFromVars(json.height);

    let renderList: ({
        text: string;
        textStyles: TextStyles;
        actions?: MaybeMissing<Action[]>;
    } | {
        image: {
            url: string;
            width: string;
            height: string;
            wrapperStyle: Style;
            svgFilterId: string;
        };
    })[] = [];
    let usedTintColors: string[] = [];

    function updateRenderList(
        text: string,
        textRanges: MaybeMissing<TextRange[]> | undefined,
        textImages: MaybeMissing<TextImage[]> | undefined
    ) {
        let newRenderList: typeof renderList = [];

        usedTintColors.forEach(color => {
            rootCtx.removeSvgFilter(color);
        });
        usedTintColors = [];

        if (!(Array.isArray(textRanges) && textRanges.length || Array.isArray(textImages) && textImages.length && text)) {
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
        let activeRanges: TextStyles[] = [];
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
            if (range.start !== undefined && range.end !== undefined) {
                list.push({
                    index: range.start,
                    range: range as typeof range & {
                        start: number;
                        end: number;
                    },
                    type: 'rangeStart',
                    isStart: true
                });
                list.push({
                    index: range.end,
                    range: range as typeof range & {
                        start: number;
                        end: number;
                    },
                    type: 'rangeEnd'
                });
            }
        });
        images.forEach((image, index) => {
            if (image.start !== undefined && image.url && image.start <= content.length) {
                list.push({
                    index: image.start,
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
                let textStyles = Object.assign({ ...$jsonRootTextStyles }, ...activeRanges as any[]) as TextStyles;
                newRenderList.push({
                    text: content.substring(prevIndex, index),
                    textStyles,
                    actions: item.type === 'rangeEnd' && item.range?.actions || undefined
                });
            }

            if (item.type === 'rangeStart' && range) {
                activeRanges.push(range);
            } else if (item.type === 'rangeEnd') {
                activeRanges = activeRanges.filter(range => range !== item.range);
            } else if (item.type === 'image') {
                let textStyles2 = Object.assign({ ...$jsonRootTextStyles }, ...activeRanges as any[]) as TextStyles;
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
                if (item.image.tint_color) {
                    const color = correctColor(item.image.tint_color);
                    svgFilterId = rootCtx.addSvgFilter(color);
                    usedTintColors.push(color);
                }

                newRenderList.push({
                    image: {
                        url: item.image.url,
                        width: imageWidth,
                        height: imageHeight,
                        wrapperStyle,
                        svgFilterId
                    }
                });
            }

            prevIndex = index;
        });

        if (prevIndex < content.length) {
            newRenderList.push({
                text: content.substring(prevIndex),
                textStyles: { ...$jsonRootTextStyles } as TextStyles
            });
        }

        renderList = newRenderList;
    }

    $: updateRenderList(text, $jsonRanges, $jsonImages);

    $: mods = {
        singleline,
        multiline,
        halign,
        valign,
        truncate,
        width: $jsonWidth?.type === 'wrap_content' && !$jsonWidth.constrained ? 'content' : '',
        height: !$jsonHeight || $jsonHeight.type === 'wrap_content' && !$jsonHeight.constrained ? 'content' : ''
    };

    $: innerMods = {
        gradient: Boolean(gradient)
    };

    $: style = {
        'font-size': pxToEm(fontSize),
        'line-height': lineHeight,
        'max-height': maxHeight,
        '-webkit-line-clamp': lineClamp,
        color: rootTextColor,
        'background-image': gradient
    };

    function onImgError(event: Event): void {
        if (event.target && 'classList' in event.target) {
            (event.target as HTMLElement).classList.add(css.text__image_hidden);
        }
    }

    onDestroy(() => {
        usedTintColors.forEach(color => {
            rootCtx.removeSvgFilter(color);
        });
    });
</script>

{#if !hasError}
    <Outer
        cls="{genClassName('text', css, mods)} {selectable ? '' : rootCss.root__unselectable}"
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
    >
        <span class={genClassName('text__inner', css, innerMods)} style={makeStyle(style)}>
            {#if renderList.length}
                {#each renderList as item, index}
                    {#if 'text' in item}
                        {#if item.text}
                            <TextRangeView
                                text={item.text}
                                rootFontSize={fontSize}
                                textStyles={item.textStyles}
                                {singleline}
                                actions={item.actions}
                            />
                        {/if}
                    {:else if item.image}
                        <span style={makeStyle(item.image.wrapperStyle)}><img
                                class={css.text__image}
                                src={item.image.url}
                                loading="lazy"
                                decoding="async"
                                aria-hidden="true"
                                alt=""
                                style={makeStyle({
                                    width: item.image.width,
                                    height: item.image.height,
                                    // Normalizes line-height for the containing text line
                                    'margin-top': customLineHeight ? `-${item.image.height}` : undefined,
                                    'margin-bottom': customLineHeight ? `-${item.image.height}` : undefined,
                                    filter: item.image.svgFilterId ? `url(#${item.image.svgFilterId})` : undefined
                                })}
                                on:error={onImgError}
                            ></span>
                    {/if}
                {/each}
            {:else}
                <TextRangeView
                    {text}
                    rootFontSize={fontSize}
                    textStyles={$jsonRootTextStyles}
                    {singleline}
                />
            {/if}
        </span>
    </Outer>
{/if}
