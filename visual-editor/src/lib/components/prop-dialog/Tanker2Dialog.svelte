<script lang="ts">
    import { afterUpdate, getContext, tick } from 'svelte';
    import { fly } from 'svelte/transition';
    import ContextDialog from './ContextDialog.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { cancel, submit, suggestDown, suggestUp } from '../../utils/keybinder/shortcuts';
    import { APP_CTX, type AppContext, type Tanker2DialogShowProps } from '../../ctx/appContext';
    import type { TranslationVariant } from '../../../lib';
    import { scrollIntoView } from '../../utils/scrollIntoView';

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { getTranslationSuggest, state } = getContext<AppContext>(APP_CTX);
    const { locale } = state;

    const DEFAULT_SUGGEST_MAX_HEIGHT = 384;

    export function show(props: Tanker2DialogShowProps): void {
        callback = props.callback;
        target = props.target;
        value = props.value;
        disabled = props.disabled;
        suggestList = [];
        suggestActive = -1;
        suggestLoaded = true;
        suggestInitiallyLoaded = false;
        isShown = true;

        tick().then(() => {
            if (input) {
                input.focus();

                if (value) {
                    onInput();
                }
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
    let contentElem: HTMLElement;

    let suggestInitiallyLoaded = false;
    let suggestShow = false;
    let suggestElem: HTMLDialogElement;
    let suggestList: TranslationVariant[] = [];
    let suggestActive = -1;
    let suggestLoaded = false;
    let suggestDirectionUp = false;
    let suggestMaxHeight = DEFAULT_SUGGEST_MAX_HEIGHT;

    function onClose(): void {
        isShown = false;
    }

    function onKeydown(event: KeyboardEvent): void {
        if (cancel.isPressed(event) && suggestShow && suggestList.length) {
            suggestShow = false;
        } else if (submit.isPressed(event) && !disabled) {
            if (suggestActive > -1) {
                selectItem(suggestList[suggestActive].key);
            }
        } else if (suggestUp.isPressed(event)) {
            if (suggestActive === -1 || suggestActive === 0) {
                suggestActive = suggestList.length - 1;
            } else {
                --suggestActive;
            }
        } else if (suggestDown.isPressed(event)) {
            if (suggestActive === -1 || suggestActive === suggestList.length - 1) {
                suggestActive = 0;
            } else {
                ++suggestActive;
            }
        } else {
            return;
        }

        event.preventDefault();
        event.stopPropagation();
    }

    function onInput(): void {
        if (disabled || !getTranslationSuggest) {
            return;
        }

        const requestValue = value;

        suggestLoaded = false;
        getTranslationSuggest(requestValue, $locale).then(list => {
            if (value !== requestValue) {
                return;
            }

            suggestInitiallyLoaded = true;
            suggestLoaded = true;

            suggestList = list;
            if (suggestActive >= list.length) {
                suggestActive = 0;
            }
        }).catch(_err => {
            suggestInitiallyLoaded = true;
            suggestLoaded = true;
        });
    }

    function onFocus(): void {
        suggestShow = true;
    }

    function onBlur(): void {
        suggestShow = false;
    }

    function selectItem(key: string): void {
        value = key;
        if (key.endsWith('.')) {
            onInput();
        } else {
            dialog?.hide();
            callback(value);
        }
    }

    afterUpdate(() => {
        if (suggestElem && !suggestElem.open) {
            const contentBbox = contentElem.getBoundingClientRect();
            const windowHeight = window.innerHeight;
            const freeSpaceBottom = windowHeight - contentBbox.bottom;

            suggestDirectionUp = freeSpaceBottom < DEFAULT_SUGGEST_MAX_HEIGHT &&
                freeSpaceBottom < contentBbox.top;
            suggestMaxHeight = Math.min(
                DEFAULT_SUGGEST_MAX_HEIGHT,
                suggestDirectionUp ?
                    contentBbox.top :
                    freeSpaceBottom
            );

            suggestElem.show();
            input.focus();
        } else if (suggestElem && suggestActive !== -1) {
            const active = suggestElem.querySelector<HTMLElement>('.tanker2-dialog__item_active');

            if (active) {
                scrollIntoView(active, {
                    top: 12,
                    bottom: 12
                }, {
                    local: true
                });
            }
        }
    });
</script>

{#if isShown && target}
    <ContextDialog
        bind:this={dialog}
        direction="down"
        overflow="visible"
        offsetY={-50}
        {target}
        on:close={onClose}
    >
        <div
            bind:this={contentElem}
            class="tanker2-dialog__content"
        >
            <div class="tanker2-dialog__title">
                {$l10nString('tankerDialogTitle')}
            </div>
            <div class="tanker2-dialog__input-box">
                <input
                    class="tanker2-dialog__input"
                    class:tanker2-dialog__input_error={suggestInitiallyLoaded && suggestLoaded && !suggestList.length}
                    autocomplete="off"
                    autocorrect="off"
                    autocapitalize="off"
                    spellcheck="false"
                    placeholder={$l10nString('tankerPlaceholder')}
                    {disabled}
                    bind:this={input}
                    bind:value={value}
                    on:keydown={onKeydown}
                    on:input={onInput}
                    on:focus={onFocus}
                    on:blur={onBlur}
                >

                <div
                    class="tanker2-dialog__loader"
                    class:tanker2-dialog__loader_shown={!suggestLoaded}
                ></div>
            </div>

            {#if suggestInitiallyLoaded && suggestLoaded && !suggestList.length}
                <div class="tanker2-dialog__missing">
                    {$l10nString('tankerMissing')}
                </div>
            {/if}
        </div>

        {#if suggestShow && suggestList.length}
            <dialog
                bind:this={suggestElem}
                class="tanker2-dialog__suggest"
                class:tanker2-dialog__suggest_up={suggestDirectionUp}
                style:max-height="{suggestMaxHeight}px"
                transition:fly={{ y: 10, duration: 150 }}
            >
                {#each suggestList as item, index}
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                    <div
                        class="tanker2-dialog__item"
                        class:tanker2-dialog__item_active={index === suggestActive}
                        on:click={() => selectItem(item.key)}
                        on:mousedown|preventDefault
                    >
                        <div class="tanker2-dialog__key">
                            {#if item.key.endsWith('.')}
                                <div class="tanker2-dialog__folder"></div>
                            {/if}
                            {item.key}
                        </div>
                        {#if item.text}
                            <div class="tanker2-dialog__text">
                                {item.text}
                            </div>
                        {/if}
                    </div>
                {/each}
            </dialog>
        {/if}
    </ContextDialog>
{/if}

<style>
    .tanker2-dialog__content {
        display: flex;
        flex-direction: column;
        gap: 8px;
        padding: 16px 20px 20px;
    }

    .tanker2-dialog__title {
        font-size: 14px;
        font-weight: 500;
        line-height: 20px;
    }

    .tanker2-dialog__input-box {
        position: relative;
    }

    .tanker2-dialog__input {
        box-sizing: border-box;
        width: 100%;
        padding: 10px 40px 10px 16px;
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

    .tanker2-dialog__input:hover {
        border-color: var(--fill-transparent-4);
    }

    .tanker2-dialog__input:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }

    .tanker2-dialog__input_error.tanker2-dialog__input_error {
        border-color: var(--accent-red);
    }

    .tanker2-dialog__loader {
        position: absolute;
        top: 14px;
        right: 14px;
        bottom: 14px;
        box-sizing: border-box;
        width: 12px;
        height: 12px;
        border-radius: 1024px;
        border: 1.5px solid var(--icons-gray);
        border-bottom-color: transparent;
        animation: rotate 1s linear infinite;
        opacity: 0;
        visibility: hidden;
        transition: .3s ease-in-out;
        transition-property: opacity, visibility;
    }

    .tanker2-dialog__loader_shown {
        opacity: 1;
        visibility: visible;
    }

    .tanker2-dialog__suggest {
        top: calc(100% - 10px);
        right: 20px;
        left: 20px;
        width: calc(100% - 40px);
        max-height: 384px;
        padding: 4px 0;
        overflow: auto;
        box-sizing: border-box;
        outline: none;
        border: none;
        border-radius: 6px;
        background: var(--background-tertiary);
        box-shadow: var(--shadow-32);
    }

    .tanker2-dialog__suggest_up {
        top: auto;
        bottom: calc(100% - 10px);
    }

    .tanker2-dialog__item {
        padding: 6px 16px;
        user-select: none;
        cursor: pointer;
        transition: background-color .15s ease-in-out;
    }

    .tanker2-dialog__item_active {
        background-color: var(--fill-transparent-1);
    }

    .tanker2-dialog__item:hover {
        background-color: var(--fill-transparent-1);
    }

    .tanker2-dialog__item:active {
        background-color: var(--fill-transparent-2);
    }

    .tanker2-dialog__key {
        font-size: 14px;
        line-height: 20px;
        color: var(--text-primary);
        word-break: break-word;
    }

    .tanker2-dialog__text {
        font-size: 12px;
        line-height: 20px;
        color: var(--text-secondary);
        word-break: break-word;
    }

    .tanker2-dialog__folder {
        display: inline-block;
        width: 12px;
        height: 10px;
        margin-right: 8px;
        vertical-align: 1px;
        background: no-repeat 50% 50% url(../../../assets/folder.svg);
        filter: var(--icon-filter);
    }

    .tanker2-dialog__missing {
        margin-top: 4px;
        font-size: 12px;
        line-height: 18px;
        color: var(--accent-red);
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
