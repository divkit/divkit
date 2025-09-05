<script lang="ts">
    import { getContext } from 'svelte';
    import Select from './Select.svelte';
    import Renderer from './Renderer.svelte';
    import CanvasButton from './CanvasButton.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import lightThemeIcon from '../../assets/lightTheme.svg?url';
    import darkThemeIcon from '../../assets/darkTheme.svg?url';
    import errorsIcon from '../../assets/errors.svg?url';
    import errorsLightIcon from '../../assets/errorsLight.svg?url';
    import warningsIcon from '../../assets/warnings.svg?url';
    import undoIcon from '../../assets/undo.svg?url';
    import redoIcon from '../../assets/redo.svg?url';
    import safeAreaOffIcon from '../../assets/safeAreaOff.svg?url';
    import safeAreaOnIcon from '../../assets/safeAreaOn.svg?url';
    import { shortcuts, type ShortcutList } from '../utils/keybinder/use';
    import { undo as undoShortcut, redo as redoShortcut } from '../utils/keybinder/shortcuts';
    import { APP_CTX, type AppContext } from '../ctx/appContext';
    import Text from './controls/Text.svelte';
    import ErrorsDialog from './ErrorsDialog.svelte';
    import { encodeBackground } from '../utils/encodeBackground';

    const { l10n, l10nString, lang } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { cardLocales, state, directionSelector, setShowErrors, showErrors } = getContext<AppContext>(APP_CTX);
    const {
        paletteEnabled,
        currentUndoStore,
        currentRedoStore,
        rendererErrorsOnly,
        rendererWarningsOnly,
        locale,
        previewThemeStore,
        themeStore,
        direction,
        safeAreaEmulation,
        safeAreaEmulationEnabled
    } = state;

    const VIEWPORT_LIST = [
        '320x568',
        '360x640',
        '375x667',
        '393x851',
        '414x896',
        '768x1024'
    ];

    const DEFAULT_VIEWPORT = '360x640';

    setShowErrors(function showErrors(opts) {
        errorsDialog.show(errorButtonNode, {
            leafId: opts?.leafId
        });
    });

    $: viewportList = [
        ...VIEWPORT_LIST,
        'custom'
    ].map(it => ({
        value: it,
        text: it === 'custom' ? $l10n('canvasCustomSize') : it
    }));

    let viewport = DEFAULT_VIEWPORT;
    let selectViewport = viewport;
    let viewportWidth = 100;
    let viewportHeight = 100;
    let errorButtonNode: HTMLElement;
    let errorsDialog: ErrorsDialog;

    $: if (VIEWPORT_LIST.includes(viewport)) {
        selectViewport = viewport;
    } else {
        selectViewport = 'custom';
    }

    $: {
        [viewportWidth, viewportHeight] = viewport.split('x').map(Number);
    }

    let canvas: HTMLElement;
    let wrapper: HTMLElement;
    let topbar: HTMLElement;

    let mousedownX = 0;
    let mousedownY = 0;
    function onOuterMousedown(event: MouseEvent): void {
        mousedownX = event.pageX;
        mousedownY = event.pageY;
    }

    function onOuterClick(event: MouseEvent): void {
        const dist = (event.pageX - mousedownX) * (event.pageX - mousedownX) +
            (event.pageY - mousedownY) * (event.pageY - mousedownY);
        const target = event.target;

        if (
            dist < 64 &&
            (
                target === wrapper ||
                target === topbar ||
                target instanceof HTMLElement && !wrapper.contains(target) && !topbar.contains(target)
            )
        ) {
            state.selectedLeaf.set(null);
        }
    }

    function showErrorsDialog(): void {
        showErrors();
    }

    function onViewportSelectChange() {
        if (selectViewport !== 'custom') {
            viewport = selectViewport;
        }
    }

    function onViewportWidthChange(event: CustomEvent<{
        value: string;
    }>): void {
        viewport = `${event.detail.value}x${viewportHeight}`;
    }

    function onViewportHeightChange(event: CustomEvent<{
        value: string;
    }>): void {
        viewport = `${viewportWidth}x${event.detail.value}`;
    }

    const SHORTCUTS: ShortcutList = [
        [undoShortcut, () => state.undo()],
        [redoShortcut, () => state.redo()]
    ];
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<!-- svelte-ignore a11y-no-static-element-interactions -->
<div
    class="canvas"
    bind:this={canvas}
    on:mousedown={onOuterMousedown}
    on:click={onOuterClick}
    use:shortcuts={SHORTCUTS}
