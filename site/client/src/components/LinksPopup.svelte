<script lang="ts">
    import { createEventDispatcher, getContext, onMount } from 'svelte';
    import { fly } from 'svelte/transition';
    import QRCode from 'qrcode';
    import { session } from '../data/session';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';
    import Button from './Button.svelte';

    import { save } from '../data/sessionController';
    import CopyButton from './CopyButton.svelte';

    export let node: HTMLElement;

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);
    const dispatch = createEventDispatcher();

    let alreadySaved = Boolean($session.uuid);
    let promise: Promise<void>;
    let linkToEdit = '';
    let linkToPreview = '';
    let linkToJSON = '';
    let qr = '';

    function setUuid(res: {
        uuid: string;
        linkToEdit: string;
        linkToPreview: string;
        linkToJSON: string;
    }) {
        return Promise.resolve().then(() => {
            linkToEdit = res.linkToEdit;
            linkToPreview = res.linkToPreview;
            linkToJSON = res.linkToJSON;

            return QRCode.toDataURL(linkToJSON);
        }).then(url => {
            qr = url;
        });
    }

    function onCancel(): void {
        dispatch('close');
    }

    function onSave(): void {
        alreadySaved = true;

        promise = save().then(setUuid);
    }

    onMount(() => {
        if (alreadySaved) {
            promise = save().then(setUuid);
        }
    });
</script>

<div class="links-popup" transition:fly={{y: 30, duration: 200}} bind:this={node}>
    {#if alreadySaved}
        {#await promise}
            <div class="links-popup__loading"></div>
        {:then res}
            <div class="links-popup__content">
                <div class="links-popup__row">
                    <label class="links-popup__label">
                        <span class="links-popup__title">{$l10n('linkToEdit')}</span>
                        <input
                            class="links-popup__input"
                            type="text"
                            autocomplete="off"
                            autocorrect="off"
                            autocapitalize="off"
                            spellcheck="false"
                            value={linkToEdit}
                        >
                    </label>
                    <CopyButton mix="links-popup__copy" copy={linkToEdit} />
                </div>
                <!--<div class="links-popup__row">
                    <label class="links-popup__label">
                        <span class="links-popup__title">{$l10n('linkToPreview')}</span>
                        <input
                            class="links-popup__input"
                            type="text"
                            autocomplete="off"
                            autocorrect="off"
                            autocapitalize="off"
                            spellcheck="false"
                            value={linkToPreview}
                        >
                    </label>
                    <CopyButton mix="links-popup__copy" copy={linkToPreview} />
                </div>-->
                <div class="links-popup__row">
                    <label class="links-popup__label">
                        <span class="links-popup__title">{$l10n('linkToJson')}</span>
                        <input
                            class="links-popup__input"
                            type="text"
                            autocomplete="off"
                            autocorrect="off"
                            autocapitalize="off"
                            spellcheck="false"
                            value={linkToJSON}
                        >
                    </label>
                    <CopyButton mix="links-popup__copy" copy={linkToJSON} />
                </div>
                <img class="links-popup__qr" src={qr} alt={$l10n('qrCode')}>
            </div>
        {:catch err}
            <div class="links-popup__content">
                {$l10n('loadError')}
            </div>
        {/await}
    {:else}
        <div class="links-popup__confirm">
            {$l10n('saveConfirm')}
            <div class="links-popup__confirm-buttons">
                <Button on:click={onCancel}>
                    {$l10n('close')}
                </Button>
                <Button theme="filled" on:click={onSave}>
                    {$l10n('okSave')}
                </Button>
            </div>
        </div>
    {/if}
</div>

<style>
    .links-popup {
        position: absolute;
        top: 100%;
        right: 0;
        z-index: 1;
        width: 400px;
        margin-top: 10px;
        margin-right: 10px;
        background: var(--alt-bg);
        color: var(--alt-text);
        border-radius: 20px;
    }

    .links-popup__content {
        padding: 20px;
    }

    .links-popup__row {
        display: flex;
        align-items: flex-end;
        gap: 10px;
    }

    .links-popup__row:not(:first-child) {
        margin-top: 20px;
    }

    .links-popup__label {
        flex: 1 1 auto;
    }

    :global(.links-popup__copy) {
        flex: 0 0 auto;
    }

    .links-popup__title {
        display: block;
        margin: 0;
        font-size: 14px;
    }

    .links-popup__input {
        box-sizing: border-box;
        width: 100%;
        margin: 6px 0 0 0;
        padding: 4px 10px;
        border: none;
        border-radius: 1024px;
        background: var(--bg-primary);
        font-family: inherit;
        font-size: 16px;
        line-height: inherit;
        appearance: none;
    }

    .links-popup__qr {
        display: block;
        width: 100%;
        margin: 20px auto 0;
        image-rendering: pixelated;
        border-radius: 16px;
    }

    .links-popup__loading {
        width: 40px;
        height: 40px;
        margin: 40px auto;
        background: no-repeat 50% 50% url(../assets/load.svg);
        animation: rotate 1s infinite linear;
    }

    .links-popup__confirm {
        padding: 20px;
        text-align: center;
    }

    .links-popup__confirm-buttons {
        display: flex;
        justify-content: center;
        gap: 12px;
        margin-top: 16px;
    }

    @keyframes rotate {
        from {
            transform: rotate(0);
        }
        to {
            transform: rotate(1turn);
        }
    }
</style>
