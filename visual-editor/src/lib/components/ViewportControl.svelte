<script lang="ts" context="module">
    const WINDOW_OFFSET = 24;

    const VIEWPORT_LIST = [
        '320x568',
        '360x640',
        '375x667',
        '768x1024'
    ];
</script>

<script lang="ts">
    import { afterUpdate, createEventDispatcher, getContext } from 'svelte';
    import { fly } from 'svelte/transition';
    import { popupClose, submit } from '../utils/keybinder/shortcuts';
    import { encodeBackground } from '../utils/encodeBackground';
    import { simpleThrottle } from '../utils/simpleThrottle';
    import Text from './controls/Text.svelte';
    import { APP_CTX, type AppContext } from '../ctx/appContext';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import RadioToggler from './controls/RadioToggler.svelte';
    import Button2 from './controls/Button2.svelte';
    import oneIcon from '../../assets/one.svg?url';
    import lightThemeIcon from '../../assets/lightTheme.svg?url';
    import darkThemeIcon from '../../assets/darkTheme.svg?url';
    import Switcher from './controls/Switcher.svelte';
    import CanvasButton from './CanvasButton.svelte';

    export let viewport: string;
    export let scale: number;
    export let disabled = false;

    const { l10nString, lang } = getContext<LanguageContext>(LANGUAGE_CTX);
    const {
        state,
        directionSelector,
        cardLocales
    } = getContext<AppContext>(APP_CTX);
    const {
        safeAreaEmulationEnabled,
        direction,
        locale,
        paletteEnabled,
        previewThemeStore
    } = state;

    const id = 'select' + Math.random();

    const dispatch = createEventDispatcher();

    let toggled = false;
    let popupDirection = 'down';
    let node: HTMLElement;
    let control: HTMLElement;
    let popup: HTMLElement;

    function onWindowKeydown(event: KeyboardEvent): void {
        if (popupClose.isPressed(event)) {
            toggled = false;
            event.preventDefault();
        }
    }

    function onWindowClick(event: MouseEvent): void {
        const path = event.composedPath();
        if (!path.includes(node)) {
            toggled = false;
        }
    }

    function onControlClick(): void {
        if (disabled) {
            return;
        }

        toggled = !toggled;
    }

    function onKeyDown(event: KeyboardEvent): void {
        if (disabled) {
            return;
        }

        if (submit.isPressed(event)) {
            toggled = !toggled;
        } else {
            return;
        }

        event.preventDefault();
    }

    function fitToWindow(): void {
        dispatch('fitToWindow');
    }

    function resetZoom(): void {
        scale = 1;
    }

    function selectViewport(value: string): void {
        viewport = value;
    }

    function selectLocale(localeId: string): void {
        locale.set(localeId);
    }

    const calcDirection = (): void => {
        if (!toggled) {
            return;
        }

        const nodeBottom = node.getBoundingClientRect().bottom;
        const popupHeight = popup.offsetHeight;

        if (nodeBottom + popupHeight + WINDOW_OFFSET > window.innerHeight) {
            popupDirection = 'up';
        } else {
            popupDirection = 'down';
        }
    };

    const calcDirectionThrottled = simpleThrottle(calcDirection, 100);

    afterUpdate(() => {
        calcDirection();
    });
</script>

<svelte:window
    on:keydown={toggled ? onWindowKeydown : undefined}
    on:click={toggled ? onWindowClick : undefined}
    on:resize={toggled ? calcDirectionThrottled : undefined}
/>

