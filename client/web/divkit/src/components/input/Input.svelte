<script lang="ts">
    import { getContext, onMount } from 'svelte';

    import css from './Input.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type {
        DivInputData,
        KeyboardType,
        SelectionInput,
        KeyboardInput,
        SelectionInputItem
    } from '../../types/input';
    import type { EdgeInsets } from '../../types/edgeInserts';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { genClassName } from '../../utils/genClassName';
    import { pxToEm, pxToEmWithUnits } from '../../utils/pxToEm';
    import { wrapError } from '../../utils/wrapError';
    import { correctColor } from '../../utils/correctColor';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { correctFontWeight } from '../../utils/correctFontWeight';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { isNumber } from '../../utils/isNumber';
    import Outer from '../utilities/Outer.svelte';
    import { createVariable } from '../../expressions/variable';
    import { correctNonNegativeNumber } from '../../utils/correctNonNegativeNumber';
    import { correctEdgeInsertsObject } from '../../utils/correctEdgeInsertsObject';
    import { edgeInsertsToCss } from '../../utils/edgeInsertsToCss';
    import { makeStyle } from '../../utils/makeStyle';

    export let json: Partial<DivInputData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    let input: HTMLInputElement | HTMLTextAreaElement;
    let holder: HTMLElement;
    let isPressed = false;
    let verticalOverflow = false;

    const variable = rootCtx.getJsonWithVars(json.text_variable);

    let hasError = false;
    if (!variable) {
        hasError = true;
        rootCtx.logError(wrapError(new Error('Missing "text_variable" in "input"')));
    }

    let valueVariable = variable && rootCtx.getVariable(variable, 'string') || createVariable('temp', 'string', '');
    let value = '';
    $: {
        value = $valueVariable as string;
    }

    const jsonHintText = rootCtx.getDerivedFromVars(json.hint_text);
    $: placeholder = $jsonHintText;

    const jsonHintColor = rootCtx.getDerivedFromVars(json.hint_color);
    let hintColor = '';
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

    const jsonHighlightColor = rootCtx.getDerivedFromVars(json.highlight_color);
    let highlightColor = '';
    $: {
        highlightColor = correctColor($jsonHighlightColor, 1, highlightColor);
    }

    const jsonKeyboardType = rootCtx.getDerivedFromVars(json.keyboard_type);
    const jsonInputMethod = rootCtx.getDerivedFromVars(json.input_method);
    const KEYBOARD_MAP: Record<KeyboardType, string> = {
        email: 'email',
        number: 'number',
        phone: 'tel',
        single_line_text: 'text',
        multi_line_text: 'text',
        uri: 'url'
    };
    let keyboardType = 'multi_line_text';
    let inputType = 'text';
    let inputMethod: 'keyboard' | 'select' = 'keyboard';
    let selectItems: SelectionInputItem[] = [];
    $: {
        const inputMethodType = $jsonInputMethod?.type;
        if (inputMethodType === 'keyboard') {
            inputMethod = 'keyboard';
            const type = ($jsonInputMethod as KeyboardInput).keyboard_type;
            if (type && type in KEYBOARD_MAP) {
                inputType = KEYBOARD_MAP[type as KeyboardType];
                keyboardType = type;
            } else {
                keyboardType = 'multi_line_text';
                inputType = KEYBOARD_MAP[keyboardType as KeyboardType];
            }
        } else if (inputMethodType === 'selection') {
            inputMethod = 'select';
            const items = ($jsonInputMethod as SelectionInput).items;
            const filteredItems = Array.isArray(items) && items.filter(it => it.text);
            if (filteredItems && filteredItems.length) {
                selectItems = items;
            } else {
                selectItems = [];
                rootCtx.logError(wrapError(new Error('Empty selection "items" in "input"')));
            }
        } else if ($jsonKeyboardType && $jsonKeyboardType in KEYBOARD_MAP) {
            inputType = KEYBOARD_MAP[$jsonKeyboardType as KeyboardType];
            keyboardType = $jsonKeyboardType;
        } else {
            keyboardType = 'multi_line_text';
            inputType = KEYBOARD_MAP[keyboardType as KeyboardType];
        }
    }

    let selectText = '';
    $: {
        if (inputMethod === 'select') {
            const item = selectItems.find(it => {
                return (it.value || it.text) === $valueVariable;
            });
            if (item) {
                selectText = item.text;
            } else {
                selectText = '';
                if ($valueVariable) {
                    rootCtx.logError(wrapError(new Error('Value from the variable was not found in the selection items for "input"')));
                }
            }
        }
    }

    const jsonVisibleMaxLines = rootCtx.getDerivedFromVars(json.max_visible_lines);
    $: isMultiline = keyboardType === 'multi_line_text'/* && isPositiveNumber($jsonVisibleMaxLines) && $jsonVisibleMaxLines > 1*/;

    $: jsonPaddings = rootCtx.getDerivedFromVars(json.paddings);
    let maxHeight = '';
    let selfPadding: EdgeInsets | null = null;
    let padding = '';
    $: {
        if (isPositiveNumber($jsonVisibleMaxLines)) {
            maxHeight = `calc(${$jsonVisibleMaxLines * (lineHeight || 1.25) + 'em'} + ${pxToEmWithUnits(correctNonNegativeNumber($jsonPaddings?.top, 0) + correctNonNegativeNumber($jsonPaddings?.bottom, 0))})`;
        }
        selfPadding = correctEdgeInsertsObject(($jsonPaddings) ? $jsonPaddings : undefined, selfPadding);
        padding = selfPadding ? edgeInsertsToCss(selfPadding) : '';
    }

    $: jsonAccessibility = rootCtx.getDerivedFromVars(json.accessibility);
    let description = '';
    $: if ($jsonAccessibility?.description) {
        description = $jsonAccessibility.description;
    } else {
        rootCtx.logError(wrapError(new Error('Missing accessibility "description" for input'), {
            level: 'warn'
        }));
    }

    const jsonSelectAll = rootCtx.getDerivedFromVars(json.select_all_on_focus);

    $: mods = {
        'highlight-color': Boolean(highlightColor),
        'vertical-overflow': verticalOverflow,
        multiline: isMultiline
    };
    $: stl = {
        '--divkit-input-hint-color': hintColor,
        '--divkit-input-highlight-color': highlightColor,
        'font-size': pxToEm(fontSize),
        'font-weight': fontWeight,
        'line-height': lineHeight,
        'letter-spacing': letterSpacing,
        color: textColor,
        'max-height': maxHeight
    };

    function onInput(event: Event) {
        const value = (event.target as HTMLInputElement).value || '';

        valueVariable.setValue(value);

        checkVerticalOverflow();
    }

    // Handle text selection
    function onMousedown() {
        isPressed = false;

        setTimeout(() => {
            isPressed = true;
        }, 250);
    }

    function onClick() {
        if (!isPressed) {
            input.select();
        }
    }

    function checkVerticalOverflow(): void {
        if (holder) {
            verticalOverflow = holder.scrollHeight > holder.offsetHeight;
        }
    }

    onMount(() => {
        checkVerticalOverflow();
    });
