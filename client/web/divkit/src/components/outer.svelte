<script lang="ts">
    import { getContext, tick } from 'svelte';

    import css from './outer.module.css';

    import type { DivBaseData } from '../types/base';
    import type { Mods, Style } from '../types/general';
    import type { ActionAnimation, DivActionableData } from '../types/actionable';
    import type { LayoutParams } from '../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../typings/common';
    import type { Visibility } from '../types/base';
    import type { Action } from '../../typings/common';
    import type { MaybeMissing } from '../expressions/json';
    import { makeStyle } from '../utils/makeStyle';
    import { pxToEm, pxToEmWithUnits } from '../utils/pxToEm';
    import { getBackground } from '../utils/background';
    import { ROOT_CTX, RootCtxValue } from '../context/root';
    import { visibilityAction } from '../use/visibilityAction';
    import { genClassName } from '../utils/genClassName';
    import { devtool } from '../use/devtool';
    import { STATE_CTX, StateCtxValue } from '../context/state';
    import { correctBorderRadius } from '../utils/correctBorderRadius';
    import { correctEdgeInserts } from '../utils/correctEdgeInserts';
    import { correctNonNegativeNumber } from '../utils/correctNonNegativeNumber';
    import { correctAlpha } from '../utils/correctAlpha';
    import { assignIfDifferent } from '../utils/assignIfDifferent';
    import { correctColor } from '../utils/correctColor';
    import { correctVisibility } from '../utils/correctVisibility';
    import { wrapError } from '../utils/wrapError';
    import Actionable from './actionable.svelte';
    import OuterBackground from './outerBackground.svelte';
    import { correctCSSInterpolator } from '../utils/correctCSSInterpolator';

    export let json: Partial<DivBaseData & DivActionableData> = {};
    export let origJson: DivBase | undefined = undefined;
    export let templateContext: TemplateContext;
    export let cls = '';
    export let style: Style | undefined = undefined;
    export let layoutParams: LayoutParams = {};
    export let customDescription = false;
    export let customPaddings = false;
    export let customActions = '';
    export let forceWidth = false;
    export let forceHeight = false;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const stateCtx = getContext<StateCtxValue>(STATE_CTX);

    let currentNode: HTMLElement;
    let attrs: Record<string, string> | undefined;

    $: jsonBorder = rootCtx.getDerivedFromVars(json.border);
    let borderStyle: Style = {};
    let borderElemStyle: Style = {};
    let hasBorder = false;
    let strokeWidth = 1;
    let strokeColor = 'transparent';
    let cornerRadius = 0;
    let cornersRadius = '';
    $: {
        let newBorderStyle: Style = {};
        let newBorderElemStyle: Style = {};
        let newHasBorder = false;
        if ($jsonBorder) {
            if ($jsonBorder.has_shadow) {
                const shadow = $jsonBorder.shadow;
                if (shadow) {
                    newBorderStyle['box-shadow'] =
                        pxToEm(shadow.offset?.x?.value || 0) + ' ' +
                        pxToEm(shadow.offset?.y?.value || 0) + ' ' +
                        pxToEm(shadow.blur ?? 2) + ' ' +
                        correctColor(shadow.color || '#000000', shadow.alpha ?? 0.19);
                } else {
                    newBorderStyle['box-shadow'] = '0 1px 2px 0 rgba(0,0,0,.18), 0 0 0 1px rgba(0,0,0,.07)';
                }
            }
            if ($jsonBorder.stroke) {
                newHasBorder = true;
                strokeWidth = correctNonNegativeNumber($jsonBorder.stroke.width, strokeWidth);
                strokeColor = correctColor($jsonBorder.stroke.color, 1, strokeColor);
                newBorderElemStyle.border = `${pxToEm(strokeWidth)} solid ${strokeColor}`;
            }
            if ($jsonBorder.corners_radius) {
                cornersRadius = correctBorderRadius($jsonBorder.corners_radius, 0, 10, cornersRadius);
                newBorderElemStyle['border-radius'] = newBorderStyle['border-radius'] = cornersRadius;
            } else if ($jsonBorder.corner_radius) {
                cornerRadius = correctNonNegativeNumber($jsonBorder.corner_radius, cornerRadius);
                newBorderElemStyle['border-radius'] = newBorderStyle['border-radius'] = pxToEm(cornerRadius);
            }
        }
        borderStyle = assignIfDifferent(newBorderStyle, borderStyle);
        borderElemStyle = assignIfDifferent(newBorderElemStyle, borderElemStyle);
        hasBorder = newHasBorder;
    }

    $: jsonPaddings = rootCtx.getDerivedFromVars(json.paddings);
    let padding = '';
    $: {
        padding = correctEdgeInserts(($jsonPaddings && !customPaddings) ? $jsonPaddings : undefined, padding);
    }

    $: jsonMargins = rootCtx.getDerivedFromVars(json.margins);
    let margin = '';
    $: {
        margin = correctEdgeInserts($jsonMargins, margin);
    }

    $: jsonWidth = rootCtx.getDerivedFromVars(json.width);
    $: jsonAlignmentHorizontal = rootCtx.getDerivedFromVars(json.alignment_horizontal);
    let widthMods: Mods = {};
    let width: string | undefined;
    let widthNum = 0;
    let hAlignSelf = 'default';
    $: {
        let widthType: 'parent' | 'content' | undefined = undefined;
        let newWidth: string | undefined = undefined;
        let newWidthMods: Mods = {};
        if ($jsonWidth) {
            if ($jsonWidth.type === 'fixed') {
                widthNum = correctNonNegativeNumber($jsonWidth.value, widthNum);
                newWidth = pxToEm(widthNum);
            } else if ($jsonWidth.type === 'match_parent') {
                widthType = 'parent';
                if (layoutParams.parentLayoutOrientation === 'vertical') {
                    newWidth = `calc(100% - ${pxToEmWithUnits(($jsonMargins?.left || 0) + ($jsonMargins?.right || 0))})`;
                }
            } else if ($jsonWidth.type === 'wrap_content') {
                widthType = 'content';
                if ($jsonWidth.constrained) {
                    newWidthMods['width-constrained'] = true;
                }
            }
        } else {
            widthType = 'parent';
        }

        if (widthType === 'content' && forceWidth) {
            widthType = 'parent';
            rootCtx.logError(wrapError(new Error("Incorrect usage of width with value 'wrap_content'"), {
                level: 'warn'
            }));
        }

        if (widthType) {
            newWidthMods.width = widthType;
        }
        const newHalignSelf = $jsonAlignmentHorizontal;
        if (newHalignSelf === 'left' || newHalignSelf === 'center' || newHalignSelf === 'right') {
            newWidthMods['halign-self'] = hAlignSelf = newHalignSelf;
        } else {
            newWidthMods['halign-self'] = hAlignSelf;
            if (widthType !== 'parent' && layoutParams.parentHAlign) {
                newWidthMods['parent-halign'] = layoutParams.parentHAlign;
            }
        }
        width = newWidth;
        widthMods = assignIfDifferent(newWidthMods, widthMods);
    }

    $: jsonHeight = rootCtx.getDerivedFromVars(json.height);
    $: jsonAlignmentVertical = rootCtx.getDerivedFromVars(json.alignment_vertical);
    let heightMods: Mods = {};
    let height: string | undefined;
    let heightNum = 0;
    let vAlignSelf = 'default';
    $: {
        let heightType: 'parent' | 'content' | undefined = undefined;
        let newHeight: string | undefined = undefined;
        let newHeightMods: Mods = {};
        if ($jsonHeight) {
            if ($jsonHeight.type === 'fixed') {
                heightNum = correctNonNegativeNumber($jsonHeight.value, heightNum);
                newHeight = pxToEm(heightNum);
            } else if ($jsonHeight.type === 'match_parent') {
                heightType = 'parent';
                if (layoutParams.parentLayoutOrientation === 'horizontal') {
                    newHeight = `calc(100% - ${pxToEmWithUnits(($jsonMargins?.top || 0) + ($jsonMargins?.bottom || 0))})`;
                }
            } else if ($jsonHeight.type === 'wrap_content') {
                heightType = 'content';
                if ($jsonHeight.constrained) {
                    newHeightMods['height-constrained'] = true;
                }
            }
        } else {
            heightType = 'content';
        }

        if (heightType === 'content' && forceHeight) {
            heightType = 'parent';
            rootCtx.logError(wrapError(new Error("Incorrect usage of height with value 'wrap_content'"), {
                level: 'warn'
            }));
        }

        if (heightType) {
            newHeightMods.height = heightType;
        }
        const newValignSelf = $jsonAlignmentVertical;
        if (newValignSelf === 'top' || newValignSelf === 'center' || newValignSelf === 'bottom') {
            newHeightMods['valign-self'] = vAlignSelf = newValignSelf;
        } else {
            newHeightMods['valign-self'] = vAlignSelf;
            if (heightType !== 'parent' && layoutParams.parentVAlign) {
                newHeightMods['parent-valign'] = layoutParams.parentVAlign;
            }
        }
        height = newHeight;
        heightMods = assignIfDifferent(newHeightMods, heightMods);
    }

    $: parentOverlapMod = layoutParams.overlapParent ? true : undefined;

    $: gridArea = layoutParams.gridArea ?
        `${layoutParams.gridArea.y + 1}/${layoutParams.gridArea.x + 1}/span ${layoutParams.gridArea.rowSpan}/span ${layoutParams.gridArea.colSpan}` :
        undefined;

    $: jsonAlpha = rootCtx.getDerivedFromVars(json.alpha);
    let alpha = 1;
    let opacity: number | undefined;
    $: {
        alpha = correctAlpha($jsonAlpha, alpha);
        opacity = alpha === 1 ? undefined : alpha;
    }

    $: jsonAccessibility = rootCtx.getDerivedFromVars(json.accessibility);
    $: {
        attrs = undefined;
        if ($jsonAccessibility && !customDescription && $jsonAccessibility.description) {
            attrs = {};
            attrs['aria-label'] = $jsonAccessibility.description;
        }
    }

    $: jsonBackground = rootCtx.getDerivedFromVars(json.background);
    let backgroundStyle: Style;
    let hasImagesBg: boolean;
    $: {
        backgroundStyle = {};
        hasImagesBg = false;
        if ($jsonBackground) {
            hasImagesBg = $jsonBackground.some(it => it.type === 'image');
            if (!hasImagesBg) {
                const res = getBackground($jsonBackground);
                backgroundStyle['background-color'] = res.color;
                backgroundStyle['background-image'] = res.image;
                backgroundStyle['background-size'] = res.size;
                backgroundStyle['background-position'] = res.position;
                backgroundStyle['background-repeat'] = 'no-repeat';
            }
        }
    }

    const jsonTransitionTriggers = json.transition_triggers || ['state_change', 'visibility_change'];
    const hasStateChangeTrigger = jsonTransitionTriggers.indexOf('state_change') !== -1;
    const hasVisibilityChangeTrigger = jsonTransitionTriggers.indexOf('visibility_change') !== -1;

    let stateChangingInProgress: boolean | undefined;
    let visibilityChangingInProgress: boolean | undefined;
    let transitionChangeInProgress: boolean | undefined;
    $: {
        stateChangingInProgress = undefined;
        if (hasStateChangeTrigger && json.transition_in && rootCtx.isRunning('stateChange')) {
            stateChangingInProgress = true;
        }
    }
    $: {
        transitionChangeInProgress = undefined;
        if (
            hasStateChangeTrigger && json.transition_change &&
            rootCtx.isRunning('stateChange') && stateCtx.hasTransitionChange(json.id)
        ) {
            transitionChangeInProgress = true;
        }
    }

    $: jsonAction = rootCtx.getDerivedFromVars(json.action);
    $: jsonActions = rootCtx.getDerivedFromVars(json.actions);
    let actions: MaybeMissing<Action>[] = [];
    $: {
        let newActions = $jsonActions || $jsonAction && [$jsonAction] || [];
        if (!Array.isArray(newActions)) {
            newActions = [];
            rootCtx.logError(wrapError(new Error('Actions should be array')));
        } else if (newActions.length && customActions) {
            newActions = [];
            rootCtx.logError(wrapError(new Error(`Cannot use action on component "${customActions}"`)));
        }
        // todo check parent actions with customActions
        actions = newActions;
    }

    $: jsonActionAnimation = rootCtx.getDerivedFromVars(json.action_animation);
    let actionAnimationTransition = '';
    let animationOpacityStart = 1;
    let animationOpacityEnd = 1;
    let animationScaleStart = 1;
    let animationScaleEnd = 1;
    $: {
        if ($jsonActionAnimation) {
            actionAnimationTransition = parseActionAnimation($jsonActionAnimation);
        }
    }

    function parseActionAnimation(animation: MaybeMissing<ActionAnimation>): string {
        const startValue = animation.start_value || 1;
        const endValue = animation.end_value || 1;
        const delay = animation.start_delay || 0;
        const duration = animation.duration || 300;
        const interpolator = correctCSSInterpolator(animation.interpolator, 'ease_in_out').replace(/_/g, '-');

        switch (animation.name) {
            case 'fade':
                animationOpacityStart = startValue;
                animationOpacityEnd = endValue;
                return `opacity ${duration}ms ${interpolator} ${delay}ms`;
            case 'scale':
                animationScaleStart = startValue;
                animationScaleEnd = endValue;
                return `transform ${duration}ms ${interpolator} ${delay}ms`;
            case 'set':
                if (!animation.items?.length) {
                    rootCtx.logError(wrapError(new Error("Empty items value for action_animation with name='set'")));
                    return '';
                }

                return animation.items.map(parseActionAnimation).join(', ');
            default:
                rootCtx.logError(wrapError(new Error('Unknown action_animation name'), {
                    additional: {
                        animation: animation.name
                    }
                }));
                return '';
        }
    }

    let isVisibilityInited = false;
    let visibility: Visibility = 'visible';
    $: jsonVisibility = rootCtx.getDerivedFromVars(json.visibility);
    $: {
        const prevVisibility = visibility;
        const nextVisibility = correctVisibility($jsonVisibility, visibility);

        if (prevVisibility !== nextVisibility) {
            if (isVisibilityInited && (visibility === 'visible' || nextVisibility === 'visible')) {
                onVisibilityChange(nextVisibility);
            } else {
                visibility = nextVisibility;
            }
        }

        if (!isVisibilityInited) {
            isVisibilityInited = true;
        }
    }

    async function onVisibilityChange(nextVisibility: Visibility) {
        visibility = nextVisibility;

        const direction = nextVisibility === 'visible' ? 'in' : 'out';
        const transition = direction === 'in' ? json.transition_in : json.transition_out;

        if (
            hasVisibilityChangeTrigger &&
            transition
        ) {
            await tick();

            if (direction === 'in') {
                visibilityChangingInProgress = true;
            }
            stateCtx.runVisibilityTransition(
                {
                    ...json,
                    visibility: 'visible'
                } as DivBaseData,
                templateContext,
                transition,
                currentNode,
                direction
            ).then(() => {
                if (direction === 'in') {
                    visibilityChangingInProgress = false;
                }
            }).catch(e => {
                if (direction === 'in') {
                    visibilityChangingInProgress = false;
                }
                throw e;
            });
        }
    }

    $: mods = {
        ...widthMods,
        ...heightMods,
        'parent-overlap': parentOverlapMod,
        'scroll-snap': layoutParams.scrollSnap,
        'hide-on-transition-in': stateChangingInProgress ||
            visibilityChangingInProgress ||
            transitionChangeInProgress,
        visibility,
        'has-action-animation': Boolean(actionAnimationTransition)
    };

    $: stl = {
        ...style,
        ...backgroundStyle,
        ...borderStyle,
        width,
        height,
        'grid-area': gridArea,
        padding,
        margin,
        opacity,
        transition: actionAnimationTransition,
        '--divkit-animation-opacity-start': animationOpacityStart,
        '--divkit-animation-opacity-end': animationOpacityEnd,
        '--divkit-animation-scale-start': animationScaleStart,
        '--divkit-animation-scale-end': animationScaleEnd
    };

    function useAction(node: HTMLElement) {
        currentNode = node;
        if (hasStateChangeTrigger && json.transition_in) {
            stateCtx.registerChildWithTransitionIn(
                json as DivBaseData,
                templateContext,
                json.transition_in,
                node
            ).then(() => {
                stateChangingInProgress = false;
            }).catch(e => {
                stateChangingInProgress = false;
                throw e;
            });
        }
        if (hasStateChangeTrigger && json.transition_out) {
            stateCtx.registerChildWithTransitionOut(
                json as DivBaseData,
                templateContext,
                json.transition_out,
                node
            );
        }
        if (hasStateChangeTrigger && json.transition_change) {
            stateCtx.registerChildWithTransitionChange(
                json as DivBaseData,
                templateContext,
                json.transition_change,
                node
            ).then(() => {
                transitionChangeInProgress = false;
            }).catch(e => {
                transitionChangeInProgress = false;
                throw e;
            });
        }

        const visibilityActions = rootCtx.getJsonWithVars(
            json.visibility_actions ||
            json.visibility_action && [json.visibility_action]
        );
        visibilityAction(node, {
            visibilityActions,
            rootCtx
        });

        if (devtool) {
            const dev = devtool(node, {
                json,
                origJson,
                rootCtx,
                templateContext
            });

            return {
                destroy() {
                    dev.destroy();
                }
            };
        }
    }
</script>

<Actionable
    use={useAction}
    cls="{cls} {genClassName('outer', css, mods)}"
    style={makeStyle(stl)}
    {actions}
    {attrs}
    hasActionAnimation={Boolean(actionAnimationTransition)}
>
    {#if hasImagesBg}<OuterBackground background={$jsonBackground} />{/if}<slot />{#if hasBorder}<span class={css.outer__border} style={makeStyle(borderElemStyle)}></span>{/if}
</Actionable>
