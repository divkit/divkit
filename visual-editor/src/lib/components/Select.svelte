<script lang="ts" context="module">
    const WINDOW_OFFSET = 24;
</script>

<script lang="ts">
    import { afterUpdate, createEventDispatcher, tick } from 'svelte';
    import { fly } from 'svelte/transition';
    import { popupClose, submit, suggestDown, suggestFirst, suggestLast, suggestUp } from '../utils/keybinder/shortcuts';
    import { encodeBackground } from '../utils/encodeBackground';
    import { simpleThrottle } from '../utils/simpleThrottle';

    interface Item {
        value: string;
        text: string;
        icon?: string;
    }

    export let items: Item[];
    export let value: string;
    export let theme: 'normal' | 'canvas' | 'transparent';
    export let size: 'small' | 'medium' = 'small';
    export let iconTheme: 'normal' | 'transparent' = 'normal';
    export let disabled = false;
    export let mix = '';
    export let title = '';
    export let required = false;

    const id = 'select' + Math.random();

    $: text = items.find(item => item.value === value || !item.value && !value)?.text || value || '';
    $: icon = items.find(item => item.value === value || !item.value && !value)?.icon;
    $: requiredError = required && Boolean(!items.find(item => item.value === value)?.value);

    const dispatch = createEventDispatcher();

    let toggled = false;
    let direction = 'down';
    let node: HTMLElement;
    let control: HTMLElement;
    let popup: HTMLElement;

    function move(by: number): void {
        let index = items.findIndex(item => item.value === value || !item.value && !value);
        if (index === -1) {
            return;
        }

        index += by;

        if (index >= items.length) {
            index = 0;
        }
        if (index < 0) {
            index = items.length - 1;
        }

        selectValue(items[index].value);
    }

    function onKeyDown(event: KeyboardEvent): void {
        if (disabled) {
            return;
        }

        if (suggestUp.isPressed(event)) {
            move(-1);
        } else if (suggestDown.isPressed(event)) {
            move(1);
        } else if (suggestFirst.isPressed(event)) {
            selectValue(items[0].value);
        } else if (suggestLast.isPressed(event)) {
            selectValue(items[items.length - 1].value);
        } else if (submit.isPressed(event)) {
            toggled = !toggled;
        } else {
            return;
        }

        event.preventDefault();
    }

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

    function selectValue(val: string): void {
        value = val;
        Promise.resolve().then(() => dispatch('change', val));
    }

    function select(val: string): void {
        if (val !== value) {
            selectValue(val);
        }

        toggled = false;

        tick().then(() => {
            control?.focus();
        });
    }

    function onControlClick(): void {
        if (disabled) {
            return;
        }

        toggled = !toggled;
    }

    const calcDirection = (): void => {
        if (!toggled) {
            return;
        }

        const nodeBottom = node.getBoundingClientRect().bottom;
        const popupHeight = popup.offsetHeight;

        if (nodeBottom + popupHeight + WINDOW_OFFSET > window.innerHeight) {
            direction = 'up';
        } else {
            direction = 'down';
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

<div
    bind:this={node}
    class="select select_theme_{theme} select_size_{size} select_icon-theme_{iconTheme} {mix}"
    class:select_disabled={disabled}
    class:select_error={requiredError}
>
    <div
        bind:this={control}
        class="select__select"
        class:select__select_icon={icon}
        tabindex="0"
        aria-disabled={disabled}
        aria-expanded={toggled}
        aria-autocomplete="list"
        aria-controls="popup_{id}"
        aria-label={title}
        data-custom-tooltip={toggled ? undefined : title}
        role="combobox"
        on:click|preventDefault={onControlClick}
        on:keydown={onKeyDown}
    >
        {#if icon}
            <div class="select__icon-wrapper">
                <div
                    class="select__icon"
                    style:background-image="url({encodeBackground(icon)})"
                />
            </div>
        {/if}
        {text}
    </div>
    <div class="select__arrow"></div>

    {#if toggled}
        <div
            bind:this={popup}
            class="select__popup select__popup_direction_{direction}"
            id="popup_{id}"
            transition:fly={{
                duration: 200,
                y: 10
            }}
        >
            <ul class="select__list">
                {#each items as item}
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <li
                        class="select__item"
                        class:select__item_selected={item.value === value}
                        class:select__item_icon={item.icon}
                        on:click|preventDefault={() => select(item.value)}
                    >
                        {#if item.icon}
                            <div class="select__icon-wrapper">
                                <div
                                    class="select__icon"
                                    style:background-image="url({encodeBackground(item.icon)})"
                                />
                            </div>
                        {/if}
                        {item.text ?? item.value}
                    </li>
                {/each}
            </ul>
        </div>
    {/if}
</div>

<style>
    .select {
        position: relative;
        user-select: none;
    }

    .select__arrow {
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

    .select_disabled .select__arrow {
        opacity: .33;
    }

    .select__select {
        box-sizing: border-box;
        display: flex;
        align-items: center;
        width: 100%;
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

    .select__select_icon {
        padding-left: 6px;
    }

    .select:not(.select_disabled) .select__select {
        cursor: pointer;
    }

    .select_theme_normal .select__select {
        padding: 5px 39px 5px 15px;
        border-radius: 6px;
        border: 1px solid var(--fill-transparent-3);
        transition: border-color .15s ease-in-out;
    }

    .select_theme_normal.select_error .select__select {
        border-color: var(--accent-red);
    }

    .select_theme_normal .select__select_icon {
        padding-left: 5px;
    }

    .select_theme_normal.select_icon-theme_transparent .select__select_icon {
        padding-left: 12px;
    }

    .select_theme_normal.select_size_medium .select__select {
        min-height: 40px;
    }

    .select_disabled.select_theme_normal .select__select {
        border: none;
        background: var(--fill-transparent-1);
    }

    .select_theme_canvas .select__select {
        border-radius: 8px;
        background-color: var(--fill-transparent-2);
        transition: background-color .15s ease-in-out;
    }

    .select_theme_normal:not(.select_disabled) .select__select:hover {
        border-color: var(--fill-transparent-4);
    }

    .select_theme_canvas .select__select:hover {
        background-color: var(--fill-transparent-3);
    }

    .select_theme_canvas .select__select:active {
        background-color: var(--fill-transparent-4);
    }

    .select_theme_transparent .select__select {
        padding: 4px 6px;
        border-radius: 8px;
        transition: background-color .15s ease-in-out;
    }

    .select_theme_transparent:not(.select_disabled) .select__select:hover {
        background: var(--fill-transparent-1);
    }

    .select_theme_transparent .select__select:focus-visible {
        outline: 1px solid var(--accent-purple);
    }

    .select_theme_transparent .select__arrow {
        display: none;
    }

    .select_theme_normal .select__select:focus-visible,
    .select_theme_normal .select__select:focus-visible:hover {
        outline: none;
        border-color: var(--accent-purple);
    }

    .select_theme_canvas .select__select:focus-visible {
        outline: 2px solid var(--accent-purple);
    }

    .select__popup {
        position: absolute;
        z-index: 1;
        top: calc(100% + 4px);
        left: 0;
        box-sizing: border-box;
        min-width: 100%;
        max-height: calc(50vh - 50px);
        padding: 4px 0;
        border-radius: 8px;
        box-shadow: var(--shadow-16);
        background: var(--background-tertiary);
        overflow: auto;
    }

    .select__popup_direction_up {
        top: auto;
        bottom: calc(100% + 4px);
    }

    .select__list {
        margin: 0;
        padding: 0;
        list-style: none;
    }

    .select__item {
        box-sizing: border-box;
        display: flex;
        align-items: center;
        min-height: 40px;
        padding: 6px 16px;
        font-size: 14px;
        cursor: pointer;
        transition: background-color 0s ease-in-out;
    }

    .select__item_icon {
        padding-left: 6px;
    }

    .select__item_selected {
        background-color: var(--fill-transparent-1);
        transition-duration: .15s;
    }

    .select__item:hover {
        background-color: var(--fill-transparent-2);
    }

    .select__item:active {
        background-color: var(--fill-transparent-3);
    }

    .select_icon-theme_transparent  .select__item_icon {
        padding-left: 12px;
    }

    .select__icon-wrapper {
        flex: 0 0 auto;
        width: 28px;
        height: 28px;
        margin-right: 8px;
        border-radius: 6px;
        background: var(--fill-accent-2);
        overflow: hidden;
    }

    .select_icon-theme_transparent .select__icon-wrapper {
        width: 20px;
        height: 20px;
        background: none;
    }

    .select__icon {
        width: 100%;
        height: 100%;
        background: 50% 50% no-repeat;
        background-size: contain;
        filter: var(--icon-filter);
    }
</style>
