<script lang="ts" context="module">
    const DEFAULT_COLOR: ParsedColor = {
        a: 255,
        r: 0,
        g: 0,
        b: 0
    };
</script>

<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    import { parseColor, stringifyColorToCss, rgbToHsv, hsvToRgb, stringifyColorToDivKit, type ParsedColor } from '../utils/colors';
    import { spectrumDown, spectrumLeft, spectrumRight, spectrumUp } from '../utils/keybinder/shortcuts';
    import ColorInput from './controls/ColorInput.svelte';

    export let value: string;
    export let disabled = false;

    const dispatch = createEventDispatcher();

    let parsed = parseColor(value || '') || { ...DEFAULT_COLOR };

    $: cssColorWithoutAlpha = stringifyColorToCss({ ...parsed, a: 255 });
    $: cssColorHue = stringifyColorToCss(hsvToRgb({ h: hue, s: 1, v: 1, a: 1 }));
    let hue = 0;
    let saturation = 0;
    let val = 0;
    $: alpha = parsed.a / 255;

    let spectrumElem: HTMLElement;

    setParsedColor([]);

    $: if (value !== stringifyColorToDivKit(parsed)) {
        parsed = parseColor(value || '') || { ...DEFAULT_COLOR };
        setParsedColor([]);
    }

    function onHueInput(event: Event): void {
        hue = Number((event.target as HTMLInputElement).value);
        const newColor = hsvToRgb({
            h: hue,
            s: saturation,
            v: val,
            a: alpha
        });
        parsed = newColor;
        setParsedColor(['hue', 'saturation', 'value']);
        dispatch('change', value);
    }

    function onAlphaInput(event: Event): void {
        parsed = {
            ...parsed,
            a: Math.round(Number((event.target as HTMLInputElement).value) * 255)
        };
        setParsedColor(['alpha']);
        dispatch('change', value);
    }

    function onInput(): void {
        const newParsed = parseColor(value);
        if (newParsed) {
            parsed = newParsed;
            setParsedColor(['hex']);
            dispatch('change', value);
        }
    }

    function setParsedColor(except: string[]): void {
        const hsv = rgbToHsv(parsed);

        if (!except.includes('hue')) {
            hue = hsv.h;
        }
        if (!except.includes('saturation')) {
            saturation = hsv.s;
        }
        if (!except.includes('value')) {
            val = hsv.v;
        }

        value = stringifyColorToDivKit(parsed);
    }

    function updateSpectrumColor(event: PointerEvent): void {
        const spectrumBbox = spectrumElem.getBoundingClientRect();
        const x = Math.max(0, Math.min(1, (event.clientX - spectrumBbox.left) / spectrumBbox.width));
        const y = Math.max(0, Math.min(1, (event.clientY - spectrumBbox.top) / spectrumBbox.height));

        saturation = x;
        val = 1 - y;

        const newColor = hsvToRgb({
            h: hue,
            s: saturation,
            v: val,
            a: alpha
        });
        parsed = newColor;
        setParsedColor(['hue', 'saturation', 'value']);
        dispatch('change', value);
    }

    function onSpectrumPointerDown(event: PointerEvent): void {
        if (event.button !== 0 || disabled) {
            return;
        }

        updateSpectrumColor(event);
        event.preventDefault();
        spectrumElem.focus({
            preventScroll: true
        });

        function pointermove(event: PointerEvent): void {
            updateSpectrumColor(event);
            event.preventDefault();
        }

        function pointerup(event: PointerEvent): void {
            event.preventDefault();
            document.body.removeEventListener('pointermove', pointermove);
            document.body.removeEventListener('pointerup', pointerup);
            document.body.removeEventListener('pointercancel', pointerup);

            const cancelClick = (event: MouseEvent) => {
                event.preventDefault();
                event.stopPropagation();
            };
            document.body.addEventListener('click', cancelClick);
            setTimeout(() => {
                document.body.removeEventListener('click', cancelClick);
            }, 150);
        }

        document.body.addEventListener('pointermove', pointermove);
        document.body.addEventListener('pointerup', pointerup);
        document.body.addEventListener('pointercancel', pointerup);
    }

    function moveSpectrum(x: number, y: number): void {
        saturation = Math.max(0, Math.min(1, saturation + x));
        val = Math.max(0, Math.min(1, val - y));

        const newColor = hsvToRgb({
            h: hue,
            s: saturation,
            v: val,
            a: alpha
        });
        parsed = newColor;
        setParsedColor(['hue', 'saturation', 'value']);
        dispatch('change', value);
    }

    function onSpectrumKeyDown(event: KeyboardEvent): void {
        if (spectrumUp.isPressed(event)) {
            moveSpectrum(0, -.05);
        } else if (spectrumRight.isPressed(event)) {
            moveSpectrum(.05, 0);
        } else if (spectrumDown.isPressed(event)) {
            moveSpectrum(0, .05);
        } else if (spectrumLeft.isPressed(event)) {
            moveSpectrum(-.05, 0);
        } else {
            return;
        }

        event.preventDefault();
        event.stopPropagation();
    }
