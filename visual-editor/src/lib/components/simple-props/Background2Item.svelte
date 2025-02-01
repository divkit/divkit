<script lang="ts">
    import { afterUpdate, createEventDispatcher, getContext, onDestroy } from 'svelte';
    import type { Background } from '../../data/background';
    import { parseColor, stringifyColorToCss, stringifyColorToDivKit } from '../../utils/colors';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { isPaletteColor, valueToPaletteId, type PaletteItem } from '../../data/palette';
    import ColorPreview from '../ColorPreview.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let value: Background;

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state, background2Dialog } = getContext<AppContext>(APP_CTX);
    const {
        palette,
        previewThemeStore,
        readOnly,
        highlightMode,
        highlightGradientAngle
    } = state;

    const dispatch = createEventDispatcher();

    let elem: HTMLElement;
    let preview = '';
    let firstVal = '';
    let secondVal = '';
    let paletteId = '';
    let paletteItem: PaletteItem | null = null;
    let max = 100;
    let isSelfUpdate = false;
    let error = '';
    let colorInputElem: HTMLInputElement;

    function updateVal(value: Background): void {
        if (isSelfUpdate || colorInputElem === document.activeElement) {
            return;
        }
        error = '';
        if (value.type === 'solid') {
            preview = '';

            if (isPaletteColor(value.color)) {
                paletteId = valueToPaletteId(value.color);
                paletteItem = $palette.find(it => it.id === paletteId) || null;

                if (!paletteItem) {
                    error = $l10nString('missingPaletteColor').replace('%s', () => paletteId);
                }
            } else {
                paletteId = '';
                paletteItem = null;
                const parsed = value.color && parseColor(value.color);
                if (parsed) {
                    secondVal = String(Math.round(parsed.a / 255 * 100));
                    parsed.a = 255;
                    firstVal = stringifyColorToDivKit(parsed);
                } else {
                    firstVal = '';
                    secondVal = '';
                }
                max = 100;
            }
        } else if (value.type === 'gradient') {
            firstVal = '';
            secondVal = String(value.angle || 0);
            if (Array.isArray(value.colors)) {
                preview = `linear-gradient(${90 - (value.angle || 0)}deg, ${value.colors.map(color => {
                    const parsed = parseColor(color);
                    if (!parsed) {
                        return 'transparent';
                    }
                    return stringifyColorToCss(parsed);
                }).join(', ')})`;
            } else {
                preview = '';
            }
            max = 360;
        } else if (value.type === 'image') {
            firstVal = '';
            secondVal = String(Math.round((value.alpha ?? 1) * 100));
            preview = `url("${value.image_url}")`;
            max = 100;
        }
    }

    $: updateVal(value);

    $: if (value.type === 'solid' && paletteItem && $previewThemeStore) {
        preview = `linear-gradient(to bottom, ${paletteItem[$previewThemeStore]}, ${paletteItem[$previewThemeStore]})`;
    }

    function rebuildVal(): void {
        if (value.type === 'solid') {
            const parsed = parseColor(firstVal);
            if (parsed) {
                parsed.a = Math.round(Number(secondVal) * 255 / 100);
                isSelfUpdate = true;
                value.color = stringifyColorToDivKit(parsed);
                dispatch('change');
            }
        } else if (value.type === 'gradient') {
            isSelfUpdate = true;
            value.angle = Number(secondVal);
            highlightGradientAngle.set(value.angle);
            dispatch('change');
        } else if (value.type === 'image') {
            isSelfUpdate = true;
            value.alpha = Number(secondVal) / 100;
            dispatch('change');
        }
    }

    function onSecondValFocus(): void {
        if (value.type === 'gradient') {
            highlightMode.set('gradient');
            highlightGradientAngle.set(value.angle || 0);
        }
    }

    function onSecondValBlur(): void {
        highlightMode.set('');
    }

    function onPreviewClick(): void {
        background2Dialog().show({
            value,
            target: elem,
            readOnly: $readOnly,
            callback(newValue) {
                value = newValue;
                dispatch('change');
            }
        });
    }

    afterUpdate(() => {
        if (isSelfUpdate) {
            isSelfUpdate = false;
        }
    });

    onDestroy(() => {
        background2Dialog().hide();
    });
</script>

<div
    class="background2-item"
    class:background2-item_readonly={$readOnly}
    class:background2-item_error={Boolean(error)}
    bind:this={elem}
    title={error}
