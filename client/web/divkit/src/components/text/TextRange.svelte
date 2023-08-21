<script lang="ts">
    import { getContext } from 'svelte';
    import css from './TextRange.module.css';

    import type { TextRange } from '../../types/text';
    import type { Action } from '../../../typings/common';
    import type { MaybeMissing } from '../../expressions/json';
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

    export let text: string;
    export let rootFontSize: number;
    export let textStyles: Partial<TextRange> = {};
    export let singleline = false;
    export let actions: MaybeMissing<Action[]> | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let decoration = 'none';
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

    let fontSize = 12;
    $: {
        fontSize = correctPositiveNumber(textStyles.font_size, fontSize);
    }

    let lineHeight = 1.25;
    $: {
        if (isPositiveNumber(textStyles.line_height)) {
            lineHeight = Number(textStyles.line_height) / fontSize;
        }
    }

    let letterSpacing = '';
    $: {
        if (isNonNegativeNumber(textStyles.letter_spacing)) {
            letterSpacing = pxToEm(textStyles.letter_spacing);
        }
    }

    let fontWeight: number | undefined = undefined;
    let fontFamily = '';
    $: {
        fontWeight = correctFontWeight(textStyles.font_weight, fontWeight);
        if (typeof textStyles.font_family === 'string' && textStyles.font_family) {
            fontFamily = rootCtx.typefaceProvider(textStyles.font_family, {
                fontWeight: fontWeight || 400
            });
        } else {
            fontFamily = '';
        }
    }

    let color = '';
    $: {
        color = correctColor(textStyles.text_color, 1, color);
    }

    $: topOffset = textStyles.top_offset ? pxToEm(textStyles.top_offset) : '';

    $: bg = textStyles.background ? getBackground([textStyles.background]) : null;

    let border: {
        color: string;
        width: number;
    } | null = null;
    $: if (textStyles.border?.stroke && correctColor(textStyles.border.stroke.color) !== 'transparent' && isPositiveNumber(textStyles.border.stroke.width)) {
        border = {
            color: textStyles.border.stroke.color,
            width: textStyles.border.stroke.width
        };
    } else {
        border = null;
    }

    $: borderRadius = correctPositiveNumber(textStyles.border?.corner_radius, 0);

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
        color,
        background: bg?.color || undefined,
        /**
         * box-shadow instead of border because:
         * 1) Doesn't take space as border does
         * 2) There should not be a border-radius on line breaks, but there should be a border
         */
        'box-shadow': border ? `inset 0 0 0 ${pxToEm(border.width)} ${border.color}` : undefined,
        'border-radius': borderRadius ? pxToEm(borderRadius) : undefined
    };
</script>

{#if topOffset}<span class={css['text-range__top-offset']} style:margin-top={topOffset}></span>{/if}<Actionable
    cls={genClassName('text-range', css, mods)}
    {actions}
    style={makeStyle(style)}
>
    {text}
</Actionable>
