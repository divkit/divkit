<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';
    import { get } from 'svelte/store';
    import type { DivkitInstance } from '@yandex-int/divkit/typings/common';
    import { render } from '@yandex-int/divkit/client-devtool';
    import '@yandex-int/divkit/dist/client.css';
    import ViewportSelect from './ViewportSelect.svelte';
    import PlatformSelect from './PlatformSelect.svelte';
    import {
        components,
        highlightElem,
        highlightMode,
        highlightModeClicked,
        highlightPart,
        selectedElem,
        updateComponents
    } from '../data/webStructure';
    import { jsonStore } from '../data/jsonStore';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';
    import { editorMode } from '../data/editorMode';
    import { codeRunStore } from '../data/valueStore';
    import { runCode } from '../utils/shortcuts';
    import { webViewerErrors } from '../data/webViewerErrors';

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    let json;
    let viewport = '';
    let platform: 'desktop' | 'touch' | 'auto' = 'auto';
    $: splitted = viewport.split('x');
    $: width = splitted[0] || 0;
    $: height = splitted[1] || 0;

    let componentsCount = 0;
    let renderTime = 0;

    let root: HTMLElement;
    let previewWrapper: HTMLElement;
    let rootPreview: HTMLElement;
    let divBlock: DivkitInstance | null;
    let highlight: {
        elem: HTMLElement;
        previewBbox: {
            top: number;
            left: number;
            width: number;
            height: number;
        };
        top: number;
        left: number;
        right: number;
        bottom: number;
        margin: string;
        padding: string;
    } | null = null;

    // todo ResizeObserver
    const unsubscribeHighlight = highlightElem.subscribe(elem => {
        if (elem) {
            const rootBbox = root.getBoundingClientRect();
            const previewBbox = previewWrapper.getBoundingClientRect();
            const bbox = elem.getBoundingClientRect();
            const computedStyle = getComputedStyle(elem);
            const margin = computedStyle.margin;
            const marginTop = parseInt(computedStyle.marginTop);
            const marginRight = parseInt(computedStyle.marginRight);
            const marginBottom = parseInt(computedStyle.marginBottom);
            const marginLeft = parseInt(computedStyle.marginLeft);
            const padding = computedStyle.padding;

            let finalBox = {
                top: bbox.top - marginTop,
                right: bbox.right + marginRight,
                bottom: bbox.bottom + marginBottom,
                left: bbox.left - marginLeft,
            };

            highlight = {
                elem: elem,
                previewBbox: {
                    top: previewBbox.top - rootBbox.top,
                    left: previewBbox.left - rootBbox.left,
                    width: previewBbox.width,
                    height: previewBbox.height
                },
                top: finalBox.top - previewBbox.top,
                left: finalBox.left - previewBbox.left,
                right: previewBbox.right - finalBox.right,
                bottom: previewBbox.bottom - finalBox.bottom,
                margin,
                padding
            };
        } else {
            highlight = null;
        }
    });

    function rerender(json: any, platform: 'desktop' | 'touch' | 'auto'): void {
        if (!rootPreview) {
            return;
        }

        if (divBlock) {
            divBlock.$destroy();
        }
        components.clear();
        $webViewerErrors = [];
        let count = 0;
        let renderStart = performance.now();

        divBlock = render({
            target: rootPreview,
            json,
            id: 'test',
            platform,
            onError(event) {
                const additional = event.error.additional || {};

                let args = Object.keys(additional).reduce((acc: string[], item: string) => {
                    acc.push(`${item}=${additional[item]}`);
                    return acc;
                }, []);

                $webViewerErrors = [
                    ...$webViewerErrors,
                    {
                        message: event.error.message + (args.length ? ` (${args.join(', ')})` : ''),
                        stack: (event.error.stack || '').split('\n'),
                        level: event.error.level
                    }
                ];
            },
            onComponent(event) {
                ++count;
                if (event.type === 'mount') {
                    components.set(event.node, event);
                } else if (event.type === 'destroy') {
                    components.delete(event.node);
                }

                updateComponents();
            }
        });

        requestAnimationFrame(() => {
            renderTime = performance.now() - renderStart;
            componentsCount = count;
        });
    }

    $: rerender($jsonStore, platform);

    function updateHovered(): void {
        if (!prevCoords) {
            highlightElem.set(null);
            return;
        }

        const elems = new Set(components.keys());

        const hovered = document.elementsFromPoint(...prevCoords);
        for (const elem of hovered) {
            if (elems.has(elem as HTMLElement)) {
                highlightElem.set(elem as HTMLElement);
                return;
            }
        }

        highlightElem.set(null);
    }

    function onPreviewScroll(): void {
        if ($highlightMode) {
            updateHovered();
        }
    }

    let prevCoords: [number, number] | null = null;
    function onHighlightOverlayMove(event: MouseEvent): void {
        prevCoords = [event.pageX, event.pageY];
        updateHovered();
    }

    function onHighlightOverlayOut(): void {
        highlightElem.set(null);
    }

    function onHighlightClick(): void {
        highlightModeClicked.set(true);
        selectedElem.set(get(highlightElem));
        highlightElem.set(null);
        highlightMode.set(false);
    }

    onMount(() => {
        rerender($jsonStore, platform);
    });

    onDestroy(() => {
        components.clear();
        if (divBlock) {
            divBlock.$destroy();
        }
        unsubscribeHighlight();
    });
</script>

