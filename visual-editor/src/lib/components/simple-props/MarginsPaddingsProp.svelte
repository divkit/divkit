<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import type { ComponentProperty } from '../../data/componentProps';
    import { getProp } from '../../data/props';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    const { directionSelector, state } = getContext<AppContext>(APP_CTX);
    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { direction, highlightMode, readOnly } = state;

    interface EdgeInsetsObj {
        top?: number;
        right?: number;
        bottom?: number;
        left?: number;
        start?: number;
        end?: number;
    }

    export let item: ComponentProperty;
    export let processedJson;

    $: paddings = getProp(processedJson, 'paddings');
    $: margins = getProp(processedJson, 'margins');

    const dispatch = createEventDispatcher();

    let paddingTop = paddings?.top ?? undefined;
    let paddingRight = paddings?.right ?? undefined;
    let paddingBottom = paddings?.bottom ?? undefined;
    let paddingLeft = paddings?.left ?? undefined;

    let marginTop = margins?.top ?? undefined;
    let marginRight = margins?.right ?? undefined;
    let marginBottom = margins?.bottom ?? undefined;
    let marginLeft = margins?.left ?? undefined;

    function updatePaddings(value: EdgeInsetsObj | undefined, direction: 'ltr' | 'rtl'): void {
        const vals = edgeInsetsToVals(value, direction);

        paddingTop = vals.top;
        paddingRight = vals.right;
        paddingBottom = vals.bottom;
        paddingLeft = vals.left;
    }

    function updateMargins(value: EdgeInsetsObj | undefined, direction: 'ltr' | 'rtl'): void {
        const vals = edgeInsetsToVals(value, direction);

        marginTop = vals.top;
        marginRight = vals.right;
        marginBottom = vals.bottom;
        marginLeft = vals.left;
    }

    $: updatePaddings(paddings, $direction);
    $: updateMargins(margins, $direction);

    function edgeInsetsToVals(insets: EdgeInsetsObj | undefined, direction: 'ltr' | 'rtl'): EdgeInsetsObj {
        if (!insets) {
            return {
                top: undefined,
                right: undefined,
                bottom: undefined,
                left: undefined
            };
        }

        const top = insets.top;
        const bottom = insets.bottom;
        const left = (direction === 'ltr' ? insets.start : insets.end) ?? insets.left;
        const right = (direction === 'ltr' ? insets.end : insets.start) ?? insets.right;

        return {
            top,
            right,
            bottom,
            left
        };
    }

    function valsToEdgeInsets(vals: EdgeInsetsObj): EdgeInsetsObj | undefined {
        if (!(vals.top || vals.right || vals.bottom || vals.left)) {
            return;
        }

        if (directionSelector) {
            return {
                top: vals.top,
                bottom: vals.bottom,
                start: $direction === 'ltr' ? vals.left : vals.right,
                end: $direction === 'ltr' ? vals.right : vals.left,
            };
        }

        return vals;
    }

    function onChange(type: 'paddings' | 'margins') {
        $highlightMode = type;

        if (type === 'paddings') {
            paddings = valsToEdgeInsets({
                top: paddingTop ?? undefined,
                right: paddingRight ?? undefined,
                bottom: paddingBottom ?? undefined,
                left: paddingLeft ?? undefined
            });

            dispatch('change', {
                values: [{
                    prop: 'paddings',
                    value: paddings
                }],
                item
            });
        } else {
            margins = valsToEdgeInsets({
                top: marginTop ?? undefined,
                right: marginRight ?? undefined,
                bottom: marginBottom ?? undefined,
                left: marginLeft ?? undefined
            });

            dispatch('change', {
                values: [{
                    prop: 'margins',
                    value: margins
                }],
                item
            });
        }
    }
</script>

<div
    class="margins-paddings-prop"
    class:margins-paddings-prop_hover_margins={$highlightMode === 'margins'}
    class:margins-paddings-prop_hover_paddings={$highlightMode === 'paddings'}
    class:margins-paddings-prop_disabled={$readOnly}
    on:blur|capture={() => $highlightMode = ''}
