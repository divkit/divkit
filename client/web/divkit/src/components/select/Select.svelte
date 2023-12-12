<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';

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

    let prevId: string | undefined;
    let select: HTMLSelectElement;
    let hasError = false;
    let selectText = '';
    let selfPadding: EdgeInsets | null = null;
    let padding = '';
    let hintColor = 'rgba(0,0,0,.45)';
    let fontSize = 12;
    let fontWeight: number | undefined = undefined;
    let fontFamily = '';
    let lineHeight: number | undefined = undefined;
    let letterSpacing = '';
    let textColor = '#000';
    let description = '';

    $: if (json) {
        selfPadding = null;
        hintColor = 'rgba(0,0,0,.45)';
        fontSize = 12;
        fontWeight = undefined;
        fontFamily = '';
        lineHeight = undefined;
        letterSpacing = '';
        textColor = '#000';
        description = '';
    }

    $: variable = json.value_variable;
    $: items = json.options;
    $: filteredItems = Array.isArray(items) && items.filter(it => typeof it.value === 'string') || [];

    $: valueVariable = variable && rootCtx.getVariable(variable, 'string') || createVariable('temp', 'string', '');

    $: jsonPaddings = rootCtx.getDerivedFromVars(json.paddings);
    $: jsonHintText = rootCtx.getDerivedFromVars(json.hint_text);
    $: jsonHintColor = rootCtx.getDerivedFromVars(json.hint_color);
    $: jsonFontSize = rootCtx.getDerivedFromVars(json.font_size);
    $: jsonFontWeight = rootCtx.getDerivedFromVars(json.font_weight);
    $: jsonFontFamily = rootCtx.getDerivedFromVars(json.font_family);
    $: jsonLineHeight = rootCtx.getDerivedFromVars(json.line_height);
    $: jsonLetterSpacing = rootCtx.getDerivedFromVars(json.letter_spacing);
    $: jsonTextColor = rootCtx.getDerivedFromVars(json.text_color);
    $: jsonAccessibility = rootCtx.getDerivedFromVars(json.accessibility);

    $: if (!(Array.isArray(filteredItems) && filteredItems.length)) {
        rootCtx.logError(wrapError(new Error('Empty selection "items" in "select"')));
    }

    $: if (variable) {
        hasError = false;
    } else {
        hasError = true;
        rootCtx.logError(wrapError(new Error('Missing "value_variable" in "select"')));
    }

    $: {
        const item = filteredItems.find(it => {
            return it.value === $valueVariable;
        });
        if (item) {
            selectText = typeof item.text === 'string' ? item.text : item.value;
        } else {
            selectText = '';
            if ($valueVariable) {
                rootCtx.logError(wrapError(new Error('Value from the variable was not found in the selection items for "select"')));
            }
        }
    }

    $: {
        selfPadding = correctEdgeInsertsObject(($jsonPaddings) ? $jsonPaddings : undefined, selfPadding);
        padding = selfPadding ? edgeInsertsToCss({
            top: (Number(selfPadding.top) || 0) / fontSize * 10,
            right: (Number(selfPadding.right) || 0) / fontSize * 10,
            bottom: (Number(selfPadding.bottom) || 0) / fontSize * 10,
            left: (Number(selfPadding.left) || 0) / fontSize * 10
        }) : '';
    }

    $: {
        hintColor = correctColor($jsonHintColor, 1, hintColor);
    }

    $: {
        fontSize = correctPositiveNumber($jsonFontSize, fontSize);
    }

    $: {
        fontWeight = correctFontWeight($jsonFontWeight, fontWeight);
        if ($jsonFontFamily && typeof $jsonFontFamily === 'string') {
            fontFamily = rootCtx.typefaceProvider($jsonFontFamily, {
                fontWeight: fontWeight || 400
            });
        } else {
            fontFamily = '';
        }
    }

    $: {
        const val = $jsonLineHeight;
        if (isPositiveNumber(val)) {
            lineHeight = val / fontSize;
        }
    }

    $: {
        if (isNumber($jsonLetterSpacing)) {
            letterSpacing = pxToEm($jsonLetterSpacing / fontSize * 10);
        }
    }

    $: {
        textColor = correctColor($jsonTextColor, 1, textColor);
    }

    $: if ($jsonAccessibility?.description) {
        description = $jsonAccessibility.description;
    } else {
        rootCtx.logError(wrapError(new Error('Missing accessibility "description" for select'), {
            level: 'warn'
        }));
    }

    $: mods = {
        hint: !selectText
    };
    $: stl = {
        '--divkit-input-hint-color': hintColor,
        'font-weight': fontWeight,
        'font-family': fontFamily,
        color: textColor
    };
    $: innerStl = {
        padding,
        'font-size': pxToEm(fontSize),
        'line-height': lineHeight,
        'letter-spacing': letterSpacing
    };
    $: selectStl = {
        'font-size': pxToEm(fontSize),
        'line-height': lineHeight,
        'letter-spacing': letterSpacing
    };

    $: if (json && select) {
        if (prevId) {
            rootCtx.unregisterFocusable(prevId);
            prevId = undefined;
        }

        if (json.id && !layoutParams?.fakeElement) {
            prevId = json.id;
            rootCtx.registerFocusable(json.id, {
                focus() {
                    if (select) {
                        select.focus();
                    }
                }
            });
        }
    }

    onDestroy(() => {
        if (prevId) {
            rootCtx.unregisterFocusable(prevId);
            prevId = undefined;
        }
    });
</script>

{#if !hasError}
    <Outer
        let:hasCustomFocus
        let:focusHandler
        let:blurHandler
        cls={genClassName('select', css, mods)}
        style={stl}
        customDescription={true}
        customActions={'select'}
        customPaddings={true}
        hasInnerFocusable={true}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
    >
        <span class={css['select__select-text']} style={makeStyle(innerStl)} aria-hidden="true">
            <!--Space holder should have height even it has no value-->
            {selectText || $jsonHintText || '​'}
        </span>

        <select
            class={genClassName('select__select', css, { 'has-custom-focus': hasCustomFocus })}
            aria-label={description}
            bind:this={select}
            bind:value={$valueVariable}
            style={makeStyle(selectStl)}
            on:focus={focusHandler}
            on:blur={blurHandler}
        >
            {#each filteredItems as item}
                <option class={css.select__option} value={item.value}>{item.text || item.value}</option>
            {/each}
        </select>
    </Outer>
{/if}
