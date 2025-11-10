<script lang="ts">
    import './ErrorsSection.styl';
    import type { PreviewError, UIMessage } from '../types/messages';

    export let showErrors: boolean;
    export let previewErrors: PreviewError[];
    export let previewWarns: PreviewError[];

    const selectNodeWithError = (id: string) => {
        parent.postMessage<UIMessage>({
            pluginMessage: {
                type: 'setFigmaSelection',
                data: { id },
            },
        }, '*');
    };
</script>

<section class="errors-section">
    <div class="errors-section__header">
        <button class="errors-section__back-button" on:click={() => showErrors = false}>
            <span class="errors-section__back-icon"></span>
        </button>
        <h1 class="plugin-header errors-section__header-title">Issues</h1>
    </div>
    {#if previewErrors.length}
        <h2 class="errors-section__subheader">Errors</h2>
        <ul class="errors-section__list">
            {#each previewErrors as error}
                <li class="errors-section__list-item">
                    <span class="errors-section__error-message">{error.message}</span>
                    <button
                        class="errors-section__node-name"
                        on:click={() => selectNodeWithError(error.nodeInfo.id)}
                    >
                        {error.nodeInfo.name}
                    </button>
            {/each}
        </ul>
    {/if}
    {#if previewWarns.length}
        <h2 class="errors-section__subheader">Warnings</h2>
        <ul class="errors-section__list">
            {#each previewWarns as warning}
                <li class="errors-section__list-item">
                    <span class="errors-section__error-message">{warning.message}</span>
                    <button
                        class="errors-section__node-name"
                        on:click={() => selectNodeWithError(warning.nodeInfo.id)}
                    >
                        {warning.nodeInfo.name}
                    </button>
                </li>
            {/each}
        </ul>
    {/if}
</section>
