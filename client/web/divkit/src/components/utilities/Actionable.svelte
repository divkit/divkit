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

    export let actions: MaybeMissing<Action[]> | undefined = undefined;
    export let cls = '';
    export let style: string | null = null;
    export let attrs: Record<string, string | undefined> | undefined = undefined;
    export let use: ((element: HTMLElement, opts?: any) => void) = doNothing;
    export let customAction: ((event: Event) => boolean) | null = null;
    export let hasActionAnimation = false;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const actionCtx = getContext<ActionCtxValue>(ACTION_CTX);

    setContext<ActionCtxValue>(ACTION_CTX, {
        hasAction(): boolean {
            return Boolean(actionCtx.hasAction() || actions?.length);
        }
    });

    let href = '';
    let target: string | undefined = undefined;

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
    if (href && !isBuiltinSchema(getUrlSchema(href))) {
        href = '';
        hasJSAction = true;
    }

    function onClick(event: MouseEvent): void {
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
            actions.forEach(action => {
                const actionUrl = action.url;
                if (actionUrl) {
                    const schema = getUrlSchema(actionUrl);
                    if (schema && !isBuiltinSchema(schema)) {
                        event.preventDefault();

                        if (schema === 'div-action') {
                            rootCtx.execAction(action);
                        } else if (action.log_id) {
                            rootCtx.execCustomAction(action as Action & { url: string });
                        }
                    }
                }
            });
            actions.forEach(action => {
                rootCtx.logStat('click', action);
            });
        }
    }
</script>

{#if href}
    <a
        use:use
        {href}
        {target}
        {style}
        class="{cls} {hasActionAnimation ? '' : rootCss.root__clickable}"
        on:click={onClick}
        on:click
        on:keydown
        {...attrs}
    >
        <slot />
    </a>
{:else}
    <span
        use:use
        class="{cls}{hasJSAction ? ` ${hasActionAnimation ? '' : rootCss.root__clickable} ${rootCss.root__unselectable}` : ''}"
        {style}
        role={hasJSAction ? 'button' : null}
        on:click={hasJSAction ? onClick : null}
        on:click
        on:keydown
        {...attrs}
    >
        <slot />
    </span>
{/if}
