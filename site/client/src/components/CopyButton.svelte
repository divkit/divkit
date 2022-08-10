<script lang="ts">
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    export let copy: string;
    export let mix = '';

    let copyOk = false;
    let okTimeout: number;

    function onClick(): void {
        copyOk = false;
        clearTimeout(okTimeout);

        navigator.clipboard.writeText(copy).then(() => {
            copyOk = true;

            clearTimeout(okTimeout);
            okTimeout = window.setTimeout(() => {
                copyOk = false;
            }, 2000);
        });
    }
</script>

<button class="copy-button {mix}" title={$l10n('copy')} on:click={onClick}>
    {#if copyOk}
        <svg aria-hidden="true" class="copy-button__icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="m3 11 7 8L21 5"/></svg>
    {:else}
        <svg aria-hidden="true" class="copy-button__icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><rect fill="none" stroke="currentColor" stroke-width="2" width="13" height="13" x="8" y="9" rx="2" ry="2"/><path fill="none" stroke="currentColor" stroke-width="2" d="M16 9V5c0-1.108-.966-2-2.166-2H5.166C3.966 3 3 3.892 3 5v9c0 1.108.966 2 2.166 2H8"/></svg>
    {/if}
</button>

<style>
    .copy-button {
        width: 28px;
        height: 28px;
        margin: 0;
        padding: 0;
        background: var(--accent0);
        color: var(--accent0-text);
        border: none;
        border-radius: 1024px;
        appearance: none;
        cursor: pointer;
        transition: .15s ease-in-out;
        transition-property: background-color, color;
    }

    .copy-button:hover {
        background: var(--bg-primary);
        color: var(--accent0);
    }

    .copy-button__icon {
        display: block;
        width: 20px;
        height: 20px;
        margin: auto;
    }
</style>
