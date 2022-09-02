<script lang="ts">
    import { getContext, onMount } from 'svelte';

    import css from './Input.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivInputData, KeyboardType } from '../../types/input';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { genClassName } from '../../utils/genClassName';
    import { pxToEm } from '../../utils/pxToEm';
    import { wrapError } from '../../utils/wrapError';
    import { correctColor } from '../../utils/correctColor';
    import { correctPositiveNumber } from '../../utils/correctPositiveNumber';
    import { correctFontWeight } from '../../utils/correctFontWeight';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { isNumber } from '../../utils/isNumber';
    import Outer from '../utilities/Outer.svelte';
    import { createVariable } from '../../expressions/variable';

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
    let textColor = '';
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
        text: 'text',
        uri: 'url'
    };
    let keyboard = 'text';
    $: {
        if ($jsonKeyboardType && $jsonKeyboardType in KEYBOARD_MAP) {
            keyboard = KEYBOARD_MAP[$jsonKeyboardType as KeyboardType];
        }
    }

    const jsonMaxLines = rootCtx.getDerivedFromVars(json.max_lines);
    // text always multiline
    $: isMultiline = keyboard === 'text'/* && isPositiveNumber($jsonMaxLines) && $jsonMaxLines > 1*/;

    let maxHeight = '';
    $: {
        if (isPositiveNumber($jsonMaxLines)) {
            maxHeight = $jsonMaxLines * (lineHeight || 1.25) + 'em';
        }
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
        verticalOverflow = holder.scrollHeight > holder.offsetHeight;
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
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
    >
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
                    type={keyboard}
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
    </Outer>
{/if}
