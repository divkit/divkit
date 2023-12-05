<script lang="ts">
    import SplitView from './SplitView.svelte';
    import Editor from './Editor.svelte';
    import Viewer from './Viewer.svelte';
    import { isDesign, isSamples } from '../data/session';
    import Samples from './Samples.svelte';
    import { Truthy } from '../utils/truthy';
    import Design from './Design.svelte';

    $: components = [$isSamples && {
        component: Samples,
        weight: 1
    }, {
        component: Editor,
        weight: 3
    }, {
        component: Viewer,
        weight: 3,
        minWidth: 450
    }].filter(Truthy);
</script>

<main class="main">
    {#if $isDesign}
        <Design />
    {:else}
        <SplitView {components} />
    {/if}
</main>

<style>
    .main {
        display: flex;
        flex-direction: column;
        flex: 1 1 auto;
        min-height: 0;
    }
</style>
