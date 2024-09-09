<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import RadioSelector from './controls/RadioSelector.svelte';
    import lightThemeIcon from '../../assets/lightTheme.svg?url';
    import darkThemeIcon from '../../assets/darkTheme.svg?url';
    import Spoiler2 from './controls/Spoiler2.svelte';
    import ColorPreview from './ColorPreview.svelte';
    import { APP_CTX, type AppContext } from '../ctx/appContext';

    export let paletteId: string;

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { palette, previewThemeStore } = state;
    const dispatch = createEventDispatcher();

    function selectPalette(id: string): void {
        paletteId = id;
        dispatch('change', id);
    }
</script>

<div class="palette-selector">
    <div class="palette-selector__title">
        <div class="palette-selector__title-text">
            {$l10n('palette')}
        </div>

        <RadioSelector
            mix="palette-selector__theme"
            bind:value={$previewThemeStore}
            name="previewTheme2"
            options={[{
                text: $l10n('lightTheme'),
                value: 'light',
                icon: lightThemeIcon
            }, {
                text: $l10n('darkTheme'),
                value: 'dark',
                icon: darkThemeIcon
            }]}
            theme="normal"
            iconSize="20"
            customTooltips={false}
        />
    </div>

    <div class="palette-selector__content">
        <Spoiler2 theme="props">
            <div slot="title">
                {$l10n('localPalette')}
            </div>

            <div class="palette-selector__list">
                {#each $palette as item}
                    <button
                        class="palette-selector__item"
                        class:palette-selector__item_selected={paletteId === item.id}
                        on:click={() => selectPalette(item.id)}
                    >
                        <ColorPreview color={item[$previewThemeStore]} mix="palette-selector__preview" />

                        <div class="palette-selector__name">
                            {item.name}
                        </div>
                    </button>
                {/each}
            </div>
        </Spoiler2>
    </div>
</div>

<style>
    .palette-selector {
        display: flex;
        flex-direction: column;
        height: 100%;
    }

    .palette-selector__title {
        display: flex;
        flex: 0 0 auto;
        align-items: center;
        margin: 8px 8px 8px 20px;
    }

    .palette-selector__title-text {
        flex: 1 1 auto;
        min-width: 0;
        font-size: 14px;
        font-weight: 500;
        text-overflow: ellipsis;
    }

    :global(.palette-selector__theme) {
        pointer-events: auto;
    }

    .palette-selector__content {
        flex: 1 0 0;
        overflow: auto;
        pointer-events: auto;
    }

    .palette-selector__list {
        display: flex;
        flex-direction: column;
    }

    .palette-selector__item {
        display: flex;
        flex: 0 0 auto;
        align-items: center;
        width: calc(100% - 40px);
        margin: 4px 20px;
        padding: 9px;
        font: inherit;
        color: inherit;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background: var(--fill-transparent-minus-1);
        appearance: none;
        cursor: pointer;
        user-select: none;
        transition: border-color .15s ease-in-out;
    }

    .palette-selector__item:hover {
        border-color: var(--fill-transparent-4);
    }

    .palette-selector__item:focus-visible {
        outline: 1px solid var(--accent-purple);
        outline-offset: 2px;
    }

    .palette-selector__item_selected {
        border-color: var(--accent-purple);
    }

    .palette-selector__item_selected:hover {
        border-color: var(--accent-purple-hover);
    }

    :global(.palette-selector__preview.palette-selector__preview.palette-selector__preview) {
        flex: 0 0 auto;
        width: 20px;
        height: 20px;
        margin-right: 8px;
        border-radius: 4px;
    }

    .palette-selector__name {
        min-width: 0;
        font-size: 14px;
        white-space: nowrap;
        text-overflow: ellipsis;
    }
</style>
