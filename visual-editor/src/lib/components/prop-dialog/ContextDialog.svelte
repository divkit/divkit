<script lang="ts" context="module">
    const VERTICAL_GAP = 20;
</script>

<script lang="ts">
    import { createEventDispatcher, onMount } from 'svelte';
    import { cancel } from '../../utils/keybinder/shortcuts';

    // todo animate height change

    export let target: HTMLElement;
    export let hasClose: boolean = false;
    export let direction: 'left' | 'right' | 'down' | 'auto' = 'auto';
    export let width = '';
    export let overflow = 'hidden';
    export let canMove = false;
    export let offset = 38;
    export let offsetY = 0;
    export let wide = false;

    export function hide(): void {
        isHiding = true;
    }

    const Y_GAP = 16;

    $: if (wide !== undefined) {
        isSizeChange = true;
        setTimeout(() => {
            isSizeChange = false;
        }, 50);
    }

    const dispatch = createEventDispatcher();

    function update(): void {
        if (dialog) {
            if (customPosition) {
                const bbox = dialog.getBoundingClientRect();
                if (
                    bbox.left >= 0 && bbox.top >= 0 &&
                    bbox.right <= window.innerWidth && bbox.bottom <= window.innerHeight
                ) {
                    return;
                }
            }
            [left, top] = calcStyles();
            isHidden = false;
            customPosition = false;
        }
    }

    const resizeObserver = new ResizeObserver(update);

    let dialog: HTMLDialogElement;
    let content: HTMLElement;
    let isOutsidePointerDown = false;
    let isHiding = false;
    let isFullyShowed = false;
    let left = 0;
    let top = 0;
    let isHidden = true;
    let isMoving = false;
    let isSizeChange = false;
    let customPosition = false;

    function onDialogClick(event: MouseEvent): void {
        if (event.target === dialog && isOutsidePointerDown) {
            isHiding = true;
        }
    }

    function onClose(): void {
        isHidden = true;
        resizeObserver.unobserve(dialog);
        dispatch('close');
    }

    function onCloseClick(): void {
        isHiding = true;
    }

    function calcStyles(): [number, number] {
        let left = 0;
        let top = 0;

        const dialogBbox = dialog.getBoundingClientRect();
        const targetBbox = target.getBoundingClientRect();
        let dir = direction;

        if (dir === 'auto') {
            dir = targetBbox.left + targetBbox.width / 2 < window.innerWidth / 2 ? 'right' : 'left';
        }

        if (dir === 'left') {
            left = Math.max(16, targetBbox.left - offset - dialogBbox.width);
        } else if (dir === 'down') {
            left = targetBbox.left + targetBbox.width / 2 - dialogBbox.width / 2;
        } else {
            left = targetBbox.right + offset;
        }
        if (left + dialogBbox.width > window.innerWidth) {
            left = window.innerWidth - dialogBbox.width;
        }
        if (left < 0) {
            left = 0;
        }
        if (dir === 'left' || dir === 'right') {
            top = window.scrollY + targetBbox.top + offsetY;
            if (top + dialogBbox.height + Y_GAP > window.scrollY + window.innerHeight) {
                top = window.scrollY + window.innerHeight - (dialogBbox.height + Y_GAP);
            }
        } else {
            top = window.scrollY + targetBbox.bottom + offsetY;
        }
        top = Math.min(top, window.innerHeight - dialogBbox.height - VERTICAL_GAP);
        top = Math.max(top, VERTICAL_GAP);

        return [left, top];
    }

    function onAnimationEnd(event: AnimationEvent): void {
        if (event.target === dialog) {
            if (isHiding) {
                dialog.close();
            } else {
                isFullyShowed = true;
            }
        }
    }

    function onOutsidePointerDown(event: PointerEvent): void {
        const path = event.composedPath();
        isOutsidePointerDown = path[0] === dialog;
    }

    function onOutsideClick(event: MouseEvent): void {
        if (isFullyShowed) {
            const path = event.composedPath();
            if (dialog && !path.includes(dialog) && !path.some(it => it instanceof HTMLDialogElement)) {
                isHiding = true;
            }
        }
    }

    function onKeyDown(event: KeyboardEvent): void {
        if (cancelCurrent && cancel.isPressed(event)) {
            event.preventDefault();
            event.stopPropagation();
            cancelCurrent();
        }
    }

    let cancelCurrent: (() => void) | null = null;
    function onPointerDown(event: PointerEvent): void {
        if (event.target !== content) {
            return;
        }
        event.preventDefault();

        const startPageX = event.pageX;
        const startPageY = event.pageY;
        const startLeft = left;
        const startTop = top;
        isMoving = true;

        const pointermove = (event: PointerEvent) => {
            left = event.pageX - startPageX + startLeft;
            top = event.pageY - startPageY + startTop;
            customPosition = true;
        };

        cancelCurrent = () => {
            document.body.removeEventListener('pointermove', pointermove);
            document.body.removeEventListener('pointerup', pointerup);
            document.body.removeEventListener('pointercancel', pointerup);
            cancelCurrent = null;
            isMoving = false;
        };
        const pointerup = () => {
            cancelCurrent?.();
        };

        document.body.addEventListener('pointermove', pointermove);
        document.body.addEventListener('pointerup', pointerup);
        document.body.addEventListener('pointercancel', pointerup);
    }

    onMount(() => {
        isHiding = false;
        isFullyShowed = false;
        dialog.showModal();
        resizeObserver.observe(dialog);
        if (isHidden) {
            [left, top] = calcStyles();
            isHidden = false;
        }
    });