</script>

<div
    class="color-select"
    class:color-select_disabled={disabled}
>
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <!-- svelte-ignore a11y_no_noninteractive_tabindex -->
    <div
        class="color-select__spectrum"
        style:background={cssColorHue}
        tabindex="0"
        bind:this={spectrumElem}
        on:pointerdown={onSpectrumPointerDown}
        on:keydown={onSpectrumKeyDown}
    >
        <div class="color-select__saturation">
            <div class="color-select__value">
                <div class="color-select__target" style:--saturation={saturation} style:--value={val}></div>
            </div>
        </div>
    </div>

    <div class="color-select__values">
        <div class="color-select__range color-select__hue" style:--value={hue}>
            <div class="color-select__range-value"></div>
            <input
                type="range"
                class="color-select__range-input"
                value={hue}
                min="0"
                max="0.99"
                step="0.01"
                disabled={disabled}
                on:input={onHueInput}
            >
        </div>

        <div class="color-select__range color-select__alpha" style:--value={alpha} style:--color={cssColorWithoutAlpha}>
            <div class="color-select__alpha-bg"></div>
            <div class="color-select__alpha-value"></div>
            <div class="color-select__range-value"></div>

            <input
                type="range"
                class="color-select__range-input"
                min="0"
                max="1"
                step="0.01"
                value={alpha}
                disabled={disabled}
                on:input={onAlphaInput}
            >
        </div>

        <div class="color-select__input">
            <ColorInput
                readOnly={disabled}
                bind:value={value}
                on:change={onInput}
            />
        </div>
    </div>
</div>

<style>
    .color-select {
        display: flex;
        flex-direction: column;
        gap: 16px;
    }

    .color-select__spectrum {
        position: relative;
        flex: 0 0 auto;
        aspect-ratio: 1.138;
        cursor: crosshair;
    }

    .color-select_disabled .color-select__spectrum {
        cursor: default;
    }

    .color-select__spectrum:focus-visible {
        outline: 1px solid var(--accent-purple);
    }

    .color-select__saturation {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-image: linear-gradient(to right, rgb(255, 255, 255), rgba(204, 154, 129, 0));
    }

    .color-select__target {
        position: absolute;
        top: calc(100% - 100% * var(--value));
        left: calc(100% * var(--saturation));
        box-sizing: border-box;
        width: 12px;
        height: 12px;
        margin-top: -6px;
        margin-left: -6px;
        border-radius: 1024px;
        border: 2px solid #fff;
        box-shadow: 0 0 0 1px rgba(0, 0, 0, .5);
    }

    .color-select__values {
        margin: 0 16px;
    }

    .color-select__value {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-image: linear-gradient(to top, rgb(0, 0, 0), rgba(204, 154, 129, 0));
    }

    .color-select__range {
        position: relative;
        box-sizing: border-box;
        height: 16px;
        border-radius: 1024px;
        box-shadow: inset 0 0 0 1px var(--fill-transparent-3);
        overflow: hidden;
    }

    .color-select__hue {
        background: linear-gradient(
            to right,
            rgb(255, 0, 0) 0%,
            rgb(255, 255, 0) 17%,
            rgb(0, 255, 0) 33%,
            rgb(0, 255, 255) 50%,
            rgb(0, 0, 255) 67%,
            rgb(255, 0, 255) 83%,
            rgb(255, 0, 0) 100%
        );
    }

    .color-select__alpha {
        margin-top: 12px;
        background: #fff;
    }

    .color-select__alpha-bg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: url(../../assets/alpha.png);
        background-size: 6px;
        opacity: .3;
    }

    .color-select__alpha-value {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(to right, transparent, var(--color));
    }

    .color-select__range-value {
        position: absolute;
        top: 2px;
        left: calc(2px + calc(100% - 16px) * 100 / 99 * var(--value));
        box-sizing: border-box;
        width: 12px;
        height: 12px;
        border: 2px solid #fff;
        border-radius: 1024px;
        box-shadow: 0 0 0 1px var(--fill-transparent-3);
        transition: border-color .15s ease-in-out;
    }

    .color-select__range:focus-within .color-select__range-value {
        outline: 3px solid var(--accent-purple);
    }

    .color-select__alpha .color-select__range-value {
        left: calc(2px + calc(100% - 16px) * var(--value));
        box-shadow: 0 0 0 1px rgba(0, 0, 0, .5);
    }

    .color-select__range-input {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        margin: 0;
        opacity: 0;
    }

    .color-select__input {
        margin-top: 20px;
    }
</style>
