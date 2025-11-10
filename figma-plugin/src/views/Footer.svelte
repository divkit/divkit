<script lang="ts">
    import './Footer.styl';
    import type { PluginUIScreen } from '../types/common';
    import attentionIcon from '../assets/attention.svg?raw';

    export let section: PluginUIScreen;
    export let showErrors: boolean;
    export let previewErrorsCount: number;
    export let previewWarnsCount: number;

    const helpUrl = process.env.__HELP_URL || 'https://divkit.tech/en/learn/';
    const version = process.env.VERSION;
</script>

<footer class="footer-section">
    {#if section === 'preview'}
        {#if previewErrorsCount || previewWarnsCount}
            <button
                class="footer-section__errors-button"
                on:click={() => {
                    showErrors = true;
                    window.scrollTo(0, 0);
                }}
            >
                <span class="footer-section__errors-icon">
                    <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                    {@html attentionIcon}
                </span>
                <span
                    class="footer-section__errors-message"
                >
                    {`${previewErrorsCount} errors, ${previewWarnsCount} warnings`}
                </span>
            </button>
        {:else}
            <span class="footer-section__errors-message">No errors</span>
        {/if}
    {:else}
        <span class="footer-section__version">{version}</span>
    {/if}
    <div class="footer-section__right">
        <a
            class="footer-section__button"
            href={helpUrl}
            target="_blank"
        >
            <span class="footer-section__help-icon"></span>
        </a>
        {#if section === 'main'}
            <button
                class="footer-section__button"
                on:click={() => section = 'settings'}
            >
                <span class="footer-section__settings-icon"></span>
            </button>
        {/if}
    </div>
</footer>
