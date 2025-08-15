<script lang="ts">
    import { fade } from 'svelte/transition';
    import type { ViewerBase } from '../data/externalViewers';

    export let viewers: ViewerBase[];

    export let currentViewer: string;

    function viewerTitle(viewer: ViewerBase) {
        if (!viewer.device) {
            return '';
        }

        return `${viewer.device.app_name || viewer.device.app} ${viewer.device.app_version || viewer.device.version} / ${viewer.device.os_name || viewer.device.os} ${viewer.device.os_version || viewer.device.sdk_name}`;
    }

    function viewerText(viewer: ViewerBase) {
        return viewer.device?.device || 'Web';
    }
</script>

<div class="viewer-buttons">
    {#each viewers as viewer}
        <label
            class="viewer-buttons__button"
            class:viewer-buttons__button_disconnected={!viewer.connected}
            class:viewer-buttons__button_active={viewer.id === currentViewer}
            title={viewerTitle(viewer)}
            transition:fade
        >
            <input
                class="viewer-buttons__input"
                type="radio"
                name="viewers"
                bind:group={currentViewer}
                value={viewer.id}
            >
            {viewerText(viewer)}
        </label>
    {/each}
</div>

<style>
    .viewer-buttons {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        gap: 16px;
        min-height: 50px;
        padding: 0 16px;
    }

    .viewer-buttons__button {
        position: relative;
        display: inline-block;
        padding: 4px 0;
        cursor: pointer;
        transition: .1s ease-in-out;
        transition-property: background, opacity;
        user-select: none;
        appearance: none;
        border: none;
        border-bottom: 2px solid transparent;
        color: var(--text-primary-semi);
        font: inherit;
    }

    .viewer-buttons__button_disconnected {
        opacity: .4;
    }

    .viewer-buttons__button:hover {
        border-bottom-color: var(--text-primary-semi);
    }

    .viewer-buttons__button_active {
        border-bottom-color: currentColor;
        color: inherit;
        cursor: default;
    }

    .viewer-buttons__input {
        position: absolute;
        left: 0;
        top: 0;
        opacity: 0;
        width: 1px;
        height: 1px;
    }
</style>
