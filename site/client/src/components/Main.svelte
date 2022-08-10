<script lang="ts">
    import SplitView from './SplitView.svelte';
    import Editor from './Editor.svelte';
    import Viewer from './Viewer.svelte';

    $: components = [{
        component: Editor,
        weight: 3
    }, {
        component: Viewer,
        weight: 3,
        minWidth: 450
    }];
</script>

<main class="main">
    <SplitView {components} />
</main>

<style>
    .main {
        display: flex;
        flex-direction: column;
        flex: 1 1 auto;
        min-height: 0;
    }
</style>
