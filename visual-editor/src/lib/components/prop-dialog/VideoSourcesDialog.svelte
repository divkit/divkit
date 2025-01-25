<script lang="ts">
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import Text from '../controls/Text.svelte';
    import ContextDialog from './ContextDialog.svelte';
    import type { VideoSourceShowProps } from '../../ctx/appContext';
    import type { VideoSource } from '../../utils/video';

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);

    $: if (isShown && !readOnly && callback) {
        callback(value);
    }

    export function show(props: VideoSourceShowProps): void {
        callback = props.callback;
        target = props.target;
        value = props.value;
        readOnly = props.readOnly;
        isShown = true;
    }

    export function hide(): void {
        isShown = false;
    }

    let target: HTMLElement;
    let isShown = false;
    let value: VideoSource;
    let callback: ((val: VideoSource) => void) | undefined;
    let readOnly: boolean | undefined;

    function onClose(): void {
        isShown = false;
    }
</script>

{#if isShown && target}
    <ContextDialog {target} on:close={onClose} canMove={true}>
        <div class="video-sources-dialog__content">
            <div>
                <!-- svelte-ignore a11y-label-has-associated-control -->
                <label>
                    <div class="video-sources-dialog__label">
                        {$l10n('video-url')}
                    </div>
                    <Text
                        bind:value={value.url}
                        subtype="file"
                        fileType="video"
                        disabled={readOnly}
                    />
                </label>
            </div>

            <div>
                <!-- svelte-ignore a11y-label-has-associated-control -->
                <label>
                    <div class="video-sources-dialog__label">
                        {$l10n('video-mime-type')}
                    </div>
                    <Text
                        bind:value={value.mime_type}
                        disabled={readOnly}
                    />
                </label>
            </div>
        </div>
    </ContextDialog>
{/if}

<style>
    .video-sources-dialog__content {
        display: flex;
        flex-direction: column;
        gap: 24px;
        margin: 16px;
    }

    .video-sources-dialog__label {
        margin-bottom: 6px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
    }
</style>