>
    <div class="canvas__topbar" bind:this={topbar}>
        <Select
            bind:value={selectViewport}
            items={viewportList}
            theme="canvas"
            title={$l10nString('previewSize')}
            on:change={onViewportSelectChange}
        />

        {#if selectViewport === 'custom'}
            <Text
                value={viewportWidth}
                subtype="integer"
                size="small"
                width="small"
                min={100}
                title={$l10nString('props.width')}
                on:change={onViewportWidthChange}
            />
            <Text
                value={viewportHeight}
                subtype="integer"
                size="small"
                width="small"
                min={100}
                title={$l10nString('props.height')}
                on:change={onViewportHeightChange}
            />
        {/if}

        {#if safeAreaEmulation}
            <CanvasButton
                title={$l10n($safeAreaEmulationEnabled ? 'safeAreaOff' : 'safeAreaOn')}
                on:click={() => $safeAreaEmulationEnabled = !$safeAreaEmulationEnabled}
            >
                <div
                    class="canvas__button-icon canvas__button-icon_inversed"
                    style:background-image="url({encodeBackground($safeAreaEmulationEnabled ? safeAreaOnIcon : safeAreaOffIcon)})"
                ></div>
            </CanvasButton>
        {/if}

        {#if $paletteEnabled}
            <CanvasButton
                title={$l10n($previewThemeStore === 'light' ? 'lightTheme' : 'darkTheme')}
                on:click={() => $previewThemeStore = ($previewThemeStore === 'light' ? 'dark' : 'light')}
            >
                <div
                    class="canvas__button-icon canvas__button-icon_inversed"
                    style:background-image="url({encodeBackground($previewThemeStore === 'light' ? lightThemeIcon : darkThemeIcon)})"
                ></div>
            </CanvasButton>
        {/if}

        {#if directionSelector}
            <CanvasButton
                title={$l10n($direction === 'ltr' ? 'rtl.switchToRTL' : 'rtl.switchToLTR')}
                on:click={() => $direction = ($direction === 'ltr' ? 'rtl' : 'ltr')}
            >
                <div class="canvas__rtl-button">
                    {$direction === 'ltr' ? 'LTR' : 'RTL'}
                </div>
            </CanvasButton>
        {/if}

        {#if cardLocales.length}
            <Select
                bind:value={$locale}
                items={cardLocales.map(it => ({ value: it.id, text: it.text[$lang] }))}
                theme="canvas"
                title={$l10nString('previewLang')}
            />
        {/if}

        <CanvasButton
            title={$l10n('undo') + ($currentUndoStore ? ' — ' + $l10n($currentUndoStore.toLangKey()) : '')}
            disabled={!$currentUndoStore}
            on:click={() => state.undo()}
        >
            <div
                class="canvas__button-icon canvas__button-icon_inversed"
                class:canvas__button-icon_disabled={!$currentUndoStore}
                style:background-image="url({encodeBackground(undoIcon)})"
            ></div>
        </CanvasButton>

        <CanvasButton
            title={$l10n('redo') + ($currentRedoStore ? ' — ' + $l10n($currentRedoStore.toLangKey()) : '')}
            disabled={!$currentRedoStore}
            on:click={() => state.redo()}
        >
            <div
                class="canvas__button-icon canvas__button-icon_inversed"
                class:canvas__button-icon_disabled={!$currentRedoStore}
                style:background-image="url({encodeBackground(redoIcon)})"
            ></div>
        </CanvasButton>

        <CanvasButton
            title={$l10n('errors')}
            bind:node={errorButtonNode}
            on:click={showErrorsDialog}
        >
            <div class="canvas__button-content">
                <div class="canvas__button-counter">{$rendererErrorsOnly.length}</div>
                <div
                    class="canvas__button-icon"
                    class:canvas__button-icon_disabled={!$rendererErrorsOnly.length}
                    style:background-image="url({encodeBackground($themeStore === 'dark' ? errorsIcon : errorsLightIcon)})"
                ></div>
                <div class="canvas__button-counter">{$rendererWarningsOnly.length}</div>
                <div
                    class="canvas__button-icon"
                    class:canvas__button-icon_disabled={!$rendererWarningsOnly.length}
                    style:background-image="url({encodeBackground(warningsIcon)})"
                ></div>
            </div>
        </CanvasButton>
    </div>

    <div class="canvas__renderer-wrapper" bind:this={wrapper}>
        <Renderer bind:viewport={viewport} theme={$previewThemeStore} />
    </div>
</div>
<ErrorsDialog bind:this={errorsDialog} />

<style>
    .canvas {
        display: flex;
        flex-direction: column;
        min-height: 0;
        height: 100%;
        padding-bottom: 24px;
        overflow: auto;
        background: var(--background-overflow);
    }

    .canvas__topbar {
        position: sticky;
        z-index: 1;
        top: 0;
        display: flex;
        gap: 12px;
        height: 32px;
        padding: 20px 20px 10px;
        background: var(--background-overflow-transparent);
    }

    .canvas__renderer-wrapper {
        display: flex;
        justify-content: center;
        align-items: start;
        flex: 1 1 auto;
        margin-top: 10px;
    }

    .canvas__button-icon {
        flex: 0 0 auto;
        width: 32px;
        height: 32px;
        background: no-repeat 50% 50%;
        background-size: 20px;
    }

    .canvas__button-icon_inversed {
        filter: var(--icon-filter);
    }

    .canvas__button-icon_disabled {
        opacity: .4;
    }

    .canvas__button-counter {
        padding: 0 0 0 4px;
    }

    .canvas__button-content {
        display: flex;
        align-items: center;
        padding: 0 4px;
    }

    .canvas__rtl-button {
        padding: 0 12px;
    }
</style>
