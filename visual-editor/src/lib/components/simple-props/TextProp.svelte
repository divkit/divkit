<script lang="ts">
    import { slide } from 'svelte/transition';
    import { afterUpdate, createEventDispatcher, getContext } from 'svelte';
    import type { NumberProperty, StringProperty } from '../../data/componentProps';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { tankerKeyToVariableName } from '../../utils/tanker';
    import { calcSelectionOffset, setSelectionOffset } from '../../utils/contenteditable';
    import { parseConstraint } from '../../utils/parseConstraint';

    export let id: string = '';
    export let value: string;
    export let item: StringProperty | NumberProperty;
    export let flags: {
        subtype?: string;
        constraint?: string;
    } = {};
    export let tankerToggled = false;

    const { getTranslationKey, getSelection, state, tanker2Dialog } = getContext<AppContext>(APP_CTX);
    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { tanker, locale, readOnly } = state;

    const dispatch = createEventDispatcher();

    let elem: HTMLElement;
    let tankerKeyLoading = false;
    let tankerKeyFound = false;
    let tankerKey = '';

    $: if (value && tankerToggled) {
        tankerKey = state.getTankerKey(value);
        tankerKeyFound = tankerKey in $tanker;
    } else {
        tankerKey = '';
    }

    $: number = flags.subtype === 'number' || flags.subtype === 'integer';

    $: type = number ? 'number' : 'text';

    $: limits = parseConstraint(flags.subtype, flags.constraint);
    $: min = limits.min;
    $: max = limits.max;

    // eslint-disable-next-line no-nested-ternary
    $: step = flags.subtype === 'integer' ? 1 : (flags.subtype === 'number' ? .01 : null);

    $: pattern = flags.subtype === 'integer' ? '\\d+' : null;

    $: isTankerEditable = tankerToggled && !($readOnly || item.enabled === false);

    function onChange() {
        if (elem.hasAttribute('contenteditable')) {
            value = elem.innerText;
        }

        if (item.default === '\0') {
            value = value.replace(/\0/g, '');
            value = value || '\0';
        }

        dispatch('change', {
            value,
            item
        });
    }

    function onPaste(event: ClipboardEvent): void {
        event.preventDefault();
        if (event.clipboardData) {
            let text = event.clipboardData.getData('text/plain');
            text = text.trim();
            document.execCommand('inserttext', false, text);
        }
    }

    function onLockedClick(): void {
        tanker2Dialog().show({
            target: elem,
            value: tankerKey,
            callback(key) {
                if (!key || !getTranslationKey) {
                    value = '';
                    return;
                }

                const setValue = () => {
                    value = `@{${tankerKeyToVariableName(key)}}`;
                    state.storeTankerKey(key);
                    onChange();
                    tankerKey = key;
                    tankerKeyLoading = false;
                };

                tankerKeyLoading = true;
                getTranslationKey(key).then(res => {
                    setValue();

                    if (res) {
                        $tanker = {
                            ...$tanker,
                            [key]: res
                        };

                        tankerKeyFound = true;
                    } else {
                        tankerKeyFound = false;
                    }
                }).catch(_err => {
                    setValue();
                    tankerKeyFound = false;
                });
            }
        });
    }

    afterUpdate(() => {
        if (elem?.hasAttribute('contenteditable') && elem.innerText !== value) {
            let selection;
            let prevStart;
            let prevEnd;

            if (document.activeElement === elem) {
                selection = getSelection();
                prevStart = calcSelectionOffset(selection, elem, 'start', false);
                prevEnd = calcSelectionOffset(selection, elem, 'end', false);
            }

            elem.innerText = value;

            if (document.activeElement === elem && selection && prevStart !== undefined && prevEnd !== undefined) {
                selection.removeAllRanges();
                const range = document.createRange();
                setSelectionOffset(selection, elem, range, 'start', prevStart, false);
                setSelectionOffset(selection, elem, range, 'end', prevEnd, false);
                selection.addRange(range);
            }
        }
    });
</script>

