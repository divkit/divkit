import { isPaletteColor, palettePreview, type PaletteItem } from './palette';

export type ImageScale = 'fill' | 'no_scale' | 'fit' | 'stretch';

export type AlignmentHorizontal = 'left' | 'center' | 'right';

export type AlignmentVertical = 'top' | 'center' | 'bottom';

export interface GradientBackground {
    type: 'gradient';
    colors: string[];
    angle?: number;
}

export interface ImageBackground {
    type: 'image';
    image_url: string;
    content_alignment_vertical?: AlignmentVertical;
    content_alignment_horizontal?: AlignmentHorizontal;
    scale?: ImageScale;
    alpha?: number;
    // preload_required
    // filters?: Filter[];
}

/* export interface NinePatchImageBackground {
    type: 'nine_patch_image';
    image_url: string;
    insets: EdgeInsets;
} */

export interface SolidBackground {
    type: 'solid';
    color: string;
}

/* export interface RadialBackgroundRelativeRadius {
    type: 'relative';
    value: 'nearest_side' | 'nearest_corner' | 'farthest_side' | 'farthest_corner';
}

export type RadialBackgroundRadius = FixedSize | RadialBackgroundRelativeRadius;

export interface RadialGradientFixedCenter {
    type: 'fixed';
    value: number;
    // unit:
}

export interface RadialGradientRelativeCenter {
    type: 'relative';
    value: number;
}

export type RadialGradientCenter = RadialGradientFixedCenter | RadialGradientRelativeCenter;

export interface RadialBackground {
    type: 'radial_gradient';
    colors: string[];
    radius?: RadialBackgroundRadius;
    center_x?: RadialGradientCenter;
    center_y?: RadialGradientCenter;
} */

export type Background = GradientBackground | ImageBackground |
    SolidBackground/*  | RadialBackground | NinePatchImageBackground */;

export function backgroundPreview(background: Background[], lang: {
    noBackground: string;
    multipleBackgrounds: string;
    unknownBackgrounds: string;
    backgroundSolid: string;
    backgroundGradient: string;
    backgroundImage: string;
    noUrl: string;
}, palette: PaletteItem[]): string {
    if (!background || !Array.isArray(background) || !background.length) {
        return lang.noBackground;
    }

    if (background.length > 1) {
        return lang.multipleBackgrounds;
    }

    return backgroundPreviewSingle(background[0], lang, palette);
}

export function backgroundPreviewSingle(background: Background, lang: {
    unknownBackgrounds: string;
    backgroundSolid: string;
    backgroundGradient: string;
    backgroundImage: string;
    noUrl: string;
}, palette: PaletteItem[]): string {
    if (background.type === 'solid') {
        // todo rect preview
        const isPalette = isPaletteColor(background.color);
        const preview = isPalette ? palettePreview(palette, background.color) : background.color;
        return lang.backgroundSolid.replace('%s', () => preview);
    } else if (background.type === 'gradient') {
        return lang.backgroundGradient;
    } else if (background.type === 'image') {
        return lang.backgroundImage.replace('%s', () => background.image_url || lang.noUrl);
    }

    return lang.unknownBackgrounds;
}
