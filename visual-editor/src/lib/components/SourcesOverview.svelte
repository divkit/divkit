<script lang="ts">
    import { getContext } from 'svelte';
    import { APP_CTX, type AppContext } from '../ctx/appContext';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import PanelTitle from './PanelTitle.svelte';
    import Spoiler2 from './controls/Spoiler2.svelte';

    const { state } = getContext<AppContext>(APP_CTX);
    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { sources } = state;
</script>

<PanelTitle title={$l10n('sourcesTitle')} />

<div class="sources-overview">
    {#if $sources.length}
        {#each $sources as source}
            <div class="sources-overview__item">
                <Spoiler2>
                    <div slot="title" class="sources-overview__title">
                        <div class="sources-overview__label">
                            {$l10n('sourcesName')}
                        </div>
                        <div class="sources-overview__value">
                            {source.key}
                        </div>
                        <div class="sources-overview__label">
                            {$l10n('sourcesUrl')}
                        </div>
                        <div class="sources-overview__value sources-overview__value_url">
                            {source.url}
                        </div>
                    </div>
                    <div class="sources-overview__json">
                        {JSON.stringify(source.example, null, 4)}
                    </div>
                </Spoiler2>
            </div>
        {/each}
    {:else}
        <div class="sources-overview__gap_top"></div>
        <div class="sources-overview__empty">{$l10n('sourcesEmpty')}</div>
        <div class="sources-overview__gap_bottom"></div>
    {/if}
</div>

<style>
    .sources-overview {
        display: flex;
        flex-direction: column;
        height: 100%;
    }

    .sources-overview__gap_top {
        flex: 1 0 0;
    }

    .sources-overview__empty {
        padding: 0 40px;
        font-size: 14px;
        text-align: center;
        color: var(--text-secondary);
    }

    .sources-overview__gap_bottom {
        flex: 3 0 0;
    }

    .sources-overview__item {
        padding: 0 20px 12px;
    }

    .sources-overview__title {
        display: grid;
        grid-template-columns: auto 1fr;
        gap: 4px 8px;
        font-size: 14px;
        line-height: 20px;
    }

    .sources-overview__label {
        color: var(--text-secondary);
    }

    .sources-overview__value {
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .sources-overview__value_url {
        color: var(--accent-purple);
    }

    .sources-overview__json {
        padding: 10px 16px;
        font-size: 14px;
        font-family: var(--monospace-font);
        line-height: 20px;
        border-radius: 8px;
        background: var(--fill-transparent-05);
        white-space: pre-wrap;
    }
</style>
