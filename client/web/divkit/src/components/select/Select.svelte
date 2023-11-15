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

    const variable = json.value_variable;
    let select: HTMLSelectElement;

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
            selectText = typeof item.text === 'string' ? item.text : item.value;
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
        padding = selfPadding ? edgeInsertsToCss({
            top: (Number(selfPadding.top) || 0) / fontSize * 10,
            right: (Number(selfPadding.right) || 0) / fontSize * 10,
            bottom: (Number(selfPadding.bottom) || 0) / fontSize * 10,
            left: (Number(selfPadding.left) || 0) / fontSize * 10
        }) : '';
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
    const jsonFontFamily = rootCtx.getDerivedFromVars(json.font_family);
    let fontWeight: number | undefined = undefined;
    let fontFamily = '';
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

    onMount(() => {
        if (json.id) {
            rootCtx.registerFocusable(json.id, {
                focus() {
                    if (select) {
                        select.focus();
                    }
                }
            });
        }
    });

    onDestroy(() => {
        if (json.id) {
            rootCtx.unregisterFocusable(json.id);
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
            {selectText || hint || '​'}
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