<div class="viewport-control">
    <div
        bind:this={node}
        class="viewport-control__select-button"
        class:viewport-control__select-button_disabled={disabled}
    >
        <div
            bind:this={control}
            class="viewport-control__select"
            tabindex="0"
            aria-disabled={disabled}
            aria-expanded={toggled}
            aria-autocomplete="list"
            aria-controls="popup_{id}"
            aria-label={$l10nString('previewSettings')}
            data-custom-tooltip={$l10nString('previewSettings')}
            role="combobox"
            on:click|preventDefault={onControlClick}
            on:keydown={onKeyDown}
        >
            {viewport}
        </div>
        <div class="viewport-control__arrow"></div>

        {#if toggled}
            <div
                bind:this={popup}
                class="viewport-control__popup viewport-control__popup_direction_{popupDirection}"
                id="popup_{id}"
                transition:fly={{
                    duration: 200,
                    y: 10
                }}
            >
                <div class="viewport-control__label">
                    {$l10nString('previewViewport')}
                </div>
                <ul class="viewport-control__list">
                    {#each VIEWPORT_LIST as item}
                        <!-- svelte-ignore a11y_click_events_have_key_events -->
                        <!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
                        <li
                            class="viewport-control__item"
                            class:viewport-control__item_selected={item === viewport}
                            on:click|preventDefault={() => selectViewport(item)}
                        >
                            {item}
                        </li>
                    {/each}
                </ul>
                {#if safeAreaEmulationEnabled}
                    <hr class="viewport-control__hr">

                    <label
                        class="viewport-control__item viewport-control__item_spaced"
                    >
                        {$l10nString('safeArea')}

                        <Switcher
                            bind:checked={$safeAreaEmulationEnabled}
                        />
                    </label>
                {/if}
                <hr class="viewport-control__hr">
                <div class="viewport-control__label">
                    {$l10nString('previewZoom')}
                </div>
                <div class="viewport-control__zoom">
                    <input
                        type="range"
                        class="viewport-control__zoom-input"
                        min="0.33"
                        max="5"
                        step="0.01"
                        bind:value={scale}
                    />

                    <Text
                        bind:value={scale}
                        subtype="percent"
                        size="small"
                        width="small"
                        min={33}
                        max={500}
                    />
                </div>

                <div class="viewport-control__row">
                    <Button2
                        cls="viewport-control__expand-button"
                        theme="border-gray"
                        size="small"
                        centerContent
                        on:click={fitToWindow}
                    >
                        {$l10nString('previewFitToWindow')}
                    </Button2>

                    <Button2
                        theme="border-gray"
                        slim
                        title={$l10nString('previewResetZoom')}
                        customTooltips
                        on:click={resetZoom}
                    >
                        <div
                            class="viewport-control__icon"
                            style:background-image="url({encodeBackground(oneIcon)})"
                        ></div>
                    </Button2>
                </div>

                {#if cardLocales.length > 1}
                    <hr class="viewport-control__hr">

                    <div class="viewport-control__label">
                        {$l10nString('previewLang')}
                    </div>

                    {#each cardLocales as item}
                        <button
                            class="viewport-control__item"
                            class:viewport-control__item_selected={item.id === $locale}
                            on:click|preventDefault={() => selectLocale(item.id)}
                        >
                            {item.text[$lang]}
                        </button>
                    {/each}
                {/if}

                {#if directionSelector}
                    <hr class="viewport-control__hr">

                    <div class="viewport-control__label">
                        {$l10nString('previewDirection')}
                    </div>

                    <div class="viewport-control__row">
                        <RadioToggler
                            mix="viewport-control__toggler"
                            name="direction"
                            theme="tertiary"
                            bind:value={$direction}
                            options={[{
                                text: 'LTR',
                                value: 'ltr'
                            }, {
                                text: 'RTL',
                                value: 'rtl'
                            }]}
                        />
                    </div>
                {/if}
            </div>
        {/if}
    </div>

    {#if $paletteEnabled}
        <div class="viewport-control__divider"></div>

        <CanvasButton
            stickLeft
            title={$l10nString($previewThemeStore === 'light' ? 'lightTheme' : 'darkTheme')}
            on:click={() => $previewThemeStore = ($previewThemeStore === 'light' ? 'dark' : 'light')}
        >
            <div
                class="viewport-control__button-icon viewport-control__button-icon_inversed"
                style:background-image="url({encodeBackground($previewThemeStore === 'light' ? lightThemeIcon : darkThemeIcon)})"
            ></div>
        </CanvasButton>
    {/if}
</div>

<style>
    .viewport-control {
        display: flex;
    }

    .viewport-control__select-button {
        position: relative;
        user-select: none;
    }

    .viewport-control__arrow {
        position: absolute;
        top: 0;
        right: 16px;
        bottom: 0;
        width: 16px;
        height: 16px;
        margin: auto 0;
        background: no-repeat 50% 50% url(../../assets/arrowDown2.svg);
        background-size: contain;
        pointer-events: none;
        filter: var(--icon-filter);
    }

    .viewport-control__select-button_disabled .viewport-control__arrow {
        opacity: .33;
    }

    .viewport-control__select {
        box-sizing: border-box;
        display: flex;
        align-items: center;
        width: 100%;
        min-width: 120px;
        margin: 0;
        padding: 6px 40px 6px 16px;
        border: none;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        background: var(--fill-transparent-minus-1);
        color: inherit;
        appearance: none;
    }

    .viewport-control__select-button:not(:last-child) .viewport-control__select {
        border-top-right-radius: 0;
        border-bottom-right-radius: 0;
    }

    .viewport-control__select_icon {
        padding-left: 6px;
    }

    .viewport-control__select-button:not(.viewport-control__select-button_disabled) .viewport-control__select {
        cursor: pointer;
    }

    .viewport-control__select {
        border-radius: 8px;
        background-color: var(--fill-transparent-2);
        transition: background-color .15s ease-in-out;
    }

    .viewport-control__select:hover {
        background-color: var(--fill-transparent-3);
    }

    .viewport-control__select:active {
        background-color: var(--fill-transparent-4);
    }

    .viewport-control__select:focus-visible {
        outline: 2px solid var(--accent-purple);
    }

    .viewport-control__popup {
        position: absolute;
        z-index: 1;
        top: calc(100% + 4px);
        left: 0;
        box-sizing: border-box;
        min-width: 250px;
        max-height: calc(80vh - 50px);
        padding: 4px 0 12px;
        border-radius: 8px;
        box-shadow: var(--shadow-16);
        background: var(--background-tertiary);
        overflow: auto;
    }

    .viewport-control__popup_direction_up {
        top: auto;
        bottom: calc(100% + 4px);
    }

    .viewport-control__list {
        margin: 0;
        padding: 0;
        list-style: none;
    }

    .viewport-control__item {
        box-sizing: border-box;
        display: flex;
        align-items: center;
        width: 100%;
        min-height: 30px;
        padding: 4px 16px;
        font-size: 14px;
        border: none;
        border-radius: 0;
        background-color: transparent;
        cursor: pointer;
        transition: background-color 0s ease-in-out;
    }

    .viewport-control__item_selected {
        background-color: var(--fill-transparent-1);
        transition-duration: .15s;
    }

    .viewport-control__item_empty {
        color: var(--fill-transparent-4);
    }

    .viewport-control__item:hover {
        background-color: var(--fill-transparent-2);
    }

    .viewport-control__item:active {
        background-color: var(--fill-transparent-3);
    }

    .viewport-control__item_spaced {
        justify-content: space-between;
    }

    .viewport-control__label {
        padding: 6px 16px 0;
        font-size: 12px;
        line-height: 20px;
        color: var(--text-secondary);
    }

    .viewport-control__hr + .viewport-control__label {
        padding-top: 0;
    }

    .viewport-control__zoom {
        display: flex;
        gap: 8px;
        margin-bottom: 8px;
        padding: 0 16px;
    }

    .viewport-control__zoom-input {
        flex: 1 1 auto;
        min-width: 50px;
        width: 50px;
        margin: 0;
        accent-color: var(--accent-purple);
    }

    .viewport-control__row {
        display: flex;
        gap: 8px;
        padding: 0 16px;
    }

    :global(.viewport-control__toggler) {
        flex: 1 1 auto;
    }

    :global(.viewport-control__expand-button) {
        flex: 1 1 auto;
    }

    .viewport-control__icon {
        flex: 0 0 auto;
        width: 20px;
        height: 28px;
        background: no-repeat 50% 50%;
        background-size: 20px;
        filter: var(--icon-filter);
    }

    .viewport-control__hr {
        height: 1px;
        margin: 8px 0;
        border: none;
        background: var(--fill-transparent-1);
    }

    .viewport-control__divider {
        flex: 0 0 auto;
        width: 1px;
        background: var(--fill-transparent-minus-1);
    }

    .viewport-control__button-icon {
        flex: 0 0 auto;
        width: 32px;
        height: 32px;
        background: no-repeat 50% 50%;
        background-size: 20px;
    }

    .viewport-control__button-icon_inversed {
        filter: var(--icon-filter);
    }
</style>
