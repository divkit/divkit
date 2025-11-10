<script lang="ts">
    import '@divkitframework/divkit/dist/client.css';
    import './PreviewSection.styl';
    import ErrorsSection from './ErrorsSection.svelte';
    import { render as renderDivkit } from '@divkitframework/divkit/client';
    import type { DivkitInstance } from '@divkitframework/divkit/typings/common';
    import type { PreviewError, UIMessage } from '../types/messages';
    import type {
        GeneratedNode,
        GeneratedNodeParameter
    } from '../types/common';
    import addIcon from '../assets/add.svg?raw';
    import { copyToClipboard } from '../utils/copyToClipboard';
    import { onDestroy } from 'svelte';

    export let currentNode: GeneratedNode;
    export let loadingState: string;
    export let parameters: GeneratedNodeParameter[];
    export let isParametersButtonActive: boolean;
    export let showErrors: boolean;
    export let previewErrors: PreviewError[];
    export let previewWarns: PreviewError[];

    let divkitInstance: DivkitInstance;
    let previewNode: HTMLElement;
    let previewSectionNode: HTMLElement;
    let previewHeaderNode: HTMLElement;
    let needUpdateImages = false;
    let isPlaygroundLinkGenerating = false;
    let isGenerateClicked = false;
    let isJsonCopied = false;
    let copiedTimeout: number | null = null;

    const playgroundBaseUrl = process.env.__PLAYGROUND_BASE_URL || 'https://divkit.tech/playground';

    const openPlayground = async () => {
        try {
            isPlaygroundLinkGenerating = true;
            const res = await fetch(`${playgroundBaseUrl}/api/share`, {
                body: JSON.stringify({
                    value: currentNode.jsonString,
                    json: currentNode.jsonString,
                    language: 'json',
                }),
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (!res.ok) {
                throw new Error('Cannot generate link for playground');
            }
            const data: { uuid: string } = await res.json();
            const playgroundLink = `${playgroundBaseUrl}/?uuid=${data.uuid}`;

            window.open(playgroundLink, '_blank');
            isPlaygroundLinkGenerating = false;
        } catch (error) {
            parent.postMessage<UIMessage>({
                pluginMessage: {
                    type: 'showFigmaNotification',
                    data: {
                        message: (error as Error).message,
                        error: true,
                    },
                },
            }, '*');
            isPlaygroundLinkGenerating = false;
        }
    };

    const createJson = () => {
        loadingState = 'loading';
        parent.postMessage<UIMessage>({
            pluginMessage: {
                type: 'createJson',
                data: {
                    nodeId: currentNode.id,
                    needUpdateImages,
                },
            },
        }, '*');
        needUpdateImages = false;
        isGenerateClicked = true;
    };

    const addParameter = () => {
        parent.postMessage<UIMessage>({
            pluginMessage: {
                type: 'addParameter',
                data: {
                    id: currentNode.id,
                },
            },
        }, '*');
    };

    const removeParameter = (id: string) => {
        parent.postMessage<UIMessage>({
            pluginMessage: {
                type: 'removeParameter',
                data: {
                    parentId: currentNode.id,
                    parameterId: id,
                },
            },
        }, '*');
    };

    const onCopyClick = () => {
        if (!currentNode.jsonString || isJsonCopied) {
            return;
        }

        copyToClipboard(currentNode.jsonString)
            .then(() => {
                isJsonCopied = true;
                copiedTimeout = window.setTimeout(() => isJsonCopied = false, 1000);
            });
    };

    $: {
        if (currentNode?.jsonString && previewNode) {
            if (divkitInstance) {
                divkitInstance.$destroy();
            }

            divkitInstance = renderDivkit({
                target: previewNode,
                json: JSON.parse(currentNode.jsonString),
                id: 'preview',
            });

            if (isGenerateClicked) {
                previewSectionNode?.scrollTo({
                    top: previewHeaderNode?.offsetTop,
                    behavior: 'smooth',
                });
                isGenerateClicked = false;
            }
        }
    }

    onDestroy(() => {
        if (copiedTimeout) {
            window.clearTimeout(copiedTimeout);
            copiedTimeout = null;
        }
    });
</script>

{#if showErrors}
    <ErrorsSection
        bind:showErrors={showErrors}
        {previewErrors}
        {previewWarns}
    />
{:else}
    <section class="preview-section" bind:this={previewSectionNode}>
        <div class="preview-section__header">
            <span class="preview-section__header-icon"></span>
            <h1 class="preview-section__header-title plugin-header">{currentNode.name}</h1>
            <button
                class="preview-section__create-button plugin-button {currentNode.jsonString ? 'plugin-button_secondary' : 'plugin-button_primary'}"
                on:click={createJson}
                disabled="{loadingState === 'loading'}"
            >
                {#if loadingState === 'loading'}
                    Building JSON...
                {:else}
                    {currentNode.jsonString ? 'Update' : 'Generate'}
                {/if}
            </button>
        </div>
        <div class="preview-section__middle-section">
            <div class="preview-section__parameters-section">
                <h2 class="preview-section__parameters-section-header plugin-header">
                    Mark as image
                    <button
                        class="preview-section__add-parameter-button"
                        on:click={addParameter}
                        disabled="{!isParametersButtonActive}"
                        title="{isParametersButtonActive ? '' : 'Select child node'}"
                    >
                        <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                        {@html addIcon}
                    </button>
                </h2>
                <ul class="preview-section__parameters-list">
                    {#each parameters as parameter}
                        <li class="preview-section__parameter-item">
                            <span class="preview-section__parameter-icon"></span>
                            <span class="preview-section__parameter-name">{parameter.nodeName}</span>
                            <button
                                class="preview-section__remove-parameter-button"
                                on:click={() => removeParameter(parameter.id)}
                            >
                                <span class="preview-section__remove-parameter-icon"></span>
                            </button>
                        </li>
                    {/each}
                </ul>
            </div>
            <div class="preview-section__generation-settings">
                <h2 class="preview-section__generation-settings-header plugin-header">Generation settings</h2>
                <label class="preview-section__checkbox-wrapper">
                    <input class="preview-section__checkbox" type="checkbox" bind:checked={needUpdateImages} />
                    <span class="preview-section__checkbox-icon"></span>
                    <span class="preview-section__checkbox-title">Re-upload images</span>
                </label>
            </div>
        </div>
        {#if currentNode.jsonString}
            <div class="preview-section__subheader" bind:this={previewHeaderNode}>
                <h2 class="plugin-header">Preview</h2>
                <button
                        class="plugin-button plugin-button_secondary"
                        on:click={onCopyClick}
                >
                    {isJsonCopied ? 'Copied' : 'Copy JSON'}
                </button>
                <button
                    class="plugin-button plugin-button_secondary"
                    on:click={openPlayground}
                    disabled="{isPlaygroundLinkGenerating}"
                >
                    Open in Playground
                </button>
            </div>
            <div
                bind:this={previewNode}
                class="preview-section__preview-wrapper"
                style:width="{currentNode.width}px"
                style:height="{currentNode.height}px"
            ></div>
        {/if}
    </section>
{/if}
