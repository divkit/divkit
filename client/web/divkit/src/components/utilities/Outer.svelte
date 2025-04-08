<script lang="ts" context="module">
    const HORIZONTAL_ALIGN_TO_GENERAL_LTR = {
        left: 'start',
        center: 'center',
        right: 'end',
        start: 'start',
        end: 'end'
    };

    const HORIZONTAL_ALIGN_TO_GENERAL_RTL = {
        left: 'end',
        center: 'center',
        right: 'start',
        start: 'start',
        end: 'end'
    };

    const VERTICAL_ALIGN_TO_GENERAL = {
        top: 'start',
        center: 'center',
        bottom: 'end',
        baseline: 'baseline'
    };

    const stateChangeErrorMessage = (prop: string) =>
        `The component id with the "${prop}" property for state change is missing. Either specify the id, or specify the "transition_trigger" property without "state_change" value.`;
</script>

<script lang="ts">
    import { afterUpdate, getContext, onDestroy, tick } from 'svelte';
    import { get } from 'svelte/store';

    import css from './Outer.module.css';

    import type { DivBaseData, Extension } from '../../types/base';
    import type { Mods, Style } from '../../types/general';
    import type { DivActionableData } from '../../types/actionable';
    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivExtension } from '../../../typings/common';
    import type { Visibility } from '../../types/base';
    import type { Action, DivBase } from '../../../typings/common';
    import type { MaybeMissing } from '../../expressions/json';
    import type { EdgeInsets } from '../../types/edgeInserts';
    import type { CornersRadius } from '../../types/border';
    import type { WrapContentSize } from '../../types/sizes';
    import type { Background } from '../../types/background';
    import type { Animation, AnyAnimation } from '../../types/animation';
    import type { ComponentContext } from '../../types/componentContext';
    import { makeStyle } from '../../utils/makeStyle';
    import { pxToEm, pxToEmWithUnits } from '../../utils/pxToEm';
    import { getBackground } from '../../utils/background';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { visibilityAction } from '../../use/visibilityAction';
    import { genClassName } from '../../utils/genClassName';
    import { devtool, type DevtoolResult } from '../../use/devtool';
    import { STATE_CTX, type StateCtxValue } from '../../context/state';
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
    import { Truthy } from '../../utils/truthy';
    import { shadowToCssBoxShadow } from '../../utils/shadow';
    import { isDeepEqual } from '../../utils/isDeepEqual';
    import { filterEnabledActions } from '../../utils/filterEnabledActions';
    import { isPrefersReducedMotion } from '../../utils/isPrefersReducedMotion';
    import { layoutProvider } from '../../use/layoutProvider';
    import { ENABLED_CTX, type EnabledCtxValue } from '../../context/enabled';
    import { correctBooleanInt } from '../../utils/correctBooleanInt';
    import Actionable from './Actionable.svelte';
    import OuterBackground from './OuterBackground.svelte';

    export let componentContext: ComponentContext<DivBaseData & DivActionableData>;
    export let cls = '';
    export let style: Style | undefined = undefined;
    export let layoutParams: LayoutParams = {};
    export let customDescription = false;
    export let customPaddings = false;
    export let customActions = '';
    export let additionalPaddings: EdgeInsets | null = null;
    export let heightByAspect = false;
    export let parentOf: {
        json: MaybeMissing<DivBaseData> | undefined;
        id: string | undefined;
    }[] | undefined = undefined;
    export let parentOfSimpleMode: boolean | undefined = undefined;
    export let replaceItems: ((items: (MaybeMissing<DivBaseData> | undefined)[]) => void) | undefined = undefined;
    export let hasInnerFocusable = false;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const stateCtx = getContext<StateCtxValue>(STATE_CTX);
    const { isEnabled } = getContext<EnabledCtxValue>(ENABLED_CTX);
    const direction = rootCtx.direction;

    let currentNode: HTMLElement;
    let attrs: Record<string, string> | undefined;
    let extensions: DivExtension[] | null = null;

    let prevChilds: string[] = [];

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

    let selfPadding: EdgeInsets | null = null;
    let margin = '';

    let widthMods: Mods = {};
    let width: string | undefined;
    let widthMin: string | undefined;
    let widthMax: string | undefined;
    let widthNum = 0;
    let widthFlexGrow = 0;
    let widthFlexShrink = 0;
    let widthFill = false;
    let hasWidthError = false;

    let heightMods: Mods = {};
    let height: string | undefined;
    let heightMin: string | undefined;
    let heightMax: string | undefined;
    let heightNum = 0;
    let heightFlexGrow = 0;
    let heightFlexShrink = 0;
    let heightFill = false;
    let hasHeightError = false;

    let alpha = 1;
    let opacity: number | undefined;

    let background: MaybeMissing<Background[]> | undefined;
    let backgroundStyle: Style;
    let hasSeparateBg: boolean;

    let jsonTransitionTriggers = [];
    let hasStateChangeTrigger = false;
    let hasVisibilityChangeTrigger = false;

    let stateChangingInProgress: boolean | undefined;
    let visibilityChangingInProgress: boolean | undefined;
    let transitionChangeInProgress: boolean | undefined;

    let actions: MaybeMissing<Action>[] = [];
    let doubleTapActions: MaybeMissing<Action>[] = [];
    let longTapActions: MaybeMissing<Action>[] = [];
    let focusActions: MaybeMissing<Action>[] = [];
    let blurActions: MaybeMissing<Action>[] = [];
    let pressStartActions: MaybeMissing<Action>[] = [];
    let pressEndActions: MaybeMissing<Action>[] = [];
    let hoverStartActions: MaybeMissing<Action>[] = [];
    let hoverEndActions: MaybeMissing<Action>[] = [];

    let actionAnimationList: MaybeMissing<AnyAnimation>[] = [];
    let actionAnimationTransition = '';
    let animationOpacityStart: number | undefined = undefined;
    let animationOpacityEnd: number | undefined = undefined;
    let animationScaleStart: number | undefined = undefined;
    let animationScaleEnd: number | undefined = undefined;

    let isVisibilityInited = false;
    let visibility: Visibility = 'visible';

    let pivotXNum = 0;
    let pivotYNum = 0;
    let transformOrigin: string | undefined;
    let transform: string | undefined;

    let layoutProviderResizeObserver: ResizeObserver | undefined;

    let hasCustomFocus = false;
    let captureFocusOnAction = true;

    let prevExtensionsVal: MaybeMissing<Extension>[] | undefined = undefined;
    let prevTriggersUnsubscribe: (() => void) | undefined = undefined;

    let registred: {
        destroy(): void;
    } | undefined;
    let dev: DevtoolResult | null = null;
    let idUnregister: (() => void) | undefined;

    $: origJson = componentContext.origJson;

    function rebind(): void {
        selfPadding = null;
        margin = '';
        alpha = 1;
        isVisibilityInited = false;
        visibility = 'visible';
        pivotXNum = 0;
        pivotYNum = 0;
        transformOrigin = undefined;
        transform = undefined;
        captureFocusOnAction = true;

        jsonTransitionTriggers = componentContext.fakeElement ?
            [] :
            (componentContext.json.transition_triggers || ['state_change', 'visibility_change']);
        hasStateChangeTrigger = Boolean(jsonTransitionTriggers.indexOf('state_change') !== -1);
        hasVisibilityChangeTrigger = Boolean(jsonTransitionTriggers.indexOf('visibility_change') !== -1);

        if (currentNode) {
            useAction(currentNode);
        }

        prevTriggersUnsubscribe?.();
        if ($isEnabled) {
            prevTriggersUnsubscribe = rootCtx.processVariableTriggers(
                componentContext,
                componentContext.json.variable_triggers
            );
        }
    }

    // If origJson is same, than the component itself is the same
    // componentContext could be changed
    $: if (origJson) {
        rebind();
    }

    $: if ($isEnabled) {
        prevTriggersUnsubscribe?.();
        prevTriggersUnsubscribe = rootCtx.processVariableTriggers(
            componentContext,
            componentContext.json.variable_triggers
        );
    } else {
        prevTriggersUnsubscribe?.();
    }

    $: jsonFocus = componentContext.getDerivedFromVars(componentContext.json.focus);
    $: jsonBorder = componentContext.getDerivedFromVars(componentContext.json.border);
    $: jsonPaddings = componentContext.getDerivedFromVars(componentContext.json.paddings);
    $: jsonMargins = componentContext.getDerivedFromVars(componentContext.json.margins);
    $: jsonWidth = componentContext.getDerivedFromVars(componentContext.json.width);
    $: jsonAlignmentHorizontal = componentContext.getDerivedFromVars(componentContext.json.alignment_horizontal);
    $: jsonHeight = componentContext.getDerivedFromVars(componentContext.json.height);
    $: jsonAlignmentVertical = componentContext.getDerivedFromVars(componentContext.json.alignment_vertical);
    $: jsonAlpha = componentContext.getDerivedFromVars(componentContext.json.alpha);
    $: jsonAccessibility = componentContext.getDerivedFromVars(componentContext.json.accessibility);
    $: jsonBackground = componentContext.getDerivedFromVars(componentContext.json.background);
    $: jsonAction = componentContext.getDerivedFromVars(
        componentContext.json.action, undefined, true
    );
    $: jsonActions = componentContext.getDerivedFromVars(
        componentContext.json.actions, undefined, true
    );
    $: jsonDoubleTapActions = componentContext.getDerivedFromVars(
        componentContext.json.doubletap_actions, undefined, true
    );
    $: jsonLongTapActions = componentContext.getDerivedFromVars(
        componentContext.json.longtap_actions, undefined, true
    );
    $: jsonPressStartActions = componentContext.getDerivedFromVars(
        componentContext.json.press_start_actions, undefined, true
    );
    $: jsonPressEndActions = componentContext.getDerivedFromVars(
        componentContext.json.press_end_actions, undefined, true
    );
    $: jsonHoverStartActions = componentContext.getDerivedFromVars(
        componentContext.json.hover_start_actions, undefined, true
    );
    $: jsonHoverEndActions = componentContext.getDerivedFromVars(
        componentContext.json.hover_end_actions, undefined, true
    );
    $: jsonActionAnimation = componentContext.getDerivedFromVars(componentContext.json.action_animation);
    $: jsonVisibility = componentContext.getDerivedFromVars(componentContext.json.visibility);
    $: jsonTransform = componentContext.getDerivedFromVars(componentContext.json.transform);
    $: jsonCaptureFocusOnAction = componentContext.getDerivedFromVars(componentContext.json.capture_focus_on_action);

    $: {
        prevChilds.forEach(id => {
            rootCtx.unregisterParentOf(id);
        });
        prevChilds = [];
        if (parentOf) {
            parentOf.forEach(item => {
                if (item?.id) {
                    prevChilds.push(item.id);
                    rootCtx.registerParentOf(item.id, {
                        replaceWith,
                        isSingleMode: Boolean(parentOfSimpleMode)
                    });
                }
            });
        }
    }

    function replaceWith(id: string, items?: DivBase[]): void {
        if (!Array.isArray(parentOf) || !replaceItems) {
            return;
        }

        if (parentOfSimpleMode) {
            const newItemsLen = Array.isArray(items) && items.length || 0;
            if (newItemsLen !== 1) {
                return;
            }
        }

        const index = parentOf.findIndex(item => item?.id === id);
        const newItems = parentOf.slice();
        newItems.splice(index, 1, ...(items || [] as DivBase[]).map(it => ({
            json: it,
            id: it?.id as string | undefined
        })));

        parentOf = newItems;

        replaceItems(newItems.map(it => it?.json));
    }

    $: {
        const border = hasCustomFocus && $jsonFocus?.border ? $jsonFocus.border : $jsonBorder;
        let newBorderStyle: Style = {};
        let newBorderElemStyle: Style = {};
        let newHasBorder = false;
        let newBackgroundRadius = '';

        if (border) {
            if (correctBooleanInt(border.has_shadow, false)) {
                const shadow = border.shadow;
                if (shadow) {
                    newBorderStyle['box-shadow'] = shadowToCssBoxShadow(shadow);
                } else {
                    newBorderStyle['box-shadow'] = '0 1px 2px 0 rgba(0,0,0,.18), 0 0 0 1px rgba(0,0,0,.07)';
                }
            }
            if (border.stroke) {
                newHasBorder = true;
                strokeWidth = correctNonNegativeNumber(border.stroke.width, strokeWidth);
                strokeColor = correctColor(border.stroke.color, 1, strokeColor);
                const strokeStyle = border.stroke.style?.type === 'dashed' ? 'dashed' : 'solid';
                newBorderElemStyle['--divkit-border'] = `${pxToEm(strokeWidth + 1)} ${strokeStyle} ${strokeColor}`;
            }
            if (border.corners_radius && typeof border.corners_radius === 'object') {
                cornersRadius = correctBorderRadiusObject(border.corners_radius, cornersRadius);
                newBorderStyle['border-radius'] = borderRadius(cornersRadius);
                const biasedRadius: CornersRadius = {};
                ([
                    'top-left',
                    'top-right',
                    'bottom-right',
                    'bottom-left'
                ] as const).forEach(corner => {
                    biasedRadius[corner] = (cornersRadius[corner] || 0) + 1;
                });
                newBorderElemStyle['--divkit-border-radius'] = borderRadius(biasedRadius);
            } else if (border.corner_radius) {
                cornerRadius = correctNonNegativeNumber(border.corner_radius, cornerRadius);
                cornersRadius = {
                    'top-left': cornerRadius,
                    'top-right': cornerRadius,
                    'bottom-right': cornerRadius,
                    'bottom-left': cornerRadius
                };
                newBorderStyle['border-radius'] = pxToEm(cornerRadius);
                newBorderElemStyle['--divkit-border-radius'] = pxToEm(cornerRadius + 1);
            }

            // Clip browser rendering artifacts by border-radius + border-width/2
            if (newHasBorder && strokeWidth && (border.corners_radius || border.corner_radius)) {
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

    $: {
        selfPadding = correctEdgeInsertsObject(
            ($jsonPaddings && !customPaddings) ?
                $jsonPaddings :
                undefined,
            selfPadding
        );
    }

    $: padding = edgeInsertsToCss(sumEdgeInsets(selfPadding, additionalPaddings), $direction);

    $: {
        margin = correctEdgeInserts($jsonMargins, $direction, margin);
    }
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
            (type === 'match_parent' || !type) && layoutParams.parentHorizontalWrapContent
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
                componentContext.logError(wrapError(new Error('Incorrect child size. Container with wrap_content size contains child with match_parent size along the main axis'), {
                    level: 'warn'
                }));
            }
        } else {
            widthType = 'parent';
            if (layoutParams.parentContainerOrientation === 'vertical' && layoutParams.parentContainerWrap) {
                newWidthError = true;
                componentContext.logError(wrapError(new Error('Cannot place a match_parent items on the cross-axis of wrap'), {
                    level: 'error'
                }));
            }
            if (layoutParams.parentLayoutOrientation === 'vertical' || layoutParams.stretchWidth) {
                const leftMargin = ($direction === 'ltr' ? $jsonMargins?.start : $jsonMargins?.end) ??
                    $jsonMargins?.left ??
                    0;
                const rightMargin = ($direction === 'ltr' ? $jsonMargins?.end : $jsonMargins?.start) ??
                    $jsonMargins?.right ??
                    0;
                const totalWidth = `calc(100% - ${pxToEmWithUnits(leftMargin + rightMargin)})`;

                if (layoutParams.stretchWidth) {
                    // force preferred width to 0
                    newWidth = '0';
                    newWidthMin = totalWidth;
                } else {
                    newWidth = totalWidth;
                }
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
            if (align === 'left' || align === 'center' || align === 'right' || align === 'start' || align === 'end') {
                newWidthMods['halign-self'] = (
                    $direction === 'ltr' ?
                        HORIZONTAL_ALIGN_TO_GENERAL_LTR :
                        HORIZONTAL_ALIGN_TO_GENERAL_RTL
                )[align];
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
        } else if (type === 'match_parent' && !layoutParams.parentVerticalWrapContent) {
            heightType = 'parent';
            if (layoutParams.parentContainerOrientation === 'horizontal' && layoutParams.parentContainerWrap) {
                newHeightError = true;
                componentContext.logError(wrapError(new Error('Cannot place a match_parent items on the cross-axis of wrap'), {
                    level: 'error'
                }));
            }
            if (layoutParams.parentLayoutOrientation === 'horizontal' || layoutParams.stretchHeight) {
                const topMargin = $jsonMargins?.top ?? 0;
                const bottomMargin = $jsonMargins?.bottom ?? 0;
                const totalHeight = `calc(100% - ${pxToEmWithUnits(topMargin + bottomMargin)})`;

                if (layoutParams.stretchHeight) {
                    // force preferred height to 0
                    newHeight = '0';
                    newHeightMin = totalHeight;
                } else {
                    newHeight = totalHeight;
                }
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
                componentContext.logError(wrapError(new Error('Incorrect child size. Container with wrap_content size contains child with match_parent size along the main axis'), {
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

    $: gridArea = layoutParams.gridArea ?
        `${layoutParams.gridArea.y + 1}/${layoutParams.gridArea.x + 1}/span ${layoutParams.gridArea.rowSpan}/span ${layoutParams.gridArea.colSpan}` :
        undefined;

    $: {
        alpha = correctAlpha($jsonAlpha, alpha);
        opacity = alpha === 1 ? undefined : alpha;
    }

    $: {
        attrs = undefined;
        if ($jsonAccessibility && !customDescription && $jsonAccessibility.description) {
            attrs = {};
            attrs['aria-label'] = $jsonAccessibility.description;
        }
    }

    $: {
        background = hasCustomFocus && $jsonFocus?.background ? $jsonFocus.background : $jsonBackground;
        backgroundStyle = {};
        hasSeparateBg = false;
        if (Array.isArray(background)) {
            hasSeparateBg =
                background.some(it => it.type === 'image' || it.type === 'nine_patch_image') ||
                Boolean(backgroundRadius);

            if (!hasSeparateBg) {
                const res = getBackground(background);
                backgroundStyle['background-color'] = res.color;
                backgroundStyle['background-image'] = res.image;
                backgroundStyle['background-size'] = res.size;
                backgroundStyle['background-position'] = res.position;
                backgroundStyle['background-repeat'] = 'no-repeat';
            }
        }
    }

    $: {
        stateChangingInProgress = undefined;
        if (hasStateChangeTrigger && componentContext.id && componentContext.json.transition_in && rootCtx.isRunning('stateChange')) {
            stateChangingInProgress = true;
        }
    }
    $: {
        transitionChangeInProgress = undefined;
        if (
            hasStateChangeTrigger &&
            componentContext.id &&
            rootCtx.isRunning('stateChange') && stateCtx.hasTransitionChange(componentContext.id)
        ) {
            transitionChangeInProgress = true;
        }
    }

    $: {
        let newActions = $jsonActions || $jsonAction && [$jsonAction] || [];
        let newDoubleTapActions = $jsonDoubleTapActions || [];
        let newLongTapActions = $jsonLongTapActions || [];
        let newFocusActions = $jsonFocus?.on_focus || [];
        let newBlurActions = $jsonFocus?.on_blur || [];
        let newPressStartActions = $jsonPressStartActions || [];
        let newPressEndActions = $jsonPressEndActions || [];
        let newHoverStartActions = $jsonHoverStartActions || [];
        let newHoverEndActions = $jsonHoverEndActions || [];

        if (componentContext.fakeElement) {
            newActions = [];
            newDoubleTapActions = [];
            newLongTapActions = [];
            newFocusActions = [];
            newBlurActions = [];
        } else {
            if (!Array.isArray(newActions)) {
                newActions = [];
                componentContext.logError(wrapError(new Error('Actions should be array')));
            }
            if (!Array.isArray(newDoubleTapActions)) {
                newDoubleTapActions = [];
                componentContext.logError(wrapError(new Error('DoubleTapActions should be array')));
            }
            if (!Array.isArray(newLongTapActions)) {
                newLongTapActions = [];
                componentContext.logError(wrapError(new Error('LongTapActions should be array')));
            }
            if (!Array.isArray(newFocusActions)) {
                newFocusActions = [];
                componentContext.logError(wrapError(new Error('FocusActions should be array')));
            }
            if (!Array.isArray(newBlurActions)) {
                newBlurActions = [];
                componentContext.logError(wrapError(new Error('BlurActions should be array')));
            }
            if (!Array.isArray(newPressStartActions)) {
                newPressStartActions = [];
                componentContext.logError(wrapError(new Error('PressStartActions should be array')));
            }
            if (!Array.isArray(newPressEndActions)) {
                newPressEndActions = [];
                componentContext.logError(wrapError(new Error('PressEndActions should be array')));
            }
            if (!Array.isArray(newHoverStartActions)) {
                newHoverStartActions = [];
                componentContext.logError(wrapError(new Error('HoverStartActions should be array')));
            }
            if (!Array.isArray(newHoverEndActions)) {
                newHoverEndActions = [];
                componentContext.logError(wrapError(new Error('HoverEndActions should be array')));
            }
        }

        if ((
            newActions.length ||
            newDoubleTapActions.length ||
            newLongTapActions.length ||
            pressStartActions.length ||
            pressEndActions.length ||
            hoverStartActions.length ||
            hoverEndActions.length
        ) && customActions) {
            newActions = [];
            newDoubleTapActions = [];
            newLongTapActions = [];
            pressStartActions = [];
            pressEndActions = [];
            hoverStartActions = [];
            hoverEndActions = [];
            componentContext.logError(wrapError(new Error(`Cannot use action on component "${customActions}"`)));
        }

        // todo check parent actions with customActions
        actions = newActions.filter(filterEnabledActions);
        doubleTapActions = newDoubleTapActions.filter(filterEnabledActions);
        longTapActions = newLongTapActions.filter(filterEnabledActions);
        focusActions = newFocusActions.filter(filterEnabledActions);
        blurActions = newBlurActions.filter(filterEnabledActions);
        pressStartActions = newPressStartActions.filter(filterEnabledActions);
        pressEndActions = newPressEndActions.filter(filterEnabledActions);
        hoverStartActions = newHoverStartActions.filter(filterEnabledActions);
        hoverEndActions = newHoverEndActions.filter(filterEnabledActions);
    }

    $: {
        if ($jsonActionAnimation) {
            actionAnimationList = flattenAnimation($jsonActionAnimation as Animation);
            actionAnimationTransition = actionAnimationList.map(parseActionAnimation).filter(Boolean).join(', ');
        }
    }

    $: if (typeof $jsonCaptureFocusOnAction === 'boolean') {
        captureFocusOnAction = $jsonCaptureFocusOnAction;
    }

    function hasNativeAnimation(list: MaybeMissing<AnyAnimation>[]) {
        return list.some(it => it.name === 'native');
    }

    function parseActionAnimation(animation: MaybeMissing<AnyAnimation>): string {
        const startValue = correctNumber(animation.start_value, 1);
        const endValue = correctNumber(animation.end_value, 1);
        const delay = correctNonNegativeNumber(animation.start_delay, 0);
        const duration = isPrefersReducedMotion() ? 0 : correctNonNegativeNumber(animation.duration, 300);
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
                componentContext.logError(wrapError(new Error('Unknown action_animation name'), {
                    additional: {
                        animation: animation.name
                    }
                }));
                return '';
        }
    }

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
        const transition = direction === 'in' ? componentContext.json.transition_in : componentContext.json.transition_out;

        if (
            hasVisibilityChangeTrigger &&
            transition
        ) {
            let bbox: DOMRect | undefined;
            if (nextVisibility === 'gone') {
                bbox = currentNode.getBoundingClientRect();
            }

            await tick();

            if (direction === 'in') {
                visibilityChangingInProgress = true;
            }
            stateCtx.runVisibilityTransition(
                {
                    ...componentContext.json,
                    visibility: 'visible'
                } as DivBaseData,
                componentContext,
                transition,
                currentNode,
                direction,
                bbox
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

    function unmountExtensions(): void {
        if (extensions && currentNode) {
            const ctx = rootCtx.getExtensionContext(componentContext);
            extensions.forEach(it => {
                it.unmountView?.(currentNode, ctx);
            });
            extensions = null;
        }
    }

    $: if (componentContext.json && currentNode && !isDeepEqual(componentContext.json.extensions, prevExtensionsVal)) {
        let exts = prevExtensionsVal = componentContext.json.extensions;

        tick().then(() => {
            if (exts !== prevExtensionsVal || !currentNode) {
                return;
            }

            unmountExtensions();

            if (Array.isArray(componentContext.json.extensions)) {
                const ctx = rootCtx.getExtensionContext(componentContext);
                extensions = componentContext.json.extensions.map(it => {
                    const id = it.id;
                    if (!id) {
                        return;
                    }

                    const instance = rootCtx.getExtension(id, it.params);

                    if (instance) {
                        instance.mountView?.(currentNode, ctx);
                    }

                    return instance;
                }).filter(Truthy);
            }
        });
    }

    function afterInstanceUpdate(): void {
        if (extensions?.length) {
            const ctx = rootCtx.getExtensionContext(componentContext);
            extensions.forEach(instance => {
                instance.updateView?.(currentNode, ctx);
            });
        }
        if (dev) {
            dev.update(componentContext);
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
        'has-action-animation': Boolean(actionAnimationTransition),
        'parent-flex': layoutParams.parentContainerOrientation || undefined,
        'parent-grid': Boolean(layoutParams.gridArea) || undefined,
        'has-custom-focus': Boolean(hasCustomFocus && componentContext.json.focus)
    };

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
        registred?.destroy();

        currentNode = node;
        if (hasStateChangeTrigger && componentContext.json.transition_in) {
            if (componentContext.id) {
                stateCtx.registerChildWithTransitionIn(
                    componentContext.json as DivBaseData,
                    componentContext,
                    componentContext.json.transition_in,
                    node
                ).then(() => {
                    stateChangingInProgress = false;
                }).catch(e => {
                    stateChangingInProgress = false;
                    throw e;
                });
            } else {
                componentContext.logError(wrapError(new Error(stateChangeErrorMessage('transition_in')), {
                    level: 'warn'
                }));
            }
        }
        if (hasStateChangeTrigger && componentContext.json.transition_out) {
            if (componentContext.id) {
                stateCtx.registerChildWithTransitionOut(
                    componentContext.json as DivBaseData,
                    componentContext,
                    componentContext.json.transition_out,
                    node
                );
            } else {
                componentContext.logError(wrapError(new Error(stateChangeErrorMessage('transition_out')), {
                    level: 'warn'
                }));
            }
        }
        if (!componentContext.fakeElement) {
            if (componentContext.json.transition_change && !componentContext.id) {
                componentContext.logError(wrapError(new Error(stateChangeErrorMessage('transition_change')), {
                    level: 'warn'
                }));
            }
            stateCtx.registerChildWithTransitionChange(
                componentContext.json as DivBaseData,
                componentContext,
                componentContext.json.transition_change,
                node
            ).then(() => {
                transitionChangeInProgress = false;
            }).catch(e => {
                transitionChangeInProgress = false;
                throw e;
            });
        }

        const visibilityActions = componentContext.fakeElement ?
            [] :
            (
                componentContext.json.visibility_actions ||
                componentContext.json.visibility_action && [componentContext.json.visibility_action]
            );

        const disappearActions = componentContext.fakeElement ? [] : componentContext.json.disappear_actions;

        let visAction: {
            destroy(): void;
        } | undefined;
        if (
            Array.isArray(visibilityActions) && visibilityActions.length ||
            Array.isArray(disappearActions) && disappearActions.length
        ) {
            visAction = visibilityAction(node, {
                visibilityActions,
                disappearActions,
                rootCtx,
                componentContext
            });
        }

        const id = componentContext.id;
        if (id) {
            idUnregister?.();
            idUnregister = rootCtx.registerId(id, {
                context: () => componentContext,
                node: () => currentNode
            });
            stateCtx.registerChild(id);
        }

        componentContext.json.tooltips?.forEach(tooltip => {
            rootCtx.registerTooltip(node, tooltip);
        });

        if (layoutProviderResizeObserver) {
            layoutProviderResizeObserver.disconnect();
            layoutProviderResizeObserver = undefined;
        }
        layoutProviderResizeObserver = layoutProvider(
            currentNode,
            componentContext,
            componentContext.json.layout_provider?.width_variable_name,
            componentContext.json.layout_provider?.height_variable_name
        );

        if (devtool && !componentContext.fakeElement) {
            dev = devtool(node, rootCtx, componentContext);
        }

        registred = {
            destroy() {
                if (idUnregister) {
                    idUnregister();
                    idUnregister = undefined;
                }
                if (id) {
                    stateCtx.unregisterChild(id);
                }
                if (visAction) {
                    visAction.destroy();
                }
                if (dev) {
                    dev.destroy();
                }
            }
        };

        return registred;
    }

    function focusHandler() {
        if (!componentContext.json.focus) {
            return;
        }

        if (!get(rootCtx.isPointerFocus)) {
            hasCustomFocus = true;
        }

        componentContext.execAnyActions(focusActions);
    }

    function blurHandler() {
        if (!componentContext.json.focus) {
            return;
        }

        hasCustomFocus = false;
        componentContext.execAnyActions(blurActions);
    }

    afterUpdate(afterInstanceUpdate);

    onDestroy(() => {
        prevChilds.forEach(id => {
            rootCtx.unregisterParentOf(id);
        });
        prevChilds = [];

        if (layoutProviderResizeObserver) {
            layoutProviderResizeObserver.disconnect();
            layoutProviderResizeObserver = undefined;
        }

        componentContext.json.tooltips?.forEach(tooltip => {
            rootCtx.unregisterTooltip(tooltip);
        });

        prevTriggersUnsubscribe?.();

        unmountExtensions();
    });
</script>

{#if !hasWidthError && !hasHeightError}
    <Actionable
        {componentContext}
        id={componentContext.json.id}
        use={useAction}
        cls="{cls} {genClassName('outer', css, mods)}"
        style={makeStyle(stl)}
        {actions}
        {doubleTapActions}
        {longTapActions}
        {pressStartActions}
        {pressEndActions}
        {hoverStartActions}
        {hoverEndActions}
        {attrs}
        {hasInnerFocusable}
        isNativeActionAnimation={!actionAnimationList.length || hasNativeAnimation(actionAnimationList)}
        customAccessibility={$jsonAccessibility}
        {captureFocusOnAction}
        on:focus={focusHandler}
        on:blur={blurHandler}
    >
        <!-- eslint-disable-next-line max-len -->
        {#if hasSeparateBg}<OuterBackground {componentContext} direction={$direction} background={background} radius={backgroundRadius} />{/if}<slot {focusHandler} {blurHandler} {hasCustomFocus} />{#if hasBorder}<span class={css.outer__border} style={makeStyle(borderElemStyle)}></span>{/if}
    </Actionable>
{/if}
