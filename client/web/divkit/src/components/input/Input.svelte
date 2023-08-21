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
    import { correctAlignmentHorizontal } from '../../utils/correctAlignmentHorizontal';
    import { AlignmentHorizontal, AlignmentVertical } from '../../types/alignment';
    import { correctAlignmentVertical } from '../../utils/correctAlignmentVertical';
    import { calcSelectionOffset, setSelectionOffset } from '../../utils/contenteditable';

    export let json: Partial<DivInputData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    let input: HTMLInputElement | HTMLSpanElement;
    let isPressed = false;
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
    let contentEditableValue = '';

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
        value = contentEditableValue = $valueVariable;
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

    const jsonAlignmentHorizontal = rootCtx.getDerivedFromVars(json.text_alignment_horizontal);
    let alignmentHorizontal: AlignmentHorizontal = 'left';
    $: {
        alignmentHorizontal = correctAlignmentHorizontal($jsonAlignmentHorizontal, alignmentHorizontal);
    }

    const jsonAlignmentVertical = rootCtx.getDerivedFromVars(json.text_alignment_vertical);
    let alignmentVertical: AlignmentVertical = 'center';
    $: {
        alignmentVertical = correctAlignmentVertical($jsonAlignmentVertical, alignmentVertical);
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
    let verticalPadding = '';
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
        verticalPadding = selfPadding ? edgeInsertsToCss({
            top: (Number(selfPadding.top) || 0) / fontSize * 10,
            bottom: (Number(selfPadding.bottom) || 0) / fontSize * 10
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
        multiline: isMultiline,
        'alignment-horizontal': alignmentHorizontal,
        'alignment-vertical': alignmentVertical
    };
    $: stl = {
        '--divkit-input-hint-color': hintColor,
        '--divkit-input-highlight-color': highlightColor,
        '--divkit-input-line-height': lineHeight,
        'font-weight': fontWeight,
        'font-family': fontFamily,
        'letter-spacing': letterSpacing,
        color: textColor,
        'max-height': maxHeight
    };
    $: paddingStl = {
        'font-size': pxToEm(fontSize),
        padding
    };
    $: verticalPaddingStl = {
        'font-size': pxToEm(fontSize),
        padding: verticalPadding
    };

    function onInput(event: Event): void {
        let val = (isMultiline ?
            (event.target as HTMLDivElement).innerText :
            (event.target as HTMLInputElement).value
        ) || '';

        if (val === '\n') {
            val = '';
        }

        if (value !== val) {
            value = contentEditableValue = val;
            valueVariable.setValue(val);
            if (inputMask) {
                runValueMask();
            }
        }
    }

    function onPaste(event: ClipboardEvent): void {
        event.preventDefault();
        if (event.clipboardData) {
            let text = event.clipboardData.getData('text/plain');
            text = text.trim();
            document.execCommand('inserttext', false, text);
        }
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
            if (input instanceof HTMLInputElement) {
                input.select();
            } else {
                const selection = window.getSelection();
                const range = document.createRange();
                range.selectNodeContents(input);
                if (selection) {
                    selection.removeAllRanges();
                    selection.addRange(range);
                }
            }
        }
    }

    function getSelectionEnd(): number | undefined {
        if (input instanceof HTMLInputElement) {
            return input.selectionEnd === null ? undefined : input.selectionEnd;
        }

        return calcSelectionOffset(input);
    }

    function setCursorPosition(cursorPosition: number): void {
        if (input instanceof HTMLInputElement) {
            input.selectionStart = input.selectionEnd = cursorPosition;
        } else {
            setSelectionOffset(input, cursorPosition);
        }
    }

    async function runValueMask(): Promise<void> {
        if (!input || !inputMask) {
            return;
        }

        inputMask.applyChangeFrom(value, getSelectionEnd());

        rawValueVariable.set(inputMask.rawValue);
        $valueVariable = value = contentEditableValue = inputMask.value;
        const cursorPosition = inputMask.cursorPosition;

        await tick();

        if (document.activeElement === input) {
            setCursorPosition(cursorPosition);
        }
    }

    async function runRawValueMask(): Promise<void> {
        if (!input || !inputMask) {
            return;
        }

        inputMask.overrideRawValue($rawValueVariable);

        rawValueVariable.set(inputMask.rawValue);
        $valueVariable = value = contentEditableValue = inputMask.value;
        const cursorPosition = inputMask.cursorPosition;

        await tick();

        if (document.activeElement === input) {
            setCursorPosition(cursorPosition);
        }
    }

    onMount(() => {
        if (input && inputMask) {
            if ($rawValueVariable) {
                inputMask.overrideRawValue($rawValueVariable);
                $valueVariable = value = contentEditableValue = inputMask.value;
            }
        }
    });
</script>

{#if !hasError}
    <Outer
        let:focusHandler
        let:blurHandler
        let:hasCustomFocus
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
        {#if isMultiline}
            <span class={css['input__scroll-wrapper']}>
                {#if !contentEditableValue && placeholder}
                    <div
                        class={css.input__placeholder}
                        aria-hidden="true"
                        style={makeStyle(paddingStl)}
                    >
                        {placeholder}
                    </div>
                {/if}

                <!-- zero-width space, so other baseline-elements could be aligned without value -->
                <span
                    aria-hidden="true"
                    style={makeStyle(verticalPaddingStl)}
                >​</span>

                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <span
                    bind:this={input}
                    class={genClassName('input__input', css, { 'has-custom-focus': hasCustomFocus, multiline: true })}
                    autocapitalize="off"
                    contenteditable="true"
                    aria-label={description}
                    style={makeStyle(paddingStl)}
                    bind:innerText={contentEditableValue}
                    on:input={onInput}
                    on:paste={onPaste}
                    on:mousedown={$jsonSelectAll ? onMousedown : undefined}
                    on:click={$jsonSelectAll ? onClick : undefined}
                    on:focus={focusHandler}
                    on:blur={blurHandler}
                >
                </span>
            </span>
        {:else}
            <input
                bind:this={input}
                type={inputType}
                inputmode={inputMode}
                class={genClassName('input__input', css, { 'has-custom-focus': hasCustomFocus, singleline: true })}
                autocomplete="off"
                autocapitalize="off"
                aria-label={description}
                style={makeStyle(paddingStl)}
                {placeholder}
                {value}
                on:input={onInput}
                on:mousedown={$jsonSelectAll ? onMousedown : undefined}
                on:click={$jsonSelectAll ? onClick : undefined}
                on:focus={focusHandler}
                on:blur={blurHandler}
            >
        {/if}
    </Outer>
{/if}
