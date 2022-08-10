import type { AlignmentHorizontal, AlignmentVertical } from './alignment';
import type { ImageScale } from './imageScale';

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
}

export interface SolidBackground {
    type: 'solid';
    color: string;
}

export type Background = GradientBackground | ImageBackground | SolidBackground;