</script>

{#if !hasError}
    <Outer
        cls={genClassName('input', css, mods)}
        style={stl}
        customDescription={true}
        customActions={'input'}
        customPaddings={inputMethod === 'select'}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
    >
        {#if inputMethod === 'keyboard'}
            <span class={css.input__wrapper}>
                <span class={css.input__holder} aria-hidden="true" bind:this={holder}>
                    <!--appends zero-space at end-->
                    {value}{#if value.endsWith('\n') || !value}​{/if}
                </span>
                {#if isMultiline}
                    <textarea
                        bind:this={input}
                        class={css.input__input}
                        autocomplete="off"
                        autocapitalize="off"
                        aria-label={description}
                        {placeholder}
                        {value}
                        on:input={onInput}
                        on:mousedown={$jsonSelectAll ? onMousedown : undefined}
                        on:click={$jsonSelectAll ? onClick : undefined}
                    ></textarea>
                {:else}
                    <input
                        bind:this={input}
                        type={inputType}
                        class={css.input__input}
                        autocomplete="off"
                        autocapitalize="off"
                        aria-label={description}
                        {placeholder}
                        {value}
                        on:input={onInput}
                        on:mousedown={$jsonSelectAll ? onMousedown : undefined}
                        on:click={$jsonSelectAll ? onClick : undefined}
                    >
                {/if}
            </span>
        {:else}
            <span class={css['input__select-text']} style={makeStyle({ padding })} aria-hidden="true">
                <!--Space holder should have height even it has no value-->
                {selectText || '​'}
            </span>
            <select class={css.input__select} aria-label={description} bind:value={$valueVariable}>
                {#each selectItems as item}
                    <option class={css.input__option} value={item.value || item.text}>{item.text}</option>
                {/each}
            </select>
        {/if}
    </Outer>
{/if}
