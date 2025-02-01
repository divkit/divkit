<script lang="ts" context="module">
    const isSupportInputMode = typeof document !== 'undefined' && 'inputMode' in document.createElement('input');

    const KEYBOARD_MAP: Record<KeyboardType, string> = {
        email: 'email',
        number: 'number',
        phone: 'tel',
        single_line_text: 'text',
        multi_line_text: 'text',
        uri: 'url',
        password: 'password'
    };

    const ALLOWED_BLOCKED_MULTILINE_KEYS = new Set([
        'Backspace',
        'Delete',
        'Tab',
        'ArrowLeft',
        'ArrowRight',
        'ArrowUp',
        'ArrowDown',
        'Home',
        'End',
        'Control',
        'Shift',
        'Alt',
        'Command',
        'Meta',
        'Escape',
    ]);
</script>

<script lang="ts">
    import { getContext, onDestroy, onMount, tick } from 'svelte';
    import type { HTMLAttributes } from 'svelte/elements';

    import css from './Input.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivInputData, InputEnterKeyType, KeyboardType } from '../../types/input';
    import type { EdgeInsets } from '../../types/edgeInserts';
    import type { FixedLengthInputMask } from '../../utils/mask/fixedLengthInputMask';
    import type { MaybeMissing } from '../../expressions/json';
    import type { InputMask } from '../../types/input';
    import type { AlignmentHorizontal } from '../../types/alignment';
    import type { ComponentContext } from '../../types/componentContext';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { genClassName } from '../../utils/genClassName';
    import { pxToEm, pxToEmWithUnits } from '../../utils/pxToEm';
    import { wrapError } from '../../utils/wrapError';
    import { correctColor } from '../../utils/correctColor';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { correctFontWeight } from '../../utils/correctFontWeight';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { isNumber } from '../../utils/isNumber';
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
    import { type AlignmentVerticalMapped, correctAlignmentVertical } from '../../utils/correctAlignmentVertical';
    import { calcSelectionOffset, setSelectionOffset } from '../../utils/contenteditable';
    import { correctBooleanInt } from '../../utils/correctBooleanInt';
    import { filterEnabledActions } from '../../utils/filterEnabledActions';
    import Outer from '../utilities/Outer.svelte';
    import DevtoolHolder from '../utilities/DevtoolHolder.svelte';

    export let componentContext: ComponentContext<DivInputData>;
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
    let isEnabled = true;
    let maxLength = Infinity;
    let autocapitalization: 'characters' | 'off' | 'on' | 'none' | 'sentences' | 'words' = 'off';
    let enterKeyType: InputEnterKeyType = 'default';
    let describedBy = '';
    let mounted = false;
    let validatorsFirstRun = true;
    let selectionStart = 0;
    let selectionEnd = 0;

    $: origJson = componentContext.origJson;

    function rebind(): void {
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
        isEnabled = true;
        maxLength = Infinity;
        autocapitalization = 'off';
        enterKeyType = 'default';
        describedBy = '';
        selectionStart = 0;
        selectionEnd = 0;
    }

    $: if (origJson) {
        rebind();
    }

    $: variable = componentContext.json.text_variable;
    $: rawVariable = componentContext.json.mask?.raw_text_variable;

    $: valueVariable = variable && componentContext.getVariable(variable, 'string') || createVariable('temp', 'string', '');
    $: rawValueVariable = rawVariable && componentContext.getVariable(rawVariable, 'string') || createVariable('temp', 'string', '');

    $: jsonHintText = componentContext.getDerivedFromVars(componentContext.json.hint_text);
    $: jsonHintColor = componentContext.getDerivedFromVars(componentContext.json.hint_color);
    $: jsonFontSize = componentContext.getDerivedFromVars(componentContext.json.font_size);
    $: jsonFontWeight = componentContext.getDerivedFromVars(componentContext.json.font_weight);
    $: jsonFontWeightValue = componentContext.getDerivedFromVars(componentContext.json.font_weight_value);
    $: jsonFontFamily = componentContext.getDerivedFromVars(componentContext.json.font_family);
    $: jsonLineHeight = componentContext.getDerivedFromVars(componentContext.json.line_height);
    $: jsonLetterSpacing = componentContext.getDerivedFromVars(componentContext.json.letter_spacing);
    $: jsonTextColor = componentContext.getDerivedFromVars(componentContext.json.text_color);
    $: jsonHighlightColor = componentContext.getDerivedFromVars(componentContext.json.highlight_color);
    $: jsonAlignmentHorizontal = componentContext.getDerivedFromVars(componentContext.json.text_alignment_horizontal);
    $: jsonAlignmentVertical = componentContext.getDerivedFromVars(componentContext.json.text_alignment_vertical);
    $: jsonKeyboardType = componentContext.getDerivedFromVars(componentContext.json.keyboard_type);
    $: jsonMask = componentContext.getDerivedFromVars(componentContext.json.mask);
    $: jsonVisibleMaxLines = componentContext.getDerivedFromVars(componentContext.json.max_visible_lines);
    $: jsonPaddings = componentContext.getDerivedFromVars(componentContext.json.paddings);
    $: jsonAccessibility = componentContext.getDerivedFromVars(componentContext.json.accessibility);
    $: jsonSelectAll = componentContext.getDerivedFromVars(componentContext.json.select_all_on_focus);
    $: jsonIsEnabled = componentContext.getDerivedFromVars(componentContext.json.is_enabled);
    $: jsonMaxLength = componentContext.getDerivedFromVars(componentContext.json.max_length);
    $: jsonAutocapitalization = componentContext.getDerivedFromVars(componentContext.json.autocapitalization);
    $: jsonEnterKeyType = componentContext.getDerivedFromVars(componentContext.json.enter_key_type);
    $: jsonValidators = componentContext.getDerivedFromVars(componentContext.json.validators);
    $: jsonFilters = componentContext.getDerivedFromVars(componentContext.json.filters);

    $: if (variable) {
        hasError = false;
    } else {
        hasError = true;
        componentContext.logError(wrapError(new Error('Missing "text_variable" in "input"')));
    }

    function updateMaskData(mask: MaybeMissing<InputMask> | undefined): void {
        if (mask?.type === 'fixed_length') {
            inputMask = updateFixedMask(mask, componentContext.logError, inputMask as FixedLengthInputMask);
        } else if (mask?.type === 'currency') {
            inputMask = updateCurrencyMask(mask, componentContext.logError, inputMask as CurrencyInputMask);
        }

        if (inputMask) {
            runRawValueMask();
        }
    }
    $: updateMaskData($jsonMask);

    $: if (!inputMask && value !== $valueVariable) {
        let val = $valueVariable;
        if (val.length > maxLength) {
            val = val.slice(0, maxLength);
            valueVariable.setValue(val);
        }
        value = contentEditableValue = val;
        runValidators();
    }

    $: if (inputMask && inputMask.rawValue !== $rawValueVariable) {
        runRawValueMask();
        runValidators();
    }

    $: if ($jsonValidators && mounted) {
        runValidators();
    }

    $: placeholder = $jsonHintText;

    $: {
        hintColor = correctColor($jsonHintColor, 1, hintColor);
    }

    $: {
        fontSize = correctPositiveNumber($jsonFontSize, fontSize);
    }

    $: {
        fontWeight = correctFontWeight($jsonFontWeight, $jsonFontWeightValue, fontWeight);
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
        isEnabled = correctBooleanInt($jsonIsEnabled, isEnabled);
    }

    $: {
        maxLength = correctPositiveNumber($jsonMaxLength, maxLength);
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

    $: if ($jsonAutocapitalization === 'all_characters') {
        autocapitalization = 'characters';
    } else if ($jsonAutocapitalization === 'sentences') {
        autocapitalization = 'sentences';
    } else if ($jsonAutocapitalization === 'words') {
        autocapitalization = 'words';
    } else if ($jsonAutocapitalization === 'none' || $jsonAutocapitalization === 'auto') {
        autocapitalization = 'off';
    }

    $: if ($jsonAccessibility?.description) {
        description = $jsonAccessibility.description;
    } else {
        componentContext.logError(wrapError(new Error('Missing accessibility "description" for input'), {
            level: 'warn'
        }));
    }

    $: if (
        $jsonEnterKeyType === 'default' || $jsonEnterKeyType === 'done' || $jsonEnterKeyType === 'go' ||
        $jsonEnterKeyType === 'search' || $jsonEnterKeyType === 'send'
    ) {
        enterKeyType = $jsonEnterKeyType;
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

    function checkFilters(val: string): boolean {
        if (!Array.isArray($jsonFilters) || !val) {
            return true;
        }

        for (const filter of $jsonFilters) {
            if (!filter) {
                continue;
            }
            if (filter.type === 'regex') {
                try {
                    const re = new RegExp('^' + (filter.pattern || '') + '$');
                    if (!re.test(val)) {
                        return false;
                    }
                } catch (err) {
                    componentContext.logError(wrapError(new Error('Failed to create a regex'), {
                        additional: {
                            originalError: String(err)
                        }
                    }));
                    return true;
                }
            } else if (filter.type === 'expression') {
                if (!filter.condition) {
                    return false;
                }
            }
        }
        return true;
    }

    function onInput(event: Event): void {
        const input = event.target;
        let val = (isMultiline ?
            (input as HTMLDivElement).innerText :
            (input as HTMLInputElement).value
        ) || '';

        if (val === '\n') {
            val = '';
        }

        if (val.length > maxLength) {
            val = contentEditableValue = value;
            if (input instanceof HTMLInputElement) {
                input.value = val;
            }
        }

        if (value !== val) {
            if (checkFilters(val)) {
                value = contentEditableValue = val;
                valueVariable.setValue(val);
                if (inputMask) {
                    runValueMask();
                }
                runValidators();
            } else {
                value = contentEditableValue = val;
                if (input instanceof HTMLInputElement) {
                    input.value = val;
                }
                tick().then(() => {
                    setCursorPosition(selectionStart, selectionEnd);
                });
            }
        }
    }

    function blockOverflow(event: KeyboardEvent): void {
        if (
            value.length >= maxLength &&
            !ALLOWED_BLOCKED_MULTILINE_KEYS.has(event.key) &&
            !(event.ctrlKey || event.altKey || event.metaKey)
        ) {
            event.preventDefault();
        }
    }

    function onKeyDown(event: KeyboardEvent): void {
        selectionStart = getSelectionStart() || 0;
        selectionEnd = getSelectionEnd() || 0;

        if (event.ctrlKey || event.metaKey || event.altKey || event.shiftKey) {
            return;
        }

        const actions = componentContext.json.enter_key_actions;
        if (event.key === 'Enter' && Array.isArray(actions) && actions.length) {
            const evalledActions = componentContext.getJsonWithVars(actions);
            const filteredActions = evalledActions.filter(action => action.log_id).filter(filterEnabledActions);
            event.preventDefault();
            componentContext.execAnyActions(filteredActions);
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

    function getSelectionStart(): number | undefined {
        if (input instanceof HTMLInputElement) {
            return input.selectionStart === null ? undefined : input.selectionStart;
        }

        return calcSelectionOffset(input, 'start');
    }

    function getSelectionEnd(): number | undefined {
        if (input instanceof HTMLInputElement) {
            return input.selectionEnd === null ? undefined : input.selectionEnd;
        }

        return calcSelectionOffset(input, 'end');
    }

    function setCursorPosition(start: number, end: number): void {
        if (input instanceof HTMLInputElement) {
            input.selectionStart = start;
            input.selectionEnd = end;
        } else {
            const sel = window.getSelection();
            if (sel) {
                sel.removeAllRanges();
                const range = document.createRange();
                setSelectionOffset(input, range, 'start', start);
                setSelectionOffset(input, range, 'end', end);
                sel.addRange(range);
            }
        }
    }

    async function runValueMask(): Promise<void> {
        if (!input || !inputMask) {
            return;
        }

        const start = getSelectionStart() || 0;
        const end = getSelectionEnd() || 0;

        inputMask.applyChangeFrom(value, end === start ? end : 0);

        rawValueVariable.set(inputMask.rawValue);
        $valueVariable = value = contentEditableValue = inputMask.value;
        const cursorPosition = inputMask.cursorPosition;

        await tick();

        if (document.activeElement === input) {
            setCursorPosition(cursorPosition, cursorPosition);
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
            setCursorPosition(cursorPosition, cursorPosition);
        }
    }

    function runValidators(): void {
        const isFirstRun = validatorsFirstRun;
        validatorsFirstRun = false;

        const validators = componentContext.json.validators;
        if (!Array.isArray(validators) || !validators.length) {
            return;
        }

        const evalledValidators = componentContext.getJsonWithVars(validators);
        const filtered = evalledValidators.filter(it => (it.type === 'regex' || it.type === 'expression') && it.label_id && it.variable);
        const describeList: string[] = [];

        filtered.forEach(validator => {
            const variable = componentContext.getVariable(validator.variable as string);
            if (!variable) {
                return;
            }

            if (variable.getType() !== 'boolean') {
                if (isFirstRun) {
                    componentContext.logError(wrapError(new Error('Incorrect variable type for the validator'), {
                        additional: {
                            variable: validator.variable
                        }
                    }));
                }
                return;
            }

            let isValid = false;
            if (value === '' && (validator.allow_empty === true || validator.allow_empty === 1)) {
                isValid = true;
            } else if (validator.type === 'regex') {
                if (!validator.pattern || typeof validator.pattern !== 'string') {
                    return;
                }
                try {
                    const re = new RegExp('^' + validator.pattern + '$');
                    isValid = re.test(value);
                } catch (err) {
                    if (isFirstRun) {
                        componentContext.logError(wrapError(new Error('Failed to create a regular expression using the validator pattern'), {
                            additional: {
                                pattern: validator.pattern
                            }
                        }));
                    }
                    return;
                }
            } else if (validator.type === 'expression') {
                isValid = validator.condition === true || validator.condition === 1;
            } else {
                return;
            }

            variable.setValue(isValid);

            if (!isValid) {
                const htmlId = rootCtx.getComponentId(validator.label_id as string);
                if (htmlId) {
                    describeList.push(htmlId);
                }
            }
        });
        describedBy = describeList.join(' ');
    }

    $: if (input && componentContext.json) {
        if (prevId) {
            rootCtx.unregisterFocusable(prevId);
            prevId = undefined;
        }

        if (componentContext.id && !componentContext.fakeElement) {
            prevId = componentContext.id;
            rootCtx.registerFocusable(prevId, {
                focus() {
                    if (input) {
                        input.focus();
                        setCursorPosition(value.length, value.length);
                    }
                }
            });
        }
    }

    onMount(() => {
        mounted = true;

        if (input && inputMask) {
            if ($rawValueVariable) {
                inputMask.overrideRawValue($rawValueVariable);
                $valueVariable = value = contentEditableValue = inputMask.value;
            }
        }
    });

    onDestroy(() => {
        mounted = false;

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
        {componentContext}
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
                    class={css.input__aligner}
                    aria-hidden="true"
                    style={makeStyle(verticalPaddingStl)}
                >​</span>

                {#if isEnabled}
                    <span
                        bind:this={input}
                        class={genClassName('input__input', css, { 'has-custom-focus': hasCustomFocus, multiline: true })}
                        autocapitalize={autocapitalization}
                        contenteditable="true"
                        role="textbox"
                        tabindex="0"
                        aria-label={description}
                        aria-multiline="true"
                        enterkeyhint={enterKeyType === 'default' ? undefined : enterKeyType}
                        aria-describedby={describedBy || undefined}
                        style={makeStyle(paddingStl)}
                        bind:innerText={contentEditableValue}
                        on:input={onInput}
                        on:keydown={blockOverflow}
                        on:keydown={onKeyDown}
                        on:paste={onPaste}
                        on:mousedown={$jsonSelectAll ? onMousedown : undefined}
                        on:click={$jsonSelectAll ? onClick : undefined}
                        on:focus={focusHandler}
                        on:blur={blurHandler}
                    >
                    </span>
                {:else}
                    <span
                        bind:this={input}
                        class={genClassName('input__input', css, { multiline: true })}
                        autocapitalize={autocapitalization}
                        contenteditable="false"
                        role="textbox"
                        aria-label={description}
                        aria-disabled="true"
                        aria-multiline="true"
                        aria-describedby={describedBy || undefined}
                        style={makeStyle(paddingStl)}
                        bind:innerText={contentEditableValue}
                    >
                    </span>
                {/if}
            </span>
        {:else}
            <input
                bind:this={input}
                type={inputType}
                inputmode={inputMode}
                class={genClassName('input__input', css, { 'has-custom-focus': hasCustomFocus, singleline: true })}
                autocomplete="off"
                autocapitalize={autocapitalization}
                aria-label={description}
                aria-describedby={describedBy || undefined}
                style={makeStyle(paddingStl)}
                disabled={!isEnabled}
                maxlength={maxLength === Infinity ? undefined : maxLength}
                {placeholder}
                {value}
                enterkeyhint={enterKeyType === 'default' ? undefined : enterKeyType}
                on:input={onInput}
                on:keydown={onKeyDown}
                on:mousedown={$jsonSelectAll ? onMousedown : undefined}
                on:click={$jsonSelectAll ? onClick : undefined}
                on:focus={focusHandler}
                on:blur={blurHandler}
            >
        {/if}
    </Outer>
{:else if process.env.DEVTOOL}
    <DevtoolHolder
        {componentContext}
    />
{/if}
