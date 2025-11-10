<script lang="ts">
    import './MainSection.styl';
    import type { UIMessage } from '../types/messages';
    import type { GeneratedNode } from '../types/common';

    export let isMainButtonActive: boolean;
    export let generatedNodes: GeneratedNode[];

    const removeGeneratedNode = (id: string) => {
        parent.postMessage<UIMessage>({
            pluginMessage: {
                type: 'removeGeneratedNode',
                data: {
                    id,
                },
            },
        }, '*');
    };

    const onGeneratedNodeClick = (id: string) => {
        parent.postMessage<UIMessage>({
            pluginMessage: {
                type: 'openGeneratedNode',
                data: {
                    id,
                },
            },
        }, '*');
    };

    const saveNode = () => {
        parent.postMessage<UIMessage>({
            pluginMessage: {
                type: 'createGeneratedNode',
            },
        }, '*');
    };
</script>

<section class="main-section{generatedNodes.length ? ' main-section_with-generated-list' : ''}">
    <h1 class="plugin-header main-section__header">
        DivKit <span class="main-section__header-part">Layout Builder</span>
    </h1>
    <div class="main-section__start-section">
        <span class="main-section__start-text">Select frame with children for start working</span>
        <button
            class="plugin-button plugin-button_primary main-section__start-button"
            on:click={saveNode}
            disabled="{!isMainButtonActive}"
        >
            Start
        </button>
    </div>
    {#if generatedNodes.length}
        <div class="main-section__generated-list-section">
            <h2 class="plugin-header">Generated JSON</h2>
            <ul class="main-section__generated-list">
                {#each generatedNodes as node}
                    <li class="main-section__generated-node">
                        <button
                            class="main-section__generated-node-button"
                            on:click={() => onGeneratedNodeClick(node.id)}
                        >
                            <span class="main-section__generated-node-icon"></span>
                            <span class="main-section__generated-node-name">{node.name}</span>
                        </button>
                        <button
                            class="main-section__remove-node-button"
                            on:click={() => removeGeneratedNode(node.id)}
                        >
                            <span class="main-section__remove-icon"></span>
                        </button>
                    </li>
                {/each}
            </ul>
        </div>
    {/if}
</section>
