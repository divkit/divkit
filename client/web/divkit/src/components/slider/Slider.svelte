<script lang="ts" context="module">
    const DEFAULT_DRAWABLE_STYLE: DrawableStyle = {
        width: 10,
        height: 10,
        borderRadius: 5,
        background: '#000'
    };
    const THUMB_MARK_SHAPES = [
        'rounded_rectangle',
        'circle'
    ];
    const TRACK_SHAPES = [
        'rounded_rectangle'
    ];
</script>

<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';

    import css from './Slider.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivSliderData, SliderRange } from '../../types/slider';
    import type { ComponentContext } from '../../types/componentContext';
    import type { MaybeMissing } from '../../expressions/json';
    import type { Direction } from '../../../typings/common';
    import type { EdgeInsets } from '../../types/edgeInserts';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { ACTION_CTX, type ActionCtxValue } from '../../context/action';
    import { genClassName } from '../../utils/genClassName';
    import { correctDrawableStyle, type DrawableStyle } from '../../utils/correctDrawableStyles';
    import { pxToEm, pxToEmWithUnits } from '../../utils/pxToEm';
    import { correctSliderTextStyle, type TransformedSliderTextStyle } from '../../utils/correctSliderTextStyle';
    import { wrapError } from '../../utils/wrapError';
    import { isNonNegativeNumber } from '../../utils/isNonNegativeNumber';
    import { clamp } from '../../utils/clamp';
    import { correctNumber } from '../../utils/correctNumber';
    import Outer from '../utilities/Outer.svelte';
    import { createVariable } from '../../expressions/variable';
    import { debounce } from '../../utils/debounce';
    import { correctBooleanInt } from '../../utils/correctBooleanInt';
    import { composeAccessibilityDescription } from '../../utils/composeAccessibilityDescription';
    import DevtoolHolder from '../utilities/DevtoolHolder.svelte';

    export let componentContext: ComponentContext<DivSliderData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const actionCtx = getContext<ActionCtxValue>(ACTION_CTX);

    const direction = rootCtx.direction;

    let prevId: string | undefined;
    let input: HTMLInputElement;
    let tracksInner: HTMLElement;
    let switchedTracks = false;
    let minValue = 0;
    let maxValue = 100;
    let thumbStyle = DEFAULT_DRAWABLE_STYLE;
    let thumbSecondaryStyle = thumbStyle;
    let trackInactiveStyle = DEFAULT_DRAWABLE_STYLE;
    let trackActiveStyle = DEFAULT_DRAWABLE_STYLE;
    let markActiveTicks: number[];
    let markActiveStyle: DrawableStyle | null = null;
    let markInactiveTicks: number[];
    let markInactiveStyle: DrawableStyle | null = null;
    let textStyle: TransformedSliderTextStyle | undefined = undefined;
    let textSecondaryStyle: TransformedSliderTextStyle | undefined = textStyle;
    let description = '';
    let secondaryDescription = '';
    let isEnabled = true;
    let hasError = false;
    let renderRanges: {
        left: string;
        right: string;
        height: string;
        borderRadius: string;
        background: string;
        boxShadow: string;
    }[] = [];

    $: origJson = componentContext.origJson;

    function rebind(): void {
        thumbStyle = DEFAULT_DRAWABLE_STYLE;
        thumbSecondaryStyle = thumbStyle;
        trackInactiveStyle = DEFAULT_DRAWABLE_STYLE;
        trackActiveStyle = DEFAULT_DRAWABLE_STYLE;
        markActiveStyle = null;
        markInactiveStyle = null;
        textStyle = undefined;
        textSecondaryStyle = undefined;
        description = '';
        isEnabled = true;
        secondaryDescription = '';
    }

    $: if (origJson) {
        rebind();
    }

    $: firstVariable = componentContext.json.thumb_value_variable;
    $: secondVariable = componentContext.json.thumb_secondary_value_variable;

    $: valueVariable = firstVariable && (componentContext.getVariable(firstVariable, 'integer') || rootCtx.awaitGlobalVariable(firstVariable, 'integer', 0)) || createVariable('temp', 'integer', 0);
    $: value2Variable = secondVariable && (componentContext.getVariable(secondVariable, 'integer') || rootCtx.awaitGlobalVariable(secondVariable, 'integer', 0)) || createVariable('temp', 'integer', 0);

    $: jsonMinValue = componentContext.getDerivedFromVars(componentContext.json.min_value);
    $: jsonMaxValue = componentContext.getDerivedFromVars(componentContext.json.max_value);
    $: jsonThumbStyle = componentContext.getDerivedFromVars(componentContext.json.thumb_style);
    $: jsonThumbSecondaryStyle = componentContext.getDerivedFromVars(componentContext.json.thumb_secondary_style);
    $: jsonTrackInactiveStyle = componentContext.getDerivedFromVars(componentContext.json.track_inactive_style);
    $: jsonTrackActiveStyle = componentContext.getDerivedFromVars(componentContext.json.track_active_style);
    $: jsonMarkActiveStyle = componentContext.getDerivedFromVars(componentContext.json.tick_mark_active_style);
    $: jsonMarkInactiveStyle = componentContext.getDerivedFromVars(componentContext.json.tick_mark_inactive_style);
    $: jsonTextStyle = componentContext.getDerivedFromVars(componentContext.json.thumb_text_style);
    $: jsonSecondaryTextStyle = componentContext.getDerivedFromVars(componentContext.json.thumb_secondary_text_style);
    $: jsonAccessibility = componentContext.getDerivedFromVars(componentContext.json.accessibility);
    $: jsonSecondaryAccessibility = componentContext.getDerivedFromVars(
        componentContext.json.secondary_value_accessibility
    );
    $: jsonIsEnabled = componentContext.getDerivedFromVars(componentContext.json.is_enabled);
    $: jsonRanges = componentContext.getDerivedFromVars(componentContext.json.ranges);

    $: {
        minValue = correctNumber($jsonMinValue, minValue);
        maxValue = correctNumber($jsonMaxValue, maxValue);
        checkTicks();
    }

    let value = clamp($valueVariable || 0, minValue, maxValue);
    let value2 = clamp($value2Variable || 0, minValue, maxValue);

    $: {
        const newVal = clamp($valueVariable || 0, minValue, maxValue);
        if (newVal !== value) {
            value = newVal;
        }
    }

    $: {
        const newVal2 = clamp($value2Variable || 0, minValue, maxValue);
        if (newVal2 !== value2) {
            value2 = newVal2;
        }
    }

    $: {
        thumbStyle = correctDrawableStyle($jsonThumbStyle, THUMB_MARK_SHAPES, thumbStyle);
    }

    $: {
        thumbSecondaryStyle = correctDrawableStyle($jsonThumbSecondaryStyle, THUMB_MARK_SHAPES, thumbStyle);
    }

    $: {
        trackInactiveStyle = correctDrawableStyle($jsonTrackInactiveStyle, TRACK_SHAPES, trackInactiveStyle);
    }

    $: {
        trackActiveStyle = correctDrawableStyle($jsonTrackActiveStyle, TRACK_SHAPES, trackActiveStyle);
    }

    function fillTicks(from: number, to: number, minValue: number, maxValue: number, inside: boolean): number[] {
        let res: number[] = [];

        if (inside) {
            for (let i = from; i < to; ++i) {
                res.push((i - minValue) / (maxValue - minValue));
            }
        } else {
            for (let i = minValue; i < from; ++i) {
                res.push((i - minValue) / (maxValue - minValue));
            }
            for (let i = to; i < maxValue + 1; ++i) {
                res.push((i - minValue) / (maxValue - minValue));
            }
        }

        return res;
    }

    $: {
        let newStyle = correctDrawableStyle($jsonMarkActiveStyle, THUMB_MARK_SHAPES, DEFAULT_DRAWABLE_STYLE);

        if (newStyle !== DEFAULT_DRAWABLE_STYLE) {
            markActiveStyle = newStyle;
        }
    }
    $: if (markActiveStyle) {
        markActiveTicks = secondVariable ?
            fillTicks(Math.min(value, value2), Math.max(value, value2) + 1, minValue, maxValue, true) :
            fillTicks(minValue, value, minValue, maxValue, true);
        checkTicks();
    } else {
        markActiveTicks = [];
    }

    $: {
        let newStyle = correctDrawableStyle($jsonMarkInactiveStyle, THUMB_MARK_SHAPES, DEFAULT_DRAWABLE_STYLE);

        if (newStyle !== DEFAULT_DRAWABLE_STYLE) {
            markInactiveStyle = newStyle;
        }
    }
    $: if (markInactiveStyle) {
        markInactiveTicks = secondVariable ?
            fillTicks(Math.min(value, value2), Math.max(value, value2) + 1, minValue, maxValue, false) :
            fillTicks(value + 1, maxValue + 1, minValue, maxValue, true);
        checkTicks();
    } else {
        markInactiveTicks = [];
    }

    $: {
        textStyle = correctSliderTextStyle($jsonTextStyle, textStyle);
    }

    $: {
        textSecondaryStyle = correctSliderTextStyle($jsonSecondaryTextStyle, textStyle);
    }

    $: if ($jsonAccessibility?.description) {
        description = composeAccessibilityDescription($jsonAccessibility);
    } else {
        componentContext.logError(wrapError(new Error('Missing accessibility "description" for slider'), {
            level: 'warn'
        }));
    }

    $: {
        isEnabled = correctBooleanInt($jsonIsEnabled, isEnabled);
    }

    $: if ($jsonSecondaryAccessibility?.description) {
        secondaryDescription = composeAccessibilityDescription($jsonSecondaryAccessibility);
    } else if (secondVariable) {
        componentContext.logError(wrapError(new Error('Missing second accessibility "description" for slider'), {
            level: 'warn'
        }));
    }

    $: {
        let newHasError = false;

        if (actionCtx.hasAction()) {
            componentContext.logError(wrapError(new Error('Cannot show "slider" inside component with an action or inside accessibility mode=exclude')));
            newHasError = true;
        } else if (thumbStyle === DEFAULT_DRAWABLE_STYLE) {
            componentContext.logError(wrapError(new Error('Missing "thumb_style" in slider')));
            newHasError = true;
        } else if (trackActiveStyle === DEFAULT_DRAWABLE_STYLE) {
            componentContext.logError(wrapError(new Error('Missing "track_active_style" in slider')));
            newHasError = true;
        } else if (trackInactiveStyle === DEFAULT_DRAWABLE_STYLE) {
            componentContext.logError(wrapError(new Error('Missing "track_inactive_style" in slider')));
            newHasError = true;
        }

        if (newHasError !== hasError) {
            hasError = newHasError;
        }
    }

    $: maxThumbWidth = pxToEm(Math.max(...[
        thumbStyle.width,
        thumbSecondaryStyle.width,
        0,
    ].filter(isNonNegativeNumber)));
    $: maxThumbHeight = pxToEm(Math.max(...[
        thumbStyle.height,
        thumbSecondaryStyle.height,
        0,
    ].filter(isNonNegativeNumber)));
    $: trackPart = (value - minValue) / (maxValue - minValue);
    $: trackSecondaryPart = secondVariable ? (value2 - minValue) / (maxValue - minValue) : undefined;
    $: trackActiveOffset = trackSecondaryPart !== undefined ? Math.min(value, value2) : minValue;
    $: trackActivePart = trackSecondaryPart !== undefined ? Math.abs(value2 - value) : value - minValue;

    function updateRenderList({
        direction,
        minValue,
        maxValue,
        trackActiveOffset,
        trackActivePart,
        trackInactiveStyle,
        trackActiveStyle,
        ranges = []
    }: {
        direction: Direction;
        minValue: number;
        maxValue: number;
        trackActiveOffset: number;
        trackActivePart: number;
        trackInactiveStyle: DrawableStyle;
        trackActiveStyle: DrawableStyle;
        ranges: MaybeMissing<SliderRange[]> | undefined;
    }) {
        const parts: {
            left: number;
            right: number;
            totalLeft: number;
            totalRight: number;
            leftMargin: number;
            rightMargin: number;
            style: DrawableStyle;
        }[] = [];

        const pushParts = (type: 'active' | 'inactive', from: number, to: number) => {
            const push = (start: number, end: number, style: DrawableStyle, margins?: EdgeInsets) => {
                const intersectionStart = Math.max(start, from);
                const intersectionEnd = Math.min(end, to);
                if (intersectionEnd - intersectionStart > 0) {
                    const leftMargin = margins ? (margins[direction === 'ltr' ? 'start' : 'end'] ?? margins.left ?? 0) : 0;
                    const rightMargin = margins ? (margins[direction === 'ltr' ? 'end' : 'start'] ?? margins.right ?? 0) : 0;

                    parts.push({
                        left: start,
                        right: end,
                        totalLeft: from,
                        totalRight: to,
                        leftMargin,
                        rightMargin,
                        style
                    });
                }
            };

            if (!ranges[0] || (ranges[0].start ?? minValue) > from) {
                push(from, ranges[0] ? (ranges[0].start ?? minValue) : to, type === 'inactive' ? trackInactiveStyle : trackActiveStyle);
            }
            ranges.forEach((range, index) => {
                const selfStyle = range[type === 'inactive' ? 'track_inactive_style' : 'track_active_style'];
                const fallbackStyle = type === 'inactive' ? trackInactiveStyle : trackActiveStyle;
                const style = selfStyle ?
                    correctDrawableStyle(selfStyle, TRACK_SHAPES, DEFAULT_DRAWABLE_STYLE) :
                    fallbackStyle;
                const prev = ranges[index - 1];
                const next = ranges[index + 1];

                const start = range.start ?? prev?.end ?? from;
                const end = range.end ?? next?.start ?? to;

                push(start, end, style, range.margins);
            });
            if (ranges[ranges.length - 1] && (ranges[ranges.length - 1].end ?? maxValue) < to) {
                const start = ranges[ranges.length - 1].end ?? maxValue;
                push(start, to, type === 'inactive' ? trackInactiveStyle : trackActiveStyle);
            }
        };
        pushParts('inactive', minValue, maxValue);
        pushParts('active', trackActiveOffset, trackActiveOffset + trackActivePart);

        const total = maxValue - minValue;
        renderRanges = parts.map(part => {
            let rangeLeft = `${(part.left - minValue) * 100 / total}%`;
            if (part.leftMargin) {
                rangeLeft = `calc(${rangeLeft} + ${pxToEmWithUnits(part.leftMargin)})`;
            }
            let left;
            if (part.totalLeft < part.left) {
                left = rangeLeft;
            } else if (part.leftMargin) {
                left = `max(${(part.totalLeft - minValue) * 100 / total}%, ${rangeLeft})`;
            } else {
                left = `${(Math.max(part.totalLeft, part.left) - minValue) * 100 / total}%`;
            }
            let rangeRight = `${(1 - (part.right - minValue) / total) * 100}%`;
            if (part.rightMargin) {
                rangeRight = `calc(${rangeRight} + ${pxToEmWithUnits(part.rightMargin)})`;
            }
            let right;
            if (part.totalRight > part.right) {
                right = rangeRight;
            } else if (part.rightMargin) {
                right = `max(${(1 - (part.totalRight - minValue) / total) * 100}%, ${rangeRight})`;
            } else {
                right = `${(1 - (Math.max(part.totalRight, part.right) - minValue) / total) * 100}%`;
            }

            return {
                left,
                right,
                height: pxToEm(part.style.height),
                borderRadius: pxToEm(part.style.borderRadius),
                background: part.style.background,
                boxShadow: part.style.boxShadow || ''
            };
        });
    }

    $: updateRenderList({
        direction: $direction,
        minValue,
        maxValue,
        trackActiveOffset,
        trackActivePart,
        trackInactiveStyle,
        trackActiveStyle,
        ranges: $jsonRanges
    });

    $: stl = {
        '--divkit-slider-thumb-width': pxToEm(thumbStyle.width),
        '--divkit-slider-thumb-height': pxToEm(thumbStyle.height),

        '--divkit-slider-thumb-secondary-width': pxToEm(thumbSecondaryStyle.width),
        '--divkit-slider-thumb-secondary-height': pxToEm(thumbSecondaryStyle.height),

        '--divkit-slider-text-offset-x': textStyle?.offset?.x ? pxToEmWithUnits(textStyle.offset.x) : undefined,
        '--divkit-slider-text-offset-y': textStyle?.offset?.y ? pxToEmWithUnits(textStyle.offset.y) : undefined,

        '--divkit-slider-text-secondary-offset-x': textSecondaryStyle?.offset?.x ? pxToEmWithUnits(textSecondaryStyle.offset.x) : undefined,
        '--divkit-slider-text-secondary-offset-y': textSecondaryStyle?.offset?.y ? pxToEmWithUnits(textSecondaryStyle.offset.y) : undefined,

        '--divkit-slider-tick-active-width': markActiveStyle ? pxToEm(markActiveStyle.width) : undefined,
        '--divkit-slider-tick-active-height': markActiveStyle ? pxToEm(markActiveStyle.height) : undefined,
        '--divkit-slider-tick-active-border-radius': markActiveStyle ? pxToEm(markActiveStyle.borderRadius) : undefined,
        '--divkit-slider-tick-active-background': markActiveStyle?.background || undefined,
        '--divkit-slider-tick-active-box-shadow': markActiveStyle?.boxShadow || undefined,

        '--divkit-slider-tick-inactive-width': markInactiveStyle ? pxToEm(markInactiveStyle.width) : undefined,
        '--divkit-slider-tick-inactive-height': markInactiveStyle ? pxToEm(markInactiveStyle.height) : undefined,
        '--divkit-slider-tick-inactive-border-radius': markInactiveStyle ?
            pxToEm(markInactiveStyle.borderRadius) :
            undefined,
        '--divkit-slider-tick-inactive-background': markInactiveStyle?.background || undefined,
        '--divkit-slider-tick-inactive-box-shadow': markInactiveStyle?.boxShadow || undefined,

        '--divkit-slider-max-thumb-width': maxThumbWidth,
        '--divkit-slider-max-thumb-height': maxThumbHeight,
        '--divkit-slider-track-part': trackPart,
        '--divkit-slider-track-secondary-part': trackSecondaryPart,
    };

    $: mods = {
        direction: $direction
    };

    function onSecondMousedown(event: MouseEvent | TouchEvent): void {
        if (!isEnabled) {
            return;
        }

        const pageX = 'pageX' in event ? event.pageX : event.changedTouches?.[0]?.pageX;
        if (pageX === undefined) {
            return;
        }

        const rangeBbox = tracksInner.getBoundingClientRect();
        let part = (pageX - rangeBbox.left) / rangeBbox.width;
        if ($direction === 'rtl') {
            part = 1 - part;
        }
        const newVal = minValue + (maxValue - minValue) * part;
        const final = Math.round(clamp(newVal, minValue, maxValue));
        const middle = (value + value2) / 2;

        switchedTracks = final < middle === value < value2;
    }

    function onInputChange(event: Event, type: 'first' | 'second'): void {
        const val = Number((event.target as HTMLInputElement).value);

        if (switchedTracks === (type === 'first')) {
            value2 = val;
            value2Variable.setValue(val);
        } else {
            value = val;
            valueVariable.setValue(val);
        }
    }

    let isTicksWarning = false;

    function checkTicks() {
        if (!tracksInner) {
            return;
        }

        const ticksCount = maxValue - minValue;
        const activeTickWidth = markActiveStyle?.width || 0;
        const inactiveTickWidth = markInactiveStyle?.width || 0;
        const maxTickWidth = Math.max(activeTickWidth, inactiveTickWidth);

        if (maxTickWidth * ticksCount >= tracksInner?.clientWidth) {
            if (!isTicksWarning) {
                componentContext.logError(wrapError(new Error('Slider ticks overlap each other'), {
                    level: 'warn'
                }));
                isTicksWarning = true;
            }
        } else {
            isTicksWarning = false;
        }
    }

    const checkTicksDebounced = debounce(checkTicks, 50);

    $: if (componentContext.json && input) {
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

    onMount(() => {
        checkTicks();
    });

    onDestroy(() => {
        if (prevId) {
            rootCtx.unregisterFocusable(prevId);
            prevId = undefined;
        }
    });
</script>

<svelte:window on:resize={checkTicksDebounced} />

{#if !hasError}
    <Outer
        let:focusHandler
        let:blurHandler
        cls={genClassName('slider', css, mods)}
        style={stl}
        customDescription={true}
        customActions="slider"
        hasInnerFocusable={true}
        {componentContext}
        {layoutParams}
    >
        <div class={css['slider__tracks-wrapper']}>
            <div class={css['slider__tracks-inner']} bind:this={tracksInner}>
                <div class={css['slider__tracks-ranges'] + ($direction === 'rtl' ? ' ' + css['slider__tracks-ranges_rtl'] : '')}>
                    {#each renderRanges as range}
                        <div
                            class={css.slider__track}
                            style:left={range.left}
                            style:right={range.right}
                            style:height={range.height}
                            style:border-radius={range.borderRadius}
                            style:background={range.background}
                            style:box-shadow={range.boxShadow}
                        ></div>
                    {/each}
                </div>
                {#each markActiveTicks as val}
                    <div class="{css.slider__tick} {css.slider__tick_active}" style:--divkit-slider-tick={val}></div>
                {/each}
                {#each markInactiveTicks as val}
                    <div class="{css.slider__tick} {css.slider__tick_inactive}" style:--divkit-slider-tick={val}></div>
                {/each}
                <div
                    class={css.slider__thumb}
                    style:border-radius={pxToEm(thumbStyle.borderRadius)}
                    style:background={thumbStyle.background}
                    style:box-shadow={thumbStyle.boxShadow || ''}
                >
                    {#if textStyle}
                        <div class={css['slider__thumb-text']}>
                            <div
                                class={css['slider__thumb-text-inner']}
                                style:font-size={textStyle?.fontSize || '1em'}
                                style:font-weight={textStyle?.fontWeight || ''}
                                style:color={textStyle?.textColor || '#000'}
                            >
                                {value}
                            </div>
                        </div>
                    {/if}
                </div>
                {#if secondVariable}
                    <div
                        class="{css.slider__thumb} {css.slider__thumb_secondary}"
                        style:border-radius={pxToEm(thumbSecondaryStyle.borderRadius)}
                        style:background={thumbSecondaryStyle.background}
                        style:box-shadow={thumbSecondaryStyle.boxShadow || ''}
                    >
                        {#if textSecondaryStyle}
                            <div class="{css['slider__thumb-text']} {css['slider__thumb-text_secondary']}">
                                <div
                                    class={css['slider__thumb-text-inner']}
                                    style:font-size={textSecondaryStyle?.fontSize || '1em'}
                                    style:font-weight={textSecondaryStyle?.fontWeight || ''}
                                    style:color={textSecondaryStyle?.textColor || '#000'}
                                >
                                    {value2}
                                </div>
                            </div>
                        {/if}
                    </div>
                {/if}
                <input
                    type="range"
                    class={switchedTracks ? `${css.slider__input} ${css.slider__input_secondary}` : css.slider__input}
                    min={minValue}
                    max={maxValue}
                    step="1"
                    value={switchedTracks ? value2 : value}
                    disabled={!isEnabled}
                    aria-label={description}
                    on:input={event => onInputChange(event, 'first')}
                    on:focus={focusHandler}
                    on:blur={blurHandler}
                    bind:this={input}
                >
                {#if secondVariable}
                    <input
                        type="range"
                        class={switchedTracks ? css.slider__input : `${css.slider__input} ${css.slider__input_secondary}`}
                        min={minValue}
                        max={maxValue}
                        step="1"
                        value={switchedTracks ? value : value2}
                        disabled={!isEnabled}
                        aria-label={secondaryDescription}
                        on:input={event => onInputChange(event, 'second')}
                        on:mousedown={secondVariable ? onSecondMousedown : null}
                        on:touchstart={secondVariable ? onSecondMousedown : null}
                        on:focus={focusHandler}
                        on:blur={blurHandler}
                    >
                {/if}
            </div>
        </div>
    </Outer>
{:else if process.env.DEVTOOL}
    <DevtoolHolder
        {componentContext}
    />
{/if}
