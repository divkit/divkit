<script lang="ts">
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import type { ViewerError } from '../utils/errors';
    import ContextDialog from './prop-dialog/ContextDialog.svelte';
    import Spoiler2 from './controls/Spoiler2.svelte';
    import Button2 from './controls/Button2.svelte';
    import { findLeaf } from '../utils/tree';
    import { APP_CTX, type AppContext } from '../ctx/appContext';

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const {
        highlightLeaf,
        highlightElem,
        highlightRanges,
        rendererErrors,
        themeStore,
        tree
    } = state;

    export function show(newTarget: HTMLElement, opts?: {
        leafId?: string;
    }): void {
        isShown = true;
        target = newTarget;
        leafId = opts?.leafId || '';
    }

    let isShown = false;
    let target: HTMLElement;
    let leafId = '';

    let errors: ViewerError[] = [];

    $: {
        if (leafId) {
            errors = $rendererErrors[leafId];
        } else {
            const list: ViewerError[] = [];
            for (const key in $rendererErrors) {
                list.push(...$rendererErrors[key]);
            }
            errors = list;
        }
    }

    function onClose(): void {
        isShown = false;
    }

    function onSelectedClear(): void {
        leafId = '';
    }

    function selectLeaf(leafId: string | undefined): void {
        if (!leafId) {
            return;
        }

        const leaf = findLeaf($tree, leafId);

        state.selectedLeaf.set(leaf || null);
        isShown = false;
    }

    function onEnter(leafId: string | undefined): void {
        const leaf = leafId && findLeaf($tree, leafId) || null;
        const node = leaf?.props.node;
        const range = leaf?.props.range;

        if (range) {
            highlightLeaf.set([leaf]);
            highlightElem.set(node ? [node] : null);
            highlightRanges.set([range]);
        } else {
            highlightLeaf.set(null);
            highlightElem.set(null);
            highlightRanges.set(null);
        }
    }

    function onLeave(): void {
        highlightLeaf.set(null);
        highlightElem.set(null);
        highlightRanges.set(null);
    }

    function stringifyArg(arg: unknown): string {
        if (typeof arg === 'string') {
            return arg;
        }
        return JSON.stringify(arg);
    }
</script>

{#if isShown}
    <ContextDialog
        target={target}
        direction="down"
        offsetY={8}
        wide
        on:close={onClose}
    >
        <div
            class="errors-dialog__content"
            class:errors-dialog__content_dark={$themeStore === 'dark'}
        >
            {#if leafId}
                <div class="errors-dialog__selected-block">
                    {$l10nString('errors.selectedTitle')}

                    <Button2 on:click={onSelectedClear} theme="normal">
                        {$l10nString('errors.clear')}
                    </Button2>
                </div>
            {/if}

            {#if errors.length}
                <ul class="errors-dialog__list">
                    {#each errors as item}
                        <li
                            class="errors-dialog__item"
                            on:mouseenter={() => onEnter(item.args?.leafId)}
                            on:mouseleave={onLeave}
                        >
                            <Spoiler2 open={false} mix="errors-dialog__spoiler">
                                <div slot="title" class="errors-dialog__item-title">
                                    <span
                                        class="errors-dialog__icon"
                                        class:errors-dialog__icon_error={item.level === 'error' || !item.level}
                                        class:errors-dialog__icon_warn={item.level === 'warn'}
                                    ></span>
                                    <span class="errors-dialog__text">
                                        {item.message}
                                    </span>
                                </div>

                                <div class="errors-dialog__item-details">
                                    {#if item.args && Object.keys(item.args).length}
                                        {#each Object.keys(item.args) as key}
                                            {#if key !== 'leafId'}
                                                <div>
                                                    {key} = {stringifyArg(item.args[key])}
                                                </div>
                                            {/if}
                                        {/each}
                                    {:else}
                                        {$l10nString('errors.noDetails')}
                                    {/if}
                                </div>
                            </Spoiler2>

                            {#if item.args?.leafId}
                                <Button2
                                    title={$l10nString('errors.select')}
                                    on:click={() => selectLeaf(item.args?.leafId)}
                                >
                                    <div class="errors-dialog__select" />
                                </Button2>
                            {/if}
                        </li>
                    {/each}
                </ul>
            {:else}
                {$l10nString('no_errors')}
            {/if}
        </div>
    </ContextDialog>
{/if}

<style>
    .errors-dialog__content {
        max-height: 50vh;
        padding: 12px 16px;
        overflow: auto;
    }

    .errors-dialog__list {
        display: flex;
        flex-direction: column;
        gap: 8px;
        list-style: none;
        margin: 0;
        padding: 0;
    }

    .errors-dialog__item-title {
        display: flex;
        align-items: center;
        flex: 0 1 auto;
    }

    .errors-dialog__text {
        min-width: 0;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .errors-dialog__icon {
        flex: 0 0 auto;
        width: 20px;
        height: 20px;
        margin-right: 12px;
        background: no-repeat 50% 50%;
        background-size: 20px;
    }

    .errors-dialog__icon_error {
        background-image: url(../../assets/errorsLight.svg);
    }

    .errors-dialog__content_dark .errors-dialog__icon_error {
        background-image: url(../../assets/errors.svg);
    }

    .errors-dialog__icon_warn {
        background-image: url(../../assets/warnings.svg);
    }

    .errors-dialog__item-details {
        margin: 6px 0 0 36px;
        font-size: 14px;
        line-height: 1.35;
    }

    .errors-dialog__selected-block {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-items: center;
        gap: 12px;
        margin-bottom: 16px;
        padding: 12px 16px;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
    }

    .errors-dialog__item {
        display: flex;
        gap: 12px;
        align-items: flex-start;
        min-width: 0;
    }

    :global(.errors-dialog__spoiler) {
        flex: 1 1 auto;
        min-width: 0;
    }

    .errors-dialog__select {
        display: block;
        flex: 0 0 auto;
        width: 20px;
        height: 20px;
        background: no-repeat 50% 50% url(../../assets/select.svg);
        background-size: 20px;
        filter: var(--icon-filter);
        content: '';
    }
</style>
