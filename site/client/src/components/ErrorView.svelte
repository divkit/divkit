<script lang="ts">
    import {slide} from 'svelte/transition';
    import type { ViewerError } from '../data/externalViewers';
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    const {l10n, lang} = getContext<LanguageContext>(LANGUAGE_CTX);

    export let errors: ViewerError[];
</script>

<div class="error-view" transition:slide>
    {#if errors.length}
        <ul class="error-view__list" >
            {#each errors as error}
                <li class="error-view__item">
                    <details>
                        <summary
                            class="error-view__item-title"
                            class:error-view__item-title_warning={error.level === 'warn'}
                        >
                            {error.message}
                        </summary>
                        <div class="error-view__item-stack">
                            {#each error.stack as line}
                                <div class="error-view__item-stack-line">
                                    {line}
                                </div>
                            {/each}
                        </div>
                    </details>
                </li>
            {/each}
        </ul>
    {:else}
        <div class="error-view__empty">
            {$l10n('noErrors')}
        </div>
    {/if}

    <a class="errors-view__telegram" href={$lang === 'ru' ? 'https://t.me/divkit_community_ru' : 'https://t.me/divkit_community_en'} target="_blank" rel="noopener noreferrer">
        <div class="errors-view__telegram-icon"></div>
        {$l10n('telegramError')}
    </a>
</div>

<style>
    .error-view {
        display: flex;
        flex-direction: column;
        flex: 0 1 auto;
        min-height: 0;
    }

    .error-view__list {
        flex: 0 1 auto;
        min-height: 0;
        margin: 0;
        padding: 8px 0;
        list-style: none;
        overflow: auto;
    }

    .error-view__item-title {
        padding: 12px 20px 12px 40px;
        cursor: pointer;
        background: 16px 50% no-repeat url(../assets/errors.svg);
    }

    .error-view__item-title_warning {
        background-image: url(../assets/warnings.svg);
    }

    .error-view__item-title:hover {
        text-decoration: underline;
    }

    .error-view__item-title::marker {
        content: '';
    }

    .error-view__item-stack {
        padding: 0 20px 0 72px;
        white-space: pre-wrap;
        word-break: break-all;
    }

    .error-view__item-stack-line {
        text-indent: -20px;
    }

    .error-view__empty {
        padding: 12px 20px;
    }

    .errors-view__telegram {
        display: inline-block;
        padding: 20px 12px;
        text-decoration: none;
        color: inherit;
    }

    .errors-view__telegram:hover {
        color: red;
    }

    .errors-view__telegram-icon {
        display: inline-block;
        width: 24px;
        height: 24px;
        margin-right: 4px;
        vertical-align: -32%;
        background: no-repeat 50% 50%;
        background-image: url(../assets/telegram.svg);
        background-size: contain;
    }
</style>
