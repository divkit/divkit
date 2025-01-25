<script lang="ts">
    import { getContext } from 'svelte';
    import { isPaletteColor, valueToPaletteId } from '../data/palette';
    import { divkitColorToCss, divkitColorToCssWithoutAlpha } from '../utils/colors';
    import { APP_CTX, type AppContext } from '../ctx/appContext';

    export let color: string;
    export let mix = '';

    const { state } = getContext<AppContext>(APP_CTX);
    const { palette, previewThemeStore } = state;

    function colorToCss(color: string, withAlpha: boolean, previewTheme: 'light' | 'dark'): string {
        if (!color) {
            return 'transparent';
        }

        const func = withAlpha ? divkitColorToCss : divkitColorToCssWithoutAlpha;

        if (isPaletteColor(color)) {
            const paletteId = valueToPaletteId(color);
            const paletteItem = $palette.find(it => it.id === paletteId);
            if (paletteItem) {
                return func(paletteItem[previewTheme]);
            }
        } else {
            return func(color);
        }

        return 'transparent';
    }
</script>

<div class="color-preview {mix}">
    <div class="color-preview__bg"></div>
    <div
        class="color-preview__no-alpha"
        style:background-color={colorToCss(color, false, $previewThemeStore)}
    />
    <div
        class="color-preview__alpha"
        style:background-color={colorToCss(color, true, $previewThemeStore)}
    />
    <div class="color-preview__inset" />
</div>

<style>
    .color-preview {
        position: relative;
        overflow: hidden;
    }

    .color-preview__bg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: url(../../assets/alpha.png);
        background-size: 6px;
        opacity: .3;
    }

    .color-preview__no-alpha {
        position: absolute;
        top: -50%;
        right: -50%;
        left: -50%;
        height: 100%;
        transform-origin: 50% 100%;
        transform: rotate(-45deg);
    }

    .color-preview__alpha {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
    }

    .color-preview__inset {
        position: absolute;
        top: 0;
        left: 0;
        box-sizing: border-box;
        width: 100%;
        height: 100%;
        border-radius: 4px;
        border: 1px solid var(--fill-transparent-4);
    }
</style>
