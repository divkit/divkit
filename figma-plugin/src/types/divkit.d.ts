export type BooleanInt = 0 | 1 | false | true;

export interface DivVariable {
    type: string;
    name: string;
    value: string;
}

export interface PaletteColor {
    name: string;
    color: string;
}

export interface Palette {
    light: PaletteColor[];
    dark: PaletteColor[];
}

export interface DivBase {
    type: string;
}

export interface DivJsonState {
    state_id: number;
    div: DivBase;
}

export interface DivJson {
    card: {
        log_id: string;
        states: DivJsonState[];
        variables?: DivVariable[];
    }
    palette?: Palette;
}

export interface FixedSize {
    type: 'fixed';
    value: number;
}

export interface MatchParentSize {
    type: 'match_parent';
    weight?: number;
}

export interface WrapContentSize {
    type: 'wrap_content';
    constrained?: BooleanInt;
    min_size?: { value: number };
    max_size?: { value: number };
}

export type Size = FixedSize | MatchParentSize | WrapContentSize;

export interface Stroke {
    width?: number;
    color: string;
}

export interface CornersRadius {
    'top-left'?: number;
    'top-right'?: number;
    'bottom-left'?: number;
    'bottom-right'?: number;
}

export interface Point {
    x: { value: number };
    y: { value: number };
}

export interface Shadow {
    color?: string;
    offset: Point;
    blur?: number;
    alpha?: number;
}

export interface Border {
    has_shadow?: BooleanInt;
    shadow?: Shadow;
    stroke?: Stroke;
    corner_radius?: number;
    corners_radius?: CornersRadius;
}

export interface EdgeInsets {
    left?: number;
    right?: number;
    top?: number;
    bottom?: number;
}

export interface Accessibility {
    description?: string;
    type?: string;
}

export interface SolidBackground {
    type: 'solid';
    color: string;
}

export interface LinearGradient {
    type: 'gradient';
    colors: string[];
    angle?: number;
}

export interface RadialGradient {
    type: 'radial_gradient';
    colors: string[];
    radius?: FixedSize;
    center_x?: FixedSize;
    center_y?: FixedSize;
}

export type Background = SolidBackground | LinearGradient | RadialGradient;

export interface LayoutParams {
    alignment_horizontal?: string;
    alignment_vertical?: string;
    width?: Size;
    height?: Size;
    margins?: EdgeInsets;
    alpha?: number;
}

export interface DivBaseData extends LayoutParams {
    type: string;
    id?: string;
    border?: Border;
    background?: Background[];
    paddings?: EdgeInsets;
    accessibility?: Accessibility;
    // debug field
    name?: string;
}

export interface DivContainerData extends DivBaseData {
    type: 'container';
    content_alignment_horizontal?: string;
    content_alignment_vertical?: string;
    orientation?: 'vertical' | 'horizontal' | 'overlap';
    items: DivBaseData[];
}

export interface DivSeparatorData extends DivBaseData {
    type: 'separator';
    delimiter_style?: {
        color?: string;
        orientation?: 'vertical' | 'horizontal';
    };
}

export interface DivImageData extends DivBaseData {
    type: 'image' | 'gif';
    image_url?: string;
    gif_url?: string;
    placeholder_color?: string;
    scale?: 'no_scale' | 'fill' | 'fit';
    content_alignment_horizontal?: string;
    content_alignment_vertical?: string;
    preview?: string;
}

export interface TextStyles {
    font_size?: number;
    letter_spacing?: number;
    font_weight?: string;
    text_color?: string;
    text_gradient?: LinearGradient | RadialGradient;
    line_height?: number;
}

export interface TextRange extends TextStyles {
    start: number;
    end: number;
    background?: SolidBackground;
}

export interface DivTextData extends DivBaseData, TextStyles {
    type: 'text';
    text_alignment_horizontal?: string;
    text_alignment_vertical?: string;
    text: string;
    ranges?: TextRange[];
}
