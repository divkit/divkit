<script lang="ts">
    import { getContext } from 'svelte';

    import ColorSelect2 from '../ColorSelect2.svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import PaletteSelector from '../PaletteSelector.svelte';
    import { APP_CTX, type AppContext, type Color2DialogShowProps } from '../../ctx/appContext';

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { palette, paletteEnabled, previewThemeStore } = state;

    $: if (isShown && currentProps && !currentProps.disabled) {
        currentProps.callback(paletteId ? '' : value, paletteId);
    }

    export function show(props: Color2DialogShowProps): void {
        currentProps = props;
        value = props.value;
        paletteId = props.paletteId || '';
        isShown = true;
        togglePalette = Boolean(paletteId);
    }

    export function hide(): void {
        isShown = false;
        currentProps = null;
    }

    let currentProps: Color2DialogShowProps | null = null;
    let isShown = false;
    let value: string;
    let paletteId: string;
    let togglePalette = false;

    $: if ($previewThemeStore && paletteId) {
        const paletteItem = $palette.find(it => it.id === paletteId);

        if (paletteItem) {
            value = paletteItem[$previewThemeStore];
        }
    }

    function onClose(): void {
        isShown = false;
    }

    function onPaletteToggle(): void {
        togglePalette = !togglePalette;
    }

    function onColorChange(): void {
        paletteId = '';
    }
</script>

{#if isShown && currentProps?.target}
    <ContextDialog
        target={currentProps.target}
        direction={currentProps.direction}
        offset={currentProps.offset}
        overflow="visible"
        canMove={true}
        wide={togglePalette}
        on:close={onClose}
    >
        <div class="color2-dialog__panels">
            {#if togglePalette}
                <div class="color2-dialog__panel color2-dialog__panel_left">
                    <PaletteSelector
                        bind:paletteId={paletteId}
                    />
                </div>
            {/if}

            <div class="color2-dialog__panel">
                {#if $paletteEnabled && currentProps.showPalette}
                    <div class="color2-dialog__title">
                        <div class="color2-dialog__title-text">
                            {$l10nString('color')}
                        </div>

                        <button
                            class="color2-dialog__palette-toggle"
                            class:color2-dialog__palette-toggle_toggled={togglePalette}
                            title={$l10nString('palette')}
                            on:click={onPaletteToggle}
                        >
                            <div class="color2-dialog__palette-icon"></div>
                        </button>
                    </div>
                {/if}

                <div class="color2-dialog__content">
                    <ColorSelect2
                        bind:value={value}
                        disabled={currentProps.disabled}
                        on:change={onColorChange}
                    />
                </div>
            </div>
        </div>
    </ContextDialog>
{/if}

<style>
    .color2-dialog__content {
        margin: 12px 0;
        pointer-events: auto;
    }

    .color2-dialog__panels {
        display: flex;
        pointer-events: none;
    }

    .color2-dialog__panel {
        flex: 1 0 0;
        min-width: 0;
    }

    .color2-dialog__panel_left {
        display: flex;
        flex-direction: column;
        border-right: 1px solid var(--fill-transparent-3);
    }

    .color2-dialog__title {
        display: flex;
        gap: 12px;
        align-items: center;
        height: 32px;
        margin: 12px 12px 0 20px;
    }

    .color2-dialog__title-text {
        flex: 1 1 auto;
        min-width: 0;
        font-size: 14px;
        font-weight: 500;
        text-overflow: ellipsis;
    }

    .color2-dialog__palette-toggle {
        width: 32px;
        height: 32px;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 6px;
        background-color: var(--fill-transparent-minus-1);
        appearance: none;
        cursor: pointer;
        transition: .15s ease-in-out;
        pointer-events: auto;
    }

    .color2-dialog__palette-toggle:hover {
        border-color: var(--fill-transparent-4);
    }

    .color2-dialog__palette-toggle:focus {
        outline: none;
        border-color: var(--accent-purple);
    }

    .color2-dialog__palette-toggle_toggled,
    .color2-dialog__palette-toggle_toggled:hover {
        border-color: transparent;
    }

    .color2-dialog__palette-toggle_toggled {
        background-color: var(--accent-purple);
    }

    .color2-dialog__palette-toggle_toggled:hover {
        background-color: var(--accent-purple-hover);
    }

    .color2-dialog__palette-icon {
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50% url(../../../assets/palette.svg);
        background-size: 20px;
        filter: var(--icon-filter);
    }

    .color2-dialog__palette-toggle_toggled .color2-dialog__palette-icon {
        filter: none;
    }
</style>
