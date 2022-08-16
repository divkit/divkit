<script lang="ts" context="module">
    import type {
        ExternalViewer,
        // Device,
        // ViewerState,
        WebViewer,
        ViewerBase
    } from '../data/externalViewers';

    function isWebViewer(viewer: ViewerBase): viewer is WebViewer {
        return viewer.id === 'web';
    }
</script>

<script lang="ts">
    import WebViewerWrapper from './WebViewerWrapper.svelte';
    // import ExternalViewer from './externalViewer.svelte';
    // import ViewerButtons from './ViewerButtons.svelte';
    // import ErrorView from './errorView.svelte';
    import { externalViewers } from '../data/externalViewers';
    // import { convertToJsonStore } from '../data/jsonStore';
    import ViewerPanels from './ViewerPanels.svelte';
    import { webViewerErrors } from '../data/webViewerErrors';

    let viewers: ViewerBase[] = [{
        id: 'web',
        connected: true
    }];
    let currentViewer = 'web';
    let currentViewerObj: ViewerBase = viewers[0];

    function updateViewers(newExternalViewers: ExternalViewer[]): void {
        for (const viewer of newExternalViewers) {
            let existentViewer = viewers.find(v => v.id === viewer.id);

            if (existentViewer) {
                existentViewer.device = viewer.device;
                existentViewer.lastState = viewer.lastState;
            } else {
                viewers.push({
                    id: viewer.id,
                    connected: true,
                    device: viewer.device,
                    lastState: viewer.lastState
                });
            }
        }

        for (const existentViewer of viewers) {
            const found = newExternalViewers.some(v => v.id === existentViewer.id);

            existentViewer.connected = found || existentViewer.id === 'web';
            if (existentViewer.id === currentViewer && !found) {
                currentViewer = 'web';
            }
        }

        viewers = [...viewers];
    }

    $: updateViewers($externalViewers);

    $: {
        currentViewerObj = viewers.find(v => v.id === currentViewer) as ViewerBase;
    }
</script>

<div class="viewer">
<!--    <ViewerButtons {viewers} bind:currentViewer={currentViewer} />-->

    <div class="viewer__preview">
        {#if isWebViewer(currentViewerObj)}
            <WebViewerWrapper />
        {:else}
<!--            <ExternalViewer viewer={currentViewerObj} />-->
        {/if}
    </div>

    <ViewerPanels
        type={isWebViewer(currentViewerObj) ? 'web' : 'external'}
        errors={isWebViewer(currentViewerObj) ? $webViewerErrors : currentViewerObj.lastState?.errors}
    />
<!--    <ErrorView errors={$convertToJsonStore.length ? $convertToJsonStore : currentViewerObj.lastState?.errors} />-->
</div>

<style>
    .viewer {
        display: flex;
        flex-direction: column;
        flex: 1 1 auto;
        height: 100%;
    }

    .viewer__preview {
        flex: 1 1 auto;
        overflow: auto;
    }
</style>