>
    <div class="margins-paddings-prop__label margins-paddings-prop__label_margins">
        {$l10n('props.margins')}
    </div>
    <div class="margins-paddings-prop__label margins-paddings-prop__label_paddings">
        {$l10n('props.paddings')}
    </div>

    <!-- svelte-ignore a11y-no-static-element-interactions -->
    <div
        class="margins-paddings-prop__cell margins-paddings-prop__cell_input margins-paddings-prop__cell_margins-top"
        on:mouseenter={() => $highlightMode = 'margins'}
        on:mouseleave={() => $highlightMode = ''}
    >
        <input
            type="number"
            min="0"
            step="1"
            pattern="\d+"
            class="margins-paddings-prop__input"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            placeholder="-"
            disabled={$readOnly}
            bind:value={marginTop}
            on:input={() => onChange('margins')}
        />
    </div>

    <!-- svelte-ignore a11y-no-static-element-interactions -->
    <div
        class="margins-paddings-prop__cell margins-paddings-prop__cell_input margins-paddings-prop__cell_paddings-top"
        on:mouseenter={() => $highlightMode = 'paddings'}
        on:mouseleave={() => $highlightMode = ''}
    >
        <input
            type="number"
            min="0"
            step="1"
            pattern="\d+"
            class="margins-paddings-prop__input"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            placeholder="-"
            disabled={$readOnly}
            bind:value={paddingTop}
            on:input={() => onChange('paddings')}
        />
    </div>

    <!-- svelte-ignore a11y-no-static-element-interactions -->
    <div
        class="margins-paddings-prop__cell margins-paddings-prop__cell_input margins-paddings-prop__cell_margins-left"
        on:mouseenter={() => $highlightMode = 'margins'}
        on:mouseleave={() => $highlightMode = ''}
    >
        <input
            type="number"
            min="0"
            step="1"
            pattern="\d+"
            class="margins-paddings-prop__input"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            placeholder="-"
            disabled={$readOnly}
            bind:value={marginLeft}
            on:input={() => onChange('margins')}
        />
    </div>
    <!-- svelte-ignore a11y-no-static-element-interactions -->
    <div
        class="margins-paddings-prop__cell margins-paddings-prop__cell_input margins-paddings-prop__cell_paddings-left"
        on:mouseenter={() => $highlightMode = 'paddings'}
        on:mouseleave={() => $highlightMode = ''}
    >
        <input
            type="number"
            min="0"
            step="1"
            pattern="\d+"
            class="margins-paddings-prop__input"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            placeholder="-"
            disabled={$readOnly}
            bind:value={paddingLeft}
            on:input={() => onChange('paddings')}
        />
    </div>
    <!-- svelte-ignore a11y-no-static-element-interactions -->
    <div
        class="margins-paddings-prop__cell margins-paddings-prop__cell_input margins-paddings-prop__cell_paddings-right"
        on:mouseenter={() => $highlightMode = 'paddings'}
        on:mouseleave={() => $highlightMode = ''}
    >
        <input
            type="number"
            min="0"
            step="1"
            pattern="\d+"
            class="margins-paddings-prop__input"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            placeholder="-"
            disabled={$readOnly}
            bind:value={paddingRight}
            on:input={() => onChange('paddings')}
        />
    </div>
    <!-- svelte-ignore a11y-no-static-element-interactions -->
    <div
        class="margins-paddings-prop__cell margins-paddings-prop__cell_input margins-paddings-prop__cell_margins-right"
        on:mouseenter={() => $highlightMode = 'margins'}
        on:mouseleave={() => $highlightMode = ''}
    >
        <input
            type="number"
            min="0"
            step="1"
            pattern="\d+"
            class="margins-paddings-prop__input"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            placeholder="-"
            disabled={$readOnly}
            bind:value={marginRight}
            on:input={() => onChange('margins')}
        />
    </div>

    <!-- svelte-ignore a11y-no-static-element-interactions -->
    <div
        class="margins-paddings-prop__cell margins-paddings-prop__cell_input margins-paddings-prop__cell_paddings-bottom"
        on:mouseenter={() => $highlightMode = 'paddings'}
        on:mouseleave={() => $highlightMode = ''}
    >
        <input
            type="number"
            min="0"
            step="1"
            pattern="\d+"
            class="margins-paddings-prop__input"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            placeholder="-"
            disabled={$readOnly}
            bind:value={paddingBottom}
            on:input={() => onChange('paddings')}
        />
    </div>

    <!-- svelte-ignore a11y-no-static-element-interactions -->
    <div
        class="margins-paddings-prop__cell margins-paddings-prop__cell_input margins-paddings-prop__cell_margins-bottom"
        on:mouseenter={() => $highlightMode = 'margins'}
        on:mouseleave={() => $highlightMode = ''}
    >
        <input
            type="number"
            min="0"
            step="1"
            pattern="\d+"
            class="margins-paddings-prop__input"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            placeholder="-"
            disabled={$readOnly}
            bind:value={marginBottom}
            on:input={() => onChange('margins')}
        />
    </div>

    <!-- svelte-ignore a11y-no-static-element-interactions -->
    <div
        class="margins-paddings-prop__cell margins-paddings-prop__cell_input margins-paddings-prop__cell_border-box"
        on:mouseenter={() => $highlightMode = 'margins'}
        on:mouseleave={() => $highlightMode = ''}
    >
    </div>

    <!-- svelte-ignore a11y-no-static-element-interactions -->
    <div
        class="margins-paddings-prop__cell margins-paddings-prop__cell_input margins-paddings-prop__cell_content-box"
        on:mouseenter={() => $highlightMode = 'paddings'}
        on:mouseleave={() => $highlightMode = ''}
    >
    </div>

    <div class="margins-paddings-prop__cell margins-paddings-prop__cell_input margins-paddings-prop__cell_center">
        {$l10n('inset_content')}
    </div>
