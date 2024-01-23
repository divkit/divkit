<script lang="ts" context="module">
    const isSupportInputMode = typeof document !== 'undefined' && 'inputMode' in document.createElement('input');

    const KEYBOARD_MAP: Record<KeyboardType, string> = {
        email: 'email',
        number: 'number',
        phone: 'tel',
        single_line_text: 'text',
        multi_line_text: 'text',
        uri: 'url'
    };
</script>

<script lang="ts">
    import { getContext, onDestroy, onMount, tick } from 'svelte';
    import type { HTMLAttributes } from 'svelte/elements';

    import css from './Input.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivInputData, KeyboardType } from '../../types/input';
    import type { EdgeInsets } from '../../types/edgeInserts';
    import type { FixedLengthInputMask } from '../../utils/mask/fixedLengthInputMask';
    import type { MaybeMissing } from '../../expressions/json';
    import type { InputMask } from '../../types/input';
    import type { AlignmentHorizontal } from '../../types/alignment';
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
    import { AlignmentVerticalMapped, correctAlignmentVertical } from '../../utils/correctAlignmentVertical';
    import { calcSelectionOffset, setSelectionOffset } from '../../utils/contenteditable';

    export let json: Partial<DivInputData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const direction = rootCtx.direction;

    let prevId: string | undefined;
    let input: HTMLInputElement | HTMLSpanElement;
    let isPressed = false;
    let inputMask: BaseInputMask | null = null;
    let value = '';
    let contentEditableValue = '';
    let hasError = false;
    let hintColor = '';
    let fontSize = 12;
    let fontWeight: number | undefined = undefined;
    let fontFamily = '';
    let lineHeight: number | undefined = undefined;
    let letterSpacing = '';
    let textColor = '#000';
    let highlightColor = '';
    let alignmentHorizontal: AlignmentHorizontal = 'start';
    let alignmentVertical: AlignmentVerticalMapped = 'center';
    let keyboardType = 'multi_line_text';
    let inputType = 'text';
    let inputMode: HTMLAttributes<HTMLInputElement>['inputmode'] = undefined;
    let maxHeight = '';
    let selfPadding: EdgeInsets | null = null;
    let padding = '';
    let verticalPadding = '';
    let description = '';

    $: if (json) {
        hintColor = '';
        fontSize = 12;
        fontWeight = undefined;
        fontFamily = '';
        lineHeight = undefined;
        textColor = '#000';
        highlightColor = '';
        alignmentHorizontal = 'left';
        alignmentVertical = 'center';
        keyboardType = 'multi_line_text';
        inputType = 'text';
        inputMode = undefined;
    }

    $: variable = json.text_variable;
    $: rawVariable = json.mask?.raw_text_variable;

    $: valueVariable = variable && rootCtx.getVariable(variable, 'string') || createVariable('temp', 'string', '');
    $: rawValueVariable = rawVariable && rootCtx.getVariable(rawVariable, 'string') || createVariable('temp', 'string', '');

    $: jsonHintText = rootCtx.getDerivedFromVars(json.hint_text);
    $: jsonHintColor = rootCtx.getDerivedFromVars(json.hint_color);
    $: jsonFontSize = rootCtx.getDerivedFromVars(json.font_size);
    $: jsonFontWeight = rootCtx.getDerivedFromVars(json.font_weight);
    $: jsonFontFamily = rootCtx.getDerivedFromVars(json.font_family);
    $: jsonLineHeight = rootCtx.getDerivedFromVars(json.line_height);
    $: jsonLetterSpacing = rootCtx.getDerivedFromVars(json.letter_spacing);
    $: jsonTextColor = rootCtx.getDerivedFromVars(json.text_color);
    $: jsonHighlightColor = rootCtx.getDerivedFromVars(json.highlight_color);
    $: jsonAlignmentHorizontal = rootCtx.getDerivedFromVars(json.text_alignment_horizontal);
    $: jsonAlignmentVertical = rootCtx.getDerivedFromVars(json.text_alignment_vertical);
    $: jsonKeyboardType = rootCtx.getDerivedFromVars(json.keyboard_type);
    $: jsonMask = rootCtx.getDerivedFromVars(json.mask);
    $: jsonVisibleMaxLines = rootCtx.getDerivedFromVars(json.max_visible_lines);
    $: jsonPaddings = rootCtx.getDerivedFromVars(json.paddings);
    $: jsonAccessibility = rootCtx.getDerivedFromVars(json.accessibility);
    $: jsonSelectAll = rootCtx.getDerivedFromVars(json.select_all_on_focus);

    $: if (variable) {
        hasError = false;
    } else {
        hasError = true;
        rootCtx.logError(wrapError(new Error('Missing "text_variable" in "input"')));
    }

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

    $: placeholder = $jsonHintText;

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
            letterSpacing = pxToEm($jsonLetterSpacing);
        }
    }

    $: {
        textColor = correctColor($jsonTextColor, 1, textColor);
    }

    $: {
        highlightColor = correctColor($jsonHighlightColor, 1, highlightColor);
    }

    $: {
        alignmentHorizontal = correctAlignmentHorizontal($jsonAlignmentHorizontal, $direction, alignmentHorizontal);
    }

    $: {
        alignmentVertical = correctAlignmentVertical($jsonAlignmentVertical, alignmentVertical);
    }

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

    $: isMultiline = keyboardType === 'multi_line_text'/* && isPositiveNumber($jsonVisibleMaxLines) && $jsonVisibleMaxLines > 1*/;

    $: {
        if (isPositiveNumber($jsonVisibleMaxLines)) {
            maxHeight = `calc(${$jsonVisibleMaxLines * (lineHeight || 1.25) * (fontSize / 10) + 'em'} + ${pxToEmWithUnits(correctNonNegativeNumber($jsonPaddings?.top, 0) + correctNonNegativeNumber($jsonPaddings?.bottom, 0))})`;
        } else {
            maxHeight = '';
        }
        selfPadding = correctEdgeInsertsObject(($jsonPaddings) ? $jsonPaddings : undefined, selfPadding);
        padding = selfPadding ? edgeInsertsToCss({
            top: (Number(selfPadding.top) || 0) / fontSize * 10,
            right: (Number($direction === 'ltr' ? selfPadding.end : selfPadding.start) || Number(selfPadding.right) || 0) / fontSize * 10,
            bottom: (Number(selfPadding.bottom) || 0) / fontSize * 10,
            left: (Number($direction === 'ltr' ? selfPadding.start : selfPadding.end) || Number(selfPadding.left) || 0) / fontSize * 10
        }, $direction) : '';
        verticalPadding = selfPadding ? edgeInsertsToCss({
            top: (Number(selfPadding.top) || 0) / fontSize * 10,
            bottom: (Number(selfPadding.bottom) || 0) / fontSize * 10
        }, $direction) : '';
    }

    $: if ($jsonAccessibility?.description) {
        description = $jsonAccessibility.description;
    } else {
        rootCtx.logError(wrapError(new Error('Missing accessibility "description" for input'), {
            level: 'warn'
        }));
    }

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

    $: if (input && json) {
        if (prevId) {
            rootCtx.unregisterFocusable(prevId);
            prevId = undefined;
        }

        if (json.id && !layoutParams?.fakeElement) {
            prevId = json.id;
            rootCtx.registerFocusable(json.id, {
                focus() {
                    if (input) {
                        input.focus();
                    }
                }
            });
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

    onDestroy(() => {
        if (prevId) {
            rootCtx.unregisterFocusable(prevId);
            prevId = undefined;
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
        hasInnerFocusable={true}
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
                <!-- svelte-ignore a11y-no-static-element-interactions -->
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
