<script lang="ts">
    import {getContext} from 'svelte';
    import LanguageSelector from './LanguageSelector.svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);
</script>

<div class="header">
    <header class="header__header">
        <!-- svelte-ignore a11y-missing-content -->
        <a class="header__logo" href="/" aria-label="DivKit"></a>

        <div class="header__right">
            <ul class="header__links">
                <li class="header__item">
                    <a class="header__link" href="/">
                        Twitter
                    </a>
                </li>
                <li class="header__item">
                    <a class="header__link" href="/">
                        Github
                    </a>
                </li>
                <li class="header__item">
                    <a class="header__link" href="/">
                        Telegram
                    </a>
                </li>
            </ul>
            <LanguageSelector />
        </div>
    </header>

    <nav class="header__subnav">
        <a href="/" class="header__subnav-logo">
            {$l10n('playground')}
        </a>

        <div class="header__subnav-right">
            <ul class="header__subnav-links">
                <li>
                    <a class="header__subnav-link" href="/">{$l10n('samples')}</a>
                </li>
                <li>
                    <a class="header__subnav-link" href="/">{$l10n('share')}</a>
                </li>
            </ul>
        </div>
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

    .header__link {
        font-size: 20px;
        color: inherit;
        text-decoration: none;
    }

    .header__link:hover {
        color: red;
    }

    .header__subnav {
        display: flex;
        align-items: center;
        height: 40px;
        padding: 0 19px;
        background: var(--bg-secondary);
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
        gap: 20px;
        margin: 0;
        padding: 0;
        list-style: none;
    }

    .header__subnav-link {
        font-size: 16px;
        color: inherit;
        text-decoration: none;
    }

    .header__subnav-link:hover {
        color: red;
    }
</style>
