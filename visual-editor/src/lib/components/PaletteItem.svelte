<script lang="ts">
    import { afterUpdate, createEventDispatcher, getContext, tick } from 'svelte';
    import { parseColor, stringifyColorToDivKit } from '../utils/colors';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import type { PaletteItem } from '../data/palette';
    import { calcSelectionOffset, setSelectionOffset } from '../utils/contenteditable';
    import { APP_CTX, type AppContext } from '../ctx/appContext';
    import ColorPreview from './ColorPreview.svelte';
    import { PALETTE_ERRORS_CTX, type PaletteErrorsContext } from '../ctx/paletteErrorsContext';
    import { RenamePaletteItemCommand } from '../data/commands/renamePaletteItem';
    import { ChangePaletteItemCommand } from '../data/commands/changePaletteItem';

    export let value: PaletteItem;

    // todo mark invalid

    const MAX_NAME_LENGTH = 120;

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { getSelection, state, color2Dialog } = getContext<AppContext>(APP_CTX);
    const { errors } = getContext<PaletteErrorsContext>(PALETTE_ERRORS_CTX);
    const { previewThemeStore, readOnly } = state;

    const dispatch = createEventDispatcher();

    let elem: HTMLElement;
    let nameElem: HTMLElement;
    let nameVal = '';
    let hexVal = '';
    let opacityVal = '';
    let isSelfUpdate = false;

    function updateVal(value: PaletteItem, currentTheme: 'light' | 'dark'): void {
        if (isSelfUpdate) {
            return;
        }
        nameVal = value.name;
        const parsed = parseColor(value[currentTheme] || '');
        if (parsed) {
            opacityVal = String(Math.round(parsed.a / 255 * 100));
            parsed.a = 255;
            hexVal = stringifyColorToDivKit(parsed);
        } else {
            hexVal = '';
            opacityVal = '';
        }
    }

    $: updateVal(value, $previewThemeStore);

    function rebuildVal(): void {
        const parsed = parseColor(hexVal);
        if (parsed) {
            parsed.a = Math.round(Number(opacityVal) * 255 / 100);
            isSelfUpdate = true;
            state.pushCommand(new ChangePaletteItemCommand(value.id, $previewThemeStore, stringifyColorToDivKit(parsed)));
            dispatch('change');
        }
    }

    function onPreviewClick(): void {
        color2Dialog().show({
            value: value[$previewThemeStore],
            target: elem,
            disabled: $readOnly,
            direction: 'right',
            offset: $readOnly ? 32 : 60,
            callback(newValue) {
                state.pushCommand(new ChangePaletteItemCommand(value.id, $previewThemeStore, newValue));
            }
        });
    }

    function onNameInput(): void {
        if (nameVal.length > MAX_NAME_LENGTH) {
            const selection = getSelection();
            const cursorStart = calcSelectionOffset(selection, nameElem, 'start', false);
            const cursorEnd = calcSelectionOffset(selection, nameElem, 'end', false);
            nameVal = nameVal.slice(0, MAX_NAME_LENGTH);
            tick().then(() => {
                if (!selection) {
                    return;
                }

                selection.removeAllRanges();
                const range = document.createRange();
                setSelectionOffset(selection, nameElem, range, 'start', Math.min(cursorStart, MAX_NAME_LENGTH), false);
                setSelectionOffset(selection, nameElem, range, 'end', Math.min(cursorEnd, MAX_NAME_LENGTH), false);
                selection.addRange(range);
            });
        }

        state.pushCommand(new RenamePaletteItemCommand(value.id, nameVal));
    }

    afterUpdate(() => {
        if (isSelfUpdate) {
            isSelfUpdate = false;
        }
    });
</script>

<div
    class="palette-item"
    class:palette-item_readonly={$readOnly}
    class:palette-item_error={$errors.get(value.id)}
    title={$errors.get(value.id)}
    bind:this={elem}
