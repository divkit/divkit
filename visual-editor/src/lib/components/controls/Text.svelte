<script lang="ts" context="module">
    const FILE_TYPE_TO_LIMITS = {
        '': 'image',
        image: 'image',
        gif: 'image',
        lottie: 'lottie',
        video: 'video',
        image_preview: 'preview'
    } as const;
</script>

<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import { formatFileSize } from '../../utils/formatFileSize';
    import { calcFileSizeMod, getFileSize } from '../../utils/fileSize';
    import type { VideoSource } from '../../utils/video';
    import type { StringValueFilter } from '../../../lib';
    import { checkStringValue } from '../../utils/checkValue';

    export let id: string = '';
    export let value: string | number | undefined;
    export let subtype: '' | 'integer' | 'number' | 'percent' | 'angle' | 'file' | 'dict' | 'array' = '';
    export let fileType: '' | 'image' | 'gif' | 'lottie' | 'video' | 'image_preview' = '';
    export let min: number | undefined = undefined;
    export let max: number | undefined = undefined;
    export let required = false;
    export let anyStep = false;
    export let mix = '';
    export let placeholder = '';
    export let inlineLabel = '';
    export let hasButton = false;
    export let disabled = false;
    export let defaultValue: string | number | undefined = undefined;
    export let size: 'medium' | 'small' = 'medium';
    export let width: 'auto' | 'small' = 'auto';
    export let pattern: string | null = null;
    export let filter: StringValueFilter | undefined = undefined;
    export let error = '';
    export let title = '';
    export let generateFromVideo: VideoSource[] | undefined = undefined;
    export let generateFromLottie: string | undefined = undefined;

    const dispatch = createEventDispatcher();
    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const {
        file2Dialog,
        warnFileLimit,
        errorFileLimit,
        previewWarnFileLimit,
        previewErrorFileLimit,
        fileLimits
    } = getContext<AppContext>(APP_CTX);

    let internalValue: string | number = 0;
    let elem: HTMLElement;
    let subtypeError = false;
    let requiredError = false;
    let filterError = false;
    let isFocused = false;
    let currentSize: number | undefined = undefined;
    let sizeLabelWidth = 0;
    let customButtonsWidth = 0;

    function validateValue(value: string | number | undefined): void {
        requiredError = !value && required;

        filterError = !checkStringValue(value, filter);
    }

    function updateInternalValue(
        subtype: string,
        value: string | number | undefined,
        defaultValue: string | number | undefined
    ): void {
        if (isFocused) {
            return;
        }

        if (subtype === 'percent') {
            internalValue = Math.round(Number(value || defaultValue || 0) * 100);
        } else if (subtype === 'integer' || subtype === 'number' || subtype === 'angle') {
            internalValue = Number(value || defaultValue || 0);
        } else if (subtype === 'dict' || subtype === 'array') {
            internalValue = typeof value === 'string' ? value : JSON.stringify(value);
        } else {
            internalValue = value || defaultValue || '';
        }

        loadFileSize();

        validateValue(internalValue);
    }

    $: updateInternalValue(subtype, value, defaultValue);

    $: type = (subtype === 'angle' || subtype === 'integer' || subtype === 'number' || subtype === 'percent') ?
        'number' :
        'text';

    // eslint-disable-next-line no-nested-ternary
    $: step = anyStep ?
        'any' :
        (subtype === 'integer' || subtype === 'percent' || subtype === 'angle') ? 1 : .01;

    $: resultPattern = (subtype === 'integer' || subtype === 'percent' || subtype === 'angle') ?
        '\\d+' :
        pattern;

    $: fileSizeMod = fileType === 'image_preview' ?
        calcFileSizeMod(currentSize, FILE_TYPE_TO_LIMITS[fileType], previewWarnFileLimit, previewErrorFileLimit, fileLimits) :
        calcFileSizeMod(currentSize, FILE_TYPE_TO_LIMITS[fileType], warnFileLimit, errorFileLimit, fileLimits);

    $: totalInlineButtonsWidth = ((currentSize && currentSize > 0 && sizeLabelWidth > 0) ? sizeLabelWidth : 0) +
        customButtonsWidth;

    function onChange() {
        if (type === 'number') {
            if (internalValue !== null) {
                let val = Number(internalValue);
                if (subtype === 'percent') {
                    val /= 100;
                }
                value = val;
            }
        } else if (subtype === 'dict' || subtype === 'array') {
            const str = String(internalValue);

            if (subtype === 'dict' && !str.startsWith('{')) {
                subtypeError = true;
            } else if (subtype === 'array' && !str.startsWith('[')) {
                subtypeError = true;
            } else {
                try {
                    value = JSON.parse(String(internalValue));
                    subtypeError = false;
                } catch (err) {
                    subtypeError = true;
                }
            }
        } else {
            value = internalValue;
        }

        loadFileSize();

        validateValue(value);

        dispatch('change', {
            value
        });
    }

    function onFocus(): void {
        isFocused = true;
    }

    function onBlur(): void {
        isFocused = false;
    }

    function onMore(): void {
        if (!fileType) {
            return;
        }

        file2Dialog().show({
            title: $l10nString(fileType === 'video' ? 'props.video_sources' : 'props.image_url'),
            target: elem,
            subtype: fileType,
            value: {
                url: value ? String(value) : ''
            },
            disabled,
            generateFromVideo,
            generateFromLottie,
            callback(val) {
                value = val.url;

                dispatch('change', {
                    value
                });
            }
        });
    }

    function loadFileSize(): void {
        if (!fileType || !value) {
            currentSize = undefined;
            return;
        }

        getFileSize(String(value), fileType).then(res => {
            currentSize = res;
        });
    }
