<script lang="ts">
    import {slide} from 'svelte/transition';
    import type { ViewerError } from '../data/externalViewers';
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

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
</style>
