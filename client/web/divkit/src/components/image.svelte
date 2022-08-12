<script lang="ts">
    import { getContext, onDestroy } from 'svelte';

    import css from './image.module.css';

    import type { LayoutParams } from '../types/layoutParams';
    import type { DivImageData } from '../types/image';
    import type { DivBase, TemplateContext } from '../../typings/common';
    import type { AlignmentHorizontal, AlignmentVertical } from '../types/alignment';
    import Outer from './outer.svelte';
    import { makeStyle } from '../utils/makeStyle';
    import { genClassName } from '../utils/genClassName';
    import { ROOT_CTX, RootCtxValue } from '../context/root';
    import { wrapError } from '../utils/wrapError';
    import { htmlFilter } from '../utils/htmlFilter';
    import { imageSize } from '../utils/background';
    import { correctImagePosition } from '../utils/correctImagePosition';
    import { isPositiveNumber } from '../utils/isPositiveNumber';
    import { correctColor } from '../utils/correctColor';

    export let json: Partial<DivImageData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const FALLBACK_IMAGE = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7';
    // const DEFAULT_PLACEHOLDER_COLOR = correctColor('#14000000');
    const DEFAULT_PLACEHOLDER_COLOR = 'rgba(0,0,0,0.08)';

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    $: jsonImageUrl = rootCtx.getDerivedFromVars(json.image_url);

    $: jsonGifUrl = rootCtx.getDerivedFromVars(json.gif_url);
    $: imageUrl = $jsonImageUrl || $jsonGifUrl;

    let fallbackMode = false;
    let loading = true;
    let placeholderColor = DEFAULT_PLACEHOLDER_COLOR;

    function updateImageUrl(_url: string | undefined): void {
        loading = true;
        fallbackMode = false;
    }
    $: updateImageUrl(imageUrl);

    let hasError = false;
    $: {
        if (!imageUrl) {
            hasError = true;
            rootCtx.logError(wrapError(new Error('Missing "image_url" for "image"')));
        } else {
            hasError = false;
        }
    }

    $: jsonWidth = rootCtx.getDerivedFromVars(json.width);
    $: isWidthContent = $jsonWidth?.type === 'wrap_content';

    $: jsonHeight = rootCtx.getDerivedFromVars(json.height);
    $: isHeightContent = $jsonHeight?.type === 'wrap_content';

    $: jsonPreview = rootCtx.getDerivedFromVars(json.preview);
    let backgroundImage = '';
    $: {
        const preview = $jsonPreview;

        if ((loading || fallbackMode) && preview) {
            if (preview.startsWith('data:')) {
                backgroundImage = `url("${htmlFilter(preview)}")`;
            } else {
                backgroundImage = `url("data:image/jpg;base64,${htmlFilter(preview)}")`;
            }
        } else {
            backgroundImage = '';
        }
    }

    $: jsonPlaceholderColor = rootCtx.getDerivedFromVars(json.placeholder_color);
    function updatePlaceholderColor(loading: boolean, fallbackMode: boolean, color: string | undefined): void {
        if (loading || fallbackMode) {
            placeholderColor = correctColor(color, 1, placeholderColor);
        } else {
            placeholderColor = '';
        }
    }
    $: updatePlaceholderColor(loading, fallbackMode, $jsonPlaceholderColor);

    $: jsonScale = rootCtx.getDerivedFromVars(json.scale);
    // Exactly "none", "scale-down" would not match android
    let scale = 'none';
    $: {
        scale = imageSize($jsonScale) || scale;
    }

    $: jsonPosition = rootCtx.getDerivedFromVars({
        content_alignment_horizontal: json.content_alignment_horizontal,
        content_alignment_vertical: json.content_alignment_vertical
    });
    let position = '50% 50%';
    function updatePosition(pos: {
        content_alignment_horizontal?: AlignmentHorizontal;
        content_alignment_vertical?: AlignmentVertical;
    }): void {
        position = correctImagePosition(pos, position);
    }
    $: updatePosition($jsonPosition);

    $: jsonA11y = rootCtx.getDerivedFromVars(json.accessibility);
    $: alt = $jsonA11y?.description || '';

    $: jsonAspect = rootCtx.getDerivedFromVars(json.aspect);
    let aspectPaddingBottom = '0';
    $: {
        const newRatio = $jsonAspect?.ratio;
        if (newRatio && isPositiveNumber(newRatio)) {
            aspectPaddingBottom = (100 / Number(newRatio)).toFixed(2);
        } else {
            aspectPaddingBottom = '0';
        }
    }

    $: jsonTintColor = rootCtx.getDerivedFromVars(json.tint_color);
    let tintColor: string | undefined = undefined;
    let svgFilterId = '';
    $: {
        const val = $jsonTintColor;
        const newTintColor = val ? correctColor(val) : undefined;
        if (newTintColor !== tintColor) {
            rootCtx.removeSvgFilter(tintColor);
            svgFilterId = newTintColor ? rootCtx.addSvgFilter(newTintColor) : '';
            tintColor = newTintColor;
        }
    }

    $: mods = {
        aspect: aspectPaddingBottom !== '0',
        'is-width-content': isWidthContent,
        'is-height-content': isHeightContent
    };

    $: style = {
        'background-image': backgroundImage,
        'background-color': placeholderColor,
        'object-fit': scale,
        'object-position': position,
        filter: !loading && !fallbackMode && svgFilterId ? `url(#${svgFilterId})` : ''
    };

    $: styleStr = makeStyle(style);

    function onLoad(): void {
        loading = false;
    }

    function onError(): void {
        fallbackMode = true;
    }

    onDestroy(() => {
        rootCtx.removeSvgFilter(tintColor);
    });
</script>

{#if !hasError}
    <Outer
        cls={genClassName('image', css, mods)}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
        customDescription={true}
    >
        {#if mods.aspect}
            <span class={css['image__aspect-wrapper']} style:padding-bottom="{aspectPaddingBottom}%">
                <img
                    class={css.image__image}
                    src={fallbackMode ? FALLBACK_IMAGE : imageUrl}
                    loading="lazy"
                    decoding="async"
                    style={styleStr}
                    {alt}
                    aria-hidden={alt ? null : 'true'}
                    on:load={onLoad}
                    on:error={onError}
                >
            </span>
        {:else}
            <img
                class={css.image__image}
                src={fallbackMode ? FALLBACK_IMAGE : imageUrl}
                loading="lazy"
                decoding="async"
                style={makeStyle(style)}
                {alt}
                aria-hidden={alt ? null : 'true'}
                on:load={onLoad}
                on:error={onError}
            >
        {/if}
    </Outer>
{/if}
