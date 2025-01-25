<script lang="ts">
    import { createEventDispatcher, getContext, onDestroy } from 'svelte';
    import type { VideoSource } from '../../utils/video';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import { calcFileSizeMod, getFileSize } from '../../utils/fileSize';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { formatFileSize } from '../../utils/formatFileSize';

    export let value: VideoSource;

    const { state, videoSource2Dialog, warnFileLimit, errorFileLimit } = getContext<AppContext>(APP_CTX);
    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    let elem: HTMLElement;
    let url = '';
    let currentSize: number | undefined = undefined;
    let fileSizeMod = '';
    let sizeLabelWidth = 0;

    function updateVal(value: VideoSource): void {
        url = value.url || '';
    }

    $: updateVal(value);

    $: {
        getFileSize(String(value.url), 'video').then(res => {
            currentSize = res;
        });
    }

    $: fileSizeMod = calcFileSizeMod(currentSize, warnFileLimit, errorFileLimit);

    function onClick(): void {
        videoSource2Dialog().show({
            value,
            target: elem,
            readOnly: $readOnly,
            callback(newValue) {
                value = newValue;
                dispatch('change');
            }
        });
    }

    onDestroy(() => {
        videoSource2Dialog().hide();
    });
</script>

<button
    class="video-sources-item"
    class:video-sources-item_readonly={$readOnly}
    bind:this={elem}
    on:click={onClick}
>
    <div
        class="video-sources-item__url"
        style:--inline-size-width="{(currentSize && currentSize > 0 && sizeLabelWidth > 0) ? sizeLabelWidth : 0}px"
    >
        {url}

        {#if currentSize && currentSize > 0}
            <div
                class="video-sources-item__size-label"
                class:video-sources-item__size-label_error={fileSizeMod === 'error'}
                class:video-sources-item__size-label_warn={fileSizeMod === 'warn'}
                data-custom-tooltip={fileSizeMod ? $l10nString('file.too_big') : undefined}
                bind:offsetWidth={sizeLabelWidth}
            >
                {formatFileSize(currentSize)}
            </div>
        {/if}
    </div>
</button>

<style>
    .video-sources-item {
        position: relative;
        box-sizing: border-box;
        width: 100%;
        padding: 9px 15px;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background-color: var(--background-primary);
        transition: border-color .15s ease-in-out;
        cursor: pointer;
        text-align: left;
    }

    .video-sources-item_readonly {
        border: none;
        background: var(--fill-transparent-1);
    }

    .video-sources-item:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }

    .video-sources-item:not(.video-sources-item_readonly):hover {
        border-color: var(--fill-transparent-4);
    }

    .video-sources-item:not(.video-sources-item_readonly):focus-within {
        border-color: var(--accent-purple);
    }

    .video-sources-item__url {
        min-height: 20px;
        padding-right: var(--inline-size-width);
        font-size: 14px;
        line-height: 20px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .video-sources-item__url:focus {
        outline: none;
    }

    .video-sources-item__size-label {
        position: absolute;
        top: 0;
        bottom: 0;
        right: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        height: 24px;
        margin: auto 0;
        padding: 0 4px;
        font-size: 12px;
        background: var(--fill-transparent-1);
        border-radius: 4px;
    }

    .video-sources-item__size-label_error {
        background: var(--fill-red-2);
    }

    .video-sources-item__size-label_warn {
        background: var(--fill-orange-2);
    }
</style>
