<script lang="ts" context="module">
    const FALLBACK_IMAGE = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7';
    const EMPTY_IMAGE = 'empty://';
    // const DEFAULT_PLACEHOLDER_COLOR = correctColor('#14000000');
    const DEFAULT_PLACEHOLDER_COLOR = 'rgba(0,0,0,0.08)';

    const STATE_LOADING = 0;
    const STATE_LOADED = 1;
    const STATE_ERROR = 2;
</script>

<script lang="ts">
    import { getContext, onDestroy } from 'svelte';

    import css from './Image.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivImageData, TintMode } from '../../types/image';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { AlignmentHorizontal, AlignmentVertical } from '../../types/alignment';
    import Outer from '../utilities/Outer.svelte';
    import { makeStyle } from '../../utils/makeStyle';
    import { genClassName } from '../../utils/genClassName';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';
    import { imageSize } from '../../utils/background';
    import { correctImagePosition } from '../../utils/correctImagePosition';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { correctColor } from '../../utils/correctColor';
    import { correctCSSInterpolator } from '../../utils/correctCSSInterpolator';
    import { correctNonNegativeNumber } from '../../utils/correctNonNegativeNumber';
    import { correctTintMode } from '../../utils/correctTintMode';
    import { getCssFilter } from '../../utils/filters';
    import { prepareBase64 } from '../../utils/prepareBase64';

    export let json: Partial<DivImageData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let img: HTMLImageElement;
    let state = STATE_LOADING;
    let isEmpty = false;
    let placeholderColor = DEFAULT_PLACEHOLDER_COLOR;

    let hasError = false;
    let imageUrl: string | undefined;
    let backgroundImage = '';
    // Exactly "none", "scale-down" would not match android
    let scale = 'none';
    let position = '50% 50%';
    let aspectPaddingBottom = '0';
    let tintColor: string | undefined = undefined;
    let tintMode: TintMode = 'source_in';
    let svgFilterId = '';
    let animationInterpolator = '';
    let animationFadeStart = 0;
    let animationDelay = 0;
    let animationDuration = 0;
    let filter = '';
    let filterClipPath = '';

    $: if (json) {
        scale = 'none';
        position = '50% 50%';
        tintMode = 'source_in';
    }

    $: jsonImageUrl = rootCtx.getDerivedFromVars(json.image_url);
    $: jsonGifUrl = rootCtx.getDerivedFromVars(json.gif_url);
    $: jsonWidth = rootCtx.getDerivedFromVars(json.width);
    $: jsonHeight = rootCtx.getDerivedFromVars(json.height);
    $: jsonPreview = rootCtx.getDerivedFromVars(json.preview);
    $: jsonPlaceholderColor = rootCtx.getDerivedFromVars(json.placeholder_color);
    $: jsonScale = rootCtx.getDerivedFromVars(json.scale);
    $: jsonPosition = rootCtx.getDerivedFromVars({
        content_alignment_horizontal: json.content_alignment_horizontal,
        content_alignment_vertical: json.content_alignment_vertical
    });
    $: jsonA11y = rootCtx.getDerivedFromVars(json.accessibility);
    $: jsonAspect = rootCtx.getDerivedFromVars(json.aspect);
    $: jsonTintColor = rootCtx.getDerivedFromVars(json.tint_color);
    $: jsonTintMode = rootCtx.getDerivedFromVars(json.tint_mode);
    $: jsonAppearanceAnimation = rootCtx.getDerivedFromVars(json.appearance_animation);
    $: jsonFilters = rootCtx.getDerivedFromVars(json.filters);

    $: {
        let img = json.type === 'gif' ? $jsonGifUrl : $jsonImageUrl;
        isEmpty = img === EMPTY_IMAGE;
        if (isEmpty) {
            img = FALLBACK_IMAGE;
        }
        imageUrl = img;
    }

    function updateImageUrl(_url: string | undefined): void {
        state = STATE_LOADING;
    }
    $: updateImageUrl(imageUrl);

    $: {
        if (!imageUrl) {
            hasError = true;
            rootCtx.logError(wrapError(new Error(`Missing "${json.type === 'gif' ? 'gif_url' : 'image_url'}" for "${json.type}"`)));
        } else {
            hasError = false;
        }
    }

    $: isWidthContent = $jsonWidth?.type === 'wrap_content';

    $: isHeightContent = $jsonHeight?.type === 'wrap_content';

    $: {
        const preview = $jsonPreview;

        if ((state === STATE_LOADING || state === STATE_ERROR || isEmpty) && preview) {
            backgroundImage = `url("${prepareBase64(preview)}")`;
        } else {
            backgroundImage = '';
        }
    }

    $: if (state === STATE_LOADING || state === STATE_ERROR || isEmpty) {
        placeholderColor = correctColor($jsonPlaceholderColor, 1, placeholderColor);
    } else {
        placeholderColor = '';
    }

    $: {
        scale = imageSize($jsonScale) || scale;
    }

    function updatePosition(pos: {
        content_alignment_horizontal?: AlignmentHorizontal;
        content_alignment_vertical?: AlignmentVertical;
    }): void {
        position = correctImagePosition(pos, position);
    }
    $: updatePosition($jsonPosition);

    $: alt = $jsonA11y?.description || '';

    $: {
        const newRatio = $jsonAspect?.ratio;
        if (newRatio && isPositiveNumber(newRatio)) {
            aspectPaddingBottom = (100 / Number(newRatio)).toFixed(2);
        } else {
            aspectPaddingBottom = '0';
        }
    }

    $: {
        const val = $jsonTintColor;
        const newTintColor = val ? correctColor(val) : undefined;
        const newTintMode = correctTintMode($jsonTintMode, tintMode);
        if (newTintColor !== tintColor || newTintMode !== tintMode) {
            rootCtx.removeSvgFilter(tintColor, tintMode);
            svgFilterId = newTintColor ? rootCtx.addSvgFilter(newTintColor, newTintMode) : '';
            tintColor = newTintColor;
            tintMode = newTintMode;
        }
    }

    $: if ($jsonAppearanceAnimation && $jsonAppearanceAnimation.type === 'fade') {
        const animation = $jsonAppearanceAnimation;

        animationInterpolator = correctCSSInterpolator(animation.interpolator, 'ease_in_out').replace(/_/g, '-');
        animationDuration = correctNonNegativeNumber(animation.duration, 300);
        animationDelay = correctNonNegativeNumber(animation.start_delay, 0);
        animationFadeStart = correctNonNegativeNumber(animation.alpha, 0);
    }

    $: {
        let newFilter = '';
        let newClipPath = '';
        if (Array.isArray($jsonFilters) && $jsonFilters.length) {
            newFilter = getCssFilter($jsonFilters, rootCtx.logError);
        }
        if (newFilter) {
            newClipPath = 'polygon(0% 0%, 0% 100%, 100% 100%, 100% 0%)';
        }
        filter = newFilter;
        filterClipPath = newClipPath;
    }

    $: mods = {
        aspect: aspectPaddingBottom !== '0',
        'is-width-content': isWidthContent,
        'is-height-content': isHeightContent,
        loaded: state === STATE_LOADED,
        'before-appearance': Boolean(animationInterpolator) && state === STATE_LOADING
    };

    $: style = {
        // Image preview shows, if loading of original image is failed
        'background-image': backgroundImage,
        'background-color': backgroundImage ? undefined : placeholderColor,
        'background-size': scale,
        'clip-path': filterClipPath || undefined,
        'object-fit': scale,
        'object-position': position,
        filter: [
            state === STATE_LOADED && svgFilterId ? `url(#${svgFilterId})` : '',
            filter
        ].filter(Boolean).join(' '),
        '--divkit-appearance-interpolator': animationInterpolator || undefined,
        '--divkit-appearance-fade-start': animationInterpolator ? animationFadeStart : undefined,
        '--divkit-appearance-delay': animationInterpolator ? `${animationDelay}ms` : undefined,
        '--divkit-appearance-duration': animationInterpolator ? `${animationDuration}ms` : undefined
    };

    function onLoad(): void {
        if (state === STATE_LOADING) {
            state = STATE_LOADED;
        }
    }

    function onError(): void {
        if (state === STATE_LOADING) {
            state = STATE_ERROR;
        }
    }

    onDestroy(() => {
        rootCtx.removeSvgFilter(tintColor, tintMode);
    });

    // Recreate image on svg filter change for the Safari
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
        {#key svgFilterId}
            {#if mods.aspect}
                <span class={css['image__aspect-wrapper']} style:padding-bottom="{aspectPaddingBottom}%">
                    <img
                        bind:this={img}
                        class={css.image__image}
                        src={state === STATE_ERROR ? FALLBACK_IMAGE : imageUrl}
                        loading="lazy"
                        decoding="async"
                        style={makeStyle(style)}
                        {alt}
                        aria-hidden={alt ? null : 'true'}
                        on:load={onLoad}
                        on:error={onError}
                    >
                </span>
            {:else}
                <img
                    bind:this={img}
                    class={css.image__image}
                    src={state === STATE_ERROR ? FALLBACK_IMAGE : imageUrl}
                    loading="lazy"
                    decoding="async"
                    style={makeStyle(style)}
                    {alt}
                    aria-hidden={alt ? null : 'true'}
                    on:load={onLoad}
                    on:error={onError}
                >
            {/if}
        {/key}
    </Outer>
{/if}
