<script lang="ts">
    import css from './textRange.module.css';

    import type { TextRange } from '../types/text';
    import type { Action } from '../../typings/common';
    import type { MaybeMissing } from '../expressions/json';
    import Actionable from './actionable.svelte';
    import { pxToEm } from '../utils/pxToEm';
    import { makeStyle } from '../utils/makeStyle';
    import { genClassName } from '../utils/genClassName';
    import { correctPositiveNumber } from '../utils/correctPositiveNumber';
    import { isPositiveNumber } from '../utils/isPositiveNumber';
    import { correctFontWeight } from '../utils/correctFontWeight';
    import { correctColor } from '../utils/correctColor';
    import { isNonNegativeNumber } from '../utils/isNonNegativeNumber';

    export let text: string;
    export let rootFontSize: number;
    export let textStyles: Partial<TextRange> = {};
    export let singleline = false;
    export let actions: MaybeMissing<Action[]> | undefined = undefined;

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
    $: {
        fontWeight = correctFontWeight(textStyles.font_weight, fontWeight);
    }

    let color = '';
    $: {
        color = correctColor(textStyles.text_color, 1, color);
    }

    $: topOffset = textStyles.top_offset ? pxToEm(textStyles.top_offset) : '';

    $: mods = {
        singleline,
        decoration
    };

    $: style = {
        'font-size': pxToEm((fontSize * 10) / rootFontSize),
        'line-height': lineHeight,
        'letter-spacing': letterSpacing,
        'font-weight': fontWeight,
        color
    };
</script>

{#if topOffset}<span class={css['text-range__top-offset']} style:margin-top={topOffset}></span>{/if}<Actionable
    cls={genClassName('text-range', css, mods)}
    {actions}
    style={makeStyle(style)}
>
    {text}
</Actionable>
