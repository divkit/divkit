<script lang="ts">
    import { getContext } from 'svelte';

    import type { AnimationItem } from 'lottie-web';
    import Text from '../controls/Text.svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import Button2 from '../controls/Button2.svelte';
    import { APP_CTX, type AppContext, type File2DialogCallback, type File2DialogShowProps, type File2DialogValue } from '../../ctx/appContext';
    import loaderImage from '../../../assets/loader.svg?raw';
    import trashIcon from '../../../assets/trash.svg?raw';
    import { loadFileAsBase64 } from '../../utils/loadFileAsBase64';

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { uploadFile, previewWarnFileLimit, previewErrorFileLimit } = getContext<AppContext>(APP_CTX);

    const FILE_FILTER = {
        image: 'image/png, image/jpeg',
        gif: 'image/gif',
        lottie: 'text/json, application/json',
        video: 'video/mp4, video/mpeg, video/ogg, video/quicktime, video/webm'
    };
    const FILE_FILTER_SETS = Object.keys(FILE_FILTER).reduce<Record<string, Set<string>>>((acc, item) => {
        acc[item] = new Set(FILE_FILTER[item as keyof typeof FILE_FILTER].split(',').map(it => it.trim()));
        return acc;
    }, {});

    function updateLottie(_url: string): void {
        if (lottieItem) {
            lottieItem.destroy();
            lottieItem = null;
            if (lottieWrapper) {
                lottieWrapper.parentNode?.removeChild(lottieWrapper);
            }
        }

        if (value.url) {
            const url = value.url;
            import('../../data/lottieApi').then(({ loadAnimation }) => {
                if (url !== value.url) {
                    return;
                }
                lottieWrapper = document.createElement('div');
                lottieWrapper.className = 'file2-dialog__lottie-wrapper';
                previewNode.appendChild(lottieWrapper);
                lottieItem = loadAnimation({
                    container: lottieWrapper,
                    path: url,
                    renderer: 'svg',
                    loop: true
                });
            });
        }
    }

    $: if (isShown) {
        if (!disabled) {
            callback(value);
        }

        if (subtype === 'lottie') {
            updateLottie(value.url || '');
        } else {
            imagePreviewError = false;
        }
    }

    export function show(props: File2DialogShowProps): void {
        callback = props.callback;
        target = props.target;
        value = props.value;
        title = props.title;
        hasSize = props.hasSize;
        hasDelete = props.hasDelete;
        subtype = props.subtype;
        commonSubtype = subtype === 'image_preview' ? 'image' : subtype;
        imagePreviewError = false;
        direction = props.direction;
        onHide = props.onHide;
        disabled = props.disabled || false;
        showError = false;
        isShown = true;
    }

    export function hide(): void {
        dialog?.hide();
    }

    let target: HTMLElement;
    let isShown = false;
    let hasSize: boolean | undefined = false;
    let hasDelete: boolean | undefined = false;
    let value: File2DialogValue = {
        url: '',
        width: 0,
        height: 0
    };
    let callback: File2DialogCallback;
    let title = '';
    let subtype = '';
    let commonSubtype = '';
    let lottieItem: AnimationItem | null;
    let previewNode: HTMLElement;
    let lottieWrapper: HTMLElement | null = null;
    let globalDragOver = false;
    let globalDragOverSet = new Set<HTMLElement>();
    let dragOver = false;
    let loading = false;
    let showError: false | 'load' | 'big' | 'big-warn' = false;
    let imagePreviewError = false;
    let direction: 'left' | 'right' | undefined;
    let onHide: (() => void) | undefined;
    let disabled = false;
    let dialog: ContextDialog | undefined;

    $: showFileSelect = !value.url;

    $: placeholder = subtype ? $l10nString(`file.${commonSubtype}_placeholder`) : '';

    $: fileFilter = subtype ? FILE_FILTER[commonSubtype as keyof typeof FILE_FILTER] : '';

    function onClose(): void {
        isShown = false;
        onHide?.();
    }

    function upload(file: File): Promise<void> {
        if (subtype === 'image_preview') {
            if (file.size > previewErrorFileLimit) {
                showError = 'big';
                return Promise.resolve();
            }
        }

        loading = true;
        showError = false;

        const func = subtype === 'image_preview' ? loadFileAsBase64 : uploadFile;

        return func(file).then(url => {
            value.url = url;
            loading = false;

            if (file.size > previewWarnFileLimit && subtype !== 'video') {
                showError = 'big-warn';
                return Promise.resolve();
            }
        }).catch(_err => {
            loading = false;
            showError = 'load';
        });
    }

    function getFile(list: FileList): File | undefined {
        return Array.from(list).find(it => {
            return it.type && FILE_FILTER_SETS[commonSubtype] && FILE_FILTER_SETS[commonSubtype].has(it.type);
        });
    }

    function onFilesChange(event: Event): void {
        const input = event.target as HTMLInputElement;
        const file = input.files && input.files.length > 0 && getFile(input.files);

        if (file) {
            upload(file);
        } else {
            showError = 'load';
        }

        input.value = '';
        input.type = '';
        input.type = 'file';
    }

    function onWindowDragEnter(event: DragEvent): void {
        if (!(event.target instanceof HTMLElement)) {
            return;
        }

        event.preventDefault();
        globalDragOverSet.add(event.target);
        globalDragOver = true;
    }

    function onWindowDragLeave(event: DragEvent): void {
        if (!(event.target instanceof HTMLElement)) {
            return;
        }

        event.preventDefault();
        globalDragOverSet.delete(event.target);
        globalDragOver = globalDragOverSet.size > 0;
    }

    function onWindowDrop(): void {
        globalDragOverSet = new Set();
        globalDragOver = false;
    }

    function onDragOver(event: DragEvent): void {
        event.preventDefault();
        if (event.dataTransfer) {
            event.dataTransfer.dropEffect = 'copy';
        }
    }

    function onDragEnter(event: DragEvent): void {
        event.preventDefault();
        dragOver = true;
    }

    function onDragLeave(event: DragEvent): void {
        event.preventDefault();
        dragOver = false;
    }

    function onDrop(event: DragEvent): void {
        event.preventDefault();
        dragOver = false;
        globalDragOver = false;
        globalDragOverSet = new Set();
        const file = event.dataTransfer?.files && getFile(event.dataTransfer.files);
        if (file) {
            upload(file);
        } else {
            showError = 'load';
        }
    }

    function onInputChange(): void {
        showError = false;
    }

    function onImageError(): void {
        imagePreviewError = true;
    }

    function onCleanup(): void {
        value.url = '';
        dialog?.hide();
    }
