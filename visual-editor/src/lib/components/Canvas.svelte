<script lang="ts">
    import { getContext, onMount } from 'svelte';
    import { Tween } from 'svelte/motion';
    import Renderer from './Renderer.svelte';
    import CanvasButton from './CanvasButton.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import errorsIcon from '../../assets/errors.svg?url';
    import errorsLightIcon from '../../assets/errorsLight.svg?url';
    import warningsIcon from '../../assets/warnings.svg?url';
    import undoIcon from '../../assets/undo.svg?url';
    import redoIcon from '../../assets/redo.svg?url';
    import { shortcuts, type ShortcutList } from '../utils/keybinder/use';
    import { undo as undoShortcut, redo as redoShortcut } from '../utils/keybinder/shortcuts';
    import { APP_CTX, type AppContext } from '../ctx/appContext';
    import ErrorsDialog from './ErrorsDialog.svelte';
    import { encodeBackground } from '../utils/encodeBackground';
    import ViewportControl from './ViewportControl.svelte';

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state, setShowErrors, showErrors } = getContext<AppContext>(APP_CTX);
    const {
        currentUndoStore,
        currentRedoStore,
        rendererErrorsOnly,
        rendererWarningsOnly,
        previewThemeStore,
        themeStore,
    } = state;

    const DEFAULT_VIEWPORT = '360x640';

    setShowErrors(function showErrors(opts) {
        errorsDialog.show(errorButtonNode, {
            leafId: opts?.leafId
        });
    });

    let viewport = DEFAULT_VIEWPORT;
    let scale = new Tween(1, {
        duration: 100
    });
    let errorButtonNode: HTMLElement;
    let errorsDialog: ErrorsDialog;

    let canvas: HTMLElement;
    let wrapper: HTMLElement;
    let topbar: HTMLElement;
    let renderer: Renderer;

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

    function fitToWindow(): void {
        const TOP_MARGIN = 28 + 4;
        const RIGHT_MARGIN = 16;
        const BOTTOM_MARGIN = 16 + 24;

        const topbarHeight = topbar.offsetHeight;

        const availWidth = canvas.offsetWidth - RIGHT_MARGIN;
        const availHeight = canvas.offsetHeight - TOP_MARGIN - BOTTOM_MARGIN - topbarHeight;
        const availAspect = availWidth / availHeight;
        const size = viewport.split('x').map(Number);
        const viewportAspect = size[0] / size[1];

        let newScale;
        if (viewportAspect < availAspect) {
            newScale = availHeight / size[1];
        } else {
            newScale = availWidth / size[0];
        }
        newScale = Math.max(.33, Math.min(5, newScale));
        scale.set(newScale, {
            duration: 0
        });
    }

    function onWheel(event: WheelEvent): void {
        if (event.metaKey || event.ctrlKey) {
            event.preventDefault();

            let newScale = scale.target;
            if (event.deltaY > 0) {
                newScale /= 1.1;
            } else {
                newScale *= 1.1;
            }

            newScale = Math.max(.33, Math.min(5, newScale));
            scale.target = newScale;
        }
    }

    onMount(() => {
        if (state.fitViewportOnCreate) {
            fitToWindow();
        }
    });

    const SHORTCUTS: ShortcutList = [
        [undoShortcut, () => state.undo()],
        [redoShortcut, () => state.redo()]
    ];
</script>

<!-- svelte-ignore a11y_click_events_have_key_events -->
<!-- svelte-ignore a11y_no_static_element_interactions -->
<div
    class="canvas"
    bind:this={canvas}
    on:mousedown={onOuterMousedown}
    on:click={onOuterClick}
    use:shortcuts={SHORTCUTS}
>
    <div class="canvas__topbar" bind:this={topbar}>
        <ViewportControl
            bind:viewport={viewport}
            bind:scale={scale.target}
            on:fitToWindow={fitToWindow}
        />

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

    <div
        class="canvas__renderer-wrapper"
        bind:this={wrapper}
        on:wheel|nonpassive={onWheel}
    >
        <Renderer
            bind:this={renderer}
            bind:viewport={viewport}
            bind:scale={scale.current}
            theme={$previewThemeStore}
        />
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
        left: 0;
        display: flex;
        gap: 12px;
        height: 32px;
        padding: 20px 20px;
        background: var(--background-overflow-transparent);
    }

    .canvas__renderer-wrapper {
        display: flex;
        justify-content: center;
        align-items: start;
        flex: 1 1 auto;
        min-width: max-content;
        margin-top: 4px;
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
</style>
