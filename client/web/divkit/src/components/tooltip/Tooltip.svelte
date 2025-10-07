<script lang="ts" context="module">
    const DEFAULT_ANIMATION: Animation = {
        name: 'set',
        items: [{
            name: 'translate'
        }, {
            name: 'fade'
        }]
    };

    let openedTooltipsStack: HTMLElement[] = [];
</script>

<script lang="ts">
    import { afterUpdate, getContext, onDestroy, onMount } from 'svelte';

    import rootCss from '../Root.module.css';
    import css from './Tooltip.module.css';

    import type { Tooltip } from '../../types/base';
    import type { Animation } from '../../types/animation';
    import type { ComponentContext } from '../../types/componentContext';
    import type { MaybeMissing } from '../../expressions/json';
    import Unknown from '../utilities/Unknown.svelte';
    import { genClassName } from '../../utils/genClassName';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { inOutAnimation } from '../../utils/inOutAnimation';
    import { hasDialogSupport } from '../../utils/hasDialogSupport';

    export let ownerNode: HTMLElement;
    export let data: MaybeMissing<Tooltip>;
    export let internalId: number;
    export let parentComponentContext: ComponentContext;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const isDesktop = rootCtx.isDesktop;

    const creationTime = Date.now();

    let tooltipNode: HTMLDialogElement | HTMLElement;
    let visible = false;
    let tooltipX = '';
    let tooltipY = '';
    let tooltipWidth = '';
    let tooltipHeight = '';
    let resizeObserver: ResizeObserver | null = null;
    let componentContext: ComponentContext;
    let modal = true;
    let prevFocusedElement: Element | null = null;

    $: {
        if (componentContext) {
            componentContext.destroy();
        }
        componentContext = parentComponentContext.produceChildContext(data.div || {}, {
            isTooltipRoot: true
        });
    }

    $: position = parentComponentContext.getDerivedFromVars(data.position);
    $: offsetX = parentComponentContext.getDerivedFromVars(data.offset?.x?.value);
    $: offsetY = parentComponentContext.getDerivedFromVars(data.offset?.y?.value);

    $: animationIn = parentComponentContext.getDerivedFromVars(data.animation_in);
    $: animationOut = parentComponentContext.getDerivedFromVars(data.animation_out);

    $: if (data.mode?.type === 'non_modal') {
        modal = false;
    } else {
        modal = true;
    }

    $: mods = {
        visible,
        modal
    };

    function reposition(): void {
        if (!tooltipNode || !ownerNode) {
            return;
        }
        const parent = tooltipNode.parentElement;
        if (!parent) {
            return;
        }

        const prevTransform = tooltipNode.style.cssText;
        // Override transform from the css animation in the inOutAnimation
        // So it happens in the order:
        // 1) Attach dom node
        // 2) Run in-out transition
        // 3) Call afterUpdate
        tooltipNode.style.cssText += ';transform: none !important';

        const ownerBbox = ownerNode.getBoundingClientRect();
        const tooltipBbox = tooltipNode.getBoundingClientRect();
        const parentBbox = parent.getBoundingClientRect();

        tooltipNode.style.cssText = prevTransform;

        let x = 0;
        let y = 0;
        let width: number | null = null;
        let height: number | null = null;
        let calcedWidth = 0;
        let calcedHeight = 0;

        const jsonWidth = componentContext?.json?.width;
        const jsonHeight = componentContext?.json?.height;

        if (!jsonWidth || jsonWidth.type === 'match_parent') {
            calcedWidth = width = window.innerWidth;
        } else if (jsonWidth.type === 'fixed' && jsonWidth.value) {
            calcedWidth = width = jsonWidth.value;
        } else {
            calcedWidth = tooltipBbox.width;
        }
        if (jsonHeight?.type === 'match_parent') {
            calcedHeight = height = window.innerHeight;
        } else if (jsonHeight?.type === 'fixed' && jsonHeight.value) {
            calcedHeight = height = jsonHeight.value;
        } else {
            calcedHeight = tooltipBbox.height;
        }

        if ($position === 'left' || $position === 'bottom-left' || $position === 'top-left') {
            x = ownerBbox.left - calcedWidth;
        } else if ($position === 'top' || $position === 'bottom' || $position === 'center') {
            x = (ownerBbox.left + ownerBbox.right) / 2 - calcedWidth / 2;
        } else if ($position === 'right' || $position === 'bottom-right' || $position === 'top-right') {
            x = ownerBbox.right;
        } else {
            return;
        }

        if ($position === 'top' || $position === 'top-left' || $position === 'top-right') {
            y = ownerBbox.top - calcedHeight;
        } else if ($position === 'left' || $position === 'right' || $position === 'center') {
            y = (ownerBbox.top + ownerBbox.bottom) / 2 - calcedHeight / 2;
        } else if ($position === 'bottom-left' || $position === 'bottom' || $position === 'bottom-right') {
            y = ownerBbox.bottom;
        } else {
            return;
        }

        if (!(hasDialogSupport && modal)) {
            x -= parentBbox.left;
            y -= parentBbox.top;
        }

        x += $offsetX || 0;
        y += $offsetY || 0;

        tooltipX = `${x}px`;
        tooltipY = `${y}px`;
        tooltipWidth = width !== null ? `${width}px` : '';
        tooltipHeight = height !== null ? `${height}px` : '';
        visible = true;

        if (width === null || height === null) {
            // wrap_content by any side
            if (typeof ResizeObserver !== 'undefined' && !resizeObserver) {
                resizeObserver = new ResizeObserver(() => {
                    requestAnimationFrame(reposition);
                });
                resizeObserver.observe(tooltipNode);
            }
        } else {
            resizeObserver?.disconnect();
        }
    }

    function onOutClick(event: Event): void {
        if (openedTooltipsStack.length && openedTooltipsStack[openedTooltipsStack.length - 1] !== tooltipNode) {
            return;
        }

        const path = event.composedPath();

        if (
            Date.now() - creationTime < 100 ||
            path.includes(tooltipNode) && !(hasDialogSupport && path[0] === tooltipNode)
        ) {
            return;
        }

        closeByOutside();
    }

    function closeByOutside(event?: Event): void {
        event?.stopPropagation();
        event?.preventDefault();

        if (componentContext.getJsonWithVars(data.close_by_tap_outside) !== false) {
            openedTooltipsStack = openedTooltipsStack.filter(it => it !== tooltipNode);
            rootCtx.onTooltipClose(internalId);
        }

        if (data.tap_outside_actions) {
            componentContext.execAnyActions(data.tap_outside_actions, {
                processUrls: true
            });
        }
    }

    function onWindowResize(): void {
        reposition();
    }

    function onKeyDown(event: KeyboardEvent): void {
        if (openedTooltipsStack.length && openedTooltipsStack[openedTooltipsStack.length - 1] !== tooltipNode) {
            return;
        }

        if (event.key === 'Escape' && !event.ctrlKey && !event.shiftKey && !event.altKey && !event.metaKey) {
            openedTooltipsStack = openedTooltipsStack.filter(it => it !== tooltipNode);
            rootCtx.onTooltipClose(internalId);
        }
    }

    function onClose(event: Event): void {
        openedTooltipsStack = openedTooltipsStack.filter(it => it !== tooltipNode);
        rootCtx.onTooltipClose(internalId);
        event.preventDefault();
    }

    onMount(() => {
        try {
            prevFocusedElement = document.activeElement;
        } catch (_err) {}

        if (rootCtx.tooltipRoot) {
            const computed = window.getComputedStyle(tooltipNode);
            tooltipNode.style.fontSize = computed.fontSize;
            tooltipNode.style.fontFamily = computed.fontFamily;
            tooltipNode.style.lineHeight = computed.lineHeight;
            rootCtx.tooltipRoot.appendChild(tooltipNode);
        }

        if (hasDialogSupport && tooltipNode && tooltipNode instanceof HTMLDialogElement) {
            tooltipNode[modal ? 'showModal' : 'show']();
        }
        if (modal) {
            openedTooltipsStack.push(tooltipNode);
        }
    });

    afterUpdate(() => {
        if (!visible) {
            reposition();
        }
    });

    onDestroy(() => {
        if (componentContext) {
            componentContext.destroy();
        }

        resizeObserver?.disconnect();

        openedTooltipsStack = openedTooltipsStack.filter(it => it !== tooltipNode);

        if (modal && prevFocusedElement && prevFocusedElement instanceof HTMLElement) {
            if (hasDialogSupport && tooltipNode && tooltipNode instanceof HTMLDialogElement) {
                tooltipNode.close();
            }

            try {
                prevFocusedElement.focus({
                    preventScroll: true
                });
            } catch (_err) {}
        }
    });