>
    <input
        bind:this={nameElem}
        class="palette-item__name"
        contenteditable="true"
        autocomplete="off"
        autocorrect="off"
        autocapitalize="off"
        spellcheck="false"
        maxlength={MAX_NAME_LENGTH}
        bind:value={nameVal}
        on:input={onNameInput}
    >

    <button
        class="palette-item__preview"
        on:click={onPreviewClick}
    >
        <ColorPreview color={value[$previewThemeStore]} mix="palette-item__preview-inner" />
    </button>

    <input
        class="palette-item__input"
        type="text"
        autocomplete="off"
        autocorrect="off"
        autocapitalize="off"
        spellcheck="false"
        maxlength="7"
        bind:value={hexVal}
        on:input={rebuildVal}
        disabled={$readOnly}
        title={$l10nString('background.solid_first_title')}
    >

    <div class="palette-item__separator"></div>

    <input
        class="palette-item__second-input"
        type="number"
        autocomplete="off"
        autocorrect="off"
        autocapitalize="off"
        spellcheck="false"
        min="0"
        disabled={$readOnly}
        max="100"
        bind:value={opacityVal}
        on:input={rebuildVal}
        title={$l10nString('background.solid_second_title')}
    >

    <div class="palette-item__symbol">
        %
    </div>
</div>

<style>
    .palette-item {
        position: relative;
        display: flex;
        align-items: center;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background-color: var(--background-primary);
        transition: border-color .15s ease-in-out;
    }

    .palette-item_readonly {
        margin-right: 16px;
        border: none;
        background: var(--fill-transparent-1);
    }

    .palette-item:not(.palette-item_readonly):hover {
        border-color: var(--fill-transparent-4);
    }

    .palette-item:not(.palette-item_readonly):focus-within {
        border-color: var(--accent-purple);
    }

    .palette-item_error.palette-item_error.palette-item_error,
    .palette-item_error.palette-item_error:focus-within {
        border-color: var(--accent-red);
    }

    .palette-item__name {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        justify-content: center;
        flex: 1 1 auto;
        min-width: 0;
        margin: 4px 1px 4px 11px;
        font: inherit;
        font-size: 13px;
        line-height: 16px;
        color: inherit;
        outline: none;
        word-break: break-word;
        background: none;
        border: none;
    }

    .palette-item__name:not(:focus) {
        display: -webkit-box;
        -webkit-line-clamp: 2;
        overflow: hidden;
        -webkit-box-orient: vertical;
    }

    .palette-item__preview {
        position: relative;
        flex: 0 0 auto;
        width: 20px;
        height: 20px;
        margin: 9px 8px 9px 9px;
        padding: 0;
        border: none;
        border-radius: 4px;
        background: none;
        appearance: none;
        cursor: pointer;
        overflow: hidden;
        outline: 2px solid transparent;
        transition: outline-color .15s ease-in-out;
    }

    .palette-item__preview:hover {
        outline-color: var(--fill-transparent-4);
    }

    .palette-item__preview:active {
        outline-color: var(--fill-transparent-1);
    }

    .palette-item__preview:focus-visible {
        outline-color: var(--accent-purple);
    }

    :global(.palette-item__preview-inner.palette-item__preview-inner) {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
    }

    .palette-item__input {
        flex: 0 0 auto;
        width: 64px;
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

    .palette-item__input:focus {
        outline: none;
    }

    .palette-item__separator {
        position: absolute;
        top: 9px;
        right: 69px;
        bottom: 9px;
        width: 1px;
        background: var(--fill-transparent-3);
    }

    .palette-item__second-input {
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

    .palette-item__second-input::-webkit-inner-spin-button,
    .palette-item__second-input::-webkit-outer-spin-button {
        display: none;
    }

    .palette-item__second-input:focus {
        outline: none;
    }

    .palette-item__symbol {
        position: absolute;
        top: 9px;
        right: 15px;
        bottom: 9px;
        display: flex;
        align-items: center;
        color: var(--text-secondary);
    }
</style>