</script>

<div
    bind:this={elem}
    class="text {mix} text_size_{size} text_width_{width}"
    class:text_more={subtype === 'file'}
    class:text_inline-label={Boolean(inlineLabel)}
    class:text_button={hasButton}
    class:text_disabled={disabled}
    class:text_error={Boolean(error || subtypeError || requiredError || filterError)}
    title={error}
    aria-label={title}
    data-custom-tooltip={title}
    style:--inline-size-width="{totalInlineButtonsWidth}px"
>
    {#if type === 'text'}
        <input
            type="text"
            pattern={resultPattern}
            class="text__input"
            class:text__input_percent={subtype === 'percent'}
            class:text__input_angle={subtype === 'angle'}
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            {id}
            {placeholder}
            {disabled}
            bind:value={internalValue}
            on:input={onChange}
            on:focus={onFocus}
            on:blur={onBlur}
            on:focus
            on:blur
        >
    {:else}
        <input
            type="number"
            {min}
            {max}
            {step}
            {required}
            pattern={resultPattern}
            class="text__input"
            class:text__input_percent={subtype === 'percent'}
            class:text__input_angle={subtype === 'angle'}
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            {id}
            {placeholder}
            {disabled}
            bind:value={internalValue}
            on:input={onChange}
            on:focus={onFocus}
            on:blur={onBlur}
            on:focus
            on:blur
        >
    {/if}

    {#if subtype === 'percent' || subtype === 'angle'}
        <div class="text__symbol" aria-hidden="true">
            {#if subtype === 'percent'}
                %
            {:else if subtype === 'angle'}
                °
            {/if}
        </div>
    {/if}

    {#if subtype === 'file'}
        {#if currentSize && currentSize > 0}
            <div
                class="text__size-label"
                class:text__size-label_error={fileSizeMod === 'error'}
                class:text__size-label_warn={fileSizeMod === 'warn'}
                data-custom-tooltip={fileSizeMod ? $l10nString('file.too_big') : undefined}
                bind:offsetWidth={sizeLabelWidth}
            >
                {formatFileSize(currentSize)}
            </div>
        {/if}

        <button class="text__more" on:click={onMore}>
            <div class="text__more-icon"></div>
        </button>
    {/if}

    {#if inlineLabel}
        <div class="text__inline-label">{inlineLabel}</div>
    {/if}

    <div
        class="text__custom-buttons"
        bind:offsetWidth={customButtonsWidth}
    >
        <slot name="button" />
    </div>
</div>

<style>
    .text {
        position: relative;
    }

    .text__input {
        box-sizing: border-box;
        width: 100%;
        min-width: 0;
        margin: 0;
        padding: 9px calc(14px + var(--inline-size-width, 0)) 9px 14px;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        color: inherit;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background: var(--fill-transparent-minus-1);
        appearance: none;
        /* -moz after non-prefix on purpose */
        -moz-appearance: textfield;
        transition: border-color .15s ease-in-out;
    }

    .text_size_small .text__input {
        padding-top: 5px;
        padding-bottom: 5px;
    }

    .text_disabled .text__input {
        border: none;
        background: var(--fill-transparent-1);
    }

    .text__input::-webkit-inner-spin-button,
    .text__input::-webkit-outer-spin-button {
        display: none;
    }

    .text__input:hover {
        border-color: var(--fill-transparent-4);
    }

    .text__input:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }

    .text__input:focus-visible:hover {
        border-color: var(--accent-purple-hover);
    }

    .text__input:invalid,
    .text_error .text__input {
        border-color: var(--accent-red);
    }

    .text__input:invalid:hover,
    .text_error .text__input:hover {
        border-color: var(--accent-red-hover);
    }

    .text_more .text__input {
        padding-right: calc(51px + var(--inline-size-width, 0));
    }

    .text_inline-label .text__input {
        padding-left: 42px;
    }

    .text_button .text__input {
        padding-right: 40px;
    }

    .text_width_small .text__input {
        width: 65px;
    }

    .text__symbol {
        position: absolute;
        top: 0;
        right: 14px;
        bottom: 0;
        display: flex;
        align-items: center;
        pointer-events: none;
    }

    .text__more {
        position: absolute;
        top: 0;
        right: 15px;
        bottom: 0;
        width: 24px;
        height: 24px;
        margin: auto 0;
        padding: 0;
        border: none;
        border-radius: 4px;
        background: var(--fill-transparent-1);
        appearance: none;
        transition: background-color .15s ease-in-out;
        cursor: pointer;
    }

    .text__more:hover {
        background: var(--fill-transparent-3);
    }

    .text__more:focus-visible {
        outline: 1px solid var(--accent-purple);
    }

    .text__more:focus-visible:hover {
        outline: 1px solid var(--accent-purple-hover);
    }

    .text__more-icon {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50% url(../../../assets/more.svg);
        background-size: 20px;
        filter: var(--icon-filter);
    }

    .text__inline-label {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 16px;
        display: flex;
        align-items: center;
        color: var(--text-tertiary);
        pointer-events: none;
    }

    .text__size-label {
        position: absolute;
        top: 0;
        bottom: 0;
        right: 48px;
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

    .text__size-label_error {
        background: var(--fill-red-2);
    }

    .text__size-label_warn {
        background: var(--fill-orange-2);
    }

    .text__custom-buttons {
        position: absolute;
        top: 0;
        right: 4px;
        bottom: 0;
        height: 32px;
        margin: auto 0;
    }
</style>
