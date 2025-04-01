<script lang="ts" context="module">
    const HORIZONTAL = ['left', 'center', 'right'];
    const HORIZONTAL_LOGICAL_LTR = ['start', 'center', 'end'];
    const HORIZONTAL_LOGICAL_RTL = ['end', 'center', 'start'];
    const VERTICAL = ['top', 'center', 'bottom'];

    const HORIZONTAL_ALIGN_NORMALIZE: Record<'ltr' | 'rtl', Record<string, string>> = {
        ltr: {
            left: 'start',
            right: 'end'
        },
        rtl: {
            left: 'end',
            right: 'start'
        }
    };

    const TEXT_MAP: Record<string, string> = {
        left: 'props.align_left',
        center: 'props.align_center',
        right: 'props.align_right',
        top: 'props.align_top',
        bottom: 'props.align_bottom',
        start: 'props.align_start',
        end: 'props.align_end',
        '': 'default'
    };
</script>

<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { alignDown, alignLeft, alignRight, alignUp, clear } from '../../utils/keybinder/shortcuts';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import Select from '../Select.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let horizontal: string | undefined;
    export let vertical: string | undefined;
    export let width: {
        type?: string;
    } | undefined = undefined;
    export let height: {
        type?: string;
    } | undefined = undefined;
    export let orientation: 'horizontal' | 'vertical' | 'overlap' | undefined = undefined;
    export let isContent = false;
    export let isSelfAlign = false;
    export let parentType: string | undefined = undefined;
    export let parentOrientation: string | undefined = undefined;
    export let disabled = false;

    const { state, directionSelector } = getContext<AppContext>(APP_CTX);
    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);

    const { direction } = state;

    const dispatch = createEventDispatcher();

    $: isHorizontalDisabled = isSelfAlign && (
        !width ||
        width.type === 'match_parent' ||
        (parentType === 'container' && parentOrientation === 'horizontal') ||
        (parentType === 'gallery' && (!parentOrientation || parentOrientation === 'horizontal'))
    );
    $: isVerticalDisabled = isSelfAlign && (
        height?.type === 'match_parent' ||
        (parentType === 'container' && (!parentOrientation || parentOrientation === 'vertical')) ||
        (parentType === 'gallery' && parentOrientation === 'vertical')
    );

    // eslint-disable-next-line no-nested-ternary
    $: horizontalList = isHorizontalDisabled ?
        [directionSelector ? 'start' : 'left'] :
        // eslint-disable-next-line no-nested-ternary
        (directionSelector ? ($direction === 'ltr' ? HORIZONTAL_LOGICAL_LTR : HORIZONTAL_LOGICAL_RTL) : HORIZONTAL);
    $: verticalList = isVerticalDisabled ? ['top'] : VERTICAL;

    $: horizontalSelectValue = horizontal || '';
    $: verticalSelectValue = vertical || '';

    let isHovered = false;
    let horizontalAlignHovered = '';
    let verticalAlignHovered = '';

    function selectAlignment(newHorizontal: string | undefined, newVertical: string | undefined): void {
        if (disabled) {
            return;
        }

        if (verticalList.length < 2) {
            newVertical = undefined;
        }
        if (horizontalList.length < 2) {
            newHorizontal = undefined;
        }

        horizontal = newHorizontal;
        vertical = newVertical;

        dispatch('change', {
            horizontal,
            vertical
        });
    }

    function moveValue(dx: number, dy: number): void {
        let newHorizontal;
        let horizontalIndex = horizontalList.indexOf(horizontal || '');
        if (horizontalIndex === -1) {
            newHorizontal = horizontalList[0];
        } else {
            horizontalIndex = Math.max(0, Math.min(2, horizontalIndex + dx));
            newHorizontal = horizontalList[horizontalIndex];
        }

        let newVertical;
        let verticalIndex = verticalList.indexOf(vertical || '');
        if (verticalIndex === -1) {
            newVertical = verticalList[0];
        } else {
            verticalIndex = Math.max(0, Math.min(2, verticalIndex + dy));
            newVertical = verticalList[verticalIndex];
        }

        selectAlignment(newHorizontal, newVertical);
    }

    function onKeydown(event: KeyboardEvent): void {
        if (disabled) {
            return;
        }

        if (alignUp.isPressed(event)) {
            moveValue(0, -1);
        } else if (alignDown.isPressed(event)) {
            moveValue(0, 1);
        } else if (alignLeft.isPressed(event)) {
            moveValue(-1, 0);
        } else if (alignRight.isPressed(event)) {
            moveValue(1, 0);
        } else if (clear.isPressed(event)) {
            selectAlignment('', '');
        } else {
            return;
        }

        event.preventDefault();
        event.stopPropagation();
    }

    function onMouseLeave(): void {
        isHovered = false;
    }
