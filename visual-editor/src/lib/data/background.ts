import { isPaletteColor, palettePreview, type PaletteItem } from './palette';

export type ImageScale = 'fill' | 'no_scale' | 'fit' | 'stretch';

export type AlignmentHorizontal = 'left' | 'center' | 'right';

export type AlignmentVertical = 'top' | 'center' | 'bottom';

export interface GradientBackground {
    type: 'gradient';
    colors?: string[];
    color_map?: {
        color: string;
        position: number;
    }[];
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

export function isEqualDistribution(colors: {
    position: number;
}[]): boolean {
    return colors.every((it, index) => Math.abs(it.position - (index / (colors.length - 1))) < 1e-6);
}

export function sortColorMap<T extends {
    position: number;
}>(colors: T[]): T[] {
    return colors.slice().sort((a, b) => {
        if (Math.abs(a.position - b.position) < 1e-6) {
            return 0;
        }
        return a.position - b.position;
    });
}

export function gradientToList(gradient: GradientBackground): {
    color: string;
    position: number;
}[] {
    const colorsList = gradient.colors;

    return colorsList ?
        colorsList.map((it, index) => ({
            color: it,
            position: index / (colorsList.length - 1)
        })) :
        Array.isArray(gradient.color_map) && gradient.color_map || [];
}
