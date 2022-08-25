<script lang="ts">
    import SplitView from './SplitView.svelte';
    import WebViewer from './WebViewer.svelte';
    import WebViewerSidebar from './WebViewerSidebar.svelte';
    import { panelStructure } from '../data/panels';

    $: components = [{
        component: WebViewer,
        weight: 3,
        minWidth: 400
    }, $panelStructure && {
        component: WebViewerSidebar,
        weight: 3,
        minWidth: 50
    }].filter(Boolean);
</script>

<main class="web-viewer-wrapper">
    <SplitView {components} />
</main>

<style>
    .web-viewer-wrapper {
        display: flex;
        flex-direction: row;
        flex: 1 1 auto;
        height: 100%;
    }
</style>
