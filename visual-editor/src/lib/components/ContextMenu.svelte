<script lang="ts">
    import { afterUpdate, getContext } from 'svelte';
    import { fly } from 'svelte/transition';
    import type { ShortcutList } from '../utils/keybinder/use';
    import { shortcuts } from '../utils/keybinder/use';
    import { popupClose } from '../utils/keybinder/shortcuts';
    import ContextMenuItem from './ContextMenuItem.svelte';
    import { APP_CTX, type AppContext, type ContextMenuShowProps, type MenuItem } from '../ctx/appContext';

    const { setContextMenuApi } = getContext<AppContext>(APP_CTX);

    setContextMenuApi({
        toggle,
        hide
    });

    interface PopupStyle {
        left?: string;
        top?: string;
        width?: string;
        height?: string;
    }

    let shown = '';
    let currentProps: ContextMenuShowProps | null = null;
    let ownerBbox: DOMRect;
    let popupBbox: DOMRect | null;
    let popupStyle: PopupStyle = {};
    let popup: HTMLElement;
    let controllerElem: HTMLElement;

    let submenu: MenuItem | null = null;
    let submenuStyle: PopupStyle = {};
    let submenuTimeout: number;
    let submenuBbox: DOMRect | null;
    let submenuElem: HTMLElement;
    let submenuOwnerBbox: DOMRect;

    function toggle(props: ContextMenuShowProps): void {
        if (shown) {
            hide();
        } else {
            show(props);
        }
    }

    function show(props: ContextMenuShowProps): void {
        if (!(props.owner instanceof HTMLElement)) {
            return;
        }

        shown = props.name || '<unknown>';
        currentProps = props;

        ownerBbox = props.owner.getBoundingClientRect();
        popupBbox = null;
        popupStyle = {};
        submenuStyle = {};
    }

    function hide(): void {
        shown = '';
        currentProps = null;
        submenu = null;
        submenuBbox = null;
    }

    function onWindowClick(event: MouseEvent): void {
        const path = event.composedPath();
        if (!path.includes(controllerElem) && !(currentProps?.owner && path.includes(currentProps.owner))) {
            hide();
        }
    }

    function showSubmenu(owner: HTMLElement, item: MenuItem): void {
        submenuOwnerBbox = owner.getBoundingClientRect();
        clearTimeout(submenuTimeout);
        submenu = item;
        submenuBbox = null;
    }

    function hideSubmenu(): void {
        clearTimeout(submenuTimeout);
        submenuTimeout = setTimeout(() => {
            submenu = null;
            submenuBbox = null;
        }, 300);
    }

    function mouseenterSubmenu(): void {
        clearTimeout(submenuTimeout);
    }

    const SHORTCUTS: ShortcutList = [
        [popupClose, () => {
            if (shown) {
                hide();
                return;
            }
            return false;
        }]
    ];

    function calcStyle(ownerBbox: DOMRect, popupBbox: DOMRect, direction: 'right' | 'bottom') {
        const offsetLeft = currentProps?.offset?.x ?? -8;
        const offsetTop = currentProps?.offset?.y ?? 8;

        const pageYOffset = window.scrollY;

        let width = 'auto';
        let height = 'auto';
        let left;
        let top;

        let windowWidth = window.innerWidth;
        let windowHeight = window.innerHeight;

        let ownerLeft = ownerBbox.left + offsetLeft;
        let ownerRight = ownerBbox.right + offsetLeft;
        let ownerWidth = ownerBbox.width;

        if (ownerLeft < 0) {
            ownerLeft = 0;
            ownerWidth = ownerRight - ownerLeft;
        }

        if (ownerRight > windowWidth) {
            ownerRight = windowWidth;
            ownerWidth = ownerRight - ownerLeft;
        }

        let popupWidth = popupBbox.width;

        if (popupWidth > windowWidth) {
            popupWidth = windowWidth;
            width = popupWidth + 'px';
        } else if (popupWidth < ownerWidth && ownerWidth <= 500) {
            popupWidth = ownerWidth;
            width = popupWidth + 'px';
        }

        const targetLeft = direction === 'bottom' ? ownerLeft : ownerLeft + ownerWidth;
        if (targetLeft + popupWidth < windowWidth) {
            left = targetLeft;
        } else if (ownerRight > popupWidth) {
            left = ownerRight - popupWidth;
        } else if (ownerLeft - (windowWidth - popupWidth) < popupWidth - ownerRight) {
            left = windowWidth - popupWidth;
        } else {
            left = 0;
        }

        let ownerBottom = ownerBbox.bottom + pageYOffset + offsetTop;

        if (ownerBottom > windowHeight + pageYOffset) {
            ownerBottom = windowHeight + pageYOffset;
        }

        let popupHeight = popupBbox.height;

        if (popupHeight > windowHeight) {
            popupHeight = windowHeight;
            height = windowHeight + 'px';
        }

        if (direction === 'bottom') {
            if (ownerBottom + popupHeight < windowHeight + pageYOffset) {
                top = ownerBottom;
            } else if (ownerBbox.top + pageYOffset + offsetTop > popupHeight) {
                top = ownerBbox.top + pageYOffset + offsetTop - popupHeight;
            } else {
                top = 0;
            }
        } else if (direction === 'right') {
            if (ownerBbox.top + popupHeight < windowHeight + pageYOffset) {
                top = ownerBbox.top;
            } else if (windowHeight > popupHeight) {
                top = windowHeight - popupHeight;
            } else {
                top = 0;
            }
        } else {
            top = 0;
        }

        const controllerBbox = controllerElem.getBoundingClientRect();
        left -= controllerBbox.left;
        top -= controllerBbox.top;

        return {
            top: top + 'px',
            left: left + 'px',
            width,
            height
        };
    }

    afterUpdate(() => {
        if (!popupBbox && popup && controllerElem) {
            popupBbox = popup.getBoundingClientRect();
            popupStyle = calcStyle(ownerBbox, popupBbox, 'bottom');
        }
        if (!submenuBbox && submenuElem && controllerElem) {
            submenuBbox = submenuElem.getBoundingClientRect();
            submenuStyle = calcStyle(submenuOwnerBbox, submenuBbox, 'right');
        }
    });