<div class="web-viewer" bind:this={root}>
    <div class="web-viewer__select">
        <ViewportSelect bind:value={viewport} />
        <PlatformSelect bind:value={platform} />
<!--        <VersionButton />-->
    </div>

    <div class="web-viewer__content" bind:this={previewWrapper} style:width="{width}px" style:height="{height}px" on:scroll={onPreviewScroll}>
        {#if $highlightMode}
            <div class="web-viewer__content-highlight-overlay-wrapper">
                <!-- svelte-ignore a11y-mouse-events-have-key-events -->
                <div
                    class="web-viewer__content-highlight-overlay"
                    on:mousemove={onHighlightOverlayMove}
                    on:mouseout={onHighlightOverlayOut}
                    on:click={onHighlightClick}
                    style:width="{width}px"
                    style:height="{height}px"
                ></div>
            </div>
        {/if}
        <div bind:this={rootPreview} class="web-viewer__content-inner">
            {#if $editorMode !== 'json' && !$codeRunStore}
                <div class="web-viewer__run-placeholder">
                    {$l10n('runCodeFirst').replace('%s', runCode.toString())}
                </div>
            {/if}
        </div>
    </div>

    {#if componentsCount && renderTime}
        <ul class="web-viewer__info">
            <li class="web-viewer__info-item">
                <div class="web-viewer__info-icon web-viewer__info-icon_components"></div>
                {`${$l10n('components')} ${componentsCount}`}
            </li>
            <li class="web-viewer__info-item">
                <div class="web-viewer__info-icon web-viewer__info-icon_time"></div>
                {`${$l10n('timeToRender')} ${renderTime.toFixed(1)}ms`}
            </li>
        </ul>
    {/if}

    {#if highlight}
        {#key highlight.elem}
            <div class="web-viewer__highlight-clip" style="top:{highlight.previewBbox.top}px;left:{highlight.previewBbox.left}px;width:{highlight.previewBbox.width}px;height:{highlight.previewBbox.height}px;">
                <div class="web-viewer__highlight" class:web-viewer__highlight_show={$highlightPart & 1} style="top:{highlight.top}px;left:{highlight.left}px;right:{highlight.right}px;bottom:{highlight.bottom}px;border-width:{highlight.margin};">
                    <div class="web-viewer__highlight-inner" class:web-viewer__highlight-inner_show={$highlightPart & 2} style="border-width:{highlight.padding};">
                        <div class="web-viewer__highlight-inner2" class:web-viewer__highlight-inner2_show={$highlightPart & 4}></div>
                    </div>
                </div>
            </div>
        {/key}
    {/if}
</div>

<style>
    .web-viewer {
        position: relative;
        max-height: 100%;
        text-align: center;
    }

    .web-viewer__select {
        margin-top: 10px;
    }

    .web-viewer__content {
        position: relative;
        margin: 10px auto 24px;
        overflow: auto;
    }

    .web-viewer__content:hover::-webkit-scrollbar-thumb {
        background-color: var(--scrollbar-alt-hover-bg);
        border-color: var(--scrollbar-alt-hover-border);
    }

    .web-viewer__content::-webkit-scrollbar-thumb:hover {
        background-color: var(--scrollbar-hover-bg);
        border-color: var(--scrollbar-hover-border);
    }

    .web-viewer__content::-webkit-scrollbar-thumb:active {
        background-color: var(--scrollbar-active-bg);
        border-color: var(--scrollbar-active-border);
    }

    @supports (overflow: overlay) {
        .web-viewer__content {
            overflow: overlay;
        }
    }

    .web-viewer__content-inner {
        min-width: 100%;
        min-height: 100%;
        color: #000;
        background: #fff;
        text-align: center;
    }

    .web-viewer__content-highlight-overlay-wrapper {
        position: sticky;
        z-index: 1;
        top: 0;
        left: 0;
        width: 0;
        height: 0;
    }

    .web-viewer__content-highlight-overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
    }

    .web-viewer__highlight-clip {
        position: absolute;
        overflow: hidden;
        pointer-events: none;
    }

    .web-viewer__highlight {
        position: absolute;
        display: flex;
        border: solid transparent;
    }

    .web-viewer__highlight_show {
        border-color: rgba(246, 178, 107, 0.66);
    }

    .web-viewer__highlight-inner {
        display: flex;
        flex: 1 1 auto;
        border: solid transparent;
    }

    .web-viewer__highlight-inner_show {
        border-color: rgba(147, 196, 125, 0.55);
    }

    .web-viewer__highlight-inner2 {
        flex: 1 1 auto;
        background: transparent;
    }

    .web-viewer__highlight-inner2_show {
        background: rgba(111, 168, 220, 0.66);
    }

    .web-viewer__info {
        display: inline-flex;
        gap: 16px;
        margin: 0 0 30px;
        padding: 0;
        vertical-align: top;
        list-style: none;
        opacity: .4;
        transition: opacity .5s ease-in-out;
        cursor: default;
    }

    .web-viewer__info:hover {
        opacity: 1;
    }

    .web-viewer__info-item {
        display: flex;
        align-items: center;
    }

    .web-viewer__info-icon {
        width: 10px;
        height: 10px;
        margin-right: 4px;
        border-radius: 100%;
    }

    .web-viewer__info-icon_components {
        background: var(--accent1);
    }

    .web-viewer__info-icon_time {
        background: var(--accent2);
    }

    .web-viewer__run-placeholder {
        padding: 20px;
        background: var(--bg-secondary);
        border-radius: 10px;
    }
</style>
