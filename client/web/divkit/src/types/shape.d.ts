import type { FixedSize } from './sizes';
import type { Stroke } from './border';

export interface ShapeBase {
    background_color?: string;
    stroke?: Stroke;
}

export interface RoundedRectangle extends ShapeBase {
    type: 'rounded_rectangle';
    item_width?: FixedSize;
    item_height?: FixedSize;
    corner_radius?: FixedSize;
}

export interface Circle extends ShapeBase {
    type: 'circle';
    radius?: FixedSize;
}

export type Shape = RoundedRectangle | Circle;
