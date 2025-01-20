<script lang="ts" context="module">
    const MIN_SWIPE_PX = 8;
    const MIN_LONG_TAP_DURATION = 400;

    const SUPPORTED_ACCESSIBILITY_TYPES = new Set([
        'button',
        'image',
        'checkbox',
        'radio'
    ]);
</script>

<script lang="ts">
    import { getContext, onDestroy, onMount, setContext } from 'svelte';

    import rootCss from '../Root.module.css';

    import type { Action } from '../../../typings/common';
    import type { MaybeMissing } from '../../expressions/json';
    import type { ComponentContext } from '../../types/componentContext';
    import type { Accessibility } from '../../types/base';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { doNothing } from '../../utils/doNothing';
    import { ACTION_CTX, type ActionCtxValue } from '../../context/action';
    import { wrapError } from '../../utils/wrapError';
    import { getUrlSchema, isBuiltinSchema } from '../../utils/url';
    import { type Coords, getTouchCoords } from '../../utils/getTouchCoords';

    export let componentContext: ComponentContext;
    export let id = '';
    export let actions: MaybeMissing<Action[]> | undefined = undefined;
    export let doubleTapActions: MaybeMissing<Action[]> | undefined = undefined;
    export let longTapActions: MaybeMissing<Action[]> | undefined = undefined;
    export let pressStartActions: MaybeMissing<Action[]> | undefined = undefined;
    export let pressEndActions: MaybeMissing<Action[]> | undefined = undefined;
    export let hoverStartActions: MaybeMissing<Action[]> | undefined = undefined;
    export let hoverEndActions: MaybeMissing<Action[]> | undefined = undefined;
    export let cls = '';
    export let style: string | null = null;
    export let attrs: Record<string, string | undefined> | undefined = undefined;
    export let use: ((element: HTMLElement, opts?: any) => void) = doNothing;
    export let customAction: ((event: Event) => boolean) | null = null;
    export let isNativeActionAnimation = true;
    export let hasInnerFocusable = false;
    export let customAccessibility: MaybeMissing<Accessibility> | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const actionCtx = getContext<ActionCtxValue>(ACTION_CTX);

    setContext<ActionCtxValue>(ACTION_CTX, {
        hasAction(): boolean {
            return Boolean(actionCtx.hasAction() || actions?.length);
        }
    });

    let node: HTMLElement;
    let href = '';
    let target: string | undefined = undefined;
    let startTs = -1;
    let startCoords: Coords | null = null;
    let isChanged = false;
    let hasJSAction = false;
    let hasAnyActions = false;
    let longtapTimer: number;
    let role: string | undefined;
    let isChecked: boolean | undefined;
    let pointerdown = false;

    $: {
        if (Array.isArray(actions) && actions?.length) {
            for (let i = 0; i < actions.length; ++i) {
                const url = actions[i].url;

                if (url) {
                    href = url;
                    target = actions[i].target || undefined;
                    break;
                }
            }
        }

        if (Array.isArray(actions) && actions?.length && actionCtx.hasAction()) {
            href = '';
            componentContext.logError(wrapError(new Error('Actionable element is forbidden inside other actionable element'), {
                level: 'warn',
                additional: {
                    actions
                }
            }));
        }

        hasJSAction = Boolean(customAction);
        if (href && !isBuiltinSchema(getUrlSchema(href), rootCtx.getBuiltinProtocols())) {
            href = '';
            hasJSAction = true;
        } else if (!href && Array.isArray(actions) && actions?.length) {
            hasJSAction = true;
            if (!actions.some(action => action.url || action.typed || action.menu_items)) {
                componentContext.logError(wrapError(new Error('The component has a list of actions, but does not have a real action'), {
                    level: 'warn',
                    additional: {
                        actions
                    }
                }));
            }
        }
    }

    $: {
        if (customAccessibility?.type && SUPPORTED_ACCESSIBILITY_TYPES.has(customAccessibility.type)) {
            role = customAccessibility.type;
        } else if (href) {
            role = undefined;
        } else if (hasJSAction) {
            role = 'button';
        }

        if ((role === 'checkbox' || role === 'radio') && typeof customAccessibility?.is_checked === 'boolean') {
            isChecked = customAccessibility.is_checked;
        } else {
            isChecked = undefined;
        }
    }

    $: if (node) {
        if (href || hasJSAction) {
            node.addEventListener('click', onClick);
        } else {
            node.removeEventListener('click', onClick);
        }

        if (doubleTapActions?.length) {
            node.addEventListener('dblclick', onDoubleClick);
        } else {
            node.removeEventListener('dblclick', onDoubleClick);
        }

        if (longTapActions?.length) {
            node.addEventListener('touchstart', onTouchStart, {
                passive: true
            });
            node.addEventListener('touchmove', onTouchMove, {
                passive: true
            });
            node.addEventListener('touchend', onTouchEnd, {
                passive: true
            });
            node.addEventListener('touchcancel', onTouchEnd, {
                passive: true
            });
        } else {
            node.removeEventListener('touchstart', onTouchStart);
            node.removeEventListener('touchmove', onTouchMove);
            node.removeEventListener('touchend', onTouchEnd);
            node.removeEventListener('touchcancel', onTouchEnd);
        }

        if (pressStartActions?.length || pressEndActions?.length) {
            node.addEventListener('pointerdown', onPointerDown);
            window.addEventListener('pointerup', onPointerUp);
            window.addEventListener('pointercancel', onPointerUp);
        } else {
            node.removeEventListener('pointerdown', onPointerDown);
            window.removeEventListener('pointerup', onPointerUp);
            window.removeEventListener('pointercancel', onPointerUp);
        }
        if (hoverStartActions?.length) {
            node.addEventListener('pointerenter', onPointerEnter);
        } else {
            node.removeEventListener('pointerenter', onPointerEnter);
        }
        if (hoverEndActions?.length) {
            node.addEventListener('pointerleave', onPointerLeave);
        } else {
            node.removeEventListener('pointerleave', onPointerLeave);
        }

        hasAnyActions = Boolean(
            href ||
            hasJSAction ||
            doubleTapActions?.length ||
            longTapActions?.length ||
            pressStartActions?.length ||
            pressEndActions?.length ||
            hoverStartActions?.length ||
            hoverEndActions?.length
        );
    }

    async function onClick(event: MouseEvent): Promise<void> {
        if (actionCtx.hasAction()) {
            return;
        }

        if (event.button !== undefined && event.button !== 0) {
            return;
        }

        if (startTs > 0 && Date.now() > startTs + MIN_LONG_TAP_DURATION) {
            // Long tap action
            event.preventDefault();
            return;
        }

        const cancelled = customAction?.(event) === false;

        if (cancelled) {
            event.preventDefault();
        } else if (actions) {
            const hasCustomAction = actions.some(action => {
                if (action?.typed) {
                    return true;
                }

                const url = action?.url;
                if (!url) {
                    return false;
                }

                const schema = getUrlSchema(url);

                return schema && !isBuiltinSchema(schema, rootCtx.getBuiltinProtocols());
            });
            if (hasCustomAction) {
                event.preventDefault();
            }
            componentContext.execAnyActions(actions, { node });
        }
    }

    function onDoubleClick(event: MouseEvent): void {
        if (actionCtx.hasAction()) {
            return;
        }

        if (event.button !== undefined && event.button !== 0) {
            return;
        }

        componentContext.execAnyActions(doubleTapActions, { processUrls: true, node });
    }

    function onTouchStart(event: TouchEvent): void {
        if (event.touches.length > 1) {
            return;
        }

        startCoords = getTouchCoords(event);
        isChanged = false;
        startTs = Date.now();
        if (longtapTimer) {
            clearTimeout(longtapTimer);
        }
    }

    function onTouchMove(event: TouchEvent): void {
        if (!startCoords) {
            return;
        }

        const coords = getTouchCoords(event);

        if (Math.abs(startCoords.x - coords.x) > MIN_SWIPE_PX || Math.abs(startCoords.y - coords.y) > MIN_SWIPE_PX) {
            isChanged = true;
        }
    }

    function onTouchEnd(event: TouchEvent): void {
        if (!startCoords || startTs < 0) {
            return;
        }

        if (!isChanged && (Date.now() - startTs) >= MIN_LONG_TAP_DURATION) {
            event.stopPropagation();
            componentContext.execAnyActions(longTapActions, { processUrls: true, node });
        }

        if (longtapTimer) {
            clearTimeout(longtapTimer);
        }
        longtapTimer = window.setTimeout(() => {
            startCoords = null;
            startTs = -1;
        }, 100);
    }

    function onPointerDown(): void {
        if (actionCtx.hasAction()) {
            return;
        }

        pointerdown = true;
        componentContext.execAnyActions(pressStartActions, { node });
    }

    function onPointerUp(): void {
        if (actionCtx.hasAction() || !pointerdown) {
            return;
        }

        pointerdown = false;
        componentContext.execAnyActions(pressEndActions, { node });
    }

    function onPointerEnter(): void {
        if (actionCtx.hasAction()) {
            return;
        }

        componentContext.execAnyActions(hoverStartActions, { node });
    }

    function onPointerLeave(): void {
        if (actionCtx.hasAction()) {
            return;
        }

        componentContext.execAnyActions(hoverEndActions, { node });
    }

    function onKeydown(event: KeyboardEvent): void {
        // todo check event.target is not inside current element

        const target = event.target;
        if (target instanceof HTMLElement) {
            if (target.tagName === 'INPUT' || target.contentEditable === 'true') {
                return;
            }
        }

        if (event.ctrlKey || event.metaKey || event.altKey || event.shiftKey) {
            return;
        }

        if (event.key === 'Enter' && Array.isArray(actions) && actions.length) {
            componentContext.execAnyActions(actions);
            event.preventDefault();
        }
    }

    onMount(() => {
        if (id && !hasInnerFocusable) {
            rootCtx.registerFocusable(id, {
                focus() {
                    if (node && (href || hasJSAction)) {
                        node.focus();
                    }
                }
            });
        }
    });

    onDestroy(() => {
        if (typeof window !== 'undefined') {
            window.removeEventListener('pointerup', onPointerUp);
            window.removeEventListener('pointercancel', onPointerUp);
        }

        if (id && !hasInnerFocusable) {
            rootCtx.unregisterFocusable(id);
        }
        if (longtapTimer) {
            clearTimeout(longtapTimer);
        }
    });
