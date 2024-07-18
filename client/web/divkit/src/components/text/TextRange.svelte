<script lang="ts">
    import { getContext } from 'svelte';
    import css from './TextRange.module.css';

    import type { DivTextData, TextRange } from '../../types/text';
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
    import { correctColor } from '../../utils/correctColor';
    import { isNonNegativeNumber } from '../../utils/isNonNegativeNumber';
    import { getBackground } from '../../utils/background';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { shadowToCssFilter } from '../../utils/shadow';

    export let componentContext: ComponentContext<DivTextData>;
    export let text: string;
    export let rootFontSize: number;
    export let textStyles: MaybeMissing<Partial<TextRange>> = {};
    export let singleline = false;
    export let actions: MaybeMissing<Action[]> | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let decoration = 'none';
    let fontSize = 12;
    let lineHeight = 1.25;
    let letterSpacing = '';
    let fontWeight: number | undefined = undefined;
    let fontFamily = '';
    let color = '';
    let border: {
        color: string;
        width: number;
    } | null = null;

    $: if (componentContext.json) {
        decoration = 'none';
        fontSize = 12;
        lineHeight = 1.25;
        letterSpacing = '';
        fontWeight = undefined;
        fontFamily = '';
        color = '';
        border = null;
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

    $: bg = textStyles.background ? getBackground([textStyles.background]) : null;

    $: if (
        textStyles.border?.stroke &&
        textStyles.border.stroke.color &&
        correctColor(textStyles.border.stroke.color) !== 'transparent' &&
        isPositiveNumber(textStyles.border.stroke.width)
    ) {
        border = {
            color: textStyles.border.stroke.color,
            width: textStyles.border.stroke.width
        };
    } else {
        border = null;
    }

    $: borderRadius = correctPositiveNumber(textStyles.border?.corner_radius, 0);

    $: shadow = textStyles.text_shadow ? shadowToCssFilter(textStyles.text_shadow, fontSize) : undefined;

    $: mods = {
        singleline,
        decoration
    };

    $: style = {
        'font-size': pxToEm((fontSize * 10) / rootFontSize),
        'line-height': lineHeight,
        'letter-spacing': letterSpacing,
        'font-weight': fontWeight,
        'font-family': fontFamily,
        filter: shadow,
        color,
        background: bg?.color || undefined,
        /**
         * box-shadow instead of border because:
         * 1) Doesn't take space as border does
         * 2) There should not be a border-radius on line breaks, but there should be a border
         */
        'box-shadow': border ? `inset 0 0 0 ${pxToEm(border.width)} ${border.color}` : undefined,
        'border-radius': borderRadius ? pxToEm(borderRadius) : undefined,
        'font-feature-settings': textStyles.font_feature_settings || undefined,
    };
</script>

{#if topOffset}<span class={css['text-range__top-offset']} style:margin-top={topOffset}></span>{/if}<Actionable
    {componentContext}
    cls={genClassName('text-range', css, mods)}
    {actions}
    style={makeStyle(style)}
>
    {text}
</Actionable>