{#if type === 'text'}
    {#if $readOnly || item.enabled === false || tankerToggled}
        <!-- svelte-ignore a11y-click-events-have-key-events -->
        <!-- svelte-ignore a11y-no-static-element-interactions -->
        <div
            class="text-prop text-prop_disabled"
            class:text-prop_disabled-clickable={isTankerEditable}
            {id}
            bind:this={elem}
            on:paste={onPaste}
            on:click={isTankerEditable ? onLockedClick : null}
        >
            {#if tankerToggled}
                {$tanker[tankerKey]?.[$locale] || ''}
            {:else}
                {value}
            {/if}

            {#if isTankerEditable}
                <div
                    class="text-prop__loader"
                    class:text-prop__loader_shown={tankerKeyLoading}
                ></div>
            {/if}
        </div>
    {:else}
        <div
            {...{ autocomplete: 'off' }}
            class="text-prop"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            contenteditable="true"
            {id}
            bind:this={elem}
            on:input={onChange}
            on:paste={onPaste}
        >
        </div>
    {/if}
{:else}
    <input
        class="text-prop"
        class:text-prop_disabled={$readOnly}
        type="number"
        {min}
        {max}
        {step}
        {pattern}
        autocomplete="off"
        autocorrect="off"
        autocapitalize="off"
        spellcheck="false"
        {id}
        disabled={$readOnly}
        bind:this={elem}
        bind:value={value}
        on:input={onChange}
    />
{/if}

{#if tankerToggled}
    <div transition:slide|local>
        {#if value && tankerKey}
            <div class="text-prop__tanker">
                <div
                    class="text-prop__tanker-label"
                    class:text-prop__tanker-label_error={!tankerKeyFound}
                >
                    {$l10n(tankerKeyFound ? 'tankerKey' : 'tankerMissing')}
                </div>

                <button
                    class="text-prop__tanker-key"
                    class:text-prop__tanker-key_disabled={!isTankerEditable}
                    on:click={isTankerEditable ? onLockedClick : undefined}
                >
                    {tankerKey}
                </button>
            </div>
        {:else}
            {#if isTankerEditable}
                <div class="text-prop__tanker">
                    <button
                        class="text-prop__tanker-key"
                        on:click={onLockedClick}
                    >
                        {$l10n('tankerEnterValue')}
                    </button>
                </div>
            {/if}
        {/if}
    </div>
{/if}

<style>
    .text-prop {
        position: relative;
        box-sizing: border-box;
        width: 100%;
        margin: 0;
        padding: 8px 14px;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        color: inherit;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background: var(--fill-transparent-minus-1);
        appearance: none;
        cursor: text;
        white-space: pre-wrap;
        transition: .15s ease-in-out;
        transition-property: border-color, background-color;
    }

    .text-prop_disabled {
        border-color: transparent;
        background: var(--fill-transparent-1);
        cursor: default;
    }

    .text-prop_disabled-clickable {
        padding-right: 40px;
        cursor: pointer;
    }

    .text-prop_disabled-clickable:hover {
        background: var(--fill-accent-2);
    }

    .text-prop:invalid {
        background: indianred;
    }

    .text-prop:not(.text-prop_disabled):hover {
        border-color: var(--fill-transparent-4);
    }

    .text-prop.text-prop:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }

    .text-prop__tanker {
        display: flex;
        margin-top: 4px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
    }

    .text-prop__tanker-label {
        margin-right: 6px;
    }

    .text-prop__tanker-label_error {
        color: var(--accent-red);
    }

    .text-prop__tanker-key {
        margin: 0;
        padding: 0;
        font-family: inherit;
        font-size: 14px;
        line-height: 20px;
        border: none;
        border-radius: 4px;
        color: var(--accent-purple);
        background: none;
        cursor: pointer;
        appearance: none;
    }

    .text-prop__tanker-key:not(.text-prop__tanker-key_disabled):hover {
        text-decoration: underline;
    }

    .text-prop__tanker-key_disabled {
        cursor: default;
    }

    .text-prop__tanker-key:focus-visible {
        outline: 1px solid var(--accent-purple);
    }

    .text-prop__loader {
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

    .text-prop__loader_shown {
        opacity: 1;
        visibility: visible;
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