</script>

<div
    class="alignment alignment_direction_{$direction}"
    class:alignment_disabled={disabled}
    class:alignment_match_horizontal={isHorizontalDisabled}
    class:alignment_match_vertical={isVerticalDisabled}
>
    <!-- svelte-ignore a11y-no-noninteractive-tabindex -->
    <!-- svelte-ignore a11y-no-static-element-interactions -->
    <div
        class="alignment__item alignment__grid"
        class:alignment__grid_disabled={disabled || isHorizontalDisabled && isVerticalDisabled}
        tabindex={(disabled || isHorizontalDisabled && isVerticalDisabled) ? undefined : 0}
        on:keydown={onKeydown}
        on:mouseleave={onMouseLeave}
    >
        {#each verticalList as verticalItem}
            {#each horizontalList as horizontalItem}
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <div
                    class="alignment__val alignment__val_horizontal_{horizontalItem} alignment__val_vertical_{verticalItem}"
                    class:alignment__val_selected={
                        (
                            horizontal === horizontalItem ||
                            horizontal && horizontalItem === HORIZONTAL_ALIGN_NORMALIZE[$direction][horizontal]
                        ) &&
                        vertical === verticalItem &&
                        !(isHorizontalDisabled && isVerticalDisabled)
                    }
                    on:click|preventDefault={() => selectAlignment(horizontalItem, verticalItem)}
                    on:mouseenter={() => {
                        if (!disabled) {
                            isHovered = true;
                            horizontalAlignHovered = horizontalItem;
                            verticalAlignHovered = verticalItem;
                        }
                    }}
                ></div>
            {/each}
        {/each}
    </div>
    <div class="alignment__item alignment__text">
        <div class="alignment__select-group">
            <div class="alignment__select-wrapper">
                <div>
                    X:
                </div>
                {#if isHorizontalDisabled}
                    <div class="alignment__select-fill">
                        {$l10n('props.size_match_parent')}
                    </div>
                {:else}
                    <Select
                        items={[{
                            text: $l10n(TEXT_MAP.left),
                            value: 'left'
                        }, {
                            text: $l10n(TEXT_MAP.start),
                            value: 'start'
                        }, {
                            text: $l10n(TEXT_MAP.center),
                            value: 'center'
                        }, {
                            text: $l10n(TEXT_MAP.right),
                            value: 'right'
                        }, {
                            text: $l10n(TEXT_MAP.end),
                            value: 'end'
                        }, {
                            text: $l10n(TEXT_MAP['']),
                            value: ''
                        }]}
                        bind:value={horizontalSelectValue}
                        theme="transparent"
                        {disabled}
                        on:change={() => selectAlignment(horizontalSelectValue, vertical)}
                    />
                {/if}
            </div>
            <div class="alignment__select-wrapper">
                <div>
                    Y:
                </div>
                {#if isVerticalDisabled}
                <div class="alignment__select-fill">
                        {$l10n('props.size_match_parent')}
                    </div>
                {:else}
                    <Select
                        items={[{
                            text: $l10n(TEXT_MAP.top),
                            value: 'top'
                        }, {
                            text: $l10n(TEXT_MAP.center),
                            value: 'center'
                        }, {
                            text: $l10n(TEXT_MAP.bottom),
                            value: 'bottom'
                        }, {
                            text: $l10n(TEXT_MAP['']),
                            value: ''
                        }]}
                        bind:value={verticalSelectValue}
                        theme="transparent"
                        {disabled}
                        on:change={() => selectAlignment(horizontal, verticalSelectValue)}
                    />
                {/if}
            </div>
        </div>

        {#if isContent && orientation}
            <div
                class="alignment__preview alignment__preview_{orientation}"
                class:alignment__preview_showed={isHovered}
                class:alignment__preview_halign_left={horizontalAlignHovered === 'left' || horizontalAlignHovered === 'start' && $direction === 'ltr' || horizontalAlignHovered === 'end' && $direction === 'rtl'}
                class:alignment__preview_halign_center={horizontalAlignHovered === 'center'}
                class:alignment__preview_halign_right={horizontalAlignHovered === 'right' || horizontalAlignHovered === 'end' && $direction === 'ltr' || horizontalAlignHovered === 'start' && $direction === 'rtl'}
                class:alignment__preview_valign_top={verticalAlignHovered === 'top'}
                class:alignment__preview_valign_center={verticalAlignHovered === 'center'}
                class:alignment__preview_valign_bottom={verticalAlignHovered === 'bottom'}
            >
                <div class="alignment__preview-item"></div>
                <div class="alignment__preview-item"></div>
                <div class="alignment__preview-item"></div>
            </div>
        {/if}
    </div>
</div>

<style>
    .alignment {
        position: relative;
        display: flex;
        gap: 16px;
    }

    .alignment__item {
        position: relative;
        flex: 1 0 0;
        min-width: 0;
    }

    .alignment__grid {
        display: grid;
        grid-template-columns: repeat(3, 33.3%);
        grid-template-rows: repeat(3, 33.3%);
        min-height: 74px;
        border: 1px solid var(--fill-transparent-3);
        background: var(--fill-transparent-minus-1);
        border-radius: 6px;
        transition: border-color .15s ease-in-out;
    }

    .alignment_disabled .alignment__grid {
        border: none;
        background: var(--fill-transparent-1);
    }

    .alignment__grid:not(.alignment__grid_disabled):hover {
        border-color: var(--fill-transparent-4);
    }

    .alignment__grid.alignment__grid:focus {
        outline: none;
        border-color: var(--accent-purple);
    }

    .alignment__grid.alignment__grid:focus:hover {
        border-color: var(--accent-purple-hover);
    }

    .alignment__val {
        display: flex;
        cursor: pointer;
    }

    .alignment__grid_disabled .alignment__val {
        cursor: default;
    }

    .alignment__val::before {
        display: block;
        flex: 0 0 auto;
        width: 6px;
        height: 6px;
        margin: 7px;
        background: var(--fill-transparent-2);
        border-radius: 2px;
        content: '';
        transition: .15s ease-in-out;
    }

    .alignment__grid:not(.alignment__grid_disabled) .alignment__val:not(.alignment__val_selected):hover::before {
        background-color: var(--fill-accent-3);
    }

    .alignment:not(.alignment_disabled):not(.alignment_match_horizontal)
    .alignment__val:not(.alignment__val_selected):hover::before {
        width: 12px;
    }

    .alignment:not(.alignment_disabled):not(.alignment_match_vertical)
    .alignment__val:not(.alignment__val_selected):hover::before {
        height: 12px;
    }

    .alignment__val_selected::before {
        width: 12px;
        height: 12px;
        background-color: var(--accent-purple);
    }

    .alignment_match_horizontal .alignment__val {
        grid-column-start: 1;
        grid-column-end: 4;
    }

    .alignment_match_vertical .alignment__val {
        grid-row-start: 1;
        grid-row-end: 4;
    }

    .alignment_match_horizontal .alignment__val::before {
        width: calc(100% - 14px);
    }

    .alignment_match_vertical .alignment__val::before {
        height: calc(100% - 14px);
    }

    .alignment__val_horizontal_left,
    .alignment_direction_ltr .alignment__val_horizontal_start ,
    .alignment_direction_rtl .alignment__val_horizontal_end {
        justify-content: start;
    }

    .alignment__val_horizontal_center {
        justify-content: center;
    }

    .alignment__val_horizontal_right,
    .alignment_direction_ltr .alignment__val_horizontal_end ,
    .alignment_direction_rtl .alignment__val_horizontal_start {
        justify-content: end;
    }

    .alignment__val_vertical_top {
        align-items: start;
    }

    .alignment__val_vertical_center {
        align-items: center;
    }

    .alignment__val_vertical_bottom {
        align-items: end;
    }

    .alignment__text {
        display: flex;
        align-items: center;
    }

    .alignment__select-wrapper {
        position: relative;
        display: flex;
        flex-direction: row;
        align-items: baseline;
        gap: 8px;
    }

    .alignment__select-fill {
        padding: 4px 6px;
    }

    .alignment__select {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border: none;
        cursor: pointer;
        opacity: 0;
    }

    .alignment__select-visual {
        position: relative;
        padding: 4px 6px;
        background: var(--background-primary);
        border-radius: 6px;
        pointer-events: none;
        transition: background-color .15s ease-in-out;
    }

    .alignment__select:not([disabled]):hover ~ .alignment__select-visual {
        background-color: var(--fill-transparent-1);
    }

    .alignment__select-group {
        display: flex;
        flex-direction: column;
        gap: 2px;
    }

    .alignment__preview {
        position: absolute;
        top: 0;
        left: 0;
        box-sizing: border-box;
        width: 100%;
        height: 100%;
        padding: 7px;
        border: 1px solid var(--fill-transparent-4);
        border-radius: 6px;
        background: var(--background-overflow-transparent);
        pointer-events: none;
        opacity: 0;
        transition: opacity .15s ease-in-out;
    }

    .alignment__preview_showed {
        opacity: 1;
    }

    .alignment__preview_vertical {
        display: flex;
        flex-direction: column;
        gap: 2px;
    }

    .alignment__preview_horizontal {
        display: flex;
        flex-direction: row;
        gap: 2px;
    }

    .alignment__preview_overlap {
        display: grid;
    }

    .alignment__preview_vertical.alignment__preview_halign_left {
        align-items: start;
    }
    .alignment__preview_vertical.alignment__preview_halign_center {
        align-items: center;
    }
    .alignment__preview_vertical.alignment__preview_halign_right {
        align-items: end;
    }

    .alignment__preview_vertical.alignment__preview_valign_top {
        justify-content: start;
    }
    .alignment__preview_vertical.alignment__preview_valign_center {
        justify-content: center;
    }
    .alignment__preview_vertical.alignment__preview_valign_bottom {
        justify-content: end;
    }

    .alignment__preview_horizontal.alignment__preview_halign_left {
        justify-content: start;
    }
    .alignment__preview_horizontal.alignment__preview_halign_center {
        justify-content: center;
    }
    .alignment__preview_horizontal.alignment__preview_halign_right {
        justify-content: end;
    }

    .alignment__preview_horizontal.alignment__preview_valign_top {
        align-items: start;
    }
    .alignment__preview_horizontal.alignment__preview_valign_center {
        align-items: center;
    }
    .alignment__preview_horizontal.alignment__preview_valign_bottom {
        align-items: end;
    }

    .alignment__preview_overlap.alignment__preview_halign_left {
        justify-items: start;
    }
    .alignment__preview_overlap.alignment__preview_halign_center {
        justify-items: center;
    }
    .alignment__preview_overlap.alignment__preview_halign_right {
        justify-items: end;
    }

    .alignment__preview_overlap.alignment__preview_valign_top {
        align-items: start;
    }
    .alignment__preview_overlap.alignment__preview_valign_center {
        align-items: center;
    }
    .alignment__preview_overlap.alignment__preview_valign_bottom {
        align-items: end;
    }

    .alignment__preview-item {
        box-sizing: border-box;
        background: var(--accent-purple);
        border-radius: 4px;
        border: 1px solid var(--fill-transparent-4);
    }

    .alignment__preview_vertical .alignment__preview-item {
        height: 12px;
    }

    .alignment__preview_vertical .alignment__preview-item:nth-child(1) {
        width: 60px;
    }

    .alignment__preview_vertical .alignment__preview-item:nth-child(2) {
        width: 80px;
    }

    .alignment__preview_vertical .alignment__preview-item:nth-child(3) {
        width: 50px;
    }

    .alignment__preview_horizontal .alignment__preview-item {
        width: 30px;
    }

    .alignment__preview_horizontal .alignment__preview-item:nth-child(1) {
        height: 40px;
    }

    .alignment__preview_horizontal .alignment__preview-item:nth-child(2) {
        height: 50px;
    }

    .alignment__preview_horizontal .alignment__preview-item:nth-child(3) {
        height: 30px;
    }

    .alignment__preview_overlap .alignment__preview-item {
        grid-area: 1 / 1 / 2 / 2;
    }

    .alignment__preview_overlap .alignment__preview-item:nth-child(1) {
        width: 40px;
        height: 40px;
    }

    .alignment__preview_overlap .alignment__preview-item:nth-child(2) {
        width: 30px;
        height: 50px;
    }

    .alignment__preview_overlap .alignment__preview-item:nth-child(3) {
        width: 50px;
        height: 30px;
    }
</style>
