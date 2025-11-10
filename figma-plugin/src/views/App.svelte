<script lang="ts">
    import './App.styl';

    import type {
        FigmaCodeMessage,
        PreviewError,
        UIMessage
    } from '../types/messages';
    import type {
        GeneratedNode,
        GeneratedNodeParameter,
        PluginUIScreen
    } from '../types/common';

    import MainSection from './MainSection.svelte';
    import PreviewSection from './PreviewSection.svelte';
    import SettingsSection from './SettingsSection.svelte';
    import Footer from './Footer.svelte';
    import { PREVIEW_SIZES } from '../constants/pluginSizes';

    let generatedNodes: GeneratedNode[] = [];
    let isMainButtonActive = false;
    let isParametersButtonActive = false;
    let currentNode: GeneratedNode | null = null;
    let parameters: GeneratedNodeParameter[] = [];
    let loadingState = 'initial';
    let currentSection: PluginUIScreen = 'main';
    let showErrors = false;
    let previewErrors: PreviewError[] = [];
    let previewWarns: PreviewError[] = [];
    let isTokenSaved: boolean;
    let lightName = '';
    let darkName = '';

    onmessage = (event: MessageEvent<{ pluginMessage: FigmaCodeMessage }>) => {
        const msg = event.data.pluginMessage;

        if (msg.type === 'setPluginUIScreen') {
            currentSection = msg.data.screen;

            if (currentSection === 'main') {
                currentNode = null;
                parameters = [];
            }
        }

        if (msg.type === 'updateStyleSettings') {
            lightName = msg.data.lightName;
            darkName = msg.data.darkName;
        }

        if (msg.type === 'checkPersonalToken') {
            isTokenSaved = msg.data.isTokenSaved;
        }

        if (msg.type === 'generationError') {
            loadingState = 'initial';
        }

        if (msg.type === 'updateMainButtonState') {
            isMainButtonActive = msg.data.isActive;
        }

        if (msg.type === 'updateParametersButtonState') {
            isParametersButtonActive = msg.data.isActive;
        }

        if (msg.type === 'renderGeneratedNode') {
            currentNode = msg.data.node;
            parameters = msg.data.parameters;
            currentSection = 'preview';
            loadingState = 'initial';

            parent.postMessage<UIMessage>({
                pluginMessage: {
                    type: 'resizePlugin',
                    data: PREVIEW_SIZES,
                },
            }, '*');
        }

        if (msg.type === 'renderGeneratedNodes') {
            generatedNodes = msg.data.generatedNodes;
        }

        if (msg.type === 'renderParameters') {
            parameters = msg.data.parameters;
        }

        if (msg.type === 'showPreviewErrors') {
            previewErrors = msg.data.errors.filter(e => e.level === 'error');
            previewWarns = msg.data.errors.filter(e => e.level === 'warn');
        }
    };

    $: {
        showErrors = false;
        parent.postMessage<UIMessage>({
            pluginMessage: {
                type: 'setPluginUIScreen',
                data: {
                    screen: currentSection,
                },
            },
        }, '*');
    }
</script>

<div class="plugin">
    {#if currentSection === 'main'}
        <MainSection
            {isMainButtonActive}
            {generatedNodes}
        />
    {:else if currentSection === 'preview' && currentNode}
        <PreviewSection
            bind:loadingState={loadingState}
            bind:showErrors={showErrors}
            {isParametersButtonActive}
            {parameters}
            {currentNode}
            {previewErrors}
            {previewWarns}
        />
    {:else if currentSection === 'settings'}
        <SettingsSection
            bind:section={currentSection}
            {isTokenSaved}
            {lightName}
            {darkName}
        />
    {/if}
    {#if !showErrors}
        <Footer
            bind:section={currentSection}
            bind:showErrors={showErrors}
            previewErrorsCount={previewErrors.length}
            previewWarnsCount={previewWarns.length}
        />
    {/if}
</div>
