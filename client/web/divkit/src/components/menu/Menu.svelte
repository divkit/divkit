<script lang="ts">
    import { afterUpdate, createEventDispatcher, getContext, onDestroy, onMount } from 'svelte';
    import { fly } from 'svelte/transition';

    import rootCss from '../Root.module.css';
    import css from './Menu.module.css';

    import type { MaybeMissing } from '../../expressions/json';
    import type { ActionMenuItem } from '../../../typings/common';
    import type { ComponentContext } from '../../types/componentContext';
    import { genClassName } from '../../utils/genClassName';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import Actionable from '../utilities/Actionable.svelte';

    export let ownerNode: HTMLElement;
    export let items: MaybeMissing<ActionMenuItem[]>;
    export let parentComponentContext: ComponentContext;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const popupMix = rootCtx.getCustomization('menuPopupClass') || '';
    const itemMix = rootCtx.getCustomization('menuItemClass') || '';

    const isDesktop = rootCtx.isDesktop;

    const creationTime = Date.now();

    const dispatch = createEventDispatcher();

    let menuNode: HTMLElement;
    let visible = false;
    let menuX = '';
    let menuY = '';
    let menuWidth = '';
    let menuHeight = '';
    let resizeObserver: ResizeObserver | null = null;

    $: mods = {
        visible
    };

    function reposition(): void {
        if (!menuNode || !ownerNode) {
            return;
        }
        const parent = menuNode.parentElement;
        if (!parent) {
            return;
        }

        const ownerBbox = ownerNode.getBoundingClientRect();
        const menuBbox = menuNode.getBoundingClientRect();
        const parentBbox = parent.getBoundingClientRect();
        const windowWidth = window.innerWidth;
        const windowHeight = window.innerHeight;

        let x = 0;
        let y = 0;
        let width: number | null = null;
        let height: number | null = null;
        let calcedWidth = menuBbox.width;
        let calcedHeight = menuBbox.height;

        x = ownerBbox.left - parentBbox.left;
        y = ownerBbox.bottom - parentBbox.top;

        if (x + calcedWidth > windowWidth) {
            x = windowWidth - calcedWidth;
        }
        if (x < 0) {
            x = 0;
        }
        if (y + calcedHeight > windowHeight) {
            if (ownerBbox.top - parentBbox.top - calcedHeight > 0) {
                y = ownerBbox.top - parentBbox.top - calcedHeight;
            } else {
                y = windowHeight - calcedHeight;
            }
        }
        if (y < 0) {
            y = 0;
        }

        menuX = `${x}px`;
        menuY = `${y}px`;
        menuWidth = width !== null ? `${width}px` : '';
        menuHeight = height !== null ? `${height}px` : '';
        visible = true;

        if (width === null || height === null) {
            // wrap_content by any side
            if (typeof ResizeObserver !== 'undefined' && !resizeObserver) {
                resizeObserver = new ResizeObserver(() => {
                    requestAnimationFrame(reposition);
                });
                resizeObserver.observe(menuNode);
            }
        } else {
            resizeObserver?.disconnect();
        }
    }

    function onWindowClick(event: Event): void {
        if (Date.now() - creationTime < 100 || event.composedPath().includes(menuNode)) {
            return;
        }

        dispatch('close');
    }

    function onWindowResize(): void {
        reposition();
    }

    function onItemAction(): boolean {
        dispatch('close');
        return true;
    }

    onMount(() => {
        if (rootCtx.tooltipRoot) {
            const computed = window.getComputedStyle(menuNode);
            menuNode.style.fontSize = computed.fontSize;
            menuNode.style.fontFamily = computed.fontFamily;
            menuNode.style.lineHeight = computed.lineHeight;
            rootCtx.tooltipRoot.appendChild(menuNode);
        }
    });

    afterUpdate(() => {
        if (!visible) {
            reposition();
        }
    });

    onDestroy(() => {
        resizeObserver?.disconnect();
    });
</script>

<svelte:window
    on:click={onWindowClick}
    on:resize={onWindowResize}
/>

<div
    bind:this={menuNode}
    class="{genClassName('menu', css, mods)} {$isDesktop ? rootCss.root_platform_desktop : ''} {popupMix}"
    style:top={menuY}
    style:left={menuX}
    style:width={menuWidth}
    style:height={menuHeight}
    transition:fly={{ y: 20 }}
>
    <ul class={css.menu__list}>
        {#each items as item}
            <li>
                <Actionable
                    componentContext={parentComponentContext}
                    actions={item.actions || (item.action && [item.action])}
                    cls="{css.menu__item} {itemMix}"
                    customAction={onItemAction}
                >
                    {item.text}
                </Actionable>
            </li>
        {/each}
    </ul>
</div>
