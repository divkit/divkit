import type { AlignmentHorizontal, AlignmentVertical } from './alignment';
import type { ImageScale } from './imageScale';
import type { FixedSize } from './sizes';
import type { EdgeInsets } from './edgeInserts';
import type { Filter } from './filter';

export interface GradientColorPoint {
    color: string;
    position: number;
}

export interface GradientBackground {
    type: 'gradient';
    colors?: string[];
    color_map?: GradientColorPoint[];
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
    filters?: Filter[];
}

export interface NinePatchImageBackground {
    type: 'nine_patch_image';
    image_url: string;
    insets: EdgeInsets;
}

export interface SolidBackground {
    type: 'solid';
    color: string;
}

export interface RadialBackgroundRelativeRadius {
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
}

export type Background = GradientBackground | ImageBackground |
    SolidBackground | RadialBackground | NinePatchImageBackground;