</script>

{#if href}
    <a
        bind:this={node}
        use:use
        {href}
        {target}
        {style}
        {role}
        aria-checked={isChecked}
        class="{cls} {isNativeActionAnimation ? rootCss.root__clickable : rootCss['root__clickable-no-transition']} {longTapActions?.length ? rootCss['root_disabled-context-menu'] : ''} {hasAnyActions ? rootCss['root__any-actions'] : ''}"
        on:click
        on:keydown={onKeydown}
        on:focus
        on:blur
        {...attrs}
    >
        <slot />
    </a>
{:else}
    <!-- svelte-ignore a11y-no-noninteractive-tabindex -->
    <span
        bind:this={node}
        use:use
        class="{cls}{hasJSAction ? ` ${isNativeActionAnimation ? rootCss.root__clickable : rootCss['root__clickable-no-transition']} ${rootCss.root__unselectable}` : ''} {longTapActions?.length ? rootCss['root_disabled-context-menu'] : ''}  {hasAnyActions ? rootCss['root__any-actions'] : ''}"
        {style}
        {role}
        aria-checked={isChecked}
        tabindex={hasJSAction ? 0 : null}
        on:click
        on:keydown={onKeydown}
        on:focus
        on:blur
        {...attrs}
    >
        <slot />
    </span>
{/if}