</script>

<svelte:window on:click={onWindowClick} />

{#if shown && currentProps}
    <div class="menu-controller" bind:this={controllerElem} use:shortcuts={SHORTCUTS}>
        <ul
            bind:this={popup}
            class="menu"
            style:left={popupStyle.left}
            style:top={popupStyle.top}
            style:width={popupStyle.width}
            style:height={popupStyle.height}
            transition:fly|local={{ y: 10, duration: 150 }}
        >
            {#each currentProps.items as item}
                <ContextMenuItem
                    {item}
                    selected={item === submenu}
                    on:showsubmenu={event => showSubmenu(event.detail.elem, item)}
                    on:hidesubmenu={hideSubmenu}
                />
            {/each}
        </ul>

        {#if submenu?.submenu}
            <ul
                bind:this={submenuElem}
                class="menu menu_submenu"
                style:left={submenuStyle.left}
                style:top={submenuStyle.top}
                style:width={submenuStyle.width}
                style:height={submenuStyle.height}
                transition:fly={{ y: 10, duration: 150 }}
                on:mouseenter={mouseenterSubmenu}
                on:mouseleave={hideSubmenu}
            >
                {#each submenu.submenu as item}
                    <ContextMenuItem
                        {item}
                    />
                {/each}
            </ul>
        {/if}
    </div>
{/if}

<style>
    .menu-controller {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        overflow: hidden;
        pointer-events: none;
    }

    .menu {
        position: absolute;
        z-index: 10;
        top: -1024px;
        left: -1024px;
        min-width: 200px;
        margin: 0;
        padding: 4px 0;
        border-radius: 8px;
        background: var(--background-tertiary);
        overflow-x: hidden;
        overflow-y: auto;
        max-height: 50vh;
        list-style: none;
        -webkit-overflow-scrolling: touch;
        box-shadow: var(--shadow-16);
        pointer-events: auto;
        user-select: none;
    }

    @supports not selector(::-webkit-scrollbar-thumb) {
        .menu {
            scrollbar-width: thin;
        }
    }

    .menu_submenu {
        margin-top: -4px;
        /* margin-left: 12px; */
    }
</style>
