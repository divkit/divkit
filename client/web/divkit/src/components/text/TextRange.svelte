<script lang="ts">
    import { getContext } from 'svelte';
    import css from './TextRange.module.css';

    import type { CloudBackground, DivTextData, TextRange } from '../../types/text';
    import type { Action } from '../../../typings/common';
    import type { MaybeMissing } from '../../expressions/json';
    import type { ComponentContext } from '../../types/componentContext';
    import Actionable from '../utilities/Actionable.svelte';
    import { pxToEm } from '../../utils/pxToEm';
    import { makeStyle } from '../../utils/makeStyle';
    import { genClassName } from '../../utils/genClassName';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { correctFontWeight } from '../../utils/correctFontWeight';
    import { correctColor, correctColorWithAlpha, parseColor } from '../../utils/correctColor';
    import { isNonNegativeNumber } from '../../utils/isNonNegativeNumber';
    import { getBackground } from '../../utils/background';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { shadowToCssFilter } from '../../utils/shadow';
    import { edgeInsertsToCss } from '../../utils/edgeInsertsToCss';
    import { edgeInsertsMultiply } from '../../utils/edgeInsetsMultiply';

    export let componentContext: ComponentContext<DivTextData>;
    export let text: string;
    export let rootFontSize: number;
    export let textStyles: MaybeMissing<Partial<TextRange>> = {};
    export let singleline = false;
    export let actions: MaybeMissing<Action[]> | undefined = undefined;
    export let cloudBg = false;
    export let customLineHeight: number | null = null;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const direction = rootCtx.direction;

    const cloudFilterId = cloudBg && rootCtx.genId('text-range') || '';

    let decoration = 'none';
    let fontSize = 12;
    let lineHeight = 1.25;
    let letterSpacing = '';
    let fontWeight: number | undefined = undefined;
    let fontFamily = '';
    let color = '';
    let colorOverride: string | undefined;
    let border: {
        color: string;
        width: number;
        corner_radius?: number;
    } | null = null;
    let verticalAlign: number | undefined = undefined;
    let background: string | undefined;
    let maskAnimated = false;
    let maskColor: string | undefined;
    let maskSize: string | undefined;
    let maskDensity: string | undefined;

    $: if (componentContext.json) {
        decoration = 'none';
        fontSize = 12;
        lineHeight = 1.25;
        letterSpacing = '';
        fontWeight = undefined;
        fontFamily = '';
        color = '';
        colorOverride = undefined;
        border = null;
        verticalAlign = undefined;
        background = undefined;
        maskAnimated = false;
        maskColor = undefined;
        maskSize = undefined;
        maskDensity = undefined;
    }

    $: {
        let newDecoration = 'none';

        if (textStyles.underline || textStyles.strike) {
            if (textStyles.underline === 'single' && textStyles.strike === 'single') {
                newDecoration = 'both';
            } else if (textStyles.underline === 'single') {
                newDecoration = 'underline';
            } else if (textStyles.strike === 'single') {
                newDecoration = 'strike';
            }
        }

        decoration = newDecoration;
    }

    $: {
        fontSize = correctPositiveNumber(textStyles.font_size, fontSize);
    }

    $: {
        if (isPositiveNumber(textStyles.line_height)) {
            lineHeight = Number(textStyles.line_height) / fontSize;
        }
    }

    $: {
        if (isNonNegativeNumber(textStyles.letter_spacing)) {
            letterSpacing = pxToEm(textStyles.letter_spacing);
        }
    }

    $: {
        fontWeight = correctFontWeight(textStyles.font_weight, textStyles.font_weight_value, fontWeight);
        if (typeof textStyles.font_family === 'string' && textStyles.font_family) {
            fontFamily = rootCtx.typefaceProvider(textStyles.font_family, {
                fontWeight: fontWeight || 400
            });
        } else {
            fontFamily = '';
        }
    }

    $: {
        color = correctColor(textStyles.text_color, 1, color);
    }

    $: topOffset = textStyles.top_offset ? pxToEm(textStyles.top_offset) : '';

    $: hasCloudBg = textStyles.background?.type === 'cloud';

    $: cloudPadding = textStyles.background?.type === 'cloud' ? textStyles.background.paddings : undefined;

    $: {
        const mask = textStyles.mask;
        const hasMask = Boolean(
            mask &&
            (mask.type === 'solid' || mask.type === 'particles') &&
            mask.is_enabled !== false && mask.color
        );

        if (cloudBg || hasMask) {
            colorOverride = 'transparent';
        } else {
            colorOverride = undefined;
        }

        maskAnimated = false;
        maskColor = undefined;
        maskSize = undefined;
        maskDensity = undefined;
        if (cloudBg) {
            if (hasCloudBg) {
                background = correctColorWithAlpha((textStyles.background as CloudBackground).color, 255, 'transparent');
            } else {
                background = undefined;
            }
        } else if (mask && hasMask) {
            if (mask.type === 'solid') {
                background = correctColor(mask.color);
            } else if (mask.type === 'particles') {
                const size = correctPositiveNumber(mask.particle_size?.value, 1);
                const sizeEm = pxToEm(size * 10 / fontSize);
                const density = correctPositiveNumber(mask.density, .8);
                const color = correctColor(mask.color);

                background = undefined;
                maskColor = color;
                maskSize = sizeEm;
                maskDensity = String(density);
                maskAnimated = mask.is_animated === true;
            }
        } else if (textStyles.background?.type === 'solid') {
            background = getBackground([textStyles.background]).color;
        } else {
            background = undefined;
        }
    }

    $: if (
        textStyles.border?.stroke &&
        textStyles.border.stroke.color &&
        correctColor(textStyles.border.stroke.color) !== 'transparent' &&
        isPositiveNumber(textStyles.border.stroke.width) &&
        textStyles.background?.type !== 'cloud'
    ) {
        border = {
            color: textStyles.border.stroke.color,
            width: textStyles.border.stroke.width,
            corner_radius: textStyles.border.corner_radius
        };
    } else {
        border = null;
    }

    // eslint-disable-next-line no-nested-ternary
    $: borderRadius = cloudBg ?
        (hasCloudBg ? (textStyles.background as CloudBackground).corner_radius || 0 : 0) :
        (border ? correctPositiveNumber(border.corner_radius, 0) : 0);

    $: shadow = textStyles.text_shadow ? shadowToCssFilter(textStyles.text_shadow, fontSize) : undefined;

    $: {
        if (typeof textStyles.baseline_offset === 'number') {
            verticalAlign = textStyles.baseline_offset;
        }
    }

    $: customVerticalAlign = typeof textStyles.baseline_offset === 'number' ? undefined : textStyles.alignment_vertical;

    $: mods = {
        singleline,
        decoration,
        align: customVerticalAlign,
        cloud: hasCloudBg,
        'relative-vertical-align': Boolean(customLineHeight && verticalAlign),
        'has-particles-mask': Boolean(maskColor),
        'mask-animated': maskAnimated
    };

    $: style = {
        'font-size': pxToEm((fontSize * 10) / rootFontSize),
        'line-height': customVerticalAlign ? 'normal' : lineHeight,
        'letter-spacing': letterSpacing,
        'font-weight': fontWeight,
        'font-family': fontFamily,
        'vertical-align': (customLineHeight || verticalAlign === undefined) ? undefined : pxToEm(verticalAlign * 10 / fontSize),
        top: (customLineHeight && verticalAlign !== undefined) ? pxToEm(-verticalAlign * 10 / fontSize) : undefined,
        margin: cloudPadding ?
            edgeInsertsToCss(edgeInsertsMultiply(cloudPadding, -10 / fontSize), $direction) :
            undefined,
        padding: cloudPadding ?
            edgeInsertsToCss(edgeInsertsMultiply(cloudPadding, 10 / fontSize), $direction) :
            undefined,
        filter: cloudBg && hasCloudBg ? `url(#${cloudFilterId})` : shadow,
        color: colorOverride || color,
        background,
        opacity: cloudBg && hasCloudBg ?
            (parseColor((textStyles.background as CloudBackground).color)?.a ?? 255) / 255 :
            undefined,
        /**
         * box-shadow instead of border because:
         * 1) Doesn't take space as border does
         * 2) There should not be a border-radius on line breaks, but there should be a border
         */
        'box-shadow': border ? `inset 0 0 0 ${pxToEm(border.width * 10 / fontSize)} ${border.color}` : undefined,
        'border-radius': borderRadius ? pxToEm(borderRadius * 10 / fontSize) : undefined,
        'font-feature-settings': textStyles.font_feature_settings || undefined,
        '--divkit-text-mask-color': maskColor,
        '--divkit-text-mask-size': maskSize,
        '--divkit-text-mask-density': maskDensity,
    };
</script>

{#if cloudBg && hasCloudBg}<svg class={css['text-range__cloud-svg']}><defs><filter id={cloudFilterId}><feGaussianBlur in="SourceGraphic" result="blurred" stdDeviation="3"></feGaussianBlur><feColorMatrix in="blurred" result="withMatrix" type="matrix" values="1 0 0 0 0 0 1 0 0 0 0 0 1 0 0 0 0 0 {2 * borderRadius} -{borderRadius}"></feColorMatrix><feBlend in="SourceGraphic" in2="withMatrix"></feBlend></filter></defs>
</svg>{/if}{#if topOffset}<span class={css['text-range__top-offset']} style:margin-top={topOffset}></span>{/if}<Actionable
    {componentContext}
    cls={genClassName('text-range', css, mods)}
    {actions}
    style={makeStyle(style)}
><!-- zero-width space as default -->{#if maskColor}<div class={css['text-range__mask-animation']}></div><div class={css['text-range__mask-animation']}></div><div class={css['text-range__mask-animation']}></div><div class={css['text-range__mask-animation']}></div><div class={css['text-range__mask-animation']}></div><div class={css['text-range__mask-animation']}></div>{/if}{text || '​'}</Actionable>