>
    <button
        class="background2-item__preview"
        on:click={onPreviewClick}
    >
        {#if value.type === 'solid'}
            <ColorPreview color={value.color} mix="background2-item__preview-inner" />
        {:else}
            <div class="background2-item__preview-bg"></div>
            <div class="background2-item__preview-value" style:background-image="{preview}"></div>
            <div class="background2-item__preview-inset"></div>
        {/if}
    </button>

    {#if value.type === 'solid' && paletteId}
        <div class="background2-item__palette-name">
            {#if paletteItem}
                {paletteItem.name}
            {:else}
                {paletteId}
            {/if}
        </div>
    {:else}
        {#if value.type === 'solid'}
            <input
                class="background2-item__input"
                type="text"
                autocomplete="off"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                maxlength="7"
                bind:this={colorInputElem}
                bind:value={firstVal}
                on:input={rebuildVal}
                disabled={$readOnly}
                title={$l10nString(`background.${value.type}_first_title`)}
            >
        {:else}
            <button
                class="background2-item__text"
                on:click={onPreviewClick}
            >
                {#if value.type === 'gradient'}
                    {$l10nString('background.gradient')}
                {:else}
                    {$l10nString('background.image')}
                {/if}
            </button>
        {/if}

        <div class="background2-item__separator"></div>

        <input
            class="background2-item__second-input"
            type="number"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            min="0"
            disabled={$readOnly}
            {max}
            bind:value={secondVal}
            on:input={rebuildVal}
            on:focus={onSecondValFocus}
            on:blur={onSecondValBlur}
            title={$l10nString(`background.${value.type}_second_title`)}
        >

        <div class="background2-item__symbol">
            {#if value.type === 'gradient'}
                °
            {:else}
                %
            {/if}
        </div>
    {/if}
</div>

<style>
    .background2-item {
        position: relative;
        display: flex;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background-color: var(--background-primary);
        transition: border-color .15s ease-in-out;
    }

    .background2-item_readonly {
        border: none;
        background: var(--fill-transparent-1);
    }

    .background2-item_error {
        border-color: var(--accent-red);
    }

    .background2-item:not(.background2-item_readonly):hover {
        border-color: var(--fill-transparent-4);
    }

    .background2-item:not(.background2-item_readonly):focus-within {
        border-color: var(--accent-purple);
    }

    .background2-item__preview {
        position: relative;
        flex: 0 0 auto;
        width: 20px;
        height: 20px;
        margin: 9px 8px 9px 9px;
        border: none;
        border-radius: 4px;
        background: none;
        appearance: none;
        cursor: pointer;
        overflow: hidden;
    }

    .background2-item__preview:focus-visible {
        outline: 1px solid var(--accent-purple);
    }

    :global(.background2-item__preview-inner.background2-item__preview-inner.background2-item__preview-inner) {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
    }

    .background2-item__preview-bg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: url(../../../assets/alpha.png);
        background-size: 6px;
        opacity: .3;
    }

    .background2-item__preview-value {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-size: 100% 100%;
    }

    .background2-item__preview-inset {
        position: absolute;
        top: 0;
        left: 0;
        box-sizing: border-box;
        width: 100%;
        height: 100%;
        border-radius: 4px;
        border: 1px solid var(--fill-transparent-3);
    }

    .background2-item__input {
        flex: 1 1 auto;
        min-width: 0;
        margin: 0;
        padding: 0;
        font: inherit;
        font-size: 14px;
        line-height: 38px;
        color: inherit;
        background: none;
        border: none;
        appearance: none;
    }

    .background2-item__input:focus {
        outline: none;
    }

    .background2-item__text {
        display: flex;
        flex: 1 1 auto;
        align-items: center;
        font-size: 14px;
        border: none;
        background: none;
        appearance: none;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        cursor: pointer;
    }

    .background2-item__text:focus {
        outline: none;
    }

    .background2-item__separator {
        position: absolute;
        top: 9px;
        right: 69px;
        bottom: 9px;
        width: 1px;
        height: 20px;
        background: var(--fill-transparent-3);
    }

    .background2-item__second-input {
        width: 28px;
        padding: 0 33px 0 17px;
        flex: 0 0 auto;
        font: inherit;
        font-size: 14px;
        line-height: 38px;
        color: inherit;
        background: none;
        border: none;
        appearance: none;
        -moz-appearance: textfield;
    }

    .background2-item__second-input::-webkit-inner-spin-button,
    .background2-item__second-input::-webkit-outer-spin-button {
        display: none;
    }

    .background2-item__second-input:focus {
        outline: none;
    }

    .background2-item__symbol {
        position: absolute;
        top: 9px;
        right: 15px;
        bottom: 9px;
        display: flex;
        align-items: center;
        color: var(--text-secondary);
    }

    .background2-item__palette-name {
        flex: 1 1 auto;
        align-self: center;
        min-width: 0;
        font-size: 14px;
        text-overflow: ellipsis;
    }
</style>
