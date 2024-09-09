<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { alignDown, alignLeft, alignRight, alignUp, clear } from '../../utils/keybinder/shortcuts';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';

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

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);

    const dispatch = createEventDispatcher();

    const HORIZONTAL = ['left', 'center', 'right'];
    const VERTICAL = ['top', 'center', 'bottom'];
    const TEXT_MAP: Record<string, string> = {
        left: 'props.align_left',
        center: 'props.align_center',
        right: 'props.align_right',
        top: 'props.align_top',
        bottom: 'props.align_bottom',
        '': 'default'
    };

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

    $: horizontalList = isHorizontalDisabled ? ['left'] : HORIZONTAL;
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
    class="alignment"
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
                        horizontal === horizontalItem &&
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
                <select
                    class="alignment__select"
                    bind:value={horizontalSelectValue}
                    on:change={() => selectAlignment(horizontalSelectValue, vertical)}
                    disabled={disabled || isHorizontalDisabled}
                >
                    <option value="left">{$l10n(TEXT_MAP.left)}</option>
                    <option value="center">{$l10n(TEXT_MAP.center)}</option>
                    <option value="right">{$l10n(TEXT_MAP.right)}</option>
                    <option value="">{$l10n(TEXT_MAP[''])}</option>
                </select>
                <div class="alignment__select-visual">
                    <span class="alignment__select-label">
                        X:
                    </span>
                    {$l10n(isHorizontalDisabled ? 'props.size_match_parent' : TEXT_MAP[horizontalSelectValue || ''])}
                </div>
            </div>
            <div class="alignment__select-wrapper">
                <select
                    class="alignment__select"
                    bind:value={verticalSelectValue}
                    on:change={() => selectAlignment(horizontal, verticalSelectValue)}
                    disabled={disabled || isVerticalDisabled}
                >
                    <option value="top">{$l10n(TEXT_MAP.top)}</option>
                    <option value="center">{$l10n(TEXT_MAP.center)}</option>
                    <option value="bottom">{$l10n(TEXT_MAP.bottom)}</option>
                    <option value="">{$l10n(TEXT_MAP[''])}</option>
                </select>
                <div class="alignment__select-visual">
                    <span class="alignment__select-label">
                        Y:
                    </span>
                    {$l10n(isVerticalDisabled ? 'props.size_match_parent' : TEXT_MAP[verticalSelectValue || ''])}
                </div>
            </div>
        </div>

        {#if isContent && orientation}
            <div
                class="alignment__preview alignment__preview_{orientation}"
                class:alignment__preview_showed={isHovered}
                class:alignment__preview_halign_left={horizontalAlignHovered === 'left'}
                class:alignment__preview_halign_center={horizontalAlignHovered === 'center'}
                class:alignment__preview_halign_right={horizontalAlignHovered === 'right'}
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

    .alignment__val_horizontal_left {
        justify-content: start;
    }

    .alignment__val_horizontal_center {
        justify-content: center;
    }

    .alignment__val_horizontal_right {
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

    .alignment__select[disabled] {
        cursor: default;
    }

    .alignment__select-visual {
        position: relative;
        padding: 4px 6px;
        background: var(--background-primary);
        border-radius: 6px;
        pointer-events: none;
        transition: background-color .15s ease-in-out;
    }

    .alignment__select[disabled] ~ .alignment__select-visual {
        color: var(--text-secondary);
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