</script>

<svelte:window
    on:pointerdown={onOutsidePointerDown}
    on:click={onOutsideClick}
    on:resize={update}
/>

<!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
<dialog
    class="context-dialog context-dialog_direction_{direction}"
    class:context-dialog_hidden={isHidden}
    class:context-dialog_hiding={isHiding}
    class:context-dialog_disable-animation={isMoving || isSizeChange}
    class:context-dialog_wide={wide}
    class:context-dialog_fully-showed={isFullyShowed}
    class:context-dialog_overflow_visible={overflow === 'visible'}
    bind:this={dialog}
    on:click={onDialogClick}
    on:close={onClose}
    on:animationend={onAnimationEnd}
    on:keydown={onKeyDown}
    style:left="{left}px"
    style:top="{top}px"
    style:width={width}
>
    <div
        bind:this={content}
        class="context-dialog__content"
        on:pointerdown={canMove ? onPointerDown : null}
    >
        <slot />

        {#if hasClose}
            <button
                class="context-dialog__close"
                on:click={onCloseClick}
            ></button>
        {/if}
    </div>
</dialog>

<style>
    .context-dialog {
        top: 0;
        left: 0;
        width: 264px;
        margin: 0;
        padding: 0;
        margin: 0;
        background: var(--background-secondary);
        border: none;
        border-radius: 12px;
        box-shadow: var(--shadow-32);
        animation: context-dialog__show_left .2s ease-in-out;
    }

    .context-dialog::backdrop {
        background: none;
    }

    .context-dialog_wide {
        width: 529px;
    }

    .context-dialog_direction_right {
        animation-name: context-dialog__show_right;
    }

    .context-dialog_direction_down {
        animation-name: context-dialog__show_down;
    }

    .context-dialog_hidden {
        visibility: hidden;
        animation: none;
    }

    .context-dialog_hiding {
        animation-name: context-dialog__hide_left;
    }

    .context-dialog_hiding.context-dialog_direction_right {
        animation-name: context-dialog__hide_right;
    }

    .context-dialog_hiding.context-dialog_direction_down {
        animation-name: context-dialog__hide_down;
    }

    .context-dialog_fully-showed {
        transition: .15s ease-in-out;
        transition-property: left, top;
    }

    .context-dialog_disable-animation {
        transition-duration: 0s;
    }

    .context-dialog_overflow_visible {
        overflow: visible;
    }

    .context-dialog__content {
        display: flex;
        flex-direction: column;
    }

    .context-dialog__close {
        position: absolute;
        top: 7px;
        right: 7px;
        width: 34px;
        height: 34px;
        background: none;
        border: none;
        border-radius: 8px;
        transition: background-color .15s ease-in-out;
        cursor: pointer;
    }

    .context-dialog__close:hover {
        background-color: var(--fill-transparent-2);
    }

    .context-dialog__close:focus-visible {
        outline: 1px solid var(--accent-purple);
    }

    .context-dialog__close::before {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50% url(../../../assets/closeDialog.svg);
        background-size: 16px;
        filter: var(--icon-filter);
        content: '';
    }

    @keyframes context-dialog__show_left {
        from {
            opacity: 0;
            transform: translateX(10px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }

    @keyframes context-dialog__show_right {
        from {
            opacity: 0;
            transform: translateX(-10px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }

    @keyframes context-dialog__show_down {
        from {
            opacity: 0;
            transform: translateY(-10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    @keyframes context-dialog__hide_left {
        from {
            opacity: 1;
            transform: translateX(0);
        }
        to {
            opacity: 0;
            transform: translateX(10px);
        }
    }

    @keyframes context-dialog__hide_right {
        from {
            opacity: 1;
            transform: translateX(0);
        }
        to {
            opacity: 0;
            transform: translateX(-10px);
        }
    }

    @keyframes context-dialog__hide_down {
        from {
            opacity: 1;
            transform: translateY(0);
        }
        to {
            opacity: 0;
            transform: translateY(-10px);
        }
    }
</style>
