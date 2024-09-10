<script lang="ts">
    import { getContext, tick } from 'svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { submit } from '../../utils/keybinder/shortcuts';
    import type { Expression2ShowProps } from '../../ctx/appContext';

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);

    $: if (isShown && currentProps && !currentProps.disabled && currentProps.callback) {
        currentProps.callback(currentProps.value);
    }

    export function show(props: Expression2ShowProps): void {
        currentProps = props;
        isShown = true;

        tick().then(() => {
            if (input) {
                input.focus();
            }
        });
    }

    export function hide(): void {
        isShown = false;
        currentProps = null;
    }

    let currentProps: Expression2ShowProps | null;
    let dialog: ContextDialog;
    let isShown = false;
    let input: HTMLDivElement;

    function onClose(): void {
        isShown = false;
    }

    function onPaste(event: ClipboardEvent): void {
        event.preventDefault();
        if (event.clipboardData) {
            let text = event.clipboardData.getData('text/plain');
            text = text.trim();
            document.execCommand('inserttext', false, text);
        }
    }

    function onKeydown(event: KeyboardEvent): void {
        if (submit.isPressed(event)) {
            event.preventDefault();
            dialog?.hide();
        }
    }
</script>

{#if isShown && currentProps}
    <ContextDialog
        bind:this={dialog}
        direction="down"
        overflow="visible"
        offsetY={-50}
        target={currentProps.target}
        on:close={onClose}
    >
        <div class="expression2-dialog__content">
            <div class="expression2-dialog__title">
                {$l10nString('expressionTitle')}
            </div>
            <div class="expression2-dialog__input-box">
                {#if currentProps.disabled}
                    <div
                        class="expression2-dialog__input expression2-dialog__input_disabled"
                        bind:this={input}
                    >
                        {currentProps.value}
                    </div>
                {:else}
                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                    <div
                        {...{ autocomplete: 'off' }}
                        class="expression2-dialog__input"
                        autocorrect="off"
                        autocapitalize="off"
                        spellcheck="false"
                        placeholder={$l10nString('tankerPlaceholder')}
                        contenteditable="true"
                        on:paste={onPaste}
                        on:keydown={onKeydown}
                        bind:this={input}
                        bind:innerText={currentProps.value}
                    ></div>
                {/if}
            </div>
        </div>
    </ContextDialog>
{/if}

<style>
    .expression2-dialog__content {
        display: flex;
        flex-direction: column;
        gap: 8px;
        padding: 16px 20px 20px;
    }

    .expression2-dialog__title {
        font-size: 14px;
        font-weight: 500;
        line-height: 20px;
    }

    .expression2-dialog__input-box {
        position: relative;
    }

    .expression2-dialog__input {
        box-sizing: border-box;
        max-width: 100%;
        padding: 10px 16px;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        background: none;
        color: inherit;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        outline: none;
        transition: border-color .15s ease-in-out;
    }

    .expression2-dialog__input_disabled {
        border-color: transparent;
        background-color: var(--fill-transparent-1);
    }

    .expression2-dialog__input:not(.expression2-dialog__input_disabled):hover {
        border-color: var(--fill-transparent-4);
    }

    .expression2-dialog__input.expression2-dialog__input:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }
</style>
