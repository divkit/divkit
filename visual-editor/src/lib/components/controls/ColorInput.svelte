<script lang="ts">
    import { afterUpdate, createEventDispatcher, getContext } from 'svelte';
    import { parseColor, stringifyColorToDivKit } from '../../utils/colors';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { isPaletteColor, valueToPaletteId, type PaletteItem, paletteIdToValue } from '../../data/palette';
    import ColorPreview from '../ColorPreview.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let value: string;
    export let hasDialog = false;
    export let readOnly = false;

    // todo validate input

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state, color2Dialog } = getContext<AppContext>(APP_CTX);
    const { palette } = state;

    const dispatch = createEventDispatcher();

    let elem: HTMLElement;
    let firstVal = '';
    let secondVal = '';
    let paletteId = '';
    let paletteItem: PaletteItem | null = null;
    let max = 100;
    let isSelfUpdate = false;

    function updateVal(value: string): void {
        if (isSelfUpdate) {
            return;
        }
        if (isPaletteColor(value)) {
            paletteId = valueToPaletteId(value);
            paletteItem = $palette.find(it => it.id === paletteId) || null;
        } else {
            paletteId = '';
            paletteItem = null;
            const parsed = value && parseColor(value);
            if (parsed) {
                secondVal = String(Math.round(parsed.a / 255 * 100));
                parsed.a = 255;
                firstVal = stringifyColorToDivKit(parsed);
            } else {
                firstVal = '';
                secondVal = '';
            }
        }
    }

    $: updateVal(value);

    function rebuildVal(): void {
        const parsed = parseColor(firstVal);
        if (parsed) {
            parsed.a = Math.round(Number(secondVal) * 255 / 100);
            isSelfUpdate = true;
            value = stringifyColorToDivKit(parsed);
            dispatch('change', {
                value
            });
        }
    }

    function onPreviewClick(): void {
        let val;
        let paletteId;

        if (isPaletteColor(value)) {
            val = '';
            paletteId = valueToPaletteId(value);
        } else {
            val = value;
            paletteId = '';
        }

        color2Dialog().show({
            value: val,
            paletteId,
            showPalette: true,
            target: elem,
            disabled: readOnly,
            direction: elem.getBoundingClientRect().right < innerWidth / 2 ? 'right' : 'left',
            callback(newValue, paletteId) {
                if (paletteId) {
                    value = paletteIdToValue(paletteId);
                } else {
                    value = newValue;
                }
                dispatch('change', {
                    value
                });
            }
        });
    }

    afterUpdate(() => {
        if (isSelfUpdate) {
            isSelfUpdate = false;
        }
    });
</script>

<div
    class="color-input"
    class:color-input_readonly={readOnly}
    bind:this={elem}
>
    {#if hasDialog}
        <button
            class="color-input__preview color-input__preview_active"
            on:click={onPreviewClick}
        >
            <ColorPreview color={value} mix="color-input__preview-inner" />
        </button>
    {:else}
        <div class="color-input__preview">
            <ColorPreview color={value} mix="color-input__preview-inner" />
        </div>
    {/if}

    {#if paletteItem}
        <div class="color-input__palette-name">
            {paletteItem.name}
        </div>
    {:else}
        <input
            class="color-input__input"
            type="text"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            maxlength="7"
            disabled={readOnly}
            bind:value={firstVal}
            on:input={rebuildVal}
            title={$l10nString('background.solid_first_title')}
        >

        <div class="color-input__separator"></div>

        <input
            class="color-input__second-input"
            type="number"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            min="0"
            {max}
            disabled={readOnly}
            bind:value={secondVal}
            on:input={rebuildVal}
            title={$l10nString('background.solid_second_title')}
        >

        <div class="color-input__symbol">
            %
        </div>
    {/if}
</div>

<style>
    .color-input {
        position: relative;
        display: flex;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background-color: var(--fill-transparent-minus-1);
        transition: border-color .15s ease-in-out;
    }

    .color-input:not(.color-input_readonly):hover {
        border-color: var(--fill-transparent-4);
    }

    .color-input_readonly {
        border: none;
        background: var(--fill-transparent-1);
    }

    .color-input.color-input:focus-within {
        border-color: var(--accent-purple);
    }

    .color-input__preview {
        position: relative;
        flex: 0 0 auto;
        width: 20px;
        height: 20px;
        margin: 9px 8px 9px 9px;
        border: none;
        border-radius: 4px;
        background: none;
        background-size: 100% 100%;
        box-shadow: 0 0 0 1px var(--fill-transparent-3);
        appearance: none;
        overflow: hidden;
    }

    .color-input__preview:focus-visible {
        outline: 1px solid var(--accent-purple);
    }

    .color-input__preview_active {
        cursor: pointer;
    }

    :global(.color-input__preview-inner.color-input__preview-inner.color-input__preview-inner) {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
    }

    .color-input__input {
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

    .color-input__input:focus {
        outline: none;
    }

    .color-input__separator {
        position: absolute;
        top: 9px;
        right: 69px;
        bottom: 9px;
        width: 1px;
        height: 20px;
        background: var(--fill-transparent-3);
    }

    .color-input__second-input {
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

    .color-input__second-input::-webkit-inner-spin-button,
    .color-input__second-input::-webkit-outer-spin-button {
        display: none;
    }

    .color-input__second-input:focus {
        outline: none;
    }

    .color-input__symbol {
        position: absolute;
        top: 9px;
        right: 15px;
        bottom: 9px;
        display: flex;
        align-items: center;
        color: var(--text-secondary);
    }

    .color-input__palette-name {
        flex: 1 1 auto;
        align-self: center;
        min-width: 0;
        font-size: 14px;
        text-overflow: ellipsis;
    }
</style>
