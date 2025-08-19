<script lang="ts">
    import { getContext, onDestroy } from 'svelte';

    import css from './Switch.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivSwitchData } from '../../types/switch';
    import type { ComponentContext } from '../../types/componentContext';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { ACTION_CTX, type ActionCtxValue } from '../../context/action';
    import { genClassName } from '../../utils/genClassName';
    import { wrapError } from '../../utils/wrapError';
    import { correctColor, parseColor, stringifyColorToCss } from '../../utils/correctColor';
    import { createVariable } from '../../expressions/variable';
    import { correctBooleanInt } from '../../utils/correctBooleanInt';
    import { booleanInt } from '../../utils/booleanInt';
    import { composeAccessibilityDescription } from '../../utils/composeAccessibilityDescription';
    import Outer from '../utilities/Outer.svelte';
    import DevtoolHolder from '../utilities/DevtoolHolder.svelte';

    export let componentContext: ComponentContext<DivSwitchData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const actionCtx = getContext<ActionCtxValue>(ACTION_CTX);
    const direction = rootCtx.direction;

    let prevId: string | undefined;
    let input: HTMLInputElement;
    let value = false;
    let hasError = false;
    let description = '';
    let isEnabled = true;
    let onColor = '#129386';
    let onSubColor = '#1293864c';

    $: origJson = componentContext.origJson;

    function rebind(): void {
        isEnabled = true;
        onColor = '#129386';
        onSubColor = '#1293864c';
    }

    $: if (origJson) {
        rebind();
    }

    $: variable = componentContext.json.is_on_variable;

    $: valueVariable = variable && (componentContext.getVariable(variable, 'boolean') || rootCtx.awaitGlobalVariable(variable, 'boolean', false)) || createVariable('temp', 'boolean', false);

    $: jsonAccessibility = componentContext.getDerivedFromVars(componentContext.json.accessibility);
    $: jsonIsEnabled = componentContext.getDerivedFromVars(componentContext.json.is_enabled);
    $: jsonOnColor = componentContext.getDerivedFromVars(componentContext.json.on_color);

    $: {
        let newHasError = false;

        if (!variable) {
            newHasError = true;
            componentContext.logError(wrapError(new Error('Missing "is_on_variable" in "switch"')));
        } else if (actionCtx.hasAction() || $jsonAccessibility?.mode === 'exclude') {
            newHasError = true;
            componentContext.logError(wrapError(new Error('Cannot show "switch" inside component with an action or inside accessibility mode=exclude')));
        }

        if (hasError !== newHasError) {
            hasError = newHasError;
        }
    }

    $: if (booleanInt(value) !== booleanInt($valueVariable)) {
        value = booleanInt($valueVariable);
    }

    $: {
        isEnabled = correctBooleanInt($jsonIsEnabled, isEnabled);
    }

    $: {
        onColor = correctColor($jsonOnColor, 1, onColor);

        if (typeof $jsonOnColor === 'string') {
            const parsed = parseColor($jsonOnColor);
            if (parsed) {
                parsed.a *= .3;
                onSubColor = stringifyColorToCss(parsed);
            }
        }
    }

    $: if ($jsonAccessibility?.description) {
        description = composeAccessibilityDescription($jsonAccessibility);
    } else {
        componentContext.logError(wrapError(new Error('Missing accessibility "description" for switch'), {
            level: 'warn'
        }));
    }

    $: mods = {
        disabled: !isEnabled,
        direction: $direction
    };
    $: stl = {
        '--divkit-switch-on-color': onColor,
        '--divkit-switch-on-sub-color': onSubColor
    };

    function onInput(event: Event): void {
        value = (event.target as HTMLInputElement).checked;
        valueVariable.setValue(value);
    }

    $: if (input && componentContext.json) {
        if (prevId) {
            rootCtx.unregisterFocusable(prevId);
            prevId = undefined;
        }

        if (componentContext.id && !componentContext.fakeElement) {
            prevId = componentContext.id;
            rootCtx.registerFocusable(prevId, {
                focus() {
                    if (input) {
                        input.focus();
                    }
                }
            });
        }
    }

    onDestroy(() => {
        if (prevId) {
            rootCtx.unregisterFocusable(prevId);
            prevId = undefined;
        }
    });
</script>

{#if !hasError}
    <Outer
        let:focusHandler
        let:blurHandler
        let:hasCustomFocus
        cls={genClassName('switch', css, mods)}
        style={stl}
        customDescription={true}
        customActions={'switch'}
        hasInnerFocusable={true}
        {componentContext}
        {layoutParams}
    >
        <div
            class={genClassName('switch__tumbler', css, { checked: value })}
        >
            <div class={css.switch__thumb}></div>
        </div>
        <input
            bind:this={input}
            type="checkbox"
            class={genClassName('switch__input', css, { 'has-custom-focus': hasCustomFocus })}
            autocomplete="off"
            aria-label={description}
            disabled={!isEnabled}
            checked={value}
            on:input={onInput}
            on:focus={focusHandler}
            on:blur={blurHandler}
        >
    </Outer>
{:else if process.env.DEVTOOL}
    <DevtoolHolder
        {componentContext}
    />
{/if}
