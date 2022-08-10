<script lang="ts">
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';
    import { panelErrors, panelStructure } from '../data/panels';
    import type { ViewerError } from '../data/externalViewers';
    import ErrorView from './ErrorView.svelte';

    export let type: 'web' | 'external';
    export let errors: ViewerError[];

    $: hasErrors = errors.length > 0;
    $: onlyWarnings = hasErrors && errors.every(error => error.level === 'warn');

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);
</script>

<div class="viewer-panels">
    <div class="viewer-panels__top">
        <div class="viewer-panels__left">
            <button
                class="viewer-panels__button"
                class:viewer-panels__button_toggled={$panelErrors}
                aria-expanded={$panelErrors ? 'true' : 'false'}
                on:click={() => $panelErrors = !$panelErrors}
            >
                {#if hasErrors}
                    <span
                        class="viewer-panels__error-icon"
                        class:viewer-panels__error-icon_warnings={onlyWarnings}
                    ></span>
                {/if}
                {$l10n(onlyWarnings ? 'warnings' : 'errors')}
                {#if hasErrors}
                    {`(${errors.length})`}
                {/if}
            </button>
        </div>
        <div class="viewer-panels__right">
            {#if type === 'web'}
                <button
                    class="viewer-panels__button"
                    class:viewer-panels__button_toggled={$panelStructure}
                    aria-expanded={$panelStructure ? 'true' : 'false'}
                    on:click={() => $panelStructure = !$panelStructure}
                >
                    {$l10n('structure')}
                </button>
            {/if}
        </div>
    </div>

    {#if $panelErrors}
        <ErrorView {errors} />
    {/if}
</div>

<style>
    .viewer-panels {
        display: flex;
        flex-direction: column;
        flex: 0 0 auto;
        max-height: 50%;
        background: var(--bg-secondary);
    }

    .viewer-panels__top {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 16px;
        flex: 0 0 auto;
        height: 50px;
        padding: 0 16px;
    }

    .viewer-panels__button {
        display: flex;
        align-items: center;
        margin: 0;
        padding: 4px 0;
        font: inherit;
        line-height: inherit;
        background: none;
        border: none;
        border-bottom: 2px solid transparent;
        border-radius: 0;
        appearance: none;
        cursor: pointer;
    }

    .viewer-panels__button:hover {
        border-bottom-color: var(--text-primary-semi);
    }

    .viewer-panels__button_toggled {
        border-bottom-color: var(--text-primary);
    }

    .viewer-panels__error-icon {
        width: 16px;
        height: 16px;
        margin-right: 8px;
        margin-bottom: 2px;
        background: no-repeat 50% 50% url(../assets/errors.svg);
    }

    .viewer-panels__error-icon_warnings {
        background-image: url(../assets/warnings.svg);
    }
</style>