</script>

<svelte:window
    on:dragenter={onWindowDragEnter}
    on:dragleave={onWindowDragLeave}
    on:drop={onWindowDrop}
/>

{#if isShown && target && subtype}
    <ContextDialog
        bind:this={dialog}
        {target}
        hasClose={true}
        {direction}
        canMove={true}
        on:close={onClose}
    >
        <div class="file2-dialog__title">
            {title}
        </div>
        <div
            class="file2-dialog__drop-area"
            class:file2-dialog__drop-area_global-drag={globalDragOver || loading}
            class:file2-dialog__drop-area_disabled={disabled}
        >
            <div class="file2-dialog__standard-content">
                <div
                    class="file2-dialog__file-preview"
                    class:file2-dialog__file-preview_error={showError}
                >
                    <div class="file2-dialog__file-preview-container" bind:this={previewNode}>
                        {#if value.url}
                            {#if value.url && !imagePreviewError && (commonSubtype === 'image' || commonSubtype === 'gif')}
                                <img
                                    class="file2-dialog__image-preview"
                                    src={value.url}
                                    alt=""
                                    on:error={onImageError}
                                />
                            {:else if commonSubtype === 'video'}
                                <!-- svelte-ignore a11y-media-has-caption -->
                                <video class="file2-dialog__video-preview" src={value.url}></video>
                            {/if}
                        {/if}
                    </div>

                    <div
                        class="file2-dialog__file-select"
                        class:file2-dialog__file-select_always-shown={showFileSelect}
                    >
                        <div class="file2-dialog__file-label">
                            {$l10nString(`file.${commonSubtype}_label`)}
                        </div>

                        <Button2 tag="div" theme="normal">
                            {$l10nString('file.select')}
                            <input type="file" class="file2-dialog__file" accept={fileFilter} on:change={onFilesChange}>
                        </Button2>
                    </div>
                </div>

                {#if showError}
                    <div
                        class="file2-dialog__error"
                        class:file2-dialog__error_warn={showError === 'big-warn'}
                    >
                        {$l10nString((showError === 'big' || showError === 'big-warn') ? 'file.too_big' : `file.${commonSubtype}_error`)}
                    </div>
                {/if}

                <Text
                    bind:value={value.url}
                    {placeholder}
                    hasButton={Boolean(value.url && hasDelete && !disabled)}
                    on:change={onInputChange}
                    {disabled}
                >
                    <div slot="button">
                        {#if value.url && hasDelete && !disabled}
                            <button
                                class="file2-dialog__cleanup"
                                title={$l10nString('delete')}
                                on:click={onCleanup}
                            >
                                <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                                {@html trashIcon}
                            </button>
                        {/if}
                    </div>
                </Text>
            </div>

            <!-- svelte-ignore a11y-no-static-element-interactions -->
            <div
                class="file2-dialog__drop-target"
                class:file2-dialog__drop-target_over={dragOver}
                on:dragover={onDragOver}
                on:dragenter={onDragEnter}
                on:dragleave={onDragLeave}
                on:drop={onDrop}
            >
                {#if loading}
                    <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                    {@html loaderImage}
                {:else}
                    {$l10nString(`file.${commonSubtype}_label`)}
                {/if}
            </div>
        </div>

        {#if hasSize}
            <div class="file2-dialog__size">
                <!-- svelte-ignore a11y-label-has-associated-control -->
                <label class="file2-dialog__size-part">
                    <div class="file2-dialog__label">{$l10nString('props.width')}</div>
                    <Text
                        bind:value={value.width}
                        min={1}
                        max={1024}
                        subtype="integer"
                        inlineLabel="W"
                        {disabled}
                    />
                </label>
                <!-- svelte-ignore a11y-label-has-associated-control -->
                <label class="file2-dialog__size-part">
                    <div class="file2-dialog__label">{$l10nString('props.height')}</div>
                    <Text
                        bind:value={value.height}
                        min={1}
                        max={1024}
                        subtype="integer"
                        inlineLabel="H"
                        {disabled}
                    />
                </label>
            </div>
        {/if}
    </ContextDialog>
{/if}

<style>
    .file2-dialog__title {
        margin: 16px 64px 12px 20px;
        font-size: 14px;
        font-weight: 500;
    }

    .file2-dialog__drop-area {
        position: relative;
        margin: 0 20px 20px;
    }

    .file2-dialog__standard-content {
        transition: opacity .15s ease-in-out;
    }

    .file2-dialog__drop-area_global-drag .file2-dialog__standard-content {
        opacity: 0;
    }

    .file2-dialog__drop-target {
        position: absolute;
        top: 0;
        left: 0;
        box-sizing: border-box;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        padding: 32px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
        border-radius: 8px;
        border: 1px dashed var(--fill-accent-3);
        background: var(--fill-accent-1);
        opacity: 0;
        text-align: center;
        transition: .15s ease-in-out;
        transition-property: border, opacity;
        visibility: hidden;
    }

    .file2-dialog__drop-area_global-drag .file2-dialog__drop-target {
        opacity: 1;
        visibility: visible;
    }

    .file2-dialog__drop-target_over {
        border: 1px solid var(--accent-purple);
    }

    .file2-dialog__file-preview {
        position: relative;
        box-sizing: border-box;
        height: 138px;
        margin-bottom: 16px;
        border: 1px dashed var(--fill-accent-3);
        border-radius: 8px;
        overflow: hidden;
    }

    .file2-dialog__file-preview_error {
        margin-bottom: 8px;
    }

    .file2-dialog__file-preview-container,
    :global(.file2-dialog__lottie-wrapper) {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
    }

    .file2-dialog__file-preview-container {
        transition: opacity .15s ease-in-out;
    }

    .file2-dialog__image-preview {
        width: 100%;
        height: 100%;
        object-fit: contain;
    }

    .file2-dialog__video-preview {
        width: 100%;
        height: 100%;
    }

    .file2-dialog__file-select {
        position: absolute;
        top: 0;
        left: 0;
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 18px;
        width: 100%;
        height: 100%;
        padding: 24px;
        background-color: var(--fill-accent-1);
        opacity: 0;
        transition: opacity .15s ease-in-out;
        will-change: opacity;
    }

    .file2-dialog__drop-area_disabled .file2-dialog__file-select {
        visibility: hidden;
    }

    .file2-dialog__drop-area:not(.file2-dialog__drop-area_disabled)
    .file2-dialog__file-preview:hover
    .file2-dialog__file-preview-container {
        opacity: .2;
    }

    .file2-dialog__drop-area:not(.file2-dialog__drop-area_disabled)
    .file2-dialog__file-preview:hover
    .file2-dialog__file-select {
        opacity: 1;
    }

    .file2-dialog__drop-area:not(.file2-dialog__drop-area_disabled) .file2-dialog__file-select_always-shown {
        opacity: 1;
    }

    .file2-dialog__file-label {
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
        user-select: none;
        text-align: center;
        cursor: default;
    }

    .file2-dialog__file {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        opacity: 0;
        font-size: 1024px;
    }

    :global(.file2-dialog__loader) {
        width: 28px;
        height: 28px;
        color: var(--fill-accent-3);
        animation: file2-dialog__rotate 1s infinite linear;
    }

    .file2-dialog__error {
        margin-bottom: 12px;
        font-size: 12px;
        line-height: 18px;
        color: var(--accent-red);
    }

    .file2-dialog__error_warn {
        color: var(--accent-orange);
    }

    .file2-dialog__size {
        display: flex;
        gap: 16px;
        margin: 0 20px 20px;
    }

    .file2-dialog__size-part {
        flex: 1 0 0;
        min-width: 0;
    }

    .file2-dialog__label {
        margin-bottom: 6px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
    }

    .file2-dialog__cleanup {
        position: absolute;
        top: 0;
        right: 4px;
        bottom: 0;
        box-sizing: border-box;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 32px;
        height: 32px;
        margin: auto 0;
        border: none;
        border-radius: 4px;
        background: none;
        color: var(--icons-gray);
        transition: .15s ease-in-out;
        transition-property: color;
        cursor: pointer;
    }

    .file2-dialog__cleanup:hover {
        color: var(--accent-red);
    }

    @keyframes file2-dialog__rotate {
        from {
            transform: rotate(0deg);
        }
        to {
            transform: rotate(360deg);
        }
    }
</style>
