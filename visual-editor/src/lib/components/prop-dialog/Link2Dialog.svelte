<script lang="ts">
    import { getContext, tick } from 'svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { submit } from '../../utils/keybinder/shortcuts';
    import trashIcon from '../../../assets/trash.svg?raw';
    import type { Link2DialogShowProps } from '../../ctx/appContext';

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);

    $: if (isShown && !disabled) {
        callback(value);
    }

    export function show(props: Link2DialogShowProps): void {
        callback = props.callback;
        target = props.target;
        value = props.value;
        disabled = props.disabled;
        isShown = true;

        tick().then(() => {
            if (input) {
                input.focus();
            }
        });
    }

    export function hide(): void {
        isShown = false;
    }

    let target: HTMLElement;
    let dialog: ContextDialog | undefined;
    let isShown = false;
    let value: string;
    let callback: (val: string) => void;
    let disabled: boolean | undefined;
    let input: HTMLInputElement;

    function onClose(): void {
        isShown = false;
    }

    function cleanup(): void {
        value = '';
        dialog?.hide();
        callback(value);
    }

    function onKeydown(event: KeyboardEvent): void {
        if (submit.isPressed(event) && !disabled) {
            event.preventDefault();
            event.stopPropagation();
            dialog?.hide();
            callback(value);
        }
    }

    function onInput(): void {
        callback(value);
    }
</script>

{#if isShown && target}
    <ContextDialog bind:this={dialog} direction="down" offsetY={12} {target} on:close={onClose}>
        <div class="link2-dialog__content">
            <input
                class="link2-dialog__input"
                autocomplete="off"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                placeholder={$l10nString('link.placeholder')}
                {disabled}
                bind:this={input}
                bind:value={value}
                on:keydown={onKeydown}
                on:input={onInput}
            >

            {#if !disabled}
                <button
                    class="link2-dialog__cleanup"
                    class:link2-dialog__cleanup_visible={Boolean(value)}
                    on:click={cleanup}
                    title={$l10nString('delete')}
                >
                    <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                    {@html trashIcon}
                </button>
            {/if}
        </div>
    </ContextDialog>
{/if}

<style>
    .link2-dialog__content {
        position: relative;
        display: flex;
    }

    .link2-dialog__input {
        flex: 1 0 0;
        min-width: 0;
        padding: 10px 40px 10px 16px;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        background: none;
        color: inherit;
        border: none;
        outline: none;
    }

    .link2-dialog__cleanup {
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
        transition-property: color, opacity, visibility;
        opacity: 0;
        visibility: hidden;
        cursor: pointer;
    }

    .link2-dialog__cleanup:hover {
        color: var(--accent-red);
    }

    .link2-dialog__cleanup_visible {
        visibility: visible;
        opacity: 1;
    }
</style>
