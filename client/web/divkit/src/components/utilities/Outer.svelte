<script lang="ts">
    import { getContext, tick } from 'svelte';

    import css from './Outer.module.css';

    import type { DivBaseData } from '../../types/base';
    import type { Mods, Style } from '../../types/general';
    import type { ActionAnimation, AnyAnimation, DivActionableData } from '../../types/actionable';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, DivExtension, TemplateContext } from '../../../typings/common';
    import type { Visibility } from '../../types/base';
    import type { Action } from '../../../typings/common';
    import type { MaybeMissing } from '../../expressions/json';
    import type { EdgeInsets } from '../../types/edgeInserts';
    import type { CornersRadius } from '../../types/border';
    import type { WrapContentSize } from '../../types/sizes';
    import { makeStyle } from '../../utils/makeStyle';
    import { pxToEm, pxToEmWithUnits } from '../../utils/pxToEm';
    import { getBackground } from '../../utils/background';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { visibilityAction } from '../../use/visibilityAction';
    import { genClassName } from '../../utils/genClassName';
    import { devtool, DevtoolResult } from '../../use/devtool';
    import { STATE_CTX, StateCtxValue } from '../../context/state';
    import { correctEdgeInserts } from '../../utils/correctEdgeInserts';
    import { correctNonNegativeNumber } from '../../utils/correctNonNegativeNumber';
    import { correctAlpha } from '../../utils/correctAlpha';
    import { assignIfDifferent } from '../../utils/assignIfDifferent';
    import { correctColor } from '../../utils/correctColor';
    import { correctVisibility } from '../../utils/correctVisibility';
    import { wrapError } from '../../utils/wrapError';
    import { correctCSSInterpolator } from '../../utils/correctCSSInterpolator';
    import { correctNumber } from '../../utils/correctNumber';
    import { flattenAnimation } from '../../utils/flattenAnimation';
    import { correctEdgeInsertsObject } from '../../utils/correctEdgeInsertsObject';
    import { edgeInsertsToCss } from '../../utils/edgeInsertsToCss';
    import { sumEdgeInsets } from '../../utils/sumEdgeInsets';
    import { correctBorderRadiusObject } from '../../utils/correctBorderRadiusObject';
    import { borderRadius } from '../../utils/borderRadius';
    import { isNonNegativeNumber } from '../../utils/isNonNegativeNumber';
    import Actionable from './Actionable.svelte';
    import OuterBackground from './OuterBackground.svelte';
    import { Truthy } from '../../utils/truthy';

    export let json: Partial<DivBaseData & DivActionableData> = {};
    export let origJson: DivBase | undefined = undefined;
    export let templateContext: TemplateContext;
    export let cls = '';
    export let style: Style | undefined = undefined;
    export let layoutParams: LayoutParams = {};
    export let customDescription = false;
    export let customPaddings = false;
    export let customActions = '';
    export let additionalPaddings: EdgeInsets | null = null;
    export let heightByAspect = false;

    const HORIZONTAL_ALIGN_TO_GENERAL = {
        left: 'start',
        center: 'center',
        right: 'end'
    };

    const VERTICAL_ALIGN_TO_GENERAL = {
        top: 'start',
        center: 'center',
        bottom: 'end',
        baseline: 'baseline'
    };

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const stateCtx = getContext<StateCtxValue>(STATE_CTX);

    let currentNode: HTMLElement;
    let attrs: Record<string, string> | undefined;
    let extensions: DivExtension[] | null = null;

    $: jsonBorder = rootCtx.getDerivedFromVars(json.border);
    let borderStyle: Style = {};
    let borderElemStyle: Style = {};
    let hasBorder = false;
    let strokeWidth = 1;
    let strokeColor = 'transparent';
    let cornerRadius = 0;
    let cornersRadius: CornersRadius = {
        'top-left': 0,
        'top-right': 0,
        'bottom-right': 0,
        'bottom-left': 0
    };
    let backgroundRadius = '';
    $: {
        let newBorderStyle: Style = {};
        let newBorderElemStyle: Style = {};
        let newHasBorder = false;
        let newBackgroundRadius = '';

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
                cornersRadius = correctBorderRadiusObject($jsonBorder.corners_radius, cornersRadius);
                newBorderElemStyle['border-radius'] = newBorderStyle['border-radius'] = borderRadius(cornersRadius);
            } else if ($jsonBorder.corner_radius) {
                cornerRadius = correctNonNegativeNumber($jsonBorder.corner_radius, cornerRadius);
                cornersRadius = {
                    'top-left': cornerRadius,
                    'top-right': cornerRadius,
                    'bottom-right': cornerRadius,
                    'bottom-left': cornerRadius
                };
                newBorderElemStyle['border-radius'] = newBorderStyle['border-radius'] = pxToEm(cornerRadius);
            }

            // Clip browser rendering artifacts by border-radius + border-width/2
            if (newHasBorder && strokeWidth && ($jsonBorder.corners_radius || $jsonBorder.corner_radius)) {
                let radius: CornersRadius = { ...cornersRadius };

                ([
                    'top-left',
                    'top-right',
                    'bottom-right',
                    'bottom-left'
                ] as const).forEach(corner => {
                    radius[corner] = (radius[corner] || 0) + strokeWidth / 2;
                });

                newBackgroundRadius = borderRadius(radius);
            }
        }
        borderStyle = assignIfDifferent(newBorderStyle, borderStyle);
        borderElemStyle = assignIfDifferent(newBorderElemStyle, borderElemStyle);
        hasBorder = newHasBorder;
        backgroundRadius = newBackgroundRadius;
    }

    $: jsonPaddings = rootCtx.getDerivedFromVars(json.paddings);
    let selfPadding: EdgeInsets | null = null;
    $: {
        selfPadding = correctEdgeInsertsObject(($jsonPaddings && !customPaddings) ? $jsonPaddings : undefined, selfPadding);
    }

    $: padding = edgeInsertsToCss(sumEdgeInsets(selfPadding, additionalPaddings));

    $: jsonMargins = rootCtx.getDerivedFromVars(json.margins);
    let margin = '';
    $: {
        margin = correctEdgeInserts($jsonMargins, margin);
    }

    $: jsonWidth = rootCtx.getDerivedFromVars(json.width);
    $: jsonAlignmentHorizontal = rootCtx.getDerivedFromVars(json.alignment_horizontal);
    let widthMods: Mods = {};
    let width: string | undefined;
    let widthMin: string | undefined;
    let widthMax: string | undefined;
    let widthNum = 0;
    let widthFlexGrow = 0;
    let widthFlexShrink = 0;
    let widthFill = false;
    let hasWidthError = false;
    $: {
        let widthType: 'parent' | 'content' | undefined = undefined;
        let newWidth: string | undefined = undefined;
        let newWidthMin: string | undefined = undefined;
        let newWidthMax: string | undefined = undefined;
        let newWidthMods: Mods = {};
        let newFlexGrow = 0;
        let newFlexShrink = 0;
        let newWidthFill = false;
        let newWidthError = false;

        const type = $jsonWidth?.type;

        if (type === 'fixed') {
            widthNum = correctNonNegativeNumber($jsonWidth?.value, widthNum);
            newWidth = pxToEm(widthNum);
        } else if (
            type === 'wrap_content' ||
            !layoutParams.overlapParent && (type === 'match_parent' || !type) && layoutParams.parentHorizontalWrapContent
        ) {
            widthType = 'content';
            if (
                type === 'wrap_content' && $jsonWidth?.constrained ||
                (type === 'match_parent' || !type) && layoutParams.parentHorizontalWrapContent
            ) {
                newWidthMods['width-constrained'] = true;
                if (layoutParams.parentContainerOrientation === 'horizontal') {
                    newFlexShrink = 1;
                }
            }

            if (type === 'wrap_content') {
                const width = $jsonWidth as WrapContentSize;
                if (width.min_size && isNonNegativeNumber(width.min_size.value)) {
                    newWidthMin = pxToEm(width.min_size.value);
                }
                if (width.max_size && isNonNegativeNumber(width.max_size.value)) {
                    newWidthMax = pxToEm(width.max_size.value);
                }
            }

            if (type === 'match_parent' || !type) {
                rootCtx.logError(wrapError(new Error('Cannot place child with match_parent size inside wrap_content'), {
                    level: 'warn'
                }));
            }
        } else {
            widthType = 'parent';
            if (layoutParams.parentContainerOrientation === 'vertical' && layoutParams.parentContainerWrap) {
                newWidthError = true;
                rootCtx.logError(wrapError(new Error('Cannot place a match_parent items on the cross-axis of wrap'), {
                    level: 'error'
                }));
            }
            if (layoutParams.parentLayoutOrientation === 'vertical') {
                newWidth = `calc(100% - ${pxToEmWithUnits(($jsonMargins?.left || 0) + ($jsonMargins?.right || 0))})`;
            } else if (layoutParams.parentContainerOrientation === 'horizontal') {
                newFlexGrow = $jsonWidth && 'weight' in $jsonWidth && $jsonWidth.weight || 1;
                if (layoutParams.parentContainerWrap) {
                    newWidthFill = true;
                }
            }
        }

        if (widthType === 'parent') {
            newWidthMods['halign-self'] = 'stretch';
        } else {
            const align = $jsonAlignmentHorizontal;
            if (align === 'left' || align === 'center' || align === 'right') {
                newWidthMods['halign-self'] = HORIZONTAL_ALIGN_TO_GENERAL[align];
            } else {
                newWidthMods['halign-self'] = layoutParams.parentHAlign || 'start';
            }
        }

        if (widthType) {
            newWidthMods.width = widthType;
        }

        width = newWidth;
        widthMin = newWidthMin;
        widthMax = newWidthMax;
        widthFlexGrow = newFlexGrow;
        widthFlexShrink = newFlexShrink;
        widthMods = assignIfDifferent(newWidthMods, widthMods);
        widthFill = newWidthFill;
        hasWidthError = newWidthError;
    }

    $: jsonHeight = rootCtx.getDerivedFromVars(json.height);
    $: jsonAlignmentVertical = rootCtx.getDerivedFromVars(json.alignment_vertical);
    let heightMods: Mods = {};
    let height: string | undefined;
    let heightMin: string | undefined;
    let heightMax: string | undefined;
    let heightNum = 0;
    let heightFlexGrow = 0;
    let heightFlexShrink = 0;
    let heightFill = false;
    let hasHeightError = false;
    $: {
        let heightType: 'parent' | 'content' | undefined = undefined;
        let newHeight: string | undefined = undefined;
        let newHeightMin: string | undefined = undefined;
        let newHeightMax: string | undefined = undefined;
        let newHeightMods: Mods = {};
        let newFlexGrow = 0;
        let newFlexShrink = 0;
        let newHeightFill = false;
        let newHeightError = false;

        const type = $jsonHeight?.type;

        if (heightByAspect) {
            // auto height
            // no special css needed, so no special heightType
        } else if (type === 'fixed') {
            heightNum = correctNonNegativeNumber($jsonHeight?.value, heightNum);
            newHeight = pxToEm(heightNum);
        } else if (type === 'match_parent' && (layoutParams.overlapParent || !layoutParams.parentVerticalWrapContent)) {
            heightType = 'parent';
            if (layoutParams.parentContainerOrientation === 'horizontal' && layoutParams.parentContainerWrap) {
                newHeightError = true;
                rootCtx.logError(wrapError(new Error('Cannot place a match_parent items on the cross-axis of wrap'), {
                    level: 'error'
                }));
            }
            if (layoutParams.parentLayoutOrientation === 'horizontal') {
                newHeight = `calc(100% - ${pxToEmWithUnits(($jsonMargins?.top || 0) + ($jsonMargins?.bottom || 0))})`;
            } else if (layoutParams.parentContainerOrientation === 'vertical') {
                newFlexGrow = $jsonHeight?.weight || 1;
                if (layoutParams.parentContainerWrap) {
                    newHeightFill = true;
                }
            }
        } else {
            heightType = 'content';
            if (
                type === 'wrap_content' && $jsonHeight?.constrained ||
                type === 'match_parent' && layoutParams.parentVerticalWrapContent
            ) {
                newHeightMods['height-constrained'] = true;
                if (layoutParams.parentContainerOrientation === 'vertical') {
                    newFlexShrink = 1;
                }
            }

            if (type === 'wrap_content') {
                const height = $jsonHeight as WrapContentSize;
                if (height?.min_size && isNonNegativeNumber(height.min_size.value)) {
                    newHeightMin = pxToEm(height.min_size.value);
                }
                if (height?.max_size && isNonNegativeNumber(height.max_size.value)) {
                    newHeightMax = pxToEm(height.max_size.value);
                }
            }

            if (type === 'match_parent') {
                rootCtx.logError(wrapError(new Error('Cannot place child with match_parent size inside wrap_content'), {
                    level: 'warn'
                }));
            }
        }

        if (heightType === 'parent') {
            newHeightMods['valign-self'] = 'stretch';
        } else {
            const align = $jsonAlignmentVertical;
            if (
                align === 'top' ||
                align === 'center' ||
                align === 'bottom' ||
                align === 'baseline' && layoutParams.parentContainerOrientation === 'horizontal'
            ) {
                newHeightMods['valign-self'] = VERTICAL_ALIGN_TO_GENERAL[align];
            } else {
                newHeightMods['valign-self'] = layoutParams.parentVAlign || 'start';
            }
        }

        if (heightType) {
            newHeightMods.height = heightType;
        }

        height = newHeight;
        heightMin = newHeightMin;
        heightMax = newHeightMax;
        heightFlexGrow = newFlexGrow;
        heightFlexShrink = newFlexShrink;
        heightMods = assignIfDifferent(newHeightMods, heightMods);
        heightFill = newHeightFill;
        hasHeightError = newHeightError;
    }

    $: parentOverlapMod = layoutParams.overlapParent ? true : undefined;

    $: parentOverlapAbsoluteMod = layoutParams.overlapParent &&
        (!$jsonWidth || $jsonWidth.type === 'match_parent') &&
        $jsonHeight?.type === 'match_parent';

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
    let hasSeparateBg: boolean;
    $: {
        backgroundStyle = {};
        hasSeparateBg = false;
        if ($jsonBackground) {
            hasSeparateBg =
                $jsonBackground.some(it => it.type === 'image' || it.type === 'nine_patch_image') ||
                Boolean(backgroundRadius);

            if (!hasSeparateBg) {
                const res = getBackground($jsonBackground);
                backgroundStyle['background-color'] = res.color;
                backgroundStyle['background-image'] = res.image;
                backgroundStyle['background-size'] = res.size;
                backgroundStyle['background-position'] = res.position;
                backgroundStyle['background-repeat'] = 'no-repeat';
            }
        }
    }

    const jsonTransitionTriggers = layoutParams.fakeElement ?
        [] :
        (json.transition_triggers || ['state_change', 'visibility_change']);
    const hasStateChangeTrigger = jsonTransitionTriggers.indexOf('state_change') !== -1 && json.id;
    const hasVisibilityChangeTrigger = jsonTransitionTriggers.indexOf('visibility_change') !== -1 && json.id;

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
    $: jsonDoubleTapActions = rootCtx.getDerivedFromVars(json.doubletap_actions);
    $: jsonLongTapActions = rootCtx.getDerivedFromVars(json.longtap_actions);
    let actions: MaybeMissing<Action>[] = [];
    let doubleTapActions: MaybeMissing<Action>[] = [];
    let longTapActions: MaybeMissing<Action>[] = [];
    $: {
        let newActions = $jsonActions || $jsonAction && [$jsonAction] || [];
        let newDoubleTapActions = $jsonDoubleTapActions || [];
        let newLongTapActions = $jsonLongTapActions || [];

        if (layoutParams.fakeElement) {
            newActions = [];
            newDoubleTapActions = [];
            newLongTapActions = [];
        } else {
            if (!Array.isArray(newActions)) {
                newActions = [];
                rootCtx.logError(wrapError(new Error('Actions should be array')));
            }
            if (!Array.isArray(newDoubleTapActions)) {
                newDoubleTapActions = [];
                rootCtx.logError(wrapError(new Error('DoubleTapActions should be array')));
            }
            if (!Array.isArray(newLongTapActions)) {
                newLongTapActions = [];
                rootCtx.logError(wrapError(new Error('LongTapActions should be array')));
            }
        }

        if ((newActions.length || newDoubleTapActions.length || newLongTapActions.length) && customActions) {
            newActions = [];
            newDoubleTapActions = [];
            newLongTapActions = [];
            rootCtx.logError(wrapError(new Error(`Cannot use action on component "${customActions}"`)));
        }

        // todo check parent actions with customActions
        actions = newActions;
        doubleTapActions = newDoubleTapActions;
        longTapActions = newLongTapActions;
    }

    $: jsonActionAnimation = rootCtx.getDerivedFromVars(json.action_animation);
    let actionAnimationList: AnyAnimation[] = [];
    let actionAnimationTransition = '';
    let animationOpacityStart: number | undefined = undefined;
    let animationOpacityEnd: number | undefined = undefined;
    let animationScaleStart: number | undefined = undefined;
    let animationScaleEnd: number | undefined = undefined;
    $: {
        if ($jsonActionAnimation) {
            actionAnimationList = flattenAnimation($jsonActionAnimation as ActionAnimation);
            actionAnimationTransition = actionAnimationList.map(parseActionAnimation).filter(Boolean).join(', ');
        }
    }

    function hasNativeAnimation(list: AnyAnimation[]) {
        return list.some(it => it.name === 'native');
    }

    function parseActionAnimation(animation: MaybeMissing<AnyAnimation>): string {
        const startValue = correctNumber(animation.start_value, 1);
        const endValue = correctNumber(animation.end_value, 1);
        const delay = correctNonNegativeNumber(animation.start_delay, 0);
        const duration = correctNonNegativeNumber(animation.duration, 300);
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
            case 'native':
            case 'no_animation':
                return '';
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
        'parent-overlap-absolute': parentOverlapAbsoluteMod,
        'scroll-snap': layoutParams.scrollSnap,
        'hide-on-transition-in': stateChangingInProgress ||
            visibilityChangingInProgress ||
            transitionChangeInProgress,
        visibility,
        'has-action-animation': Boolean(actionAnimationTransition),
        'parent-flex': layoutParams.parentContainerOrientation || undefined,
        'parent-grid': Boolean(layoutParams.gridArea) || undefined
    };

    $: jsonTransform = rootCtx.getDerivedFromVars(json.transform);
    let pivotXNum = 0;
    let pivotYNum = 0;
    let transformOrigin: string | undefined;
    let transform: string | undefined;
    $: {
        if ($jsonTransform && $jsonTransform.rotation !== undefined) {
            const pivotX = $jsonTransform.pivot_x || {
                type: 'pivot-percentage',
                value: 50
            };
            pivotXNum = correctNumber(pivotX.value, pivotXNum);
            const pivotXCSSValue = pivotX.type === 'pivot-fixed' ? pxToEm(pivotXNum) : `${pivotXNum}%`;
            const pivotY = $jsonTransform.pivot_y || {
                type: 'pivot-percentage',
                value: 50
            };
            pivotYNum = correctNumber(pivotY.value, pivotYNum);
            const pivotYCSSValue = pivotY.type === 'pivot-fixed' ? pxToEm(pivotYNum) : `${pivotYNum}%`;
            transformOrigin = `${pivotXCSSValue} ${pivotYCSSValue}`;
            transform = `rotate(${$jsonTransform.rotation}deg)`;
        }
    }

    // eslint-disable-next-line no-nested-ternary
    $: flexBasis = (widthFill || heightFill) ?
        '100%' :
        ((widthFlexGrow || heightFlexGrow) ? 0 : undefined);

    $: stl = {
        ...style,
        ...backgroundStyle,
        ...borderStyle,
        width,
        'min-width': widthMin,
        'max-width': widthMax,
        height,
        'min-height': heightMin,
        // input max-height
        'max-height': heightMax || style?.['max-height'],
        'grid-area': gridArea,
        padding,
        margin,
        opacity,
        transition: actionAnimationTransition,
        'transform-origin': transformOrigin,
        transform,
        'flex-grow': widthFlexGrow || heightFlexGrow || undefined,
        'flex-shrink': (widthFlexShrink || heightFlexShrink) ? 1 : undefined,
        'flex-basis': flexBasis,
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

        const visibilityActions = layoutParams.fakeElement ?
            [] :
            (
                json.visibility_actions ||
                json.visibility_action && [json.visibility_action]
            );

        if (Array.isArray(visibilityActions) && visibilityActions.length) {
            visibilityAction(node, {
                visibilityActions,
                rootCtx
            });
        }

        const id = json.id;
        if (id) {
            stateCtx.registerChild(id);
        }

        let dev: DevtoolResult | null = null;

        if (devtool && !layoutParams.fakeElement) {
            dev = devtool(node, {
                json,
                origJson,
                rootCtx,
                templateContext
            });
        }

        if (Array.isArray(json.extensions)) {
            const ctx = rootCtx.getExtensionContext();
            extensions = json.extensions.map(it => {
                const instance = rootCtx.getExtension(it.id, it.params);

                if (instance) {
                    instance.mountView?.(node, ctx);
                }

                return instance;
            }).filter(Truthy);
        }

        return {
            destroy() {
                if (id) {
                    stateCtx.unregisterChild(id);
                }
                if (extensions) {
                    const ctx = rootCtx.getExtensionContext();
                    extensions.forEach(it => {
                        it.unmountView?.(node, ctx);
                    });
                    extensions = null;
                }
                if (dev) {
                    dev.destroy();
                }
            }
        };
    }
</script>

{#if !hasWidthError && !hasHeightError}
    <Actionable
        use={useAction}
        cls="{cls} {genClassName('outer', css, mods)}"
        style={makeStyle(stl)}
        {actions}
        {doubleTapActions}
        {longTapActions}
        {attrs}
        isNativeActionAnimation={!actionAnimationList.length || hasNativeAnimation(actionAnimationList)}
    >
        {#if hasSeparateBg}<OuterBackground background={$jsonBackground} radius={backgroundRadius} />{/if}<slot />{#if hasBorder}<span class={css.outer__border} style={makeStyle(borderElemStyle)}></span>{/if}
    </Actionable>
{/if}
