<script lang="ts">
    import { getContext, onMount, tick } from 'svelte';
    import type { HTMLAttributes } from 'svelte/elements';

    import css from './Input.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivInputData, KeyboardType } from '../../types/input';
    import type { EdgeInsets } from '../../types/edgeInserts';
    import type { FixedLengthInputMask } from '../../utils/mask/fixedLengthInputMask';
    import type { MaybeMissing } from '../../expressions/json';
    import type { InputMask } from '../../types/input';
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
    import { updateFixedMask } from '../../utils/updateFixedMask';
    import { BaseInputMask } from '../../utils/mask/baseInputMask';
    import { updateCurrencyMask } from '../../utils/updateCurrencyMask';
    import { CurrencyInputMask } from '../../utils/mask/currencyInputMask';

    export let json: Partial<DivInputData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    let input: HTMLInputElement | HTMLTextAreaElement;
    let holder: HTMLElement;
    let isPressed = false;
    let verticalOverflow = false;
    let inputMask: BaseInputMask | null = null;

    const variable = json.text_variable;
    const rawVariable = json.mask?.raw_text_variable;

    let hasError = false;
    if (!variable) {
        hasError = true;
        rootCtx.logError(wrapError(new Error('Missing "text_variable" in "input"')));
    }

    let valueVariable = variable && rootCtx.getVariable(variable, 'string') || createVariable('temp', 'string', '');
    let rawValueVariable = rawVariable && rootCtx.getVariable(rawVariable, 'string') || createVariable('temp', 'string', '');
    let value = '';

    const jsonMask = rootCtx.getDerivedFromVars(json.mask);
    function updateMaskData(mask: MaybeMissing<InputMask> | undefined): void {
        if (mask?.type === 'fixed_length') {
            inputMask = updateFixedMask(mask, rootCtx.logError, inputMask as FixedLengthInputMask);
        } else if (mask?.type === 'currency') {
            inputMask = updateCurrencyMask(mask, rootCtx.logError, inputMask as CurrencyInputMask);
        }

        if (inputMask) {
            runRawValueMask();
        }
    }
    $: updateMaskData($jsonMask);

    $: if (!inputMask && value !== $valueVariable) {
        value = $valueVariable;
    }

    $: if (inputMask && inputMask.rawValue !== $rawValueVariable) {
        runRawValueMask();
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
            letterSpacing = pxToEm($jsonLetterSpacing);
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
    const isSupportInputMode = document && 'inputMode' in document.createElement('input');
    let inputMode: HTMLAttributes<HTMLInputElement>['inputmode'] = undefined;
    $: {
        if ($jsonKeyboardType && $jsonKeyboardType in KEYBOARD_MAP) {
            inputType = KEYBOARD_MAP[$jsonKeyboardType as KeyboardType];
            keyboardType = $jsonKeyboardType;
        }

        if ($jsonMask?.type === 'currency') {
            inputType = isSupportInputMode ? 'text' : 'tel';
            inputMode = 'decimal';
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
            maxHeight = `calc(${$jsonVisibleMaxLines * (lineHeight || 1.25) * (fontSize / 10) + 'em'} + ${pxToEmWithUnits(correctNonNegativeNumber($jsonPaddings?.top, 0) + correctNonNegativeNumber($jsonPaddings?.bottom, 0))})`;
        }
        selfPadding = correctEdgeInsertsObject(($jsonPaddings) ? $jsonPaddings : undefined, selfPadding);
        padding = selfPadding ? edgeInsertsToCss({
            top: (Number(selfPadding.top) || 0) / fontSize * 10,
            right: (Number(selfPadding.right) || 0) / fontSize * 10,
            bottom: (Number(selfPadding.bottom) || 0) / fontSize * 10,
            left: (Number(selfPadding.left) || 0) / fontSize * 10
        }) : '';
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
        'font-weight': fontWeight,
        'line-height': lineHeight,
        'letter-spacing': letterSpacing,
        color: textColor,
        'max-height': maxHeight
    };
    $: paddingStl = {
        'font-size': pxToEm(fontSize),
        padding
    };

    function onInput(event: Event) {
        const val = (event.target as HTMLInputElement).value || '';

        if (value !== val) {
            value = val;
            valueVariable.setValue(val);
            if (inputMask) {
                runValueMask();
            }
        }

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
        } else {
            verticalOverflow = false;
        }
    }

    async function runValueMask(): Promise<void> {
        if (!input || !inputMask) {
            return;
        }

        inputMask.applyChangeFrom(value, input.selectionEnd !== null ? input.selectionEnd : undefined);

        rawValueVariable.set(inputMask.rawValue);
        $valueVariable = value = inputMask.value;
        const cursorPosition = inputMask.cursorPosition;

        await tick();

        if (document.activeElement === input) {
            input.selectionStart = input.selectionEnd = cursorPosition;
        }
    }

    async function runRawValueMask(): Promise<void> {
        if (!input || !inputMask) {
            return;
        }

        inputMask.overrideRawValue($rawValueVariable);

        rawValueVariable.set(inputMask.rawValue);
        $valueVariable = value = inputMask.value;
        const cursorPosition = inputMask.cursorPosition;

        await tick();

        if (document.activeElement === input) {
            input.selectionStart = input.selectionEnd = cursorPosition;
        }
    }

    onMount(() => {
        checkVerticalOverflow();

        if (input && inputMask) {
            if ($rawValueVariable) {
                inputMask.overrideRawValue($rawValueVariable);
                $valueVariable = value = inputMask.value;
            }
        }
    });
</script>

{#if !hasError}
    <Outer
        cls={genClassName('input', css, mods)}
        style={stl}
        customDescription={true}
        customActions={'input'}
        customPaddings={true}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
    >
        <span class={css.input__wrapper} style={makeStyle(paddingStl)}>
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
                    style={makeStyle({ padding })}
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
                    inputmode={inputMode}
                    class={css.input__input}
                    autocomplete="off"
                    autocapitalize="off"
                    aria-label={description}
                    style={makeStyle({ padding })}
                    {placeholder}
                    {value}
                    on:input={onInput}
                    on:mousedown={$jsonSelectAll ? onMousedown : undefined}
                    on:click={$jsonSelectAll ? onClick : undefined}
                >
            {/if}
        </span>
    </Outer>
{/if}
