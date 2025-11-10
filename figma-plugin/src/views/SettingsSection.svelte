<script lang="ts">
    import './SettingsSection.styl';

    import type { UIMessage } from '../types/messages';
    import type { PluginUIScreen } from '../types/common';

    export let section: PluginUIScreen;
    export let isTokenSaved: boolean;
    export let lightName: string;
    export let darkName: string;

    let personalToken = '';

    const saveStyles = () => {
        parent.postMessage<UIMessage>({
            pluginMessage: {
                type: 'saveStyles',
                data: {
                    lightName,
                    darkName,
                },
            },
        }, '*');
    };

    const clearStyles = () => {
        parent.postMessage<UIMessage>({
            pluginMessage: {
                type: 'clearStyles',
            },
        }, '*');
    };

    const saveToken = () => {
        parent.postMessage<UIMessage>({
            pluginMessage: {
                type: 'saveToken',
                data: {
                    personalToken,
                },
            },
        }, '*');
        personalToken = '';
    };

    const onWarnLinkClick = () => {
        window.open(
            'https://help.figma.com/hc/en-us/articles/8085703771159-Manage-personal-access-tokens#Generate_a_personal_access_token',
            '_blank'
        );
    };

    const isInternal = process.env.__SECRET_INTERNALS;
</script>

<section class="settings-section">
    <div class="settings-section__header">
        {#if isTokenSaved || isInternal}
            <button class="settings-section__back-button" on:click={() => section = 'main'}>
                <span class="settings-section__back-icon"></span>
            </button>
        {/if}
        <h1 class="plugin-header settings-section__header-title">Settings</h1>
    </div>
    {#if !isInternal}
        <div class="settings-section__token-section">
            {#if !isTokenSaved}
                <span class="settings-section__save-token-warn">
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                    <!-- eslint-disable-next-line max-len -->
                    To start using plugin you need <span class="settings-section__how-to-link" on:click={onWarnLinkClick}>Generate personal token</span>
                </span>
            {/if}
            <input
                class="settings-section__input"
                bind:value={personalToken}
                placeholder="Insert your personal token"
            />
            <button
                class="plugin-button plugin-button_secondary settings-section__save-token-button"
                on:click={saveToken}
                disabled="{!personalToken}"
            >
                Save token
            </button>
        </div>
    {/if}
    <div class="settings-section__styles-section">
        <h2 class="plugin-header">Styles settings</h2>
        <input
            class="settings-section__input"
            id="lightName"
            bind:value={lightName}
            placeholder="Light theme"
        />
        <input
            class="settings-section__input"
            id="darkName"
            bind:value={darkName}
            placeholder="Dark theme"
        />
        <div class="settings-section__styles-buttons">
            <button
                class="plugin-button plugin-button_secondary"
                on:click={saveStyles}
            >
                Save styles
            </button>
            <button
                class="plugin-button plugin-button_secondary"
                on:click={clearStyles}
            >
                Clear styles
            </button>
        </div>
    </div>
</section>
