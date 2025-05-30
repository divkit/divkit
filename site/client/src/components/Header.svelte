<script lang="ts">
    import {getContext} from 'svelte';
    import LanguageSelector from './LanguageSelector.svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';
    import LinksPopup from './LinksPopup.svelte';
    import { isFeatures } from '../data/session';

    const {l10n, lang} = getContext<LanguageContext>(LANGUAGE_CTX);

    let linksPopupShown = false;
    let linksPopup: HTMLElement;
    let linksButton: HTMLElement;

    function onBodyClick(event: MouseEvent): void {
        const target = event.target as HTMLElement;
        if (
            !linksPopup?.contains(target) &&
            !linksButton?.contains(target) &&
            document.body.contains(target)
        ) {
            linksPopupShown = false;
        }
    }
</script>

<svelte:body on:click={onBodyClick} />

<div class="header">
    <header class="header__header">
        <!-- svelte-ignore a11y-missing-content -->
        <a class="header__logo" href="/" aria-label="DivKit"></a>

        <div class="header__right">
            <ul class="header__links">
                <li class="header__item">
                    <a class="header__link" href={$lang === 'ru' ? 'https://divkit.tech/docs/ru/' : 'https://divkit.tech/docs/en/'} target="_blank" rel="noopener noreferrer">
                        {$l10n('docs')}
                    </a>
                </li>
                <li class="header__item">
                    <a class="header__link" href="https://github.com/divkit/divkit" target="_blank" rel="noopener noreferrer">
                        <div class="header__icon header__icon_github"></div>
                        <span class="header__text">GitHub</span>
                    </a>
                </li>
                <li class="header__item">
                    <a class="header__link" href={$lang === 'ru' ? 'https://t.me/divkit_community_ru' : 'https://t.me/divkit_community_en'} target="_blank" rel="noopener noreferrer">
                        <div class="header__icon header__icon_telegram"></div>
                        <span class="header__text">{$l10n('telegram')}</span>
                    </a>
                </li>
            </ul>
            <LanguageSelector />
        </div>
    </header>

    <nav class="header__subnav">
        <div class="header__subnav-left">
            <ul class="header__subnav-links">
                <li class="header__subnav-item">
                    <a href="/playground" class="header__subnav-logo">
                        <span class="header__big-text">{$l10n('playground')}</span>
                        <span class="header__small-text">{$l10n('playgroundShort')}</span>
                    </a>
                </li>
                <li class="header__subnav-item">
                    <a class="header__subnav-link" href="/playground?samples=1">
                        {$l10n('samples')}
                    </a>
                </li>
                <li class="header__subnav-item">
                    <a class="header__subnav-link" href="/playground?design=1">
                        {$l10n('design')}
                    </a>
                </li>
                <li class="header__subnav-item">
                    <a class="header__subnav-link" href="/features">
                        <span class="header__big-text">{$l10n('featureSupport')}</span>
                        <span class="header__small-text">{$l10n('featureSupportSmall')}</span>
                    </a>
                </li>
            </ul>
        </div>

        {#if !$isFeatures}
            <div class="header__subnav-right">
                <ul class="header__subnav-links">
                    <li class="header__subnav-item">
                        <button
                            class="header__subnav-link"
                            on:click={() => linksPopupShown = !linksPopupShown}
                            bind:this={linksButton}
                        >{$l10n('share')}</button>

                        {#if linksPopupShown}
                            <LinksPopup bind:node={linksPopup} on:close={() => linksPopupShown = false} />
                        {/if}
                    </li>
                </ul>
            </div>
        {/if}
    </nav>
</div>

<style>
    .header__header {
        display: flex;
        align-items: center;
        height: 80px;
        padding: 0 19px;
    }

    .header__logo {
        display: block;
        width: 160px;
        height: 41px;
        flex: 0 0 auto;
        margin-right: 20px;
        background: no-repeat 50% 50% url(../assets/logo.svg);
    }

    .header__right {
        display: flex;
        align-items: baseline;
        margin-left: auto;
    }

    .header__links {
        display: flex;
        align-items: baseline;
        gap: 24px;
        margin: 0;
        margin-right: 24px;
        padding: 0;
        list-style: none;
    }

    @media (max-width: 700px) {
        .header__links {
            gap: 8px;
            margin-right: 8px;
        }
    }

    .header__link {
        font-size: 20px;
        color: inherit;
        text-decoration: none;
    }

    .header__link:hover {
        color: red;
    }

    .header__icon {
        display: inline-block;
        width: 24px;
        height: 24px;
        margin-right: 4px;
        vertical-align: -4px;
        background: no-repeat 50% 50%;
        background-size: contain;
    }

    .header__icon_github {
        background-image: url(../assets/github.svg);
    }

    .header__icon_telegram {
        background-image: url(../assets/telegram.svg);
    }

    .header__subnav {
        position: relative;
        display: flex;
        align-items: center;
        height: 40px;
        padding: 0 19px;
        background: var(--bg-secondary);
    }

    .header__subnav-left {
        min-width: 0;
    }

    .header__subnav-logo {
        font-size: 20px;
        color: inherit;
        text-decoration: none;
    }

    .header__subnav-logo:hover {
        color: red;
    }

    .header__subnav-right {
        margin-left: auto;
    }

    .header__subnav-links {
        display: flex;
        align-items: baseline;
        gap: 20px;
        min-width: 0;
        margin: 0;
        padding: 0;
        list-style: none;
        white-space: nowrap;
    }

    @media (max-width: 700px) {
        .header__subnav-links {
            gap: 6px;
        }
    }

    .header__subnav-item {
        min-width: 20px;
        text-overflow: ellipsis;
        overflow: hidden;
    }

    .header__subnav-link {
        font-size: 16px;
        font-family: inherit;
        line-height: inherit;
        color: inherit;
        text-decoration: none;
        background: none;
        border: none;
        border-radius: 0;
        appearance: none;
        cursor: pointer;
    }

    .header__subnav-link:hover {
        color: red;
    }

    @media (max-width: 700px) {
        .header__text {
            position: absolute;
            overflow: hidden;
            clip: rect(1px 1px 1px 1px);
            font-weight: 400;
            height: 1px;
            width: 1px;
        }
    }

    .header__small-text {
        display: none;
    }

    @media (max-width: 700px) {
        .header__big-text {
            display: none;
        }

        .header__small-text {
            display: inline;
        }
    }
</style>
