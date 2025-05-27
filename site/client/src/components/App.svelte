<script lang="ts">
    import Header from './Header.svelte';
    import Main from './Main.svelte';
    import { onMount, setContext } from 'svelte';
    import { derived, get, writable } from 'svelte/store';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';
    import translations from '../auto/lang.json';
    import PointingPopup from './PointingPopup.svelte';
    // import LivePreview from './LivePreview.svelte';
    // import { viewModeStore } from '../data/viewModeStore';
    import { isInitialLoading, isLoadError, isSamples } from '../data/session';
    import Loader from './Loader.svelte';
    import ErrorPage from './ErrorPage.svelte';
    import type langObj from '../auto/lang.json';
    import { getLs, setLs } from '../utils/localStorage';
    import { initCounter } from '../data/metrika';

    const LS_LANG_KEY = 'playground:lang';

    let langVal = getLs(LS_LANG_KEY);
    if (langVal !== 'ru' && langVal !== 'en') {
        langVal = 'en';
    }
    let lang = writable<'ru' | 'en'>(langVal as 'ru' | 'en');
    const l10n = derived(lang, lang => {
        return (key: keyof typeof langObj['en'], overrideLang?: string) =>
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            (translations as any)[overrideLang || lang]?.[key] || '';
    });

    lang.subscribe(val => {
        if (val !== langVal) {
            setLs(LS_LANG_KEY, val);
            langVal = val;
        }
    });

    setContext<LanguageContext>(LANGUAGE_CTX, {
        lang,
        getLanguage(): string {
            return get(lang);
        },
        setLanguage(name: 'ru' | 'en'): void {
            lang.set(name);
        },
        l10n,
        languagesList() {
            return ['ru', 'en'];
        }
    });

    onMount(() => {
        initCounter();
    });
</script>

<svelte:head>
    <title>{`DivKit ${$l10n('playground')}`}</title>

    <meta name="description" content={$l10n('description')}>

    {#if $isSamples}
        <link rel="canonical" href="https://divkit.tech/playground?samples=1">
    {:else}
        <link rel="canonical" href="https://divkit.tech/playground">
    {/if}
</svelte:head>

{#if $isLoadError}
    <ErrorPage />
{:else if $isInitialLoading}
    <Loader />
<!--{:else if $viewModeStore === 'preview'}
    <LivePreview />-->
{:else}
    <Header />
    <Main />
    <PointingPopup />
{/if}

<style>
    @font-face {
        font-family: "YS Text Variable";
        src: url(https://yastatic.net/s3/home/fonts/ys/3/text-variable-full.woff2) format('woff2-variations');
        font-display: optional;
        font-weight: 400 700;
    }

    :global(html, body) {
        height: 100%;
    }

    :root {
        --bg-primary: #fff;
        --bg-secondary: #F1F1F1;
        --text-primary: #000;
        --text-primary-semi: rgba(0, 0, 0, 0.4);
        --separator: rgba(179, 179, 179, 0.3);

        --accent0: #FF9000;
        --accent0-text: #fff;
        --accent1: #CC2FD5;
        --accent1-text: #fff;
        --accent1-semi: rgba(204, 47, 213, 0.5);
        --accent2: #4039CD;

        --alt-bg: #252525;
        --alt-text: #fff;

        --scrollbar-bg: rgba(0, 0, 0, 0.1);
        --scrollbar-border: transparent;
        --scrollbar-alt-hover-bg: rgba(0, 0, 0, 0.2);
        --scrollbar-alt-hover-border: transparent;
        --scrollbar-hover-bg: rgba(0, 0, 0, 0.25);
        --scrollbar-hover-border: transparent;
        --scrollbar-active-bg: rgba(0, 0, 0, 0.3);
        --scrollbar-active-border: transparent;
    }

    :global(html) {
        font-size: 16px;
        font-family: 'YS Text Variable', system-ui, -apple-system, BlinkMacSystemFont, "Helvetica Neue", sans-serif;
        font-variant-numeric: proportional-nums;
        font-feature-settings: 'liga', 'kern', 'pnum';
        -webkit-font-smoothing: antialiased;
        line-height: 1.25;
    }

    :global(body) {
        display: flex;
        flex-direction: column;
        margin: 0;
        background: var(--bg-primary);
    }

    :global(*::-webkit-scrollbar) {
        display: block;
        width: 12px;
        height: 12px;
    }

    :global(*::-webkit-scrollbar-button) {
        display: none;
    }

    :global(*::-webkit-scrollbar-track) {
        background-color: transparent;
    }

    :global(*::-webkit-scrollbar-track-piece) {
        background-color: transparent;
    }

    :global(*::-webkit-scrollbar-thumb) {
        background-color: var(--scrollbar-bg);
        border: 1px solid var(--scrollbar-border);
        border-radius: 24px;
    }

    :global(*::-webkit-scrollbar-thumb:hover) {
        background-color: var(--scrollbar-hover-bg);
        border: 1px solid var(--scrollbar-hover-border);
        border-radius: 24px;
    }

    :global(*::-webkit-scrollbar-thumb:active) {
        background-color: var(--scrollbar-active-bg);
        border: 1px solid var(--scrollbar-active-border);
        border-radius: 24px;
    }
</style>
