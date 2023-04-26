<script lang="ts">
    import { onMount } from 'svelte';
    import type { Platform, DivkitInstance } from '@divkitframework/divkit/typings/common';
    import { jsonStore } from '../data/jsonStore';
    import { render, SizeProvider, lottieExtensionBuilder } from '@divkitframework/divkit/client-devtool';
    import '@divkitframework/divkit/dist/client.css';
    import Lottie from 'lottie-web/build/player/lottie_light';

    export let platform: Platform = 'auto';

    let rootPreview: HTMLElement;
    let divBlock: DivkitInstance | null = null;

    function rerender(json: any): void {
        if (!rootPreview) {
            return;
        }

        if (divBlock) {
            divBlock.$destroy();
        }

        divBlock = render({
            target: rootPreview,
            json,
            id: 'test',
            platform,
            extensions: new Map<string, DivExtensionClass>([
                ['size_provider', SizeProvider],
                ['lottie', lottieExtensionBuilder(Lottie.loadAnimation)],
            ])
        });
    }

    $: rerender($jsonStore);

    onMount(() => {
        rerender($jsonStore);
    });
</script>

<div class="live-preview" bind:this={rootPreview}></div>

<style>
    .live-preview {
        flex: 1 0 0;
        background: #fff;
        color: #000;
    }
</style>
