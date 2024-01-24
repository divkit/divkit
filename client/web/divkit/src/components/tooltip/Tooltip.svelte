<script lang="ts" context="module">
    const DEFAULT_ANIMATION: Animation = {
        name: 'set',
        items: [{
            name: 'translate'
        }, {
            name: 'fade'
        }]
    };
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
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { inOutAnimation } from '../../utils/inOutAnimation';

    export let ownerNode: HTMLElement;
    export let data: MaybeMissing<Tooltip>;
    export let internalId: number;
    export let parentComponentContext: ComponentContext;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    const isDesktop = rootCtx.isDesktop;

    const creationTime = Date.now();

    let tooltipNode: HTMLElement;
    let visible = false;
    let tooltipX = '';
    let tooltipY = '';
    let tooltipWidth = '';
    let tooltipHeight = '';
    let resizeObserver: ResizeObserver | null = null;

    $: componentContext = parentComponentContext.produceChildContext(data.div);

    $: position = parentComponentContext.getDerivedFromVars(data.position);
    $: offsetX = parentComponentContext.getDerivedFromVars(data.offset?.x?.value);
    $: offsetY = parentComponentContext.getDerivedFromVars(data.offset?.y?.value);

    $: animationIn = parentComponentContext.getDerivedFromVars(data.animation_in);
    $: animationOut = parentComponentContext.getDerivedFromVars(data.animation_out);

    $: mods = {
        visible
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

        const jsonWidth = parentComponentContext.getJsonWithVars(data.div?.width);
        const jsonHeight = parentComponentContext.getJsonWithVars(data.div?.width);

        if (!jsonWidth || jsonWidth.type === 'match_parent') {
            calcedWidth = width = ownerBbox.width;
        } else if (jsonWidth.type === 'fixed' && jsonWidth.value) {
            calcedWidth = width = jsonWidth.value;
        } else {
            calcedWidth = tooltipBbox.width;
        }
        if (jsonHeight?.type === 'match_parent') {
            calcedHeight = height = ownerBbox.height;
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

        x -= parentBbox.left;
        y -= parentBbox.top;

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

    function onWindowClick(event: Event): void {
        if (Date.now() - creationTime < 100 || event.composedPath().includes(tooltipNode)) {
            return;
        }

        rootCtx.onTooltipClose(internalId);
    }

    function onWindowResize(): void {
        reposition();
    }

    onMount(() => {
        if (rootCtx.tooltipRoot) {
            const computed = window.getComputedStyle(tooltipNode);
            tooltipNode.style.fontSize = computed.fontSize;
            tooltipNode.style.fontFamily = computed.fontFamily;
            tooltipNode.style.lineHeight = computed.lineHeight;
            rootCtx.tooltipRoot.appendChild(tooltipNode);
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
    bind:this={tooltipNode}
    class="{genClassName('tooltip', css, mods)} {$isDesktop ? rootCss.root_platform_desktop : ''}"
    style:top={tooltipY}
    style:left={tooltipX}
    style:width={tooltipWidth}
    style:height={tooltipHeight}
    in:inOutAnimation={{ animations: $animationIn || DEFAULT_ANIMATION, direction: 'in' }}
    out:inOutAnimation={{ animations: $animationOut || DEFAULT_ANIMATION, direction: 'out' }}
>
    <Unknown
        {componentContext}
    />
</div>