</div>

<style>
    .margins-paddings-prop {
        display: grid;
        grid-template-columns: repeat(5, 1fr);
        grid-template-rows: 1fr auto repeat(4, 1fr);
    }

    .margins-paddings-prop__label {
        position: relative;
        z-index: 1;
        margin: 4px 8px;
        font-size: 12px;
        line-height: 16px;
        color: var(--text-secondary);
        pointer-events: none;
    }

    .margins-paddings-prop__label_margins {
        grid-area: 1 / 1 / 2 / 2;
    }

    .margins-paddings-prop__label_paddings {
        grid-area: 2 / 2 / 3 / 5;
    }

    .margins-paddings-prop__cell_input {
        display: grid;
        align-content: center;
        justify-content: center;
    }

    .margins-paddings-prop__cell_margins-top {
        grid-area: 1 / 3 / 2 / 4;
    }

    .margins-paddings-prop__cell_margins-right {
        grid-area: 4 / 5 / 5 / 6;
    }

    .margins-paddings-prop__cell_margins-bottom {
        grid-area: 6 / 3 / 7 / 4;
    }

    .margins-paddings-prop__cell_margins-left {
        grid-area: 4 / 1 / 5 / 2;
    }

    .margins-paddings-prop__cell_paddings-top {
        grid-area: 3 / 3 / 4 / 4;
    }

    .margins-paddings-prop__cell_paddings-right {
        grid-area: 4 / 4 / 5 / 5;
    }

    .margins-paddings-prop__cell_paddings-bottom {
        grid-area: 5 / 3 / 6 / 4;
    }

    .margins-paddings-prop__cell_paddings-left {
        grid-area: 4 / 2 / 5 / 3;
    }

    .margins-paddings-prop__input {
        position: relative;
        z-index: 1;
        box-sizing: border-box;
        margin: 3px 4px;
        padding: 4px;
        font: inherit;
        font-size: 14px;
        color: inherit;
        width: 48px;
        height: 20px;
        border: 1px solid transparent;
        border-radius: 4px;
        background: none;
        appearance: none;
        -moz-appearance: textfield;
        text-align: center;
        transition: .15s ease-in-out;
        transition-property: border-color, background-color;
    }

    .margins-paddings-prop:not(.margins-paddings-prop_disabled) .margins-paddings-prop__input:hover {
        border-color: var(--fill-transparent-3);
        background-color: var(--background-primary);
    }

    .margins-paddings-prop__input:invalid {
        background: indianred;
    }

    .margins-paddings-prop__input::-webkit-outer-spin-button,
    .margins-paddings-prop__input::-webkit-inner-spin-button {
        display: none;
    }

    .margins-paddings-prop__input:focus-visible {
        background-color: var(--background-primary);
        outline: 1px solid var(--accent-purple);
    }

    .margins-paddings-prop__input:focus-visible:hover {
        outline-color: var(--accent-purple-hover);
    }

    .margins-paddings-prop__cell_border-box {
        grid-area: 1 / 1 / 7 / 6;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background-color: var(--fill-transparent-minus-1);
        transition: background-color .15s ease-in-out;
    }

    .margins-paddings-prop_hover_margins .margins-paddings-prop__cell_border-box {
        background-color: var(--fill-accent-2);
    }

    .margins-paddings-prop__cell_content-box {
        grid-area: 2 / 2 / 6 / 5;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background-color: var(--fill-transparent-minus-1);
        transition: background-color .15s ease-in-out;
    }

    .margins-paddings-prop_hover_paddings .margins-paddings-prop__cell_content-box {
        background-color: var(--fill-accent-2);
    }

    .margins-paddings-prop__cell_center {
        display: flex;
        align-items: center;
        justify-content: center;
        grid-area: 4 / 3 / 5 / 4;
        padding: 4px;
        background: var(--fill-transparent-1);
        border-radius: 4px;
        font-size: 10px;
        text-align: center;
        color: var(--text-secondary);
        user-select: none;
    }
</style>
