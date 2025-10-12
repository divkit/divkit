<script lang="ts">
    import { getContext } from 'svelte';
    import type { VideoSource } from '@divkitframework/divkit/typings/common';
    import type { ComponentProperty, SiblingComponentProperty } from '../../data/componentProps';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import Text from '../controls/Text.svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { APP_CTX, type AppContext, type VideoSourceShowProps } from '../../ctx/appContext';
    import UnknownPropWithLabel from '../simple-props/UnknownPropWithLabel.svelte';

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { rendererApi } = getContext<AppContext>(APP_CTX);

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

    $: evalValue = value ? (rendererApi().evalJson({
        url: value.url
    }) as {
        url: string;
    }).url : undefined;

    let target: HTMLElement;
    let isShown = false;
    let value: VideoSource;
    let callback: ((val: VideoSource) => void) | undefined;
    let readOnly: boolean | undefined;

    function onClose(): void {
        isShown = false;
    }

    function onUrlChange(event: CustomEvent<{
        value: unknown;
        item: ComponentProperty | SiblingComponentProperty;
    } | {
        values: {
            prop: string;
            value: unknown;
        }[];
        item: ComponentProperty | SiblingComponentProperty;
    }>): void {
        if (!('value' in event.detail)) {
            // Not supported
            return;
        }

        value.url = String(event.detail.value);
    }
</script>

{#if isShown && target}
    <ContextDialog {target} on:close={onClose} canMove={true}>
        <div class="video-sources-dialog__content">
            <div>
                <UnknownPropWithLabel
                    item={{
                        type: 'file',
                        subtype: 'video',
                        enablePerTheme: true,
                        name: 'video-url',
                    }}
                    value={value.url}
                    {evalValue}
                    on:change={onUrlChange}
                />
            </div>

            <div>
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
