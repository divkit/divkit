<script lang="ts">
    import { getContext } from 'svelte';
    import PanelTitle from './PanelTitle.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import type { TreeLeaf } from '../ctx/tree';
    import { findLeaf } from '../utils/tree';
    import { APP_CTX, type AppContext } from '../ctx/appContext';

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { getTranslationKey, state } = getContext<AppContext>(APP_CTX);
    const {
        tanker,
        tankerKeyToNodes,
        selectedLeaf,
        highlightLeaf,
        highlightElem,
        highlightRanges,
        locale,
        tree
    } = state;

    let isUpdateInProgress = false;

    function getLeafByKey(key: string): TreeLeaf | undefined {
        const set = $tankerKeyToNodes.get(key);
        if (!set) {
            return;
        }

        const id = [...set][0];
        return findLeaf($tree, id);
    }

    function onTankerClick(key: string): void {
        const leaf = getLeafByKey(key);
        if (leaf) {
            selectedLeaf.set(leaf);
        }
    }

    function toggleHover(leaf: TreeLeaf | undefined, toggle: boolean): void {
        const node = leaf?.props.node;
        const range = leaf?.props.range;
        if (node && range && toggle) {
            highlightLeaf.set([leaf]);
            highlightElem.set([node]);
            highlightRanges.set([range]);
        } else {
            highlightLeaf.set(null);
            highlightElem.set(null);
            highlightRanges.set(null);
        }
    }

    function onOver(key: string): void {
        toggleHover(getLeafByKey(key), true);
    }

    function onOut(key: string): void {
        toggleHover(getLeafByKey(key), false);
    }

    async function updateTanker(): Promise<void> {
        if (isUpdateInProgress || !getTranslationKey) {
            return;
        }

        isUpdateInProgress = true;

        try {
            const keys = Array.from($tankerKeyToNodes.keys());
            const meta: Record<string, Record<string, string>> = {};
            for (const key of keys) {
                const obj = await getTranslationKey(key);
                if (obj) {
                    meta[key] = obj;
                }
            }
            tanker.set(meta);
        } finally {
            isUpdateInProgress = false;
        }
    }
</script>

<PanelTitle title={$l10n('tankerOverview')} />

{#if $tankerKeyToNodes.size}
    <div class="tanker-overview__list">
        {#each Array.from($tankerKeyToNodes.keys()) as key}
            <div class="tanker-overview__item">
                <button
                    class="tanker-overview__preview"
                    on:click={() => onTankerClick(key)}
                    on:mouseenter={() => onOver(key)}
                    on:mouseleave={() => onOut(key)}
                >
                    {$tanker[key]?.[$locale] || ''}
                </button>

                <div class="tanker-overview__info">
                    <div class="tanker-overview__tanker-label">
                        {$l10n('tankerKey')}
                    </div>

                    <button
                        class="tanker-overview__tanker-key"
                        on:click={() => onTankerClick(key)}
                        on:mouseenter={() => onOver(key)}
                        on:mouseleave={() => onOut(key)}
                    >
                        {key}
                    </button>
                </div>
            </div>
        {/each}

        <button class="tanker-overview__update" on:click={updateTanker}>
            {#if isUpdateInProgress}
                <div class="tanker-overview__update-loader"></div>
            {:else}
                <div class="tanker-overview__update-icon"></div>
            {/if}
            {$l10n('tankerUpdate')}
        </button>
    </div>
{:else}
    <div class="tanker-overview__empty">
        {$l10n('tankerOverviewEmpty')}
    </div>
{/if}

<style>
    .tanker-overview__list {
        padding: 8px 20px 4px;
    }

    .tanker-overview__item {
        margin-bottom: 16px;
    }

    .tanker-overview__preview {
        box-sizing: border-box;
        width: 100%;
        margin: 0;
        padding: 10px 14px;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        color: inherit;
        border: none;
        border-radius: 8px;
        background: var(--fill-transparent-1);
        text-align: left;
        cursor: pointer;
        transition: .15s ease-in-out;
        transition-property: border-color, background-color;
    }

    .tanker-overview__preview:hover {
        background: var(--fill-accent-2);
    }

    .tanker-overview__preview:active {
        background: var(--fill-accent-3);
    }

    .tanker-overview__preview:focus-visible {
        outline: 1px solid var(--accent-purple);
    }

    .tanker-overview__info {
        display: flex;
        margin-top: 4px;
    }

    .tanker-overview__tanker-label {
        font-size: 14px;
        line-height: 20px;
    }

    .tanker-overview__tanker-label {
        margin-right: 6px;
        color: var(--text-secondary);
    }

    .tanker-overview__tanker-key {
        margin: 0;
        padding: 0;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        color: var(--accent-purple);
        border: none;
        border-radius: 4px;
        background: none;
        appearance: none;
        cursor: pointer;
        transition: color .15s ease-in-out;
    }

    .tanker-overview__tanker-key:hover {
        text-decoration: underline;
    }

    .tanker-overview__tanker-key:active {
        color: var(--accent-purple-sub);
    }

    .tanker-overview__tanker-key:focus-visible {
        outline: 1px solid var(--accent-purple);
    }

    .tanker-overview__empty {
        padding: 8px 20px 20px;
        font-size: 14px;
        color: var(--text-secondary);
    }

    .tanker-overview__update {
        box-sizing: border-box;
        display: flex;
        align-items: center;
        margin: 0;
        padding: 9px 16px;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background: none;
        appearance: none;
        cursor: pointer;
        transition: .15s ease-in-out;
        transition-property: background-color, border-color;
    }

    .tanker-overview__update:hover {
        border-color: var(--fill-transparent-3);
        background-color: var(--fill-transparent-2);
    }

    .tanker-overview__update:active {
        border-color: var(--fill-transparent-3);
        background-color: var(--fill-transparent-3);
    }

    .tanker-overview__update:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }

    .tanker-overview__update-icon {
        width: 20px;
        height: 20px;
        margin-right: 8px;
        background: no-repeat 50% 50% url(../../assets/pullTanker.svg);
        background-size: contain;
        filter: var(--icon-filter);
    }

    .tanker-overview__update-loader {
        box-sizing: border-box;
        width: 20px;
        height: 20px;
        margin-right: 8px;
        border: 2px solid var(--text-primary);
        border-bottom-color: transparent;
        border-radius: 1024px;
        animation: rotate 1s linear infinite;
    }

    @keyframes rotate {
        from {
            transform: rotate(0deg);
        }
        to {
            transform: rotate(360deg);
        }
    }
</style>