</script>

<svelte:window
    on:resize={onWindowResize}
/>

<svelte:body
    on:click|capture={onOutClick}
/>

{#if hasDialogSupport}
    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
    <dialog
        bind:this={tooltipNode}
        class="{genClassName('tooltip', css, mods)} {$isDesktop ? rootCss.root_platform_desktop : ''}"
        style:top={tooltipY}
        style:left={tooltipX}
        style:width={tooltipWidth}
        style:height={tooltipHeight}
        in:inOutAnimation|global={{ animations: $animationIn || DEFAULT_ANIMATION, direction: 'in' }}
        out:inOutAnimation|global={{ animations: $animationOut || DEFAULT_ANIMATION, direction: 'out' }}
        on:keydown={onKeyDown}
        on:close={onClose}
        on:cancel={onClose}
        on:click={onOutClick}
    >
        {#if visible && modal && data.background_accessibility_description}
            <button
                class={css.tooltip__overlay}
                type="button"
                aria-label={data.background_accessibility_description}
                on:click={closeByOutside}
            ></button>
        {/if}

        <div class={css.tooltip__inner}>
            <Unknown
                {componentContext}
            />
        </div>
    </dialog>
{:else}
    {#if visible && modal}
        {#if data.background_accessibility_description}
            <button
                class={css.tooltip__overlay}
                type="button"
                aria-label={data.background_accessibility_description}
                on:click={closeByOutside}
            ></button>
        {:else}
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-static-element-interactions -->
            <div
                class={css.tooltip__overlay}
                on:click={closeByOutside}
            ></div>
        {/if}
    {/if}

    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
    <div
        bind:this={tooltipNode}
        class="{genClassName('tooltip', css, mods)} {$isDesktop ? rootCss.root_platform_desktop : ''}"
        role="dialog"
        aria-modal={modal}
        style:top={tooltipY}
        style:left={tooltipX}
        style:width={tooltipWidth}
        style:height={tooltipHeight}
        in:inOutAnimation|global={{ animations: $animationIn || DEFAULT_ANIMATION, direction: 'in' }}
        out:inOutAnimation|global={{ animations: $animationOut || DEFAULT_ANIMATION, direction: 'out' }}
        on:keydown={onKeyDown}
    >
        <div class={css.tooltip__inner}>
            <Unknown
                {componentContext}
            />
        </div>
    </div>
{/if}
