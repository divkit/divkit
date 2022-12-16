<script lang="ts">
    import { getContext, setContext } from 'svelte';

    import rootCss from '../Root.module.css';

    import type { Action } from '../../../typings/common';
    import type { MaybeMissing } from '../../expressions/json';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { doNothing } from '../../utils/doNothing';
    import { ACTION_CTX, ActionCtxValue } from '../../context/action';
    import { wrapError } from '../../utils/wrapError';
    import { getUrlSchema, isBuiltinSchema } from '../../utils/url';
    import { Coords, getTouchCoords } from '../../utils/getTouchCoords';

    export let actions: MaybeMissing<Action[]> | undefined = undefined;
    export let doubleTapActions: MaybeMissing<Action[]> | undefined = undefined;
    export let longTapActions: MaybeMissing<Action[]> | undefined = undefined;
    export let cls = '';
    export let style: string | null = null;
    export let attrs: Record<string, string | undefined> | undefined = undefined;
    export let use: ((element: HTMLElement, opts?: any) => void) = doNothing;
    export let customAction: ((event: Event) => boolean) | null = null;
    export let isNativeActionAnimation = true;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const actionCtx = getContext<ActionCtxValue>(ACTION_CTX);

    setContext<ActionCtxValue>(ACTION_CTX, {
        hasAction(): boolean {
            return Boolean(actionCtx.hasAction() || actions?.length);
        }
    });

    const MIN_SWIPE_PX = 8;
    const MIN_LONG_TAP_DURATION = 400;

    let href = '';
    let target: string | undefined = undefined;
    let startTs = -1;
    let startCoords: Coords | null = null;
    let isChanged = false;

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
        rootCtx.logError(wrapError(new Error('Actionable element is forbidden inside other actionable element'), {
            level: 'warn',
            additional: {
                actions
            }
        }));
    }

    let hasJSAction = Boolean(customAction);
    if (href && !isBuiltinSchema(getUrlSchema(href), rootCtx.getBuiltinProtocols())) {
        href = '';
        hasJSAction = true;
    }

    async function onClick(event: MouseEvent): Promise<void> {
        if (actionCtx.hasAction()) {
            return;
        }

        if (event.button !== undefined && event.button !== 0) {
            return;
        }

        const cancelled = customAction?.(event) === false;

        if (cancelled) {
            event.preventDefault();
        } else if (actions) {
            const hasCustomAction = actions.some(action => {
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
            rootCtx.execAnyActions(actions);
        }
    }

    function onDoubleClick(event: MouseEvent): void {
        if (actionCtx.hasAction()) {
            return;
        }

        if (event.button !== undefined && event.button !== 0) {
            return;
        }

        rootCtx.execAnyActions(doubleTapActions, true);
    }

    function onTouchStart(event: TouchEvent): void {
        if (event.touches.length > 1) {
            return;
        }

        startCoords = getTouchCoords(event);
        isChanged = false;
        startTs = Date.now();
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

    function onTouchEnd(): void {
        if (!startCoords || startTs < 0) {
            return;
        }

        if (!isChanged && (Date.now() - startTs) >= MIN_LONG_TAP_DURATION) {
            rootCtx.execAnyActions(longTapActions, true);
        }

        startCoords = null;
        startTs = -1;
    }

    function onKeydown(event: KeyboardEvent): void {
        if (event.ctrlKey || event.metaKey || event.altKey || event.shiftKey) {
            return;
        }

        if (event.key === 'Enter' && Array.isArray(actions) && actions.length) {
            rootCtx.execAnyActions(actions);
            event.preventDefault();
        }
    }
</script>

{#if href}
    <a
        use:use
        {href}
        {target}
        {style}
        class="{cls} {isNativeActionAnimation ? rootCss.root__clickable : rootCss['root__clickable-no-transition']} {longTapActions?.length ? rootCss['root_disabled-context-menu'] : ''}"
        on:click={onClick}
        on:click
        on:dblclick={doubleTapActions?.length ? onDoubleClick : null}
        on:touchstart={longTapActions?.length ? onTouchStart : null}
        on:touchmove={longTapActions?.length ? onTouchMove : null}
        on:touchend={longTapActions?.length ? onTouchEnd : null}
        on:touchcancel={longTapActions?.length ? onTouchEnd : null}
        on:keydown={onKeydown}
        {...attrs}
    >
        <slot />
    </a>
{:else}
    <span
        use:use
        class="{cls}{hasJSAction ? ` ${isNativeActionAnimation ? rootCss.root__clickable : rootCss['root__clickable-no-transition']} ${rootCss.root__unselectable}` : ''} {longTapActions?.length ? rootCss['root_disabled-context-menu'] : ''}"
        {style}
        role={hasJSAction ? 'button' : null}
        tabindex={hasJSAction ? 0 : null}
        on:click={hasJSAction ? onClick : null}
        on:click
        on:dblclick={doubleTapActions?.length ? onDoubleClick : null}
        on:touchstart={longTapActions?.length ? onTouchStart : null}
        on:touchmove={longTapActions?.length ? onTouchMove : null}
        on:touchend={longTapActions?.length ? onTouchEnd : null}
        on:touchcancel={longTapActions?.length ? onTouchEnd : null}
        on:keydown={onKeydown}
        {...attrs}
    >
        <slot />
    </span>
{/if}
