<script lang="ts">
    import { getContext } from 'svelte';

    import css from './Select.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { genClassName } from '../../utils/genClassName';
    import { pxToEm } from '../../utils/pxToEm';
    import { wrapError } from '../../utils/wrapError';
    import { correctColor } from '../../utils/correctColor';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { correctFontWeight } from '../../utils/correctFontWeight';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { isNumber } from '../../utils/isNumber';
    import { createVariable } from '../../expressions/variable';
    import { DivSelectData } from '../../types/select';
    import { EdgeInsets } from '../../types/edgeInserts';
    import { correctEdgeInsertsObject } from '../../utils/correctEdgeInsertsObject';
    import { edgeInsertsToCss } from '../../utils/edgeInsertsToCss';
    import { makeStyle } from '../../utils/makeStyle';
    import Outer from '../utilities/Outer.svelte';

    export let json: Partial<DivSelectData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const variable = rootCtx.getJsonWithVars(json.value_variable);

    let hasError = false;
    if (!variable) {
        hasError = true;
        rootCtx.logError(wrapError(new Error('Missing "value_variable" in "select"')));
    }

    let valueVariable = variable && rootCtx.getVariable(variable, 'string') || createVariable('temp', 'string', '');
    let value = '';
    $: {
        value = $valueVariable as string;
    }

    const items = json.options;
    const filteredItems = Array.isArray(items) && items.filter(it => typeof it.value === 'string') || [];
    if (!(Array.isArray(filteredItems) && filteredItems.length)) {
        rootCtx.logError(wrapError(new Error('Empty selection "items" in "select"')));
    }

    let selectText = '';
    $: {
        const item = filteredItems.find(it => {
            return it.value === $valueVariable;
        });
        if (item) {
            selectText = item.text || item.value;
        } else {
            selectText = '';
            if ($valueVariable) {
                rootCtx.logError(wrapError(new Error('Value from the variable was not found in the selection items for "select"')));
            }
        }
    }

    $: jsonPaddings = rootCtx.getDerivedFromVars(json.paddings);
    let selfPadding: EdgeInsets | null = null;
    let padding = '';
    $: {
        selfPadding = correctEdgeInsertsObject(($jsonPaddings) ? $jsonPaddings : undefined, selfPadding);
        padding = selfPadding ? edgeInsertsToCss(selfPadding) : '';
    }

    const jsonHintText = rootCtx.getDerivedFromVars(json.hint_text);
    $: hint = $jsonHintText;

    const jsonHintColor = rootCtx.getDerivedFromVars(json.hint_color);
    let hintColor = 'rgba(0,0,0,.45)';
    $: {
        hintColor = correctColor($jsonHintColor, 1, hintColor);
    }

    const jsonFontSize = rootCtx.getDerivedFromVars(json.font_size);
    let fontSize = 12;
    $: {
        fontSize = correctPositiveNumber($jsonFontSize, fontSize);
    }

    const jsonFontWeight = rootCtx.getDerivedFromVars(json.font_weight);
    let fontWeight: number | undefined = undefined;
    $: {
        fontWeight = correctFontWeight($jsonFontWeight, fontWeight);
    }

    const jsonLineHeight = rootCtx.getDerivedFromVars(json.line_height);
    let lineHeight: number | undefined = undefined;
    $: {
        const val = $jsonLineHeight;
        if (isPositiveNumber(val)) {
            lineHeight = val / fontSize;
        }
    }

    const jsonLetterSpacing = rootCtx.getDerivedFromVars(json.letter_spacing);
    let letterSpacing = '';
    $: {
        if (isNumber($jsonLetterSpacing)) {
            letterSpacing = pxToEm($jsonLetterSpacing / fontSize * 10);
        }
    }

    const jsonTextColor = rootCtx.getDerivedFromVars(json.text_color);
    let textColor = '#000';
    $: {
        textColor = correctColor($jsonTextColor, 1, textColor);
    }

    $: jsonAccessibility = rootCtx.getDerivedFromVars(json.accessibility);
    let description = '';
    $: if ($jsonAccessibility?.description) {
        description = $jsonAccessibility.description;
    } else {
        rootCtx.logError(wrapError(new Error('Missing accessibility "description" for select'), {
            level: 'warn'
        }));
    }

    $: mods = {
        hint: !$valueVariable
    };
    $: stl = {
        '--divkit-input-hint-color': hintColor,
        'font-size': pxToEm(fontSize),
        'font-weight': fontWeight,
        'line-height': lineHeight,
        'letter-spacing': letterSpacing,
        color: textColor
    };
</script>

{#if !hasError}
    <Outer
        cls={genClassName('select', css, mods)}
        style={stl}
        customDescription={true}
        customActions={'select'}
        customPaddings={true}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
    >
        <span class={css['select__select-text']} style={makeStyle({ padding })} aria-hidden="true">
            <!--Space holder should have height even it has no value-->
            {($valueVariable ? selectText : hint) || '​'}
        </span>

        <select class={css.select__select} aria-label={description} bind:value={$valueVariable}>
            {#each filteredItems as item}
                <option class={css.select__option} value={item.value}>{item.text || item.value}</option>
            {/each}
        </select>
    </Outer>
{/if}
