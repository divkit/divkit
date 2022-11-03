import type { Shape } from './shape';
import type { Stroke } from './border';

export interface Drawable {
    type: 'shape_drawable';
    shape: Shape;
    color: string;
    stroke?: Stroke;
}
